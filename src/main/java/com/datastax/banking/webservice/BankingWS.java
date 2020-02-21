package com.datastax.banking.webservice;

import com.datastax.banking.model.Account;
import com.datastax.banking.model.Customer;
import com.datastax.banking.model.Transaction;
import com.datastax.banking.service.BankService;

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

	@GetMapping("/customerByState")

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
	public ResponseEntity<Object> createProduct(@RequestBody uiTag tagdata) throws ParseException {
		bankService.addTag(tagdata.accountNo, tagdata.trandate, tagdata.transactionID, tagdata.tag,"+");
		return new ResponseEntity<>("Tag is created successfully", HttpStatus.CREATED);
	}

	/*


	@POST
	@Path("posttag")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.TEXT_HTML)
	public String postTagMethod(@FormParam("accountNo") String accountNo, @FormParam("trandate") String trandate,
								@FormParam("transactionID") String transactionID,
								@FormParam("tag") String tag) {
		String msg = "Tag to " + tag + " where accountNo=" + accountNo + " trandate=" + trandate +
						" transactionid=" + transactionID;
		bankService.addTag(accountNo,trandate,transactionID,tag,"+");
		return "<h2>Updated " + msg + "</h2>";
	}
	@POST
	@Path("posttagRemove")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.TEXT_HTML)
	public String postTagRemoveMethod(@FormParam("accountNo") String accountNo, @FormParam("trandate") String trandate,
								@FormParam("transactionID") String transactionID,
								@FormParam("tag") String tag) {
		String msg = "Remove Tag " + tag + " where accountNo=" + accountNo + " trandate=" + trandate +
				" transactionid=" + transactionID;
		bankService.addTag(accountNo,trandate,transactionID,tag,"-");
		return "<h2>Updated " + msg + "</h2>";
	}

	@GET
	@Path("/get/getcctransactionsTag/{creditcardno}/{from}/{to}/{tag}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getCCTransactions(@PathParam("creditcardno") String ccNo, @PathParam("from") String fromDate,
									  @PathParam("to") String toDate, @PathParam("tag") String tagstring) {
		DateTime from = DateTime.now();
		DateTime to = DateTime.now();
		try {
			from = new DateTime(inputDateFormat.parse(fromDate));
			to = new DateTime(inputDateFormat.parse(toDate));
		} catch (ParseException e) {
			String error = "Caught exception parsing dates " + fromDate + "-" + toDate;

			logger.error(error);
			return Response.status(Status.BAD_REQUEST).entity(error).build();
		}
		Set<String> tags = new HashSet<String>();
		tags.add(tagstring);

		List<Transaction> result = bankService.getTransactionsForCCNoDateSolr(ccNo,tags, from, to);
		logger.info("Returned response");
		return Response.status(Status.OK).entity(result).build();
	}

	@GET
	@Path("/get/accounts/{customerid}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAccounts(@PathParam("customerid") String customerId) {
		
		List<Account> accounts = bankService.getAccounts(customerId);
		
		return Response.status(Status.OK).entity(accounts).build();
	}
	
	@GET
	@Path("/get/transactions/{accountid}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getTransactions(@PathParam("accountid") String accountId) {
		
		List<Transaction> transactions = bankService.getTransactions(accountId);
		
		return Response.status(Status.OK).entity(transactions).build();
	}


 */

	
}
