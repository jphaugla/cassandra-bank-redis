package com.datastax.banking;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.banking.dao.BankDao;
import com.datastax.banking.dao.BankRedisDao;
import com.datastax.banking.data.BankGenerator;
import com.datastax.banking.model.Account;
import com.datastax.banking.model.Customer;
import com.datastax.banking.model.Transaction;
import com.datastax.banking.service.BankService;
import com.datastax.demo.utils.KillableRunner;
import com.datastax.demo.utils.PropertyHelper;
import com.datastax.demo.utils.ThreadUtils;
import com.datastax.demo.utils.Timer;

public class Main {

	private static Logger logger = LoggerFactory.getLogger(Main.class);

	public Main() {

		String contactPointsStr = PropertyHelper.getProperty("contactPoints", "localhost");
		String createStr = PropertyHelper.getProperty("create", "false");
		String noOfCustomersStr = PropertyHelper.getProperty("noOfCustomers", "10000");
		String noOfTransactionsStr = PropertyHelper.getProperty("noOfTransactions", "10000");
		int noOfDays = Integer.parseInt(PropertyHelper.getProperty("noOfDays", "180"));

		BlockingQueue<Transaction> queue = new ArrayBlockingQueue<Transaction>(1000);
		List<KillableRunner> tasks = new ArrayList<>();
		
		//Executor for Threads
		int noOfThreads = Integer.parseInt(PropertyHelper.getProperty("noOfThreads", "8"));
		ExecutorService executor = Executors.newFixedThreadPool(noOfThreads);
		BankDao dao = new BankDao(contactPointsStr.split(","));
		//  Redis start
		BankRedisDao rdao = new BankRedisDao();
		rdao.setHost("localhost",6379);
		rdao.createCustomerSchema();

		int noOfTransactions = Integer.parseInt(noOfTransactionsStr);
		int noOfCustomers = Integer.parseInt(noOfCustomersStr);
		boolean create = Boolean.parseBoolean(createStr);
		
		if (create){
			createCustomerAccount(noOfCustomers, dao, rdao);
		}

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
		
		logger.info("Writing realtime transactions");
		
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

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new Main();

		System.exit(0);
	}
}
