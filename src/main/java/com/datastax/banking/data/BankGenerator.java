package com.datastax.banking.data;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import org.joda.time.DateTime;
import com.datastax.banking.model.Account;
import com.datastax.banking.model.Customer;
import com.datastax.banking.model.Transaction;
import com.datastax.banking.service.BankService;

public class BankGenerator {

	// private static final Logger logger = LoggerFactory.getLogger(BankGenerator.class);
	private static final int BASE = 1000000;
	private static final int DAY_MILLIS = 1000 * 60 *60 * 24;
	private static AtomicInteger customerIdGenerator = new AtomicInteger(1);
	private static List<String> accountTypes = Arrays.asList("Current", "Joint Current", "Saving", "Mortgage",
            "E-Saving", "Deposit");
	private static List<String> accountIds = new ArrayList<String>();
	private static Map<String, List<Account>> accountsMap = new HashMap<String, List<Account>>();
	
	//We can change this from the Main
	public static DateTime date = new DateTime().minusDays(180).withTimeAtStartOfDay();
	public static Date currentDate = new Date();
	public static String  timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(currentDate);
	
	public static List<String> whiteList = new ArrayList<String>();

			

	public static String getRandomCustomerId(int noOfCustomers){
		return BASE + new Double(Math.random()*noOfCustomers).intValue() + "";
	}
	
	public static Customer createRandomCustomer(int noOfCustomers) {
		
		String customerId = BASE + customerIdGenerator.getAndIncrement() + "";

		Customer customer = new Customer();
		customer.setCustomerId(customerId);

		customer.setAddress_line1("Line1-" + customerId);
		customer.setcreated_by("Java Test");
        customer.setlast_updated_by("Java Test");
        customer.setcustomer_type("Retail");

		customer.setcreated_datetime(currentDate);
		customer.setlast_updated(currentDate);

		customer.setcustomer_nbr(customerId);
		customer.setcustomer_origin_system("RCIF");
		customer.setcustomer_status("A");
		customer.setCountry_code("00");
        customer.setgovernment_id_type("TIN");
        customer.setgovernment_id(customerId.substring(1));

		int lastDigit = Integer.parseInt(customerId.substring(6));
		if (lastDigit>7) {
			customer.setAddress_line2("Apt " + customerId);
			customer.setAddress_type("Apartment");
			customer.setbill_pay_enrolled("false");
		}
		else if (lastDigit==3){
			customer.setbill_pay_enrolled("false");
			customer.setAddress_type("Mobile");
		}
		else {
			customer.setAddress_type("Residence");
			customer.setbill_pay_enrolled("true");
		}
		customer.setCity(locations.get(lastDigit));
		customer.setstate_abbreviation(States.get(lastDigit));
		customer.setdate_of_birth(dob.get(lastDigit));
		String lastName = customerId.substring(2,7);
		String firstName = firstList.get(lastDigit);
		String middleName = middleList.get(lastDigit);
		customer.setgender(genderList.get(lastDigit));
		if (genderList.get(lastDigit)=="F"){
		    customer.setprefix("Ms");
        }
        else {
            customer.setprefix("Mr");
        }
		String zipChar = zipcodeList.get(lastDigit).toString();
		customer.setzipcode(zipChar);
		customer.setzipcode4(zipChar + "-1234");
		customer.setFirstName(firstName);
		customer.setLast_name(lastName);
		customer.setmiddle_name(middleName);
		customer.setfull_name(firstName + " " + middleName + " " + lastName);

		customer.initializeEmail();
		customer.addEmail(customerId + "@gmail.com","work","valid");
		customer.addEmail(customerId + "@gmail.com","Home","DND");
		customer.initializePhone();
		customer.addPhone(customerId + "w","work");
		customer.addPhone(customerId + "h","home");
		customer.addPhone(customerId + "c","cell");

		return customer;
	}
	
