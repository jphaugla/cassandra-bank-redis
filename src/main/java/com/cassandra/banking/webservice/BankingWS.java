package com.cassandra.banking.webservice;

import com.cassandra.banking.model.Account;
import com.cassandra.banking.model.Customer;
import com.cassandra.banking.model.Transaction;
import com.cassandra.banking.service.BankService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.List;


@RestController

public class BankingWS {
	private static final Logger logger = LoggerFactory.getLogger(BankingWS.class);

	private static class uiTag {
     	public String accountNo;
     	public String trandate;
     	public String transactionID;
     	public String tag;
	 }

	@Autowired

	//Service Layer.
	private BankService bankService = BankService.getInstance();

	
	@GetMapping("/customer")

	public Customer getCustomer(@RequestParam String customerId) {
		return bankService.getCustomer(customerId);
	}

	@GetMapping("/customerByPhone")

	public List<Customer> getCustomerByPhone(@RequestParam String phoneString) {
		logger.debug("IN get customerByPhone with phone as " + phoneString);
		return bankService.getCustomerByPhone(phoneString);
	}

	@GetMapping("/customerByStateCity")

	public List<Customer> getCustomerByStateCity(@RequestParam String state, @RequestParam String city) {
		logger.debug("IN get customerByState with state as " + state + " and city=" + city);
		return bankService.getCustomerByStateCity(state, city);
	}
	@GetMapping("/customerByZipcodeLastname")

	public List<Customer> getCustomerIdsbyZipcodeLastname(@RequestParam String zipcode, @RequestParam String lastname) {
		logger.debug("IN get getCustomerIdsbyZipcodeLastname with zipcode as " + zipcode + " and lastname=" + lastname);
		return bankService.getCustomerIdsbyZipcodeLastname(zipcode, lastname);
	}

	@GetMapping("/generateData")
	@ResponseBody
	public String generateData (@RequestParam Integer noOfCustomers, @RequestParam Integer noOfTransactions, @RequestParam Integer noOfDays,
				   @RequestParam Integer noOfThreads) {

		bankService.generateData(noOfCustomers, noOfTransactions, noOfDays, noOfThreads);

		return "Done";
	}

	@GetMapping("/customerByFullNamePhone")

	public List<Customer> getCustomerByFullNamePhone(@RequestParam String fullName, @RequestParam String phone) {
		logger.debug("IN get getCustomerByFullNamePhone with fullName as " + fullName + " and phone=" + phone);
		return bankService.getCustomerByFullNamePhone(fullName, phone);
	}

	@GetMapping("/customerByEmail")

	public List<Customer> customerByEmail(@RequestParam String email) {
		logger.debug("In get customerByEmail with email as " + email);
		return bankService.getCustomerByEmail(email);
	}

	@GetMapping("/merchantTransactions")

	public List<Transaction> getMerchantTransactions(@RequestParam String merchant, @RequestParam String account,
											  @RequestParam String from, @RequestParam String to) throws ParseException {
		logger.debug("In getMerchantTransactions merchant=" + merchant + " account=" + account + " to=" + to + " from=" + from);
		return bankService.getMerchantTransactions(merchant, account, to, from);
	}

	@GetMapping("/creditCardTransactions")

	public List<Transaction> getCreditCardTransactions(@RequestParam String creditCard, @RequestParam String account,
													 @RequestParam String from, @RequestParam String to) throws ParseException {
		logger.debug("getCreditCardTransactions creditCard=" + creditCard + " account=" + account + " to=" + to + " from=" + from);
		return bankService.getCreditCardTransactions(creditCard, account, to, from);
	}

	@PostMapping("/posttag")
	public ResponseEntity<Object> createTag(@RequestBody uiTag tagdata) throws ParseException {
		bankService.addTag(tagdata.accountNo, tagdata.trandate, tagdata.transactionID, tagdata.tag,"+");
		return new ResponseEntity<>("Tag is created successfully", HttpStatus.CREATED);
	}

	
}
