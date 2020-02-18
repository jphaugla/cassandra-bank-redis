package com.datastax.banking.webservice;

import com.datastax.banking.model.Account;
import com.datastax.banking.model.Customer;
import com.datastax.banking.model.Transaction;
import com.datastax.banking.service.BankService;

import com.datastax.demo.utils.KillableRunner;
import com.datastax.demo.utils.PropertyHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@RestController

public class BankingWS {
	private static final Logger logger = LoggerFactory.getLogger(BankingWS.class);


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

	public List<Customer> getCustomerByState(@RequestParam String state) {
		logger.debug("IN get customerByState with state as " + state);
		return bankService.getCustomerByState(state);
	}
	@RequestMapping("/generateData")
	public String generateData() {

		bankService.generateData();

		return "Done";
	}

	/*
	@GET
	@Path("/get/getcctransactions/{creditcardno}/{from}/{to}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getCCTransactions(@PathParam("creditcardno") String ccNo, @PathParam("from") String fromDate,
	@PathParam("to") String toDate) {
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

		List<Transaction> result = bankService.getTransactionsForCCNoDateSolr(ccNo,null, from, to);
		logger.info("Returned response");
		return Response.status(Status.OK).entity(result).build();
	}
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

	@POST
	@Path("custChange")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.TEXT_HTML)
	public String postTagMethod(@FormParam("accountNo") String accountNo, 
								@FormParam("customerId") String custID,
@FormParam("chgdate") String chgdate )
								 {
		String msg = "Customer change where custID= " + custID  + " accountNo=" + accountNo + " chgdate=" + chgdate ;

		bankService.addCustChange(accountNo,custID,chgdate);
		return "<h2>Updated " + msg + "</h2>";
	}
	@POST
	@Path("post")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.TEXT_HTML)
	public String postMethod(@FormParam("name") String name) {
		return "<h2>Hello, " + name + "</h2>";
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
	@Path("/get/customerByFullNamePhone/{fullName}/{phoneString}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getCustomerByFullNamePhone(@PathParam("fullName") String fullName,
											   @PathParam("phoneString") String phoneString) {

		List<Customer> customers = bankService.getCustomerByFullNamePhone(fullName,phoneString);

		return Response.status(Status.OK).entity(customers).build();
	}

	@GET
	@Path("/get/customerByEmail/{emailString}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getCustomerByEmail(@PathParam("emailString") String emailString) {

		List<Customer> customers = bankService.getCustomerByEmail(emailString);

		return Response.status(Status.OK).entity(customers).build();
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
	@GET
	@Path("/get/categorydescrip/{mrchntctgdesc}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getTransactionsCTGDESC(@PathParam("mrchntctgdesc") String mrchntctgdesc) {

		List<Transaction> transactions = bankService.getTransactionsCTGDESC(mrchntctgdesc);

		return Response.status(Status.OK).entity(transactions).build();
	}

 */

	
}