	public static List<Account> createRandomAccountsForCustomer(Customer customer, int noOfCustomers) {
		
		int noOfAccounts = Math.random() < .1 ? 4 : 3;
		List<Account> accounts = new ArrayList<Account>();

		
		for (int i = 0; i < noOfAccounts; i++){
			
			Account account = new Account();
			account.defineAllCustomerColumns(customer);
			String accountNumber = "Acct" + Integer.toString(i);
			account.setAccountNo(accountNumber);
			account.setAccountType(accountTypes.get(i));
			account.setCountry_name("USA");
			account.setAccount_status("Open");
            account.setCreated_by("Java Test");
            account.setlast_updated_by("Java Test");
            account.setCreated_datetime(currentDate);
            account.setLast_updated(currentDate);

			
			if (i == 3){
				//add Joint account
	//			customerIds.add(getRandomCustomerId(noOfCustomers));
			}
			accounts.add(account);

			
			//Keep a list of all Account Nos to create the transactions
			accountIds.add(account.getAccountNo());
		}
		
		return accounts;
	}
	public static Transaction createRandomTransaction(int noOfDays, int noOfCustomers, BankService bankService, Integer idx) {

		int noOfMillis = noOfDays * DAY_MILLIS;
		// create time by adding a random no of millis
		DateTime newDate = date.plusMillis(new Double(Math.random() * noOfMillis).intValue() + 1);

		return createRandomTransaction(newDate, noOfCustomers, bankService, idx);
	}
	public static Transaction createRandomTransaction(DateTime newDate, int noOfCustomers, BankService bankService, Integer idx) {

		//Random account	
		String customerId = getRandomCustomerId(noOfCustomers);
		//  grab the customer information for this customerID
		// Customer customer = bankService.getCustomer(customerId);


		List<Account> accounts;
		if (accountsMap.containsKey(customerId)){
			accounts = accountsMap.get(customerId);
		}else{
			accounts = bankService.getAccounts(customerId);
			accountsMap.put(customerId, accounts);
		}

		if (accounts.size() == 0){
			return null;
		}

		Account account = accounts.get(new Double(Math.random() * accounts.size()).intValue());
			
		int noOfItems = new Double(Math.ceil(Math.random() * 3)).intValue();
		String location = locations.get(new Double(Math.random() * locations.size()).intValue());

		int randomLocation = new Double(Math.random() * issuers.size()).intValue();
		String issuer = issuers.get(randomLocation);
		String note = notes.get(randomLocation);
		String tag = tagList.get(randomLocation);
		String merchantCtygCd = issuersCD.get(randomLocation);
        String merchantCtygDesc = issuersCDdesc.get(randomLocation);
		Set<String> tags = new HashSet<String>();
		tags.add(note);
		tags.add(tag);
		Date aNewDate = newDate.toDate();
        String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(aNewDate);

		Transaction transaction = new Transaction();
		createItemsAndAmount(noOfItems, transaction);
		transaction.setAccountNo(account.getAccountNo());
		transaction.setMerchant(issuer);
		transaction.setTransactionId(idx);
        transaction.setcardNum( UUID.randomUUID().toString());
		transaction.setTransactionTime(aNewDate);
		transaction.setLocation(location);
		transaction.setTags(tags);
		if(randomLocation<5) {
            transaction.setamountType("Debit");
        }
        else{
            transaction.setamountType("Credit");
        }
		
        transaction.setmerchantCtygCd(merchantCtygCd);
        transaction.setmerchantCtgyDesc(merchantCtygDesc);
        transaction.setMerchant(issuer);

        transaction.setreferenceKeyType("reftype");
        transaction.setreferenceKeyValue("thisRef");

        transaction.settranCd("tranCd1");
        transaction.settranDescription("this is the transaction description");
        transaction.settranExpDt(timeStamp);
        transaction.settranInitDt(aNewDate);
        transaction.settranStat("OK");
        transaction.settranType("TranTyp1");
        transaction.settransRsnCd("transRsnCd1");
        transaction.settransRsnDesc("transRsnDesc");
        transaction.settransRsnType("transRsnType");
        transaction.settransRespCd("transRespCd");
        transaction.settransRespDesc("transRespDesc");
        transaction.settransRespType("transRespType");
        return transaction;
	}
	
	/**
	 * Creates a random transaction with some skew for some accounts.
	 * @return
	 */
	
