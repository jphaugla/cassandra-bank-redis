package com.datastax.banking.service;

import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import com.datastax.banking.dao.BankDao;
import com.datastax.banking.dao.BankRedisDao;
import com.datastax.banking.model.Account;
import com.datastax.banking.model.Customer;
import com.datastax.banking.model.Email;
import com.datastax.banking.model.Transaction;
import com.datastax.demo.utils.PropertyHelper;
import com.datastax.demo.utils.Timer;
import org.joda.time.DateTime;


public class BankService {

	private static String contactPointsStr = PropertyHelper.getProperty("contactPoints", "localhost");
	private static BankService bankService = new BankService();
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
		List<String> customerIDList = redisDao.getCustomerIdsbyPhone(phoneString);
		return dao.getCustomerListFromIDs(customerIDList);
	}
	public List<Customer> getCustomerByFullNamePhone(String fullName, String phoneString){

		return dao.getCustomerByFullNamePhone(fullName, phoneString);
	}
	public List<Customer> getCustomerByEmail(String emailString){

		return dao.getCustomerByEmail(emailString);
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
}
