package com.datastax.banking.service;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

import com.datastax.banking.service.TransactionWriter;
import com.datastax.banking.dao.BankDao;
import com.datastax.banking.dao.BankRedisDao;
import com.datastax.banking.data.BankGenerator;
import com.datastax.banking.model.Account;
import com.datastax.banking.model.Customer;
import com.datastax.banking.model.Email;
import com.datastax.banking.model.Transaction;
import com.datastax.banking.webservice.BankingWS;
import com.datastax.demo.utils.KillableRunner;
import com.datastax.demo.utils.PropertyHelper;
import com.datastax.demo.utils.ThreadUtils;
import com.datastax.demo.utils.Timer;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service

public class BankService {

	private static String contactPointsStr = PropertyHelper.getProperty("contactPoints", "localhost");
	private static BankService bankService = new BankService();
	private static final Logger logger = LoggerFactory.getLogger(BankService.class);
	private BankDao dao;
	private BankRedisDao redisDao;
	private long timerSum = 0;
	private AtomicLong timerCount= new AtomicLong();
	
	private BankService(){
		dao = new BankDao(contactPointsStr.split(","));
		redisDao = new BankRedisDao();
		redisDao.setHost("localhost",6379);
	}

	
	public static BankService getInstance(){
		return bankService;		
	}
	
	public Customer getCustomer(String customerId){
		
		return dao.getCustomer(customerId);
	}
	public List<Customer> getCustomerByPhone(String phoneString){
		logger.warn("in bankservice getCustByPhone with phone=" + phoneString);
		List<String> customerIDList = redisDao.getCustomerIdsbyPhone(phoneString);
		logger.warn("after call to rediDao getCustByPhone" + customerIDList.size());
		return dao.getCustomerListFromIDs(customerIDList);
	}

	public List<Customer> getCustomerByState(String state){
		List<String> customerIDList = redisDao.getCustomerIdsbyState(state);
		return dao.getCustomerListFromIDs(customerIDList);
	}

	public List<Customer> getCustomerByEmail(String email){
		List<String> customerIDList = redisDao.getCustomerIdsbyEmail(email);
		return dao.getCustomerListFromIDs(customerIDList);
	}

	public List<Customer> getCustomerByFullNamePhone(String fullName, String phoneString){
		List<String> customerIDList = redisDao.getCustomerByFullNamePhone(fullName, phoneString);
		return dao.getCustomerListFromIDs(customerIDList);
	}
		
	public List<Account> getAccounts(String customerId){
		
		return dao.getCustomerAccounts(customerId);
	}

	public List<Transaction> getTransactions(String accountId) {


		return dao.getTransactions(accountId);
	}
	public List<Transaction> getTransactionsForCCNoDateSolr(String ccNo, Set<String> tags, DateTime from, DateTime to) {

		List<Transaction> transactions;

		transactions = dao.getTransactionsForCCNoDateSolr(ccNo, tags, from, to);

		return transactions;
	}

	public void addTag(String accountNo, String trandate, String transactionID, String tag, String operation) {
		dao.addTag(accountNo,trandate, transactionID,tag,operation);
	}

	public void addCustChange(String accountNo,String custid, String chgdate) {
		dao.addCustChange(accountNo,custid,chgdate);
	}

	public List<Transaction> getTransactionsCTGDESC(String mrchntctgdesc) {
		return dao.getTransactionsCTGDESC(mrchntctgdesc);
	}

	public String generateData(Integer noOfCustomers, Integer noOfTransactions, Integer noOfDays,
	Integer noOfThreads) {

		BlockingQueue<Transaction> queue = new ArrayBlockingQueue<Transaction>(1000);
		List<KillableRunner> tasks = new ArrayList<>();

		//Executor for Threads
		ExecutorService executor = Executors.newFixedThreadPool(noOfThreads);
		redisDao.setHost("localhost", 6379);
		redisDao.createCustomerSchema();

		createCustomerAccount(noOfCustomers, dao, redisDao);

		for (int i = 0; i < noOfThreads; i++) {

			KillableRunner task = new TransactionWriter(dao, queue);
			executor.execute(task);
			tasks.add(task);
		}

		BankGenerator.date = new DateTime().minusDays(noOfDays).withTimeAtStartOfDay();
		Timer timer = new Timer();

		int totalTransactions = noOfTransactions * noOfDays;

		logger.info("Writing " + totalTransactions + " transactions for " + noOfCustomers + " customers.");
		for (int i = 0; i < totalTransactions; i++) {

			try{
				Transaction randomTransaction = BankGenerator.createRandomTransaction(noOfDays, noOfCustomers, BankService.getInstance());
				if (randomTransaction!=null){
					queue.put(randomTransaction);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			if (i%10000 == 0){
				sleep(10);
			}
		}
		timer.end();
		Random r = new Random();

		while(true){
			try{
				Transaction randomTransaction = BankGenerator.createRandomTransaction(new DateTime(), noOfCustomers, BankService.getInstance());
				if (randomTransaction!=null){
					queue.put(randomTransaction);
				}
				sleep(new Double(Math.random()*20).intValue());

				double d = r.nextGaussian()*-1d;
				int test = new Double(Math.random()*10).intValue();

				if (d*test < 1){
					int someNumber = new Double(Math.random()*10).intValue();

					for (int i=0; i < someNumber; i++){
						randomTransaction = BankGenerator.createRandomTransaction(new DateTime(), noOfCustomers, BankService.getInstance());
						if (randomTransaction!=null){
							queue.put(randomTransaction);
						}

						if (new Double(Math.random()*100).intValue() > 2){
							sleep(new Double(Math.random()*100).intValue());
						}
					}
				}

			} catch (InterruptedException e) {
				e.printStackTrace();
				break;
			}
		}

		ThreadUtils.shutdown(tasks, executor);

		System.exit(0);
		return "Done";
	}

	private void createCustomerAccount(int noOfCustomers, BankDao dao, BankRedisDao rdao){

		logger.info("Creating " + noOfCustomers + " customers with accounts");
		Timer custTimer = new Timer();

		for (int i=0; i < noOfCustomers; i++){
			Customer customer = BankGenerator.createRandomCustomer(noOfCustomers);
			List<Account> accounts = BankGenerator.createRandomAccountsForCustomer(customer, noOfCustomers);
			Set<String> accountNos = new HashSet<String>();


			for (Account account : accounts){
				dao.insertAccount(account);
				accountNos.add(account.getAccountNo());
			}
			customer.setCustaccounts(accountNos);
			dao.insertCustomer(customer);
			rdao.addCustomerDocument(customer);

			if (i == 1000){
				sleep(100);
			}
		}

		custTimer.end();
		logger.info("Customers and Accounts created in " + custTimer.getTimeTakenSeconds() + " secs");
	}

	private void sleep(int i) {
		try {
			Thread.sleep(i);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