	private static void createItemsAndAmount(int noOfItems, Transaction transaction) {
		Map<String, Double> items = new HashMap<String, Double>();
		double totalAmount = 0;

		for (int i = 0; i < noOfItems; i++) {

			double amount = new Double(Math.random() * 100);
			items.put("item" + i, amount);

			totalAmount += amount;
		}
		transaction.setAmount(totalAmount);
        transaction.setorigTranAmt(Double.toString(totalAmount));
        transaction.settranAmt(totalAmount);
	}
	
	public static List<String> locations = Arrays.asList("Chicago", "Minneapolis", "St. Paul", "Plymouth", "Edina",
			"Duluth", "Bloomington", "Bloomington", "Rockford", "Champaign");
    public static List<Integer> zipcodeList = Arrays.asList(60601, 55401, 55101, 55441, 55435,
            55802, 61704, 55435, 61101, 16821);
	public static List<String> dob = Arrays.asList("08/19/1964", "07/14/1984", "01/20/2000", "06/10/1951", "11/22/1995",
			"12/13/1954", "08/12/1943", "11/29/1964", "02/01/1994", "07/12/1944");

	public static List<String> States = Arrays.asList("IL", "MN", "MN", "MN", "MN",
			"MN", "IL", "MN", "MN", "IL");

    public static List<String> genderList = Arrays.asList("M", "F", "F", "M", "F",
            "F", "M", "M", "M", "F");

    public static List<String> middleList = Arrays.asList("Paul", "Ann", "Mary", "Joseph", "Amy",
            "Elizabeth", "John", "Javier", "Golov", "Eliza");

    public static List<String> firstList = Arrays.asList("Jason", "Catherine", "Esmeralda", "Marcus", "Louisa",
            "Julia", "Miles", "Luis", "Igor", "Angela");

	public static List<String> issuers = Arrays.asList("Tesco", "Sainsbury", "Wal-Mart Stores",
            "Morrisons",
			"Marks & Spencer", "Walmart", "John Lewis", "Cub Foods", "Argos", "Co-op", "Currys", "PC World", "B&Q",
			"Somerfield", "Next", "Spar", "Amazon", "Costa", "Starbucks", "BestBuy", "Lowes", "BarnesNoble",
            "Carlson Wagonlit Travel",
			"Pizza Hut", "Local Pub");

    public static List<String> issuersCD = Arrays.asList("5411", "5411", "5310", "5499",
            "5310", "5912", "5311", "5411", "5961", "5300", "5732", "5964", "5719",
            "5411", "5651", "5411", "5310", "5691", "5814", "5732", "5211", "5942", "5962",
            "5814", "5813");

    public static List<String> issuersCDdesc = Arrays.asList("Grocery Stores", "Grocery Stores",
            "Discount Stores", "Misc Food Stores Convenience Stores and Specialty Markets",
            "Discount Stores", "Drug Stores and Pharmacies", "Department Stores", "Supermarkets", "Mail Order Houses",
            "Wholesale Clubs", "Electronic Sales",
            "Direct Marketing Catalog Merchant", "Miscellaneous Home Furnishing Specialty Stores",
            "Grocery Stores", "Family Clothing Stores", "Grocery Stores", "Discount Stores",
			"Mens and Womens Clothing Stores",
            "Fast Food Restaurants",
            "Electronic Sales",
            "Lumber and Building Materials Stores",
            "Book Stores", "Direct Marketing Travel Related Services",
            "Fast Food Restaurants", "Drinking Places, Bars, Taverns, Cocktail lounges, Nightclubs and Discos");

	public static List<String> notes = Arrays.asList("Shopping", "Shopping", "Shopping", "Shopping", "Shopping",
			"Pharmacy", "HouseHold", "Shopping", "Household", "Shopping", "Tech", "Tech", "Diy", "Shopping", "Clothes",
			"Shopping", "Amazon", "Coffee", "Coffee", "Tech", "Diy", "Travel", "Travel", "Eating out", "Eating out");

	public static List<String> tagList = Arrays.asList("Home", "Home", "Home", "Home", "Home", "Home", "Home", "Home",
			"Work", "Work", "Work", "Home", "Home", "Home", "Work", "Work", "Home", "Work", "Work", "Work", "Work",
			"Work", "Work", "Work", "Work", "Expenses", "Luxury", "Entertaining", "School");

}
