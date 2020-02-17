package com.datastax.banking.dao;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import com.datastax.banking.dao.BankRedisDao;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.banking.data.BankGenerator;
import com.datastax.banking.model.Account;
import com.datastax.banking.model.Customer;
import com.datastax.banking.model.Transaction;
import com.datastax.banking.model.Email;
import com.datastax.banking.model.Phone;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.ResultSetFuture;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.MappingManager;
import com.datastax.driver.mapping.Result;
import com.datastax.driver.mapping.Mapper.Option;

import static com.datastax.driver.mapping.Mapper.Option.saveNullFields;
import static com.datastax.driver.mapping.Mapper.Option.tracing;

/**
 * Inserts into 2 tables
 *
 */
public class BankDao {

	private static Logger logger = LoggerFactory.getLogger(BankDao.class);
	private Session session;

	private static String keyspaceName = "bank";

	private static String transactionTable = keyspaceName + ".transaction";
	private static String accountsTable = keyspaceName + ".account";
	private static String customerChangeTable = keyspaceName + ".cust_change";

	private static final String GET_CUSTOMER_ACCOUNTS = "select * from " + accountsTable + " where customer_id = ?";
	// private static final String GET_TRANSACTIONS_BY_MSGDESC = "select * from " + transactionTable +" where solr_query = ?";
	private static final String GET_TRANSACTIONS_BY_ID = "select * from " + transactionTable + " where account_no = ?";

	private static final String GET_TRANSACTIONS_BY_TIMES = "select * from " + transactionTable
			+ " where account_no = ? and tranPostDt >= ? and tranPostDt < ?";
	private static final String GET_TRANSACTIONS_SINCE = "select * from " + transactionTable
			+ " where account_no = ? and tranPostDt >= ?";
	private static final String GET_ALL_ACCOUNT_CUSTOMERS = "select * from " + accountsTable;
	private static final String ADD_TRANSACTION_TAG = "update " + transactionTable +
			" set tags = ? where account_no = ? and tranPostDt = ? and tranId = ?";
	private static final String ADD_CUSTOMER_CHANGE = "insert into " + customerChangeTable +
			" (account_no, customer_id, last_updated ) VALUES ( ? , ? , ?)";
	private SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
	
	private PreparedStatement getTransactionByAccountId;
	// private PreparedStatement getTransactionByMsgDesc;
	private PreparedStatement getTransactionBetweenTimes;
	private PreparedStatement getTransactionSinceTime;
	private PreparedStatement addTransactionTag;
	private PreparedStatement addCustomerChange;
	private PreparedStatement getCustomerAccounts;
	private PreparedStatement getAccountCustomers;
	private PreparedStatement getLatestTransactionByCCno;
	

	private AtomicLong count = new AtomicLong(0);
	private Mapper<Customer> customerMapper;
	private Mapper<Account> accountMapper;
	private Mapper<Transaction> transactionMapper;


	

	public BankDao(String[] contactPoints) {

		Cluster cluster = Cluster.builder().addContactPoints(contactPoints).withCredentials("cassandra","jph").build();

		this.session = cluster.connect();

		this.getTransactionByAccountId = session.prepare(GET_TRANSACTIONS_BY_ID);
	//	this.getTransactionByMsgDesc = session.prepare(GET_TRANSACTIONS_BY_MSGDESC);
		this.getTransactionBetweenTimes = session.prepare(GET_TRANSACTIONS_BY_TIMES);
		this.getTransactionSinceTime = session.prepare(GET_TRANSACTIONS_SINCE);
		this.getAccountCustomers = session.prepare(GET_ALL_ACCOUNT_CUSTOMERS);
		this.getCustomerAccounts = session.prepare(GET_CUSTOMER_ACCOUNTS);
		this.addTransactionTag = session.prepare(ADD_TRANSACTION_TAG);
		this.addCustomerChange = session.prepare(ADD_CUSTOMER_CHANGE);
		
		customerMapper = new MappingManager(this.session).mapper(Customer.class);
		customerMapper.setDefaultSaveOptions(saveNullFields(false));
		accountMapper = new MappingManager(this.session).mapper(Account.class);
		accountMapper.setDefaultSaveOptions(saveNullFields(false));
		transactionMapper = new MappingManager(this.session).mapper(Transaction.class);
		transactionMapper.setDefaultSaveOptions(saveNullFields(false));
	}
	
	public Map<String, Set<String>> getAccountCustomers(){
		ResultSet rs = this.session.execute(this.getAccountCustomers.bind());
		Map<String, Set<String>> accountCustomers = new HashMap<String, Set<String>>();
		Iterator<Row> iterator = rs.iterator();
		
		while (iterator.hasNext()){
			Row row = iterator.next();
		
			accountCustomers.put(row.getString("account_no"), row.getSet("customers", String.class));
			
			if (accountCustomers.size() % 10000==0){
				logger.info(accountCustomers.size() + " loaded.");
			}
		}
		return accountCustomers;
	}

	public void saveTransaction(Transaction transaction) {
		insertTransactionAsync(transaction);
	}

	public void insertTransactionsAsync(List<Transaction> transactions) {

		
		for (Transaction transaction : transactions) {
			checkBucketForTransaction(transaction);
			transactionMapper.save(transaction);

			long total = count.incrementAndGet();
			if (total % 10000 == 0) {
				logger.info("Total transactions processed : " + total);
			}
		}
	}
    public List<Transaction> getTransactionsForCCNoDateSolr(String ccNo,Set<String> tags, DateTime from, DateTime to) {
        String fromDate = from.toString("yyyy-MM-dd");
        String toDate = to.toString("yyyy-MM-dd");
        String restDate = "T00:00:00Z";
        String cql = "select * from bank.transaction where solr_query = "
                + "'cardnum:" + ccNo + " AND tranpostdt:[" + fromDate + restDate + " TO " + toDate + restDate + "]' limit  100";
        logger.debug(cql);

        ResultSet resultSet = this.session.execute(cql);

        return processResultSet(resultSet, tags);
    }
	public void insertTransactionAsync(Transaction transaction) {

		checkBucketForTransaction(transaction);
		
		transactionMapper.save(transaction);
		
		long total = count.incrementAndGet();

		if (total % 10000 == 0) {
			logger.info("Total transactions processed : " + total);
		}
	}

	private void checkBucketForTransaction(Transaction transaction) {
		if (BankGenerator.whiteList.contains(transaction.getAccountNo())){
			transaction.setBucket(formatter.format(new DateTime(transaction.getTransactionTime()).toDate()));
		}		
	}

	public List<Transaction> getTransactions(String accountId) {

		ResultSetFuture rs = this.session.executeAsync(this.getTransactionByAccountId.bind(accountId));
		
		return this.processResultSet(rs.getUninterruptibly(), null);
	}
	public List<Transaction> getTransactionsCTGDESC(String mrchntctgdesc) {

		//  String solrquery = "merchantctgydesc:" + mrchntctgdesc;
		String cql = "select * from bank.transaction where solr_query = 'merchantctgydesc:" + mrchntctgdesc + "'";

		ResultSet resultSet = this.session.execute(cql);

		return processResultSet(resultSet,null);
	}

	public void insertAccount(Account account) {
		accountMapper.saveAsync(account);
	}

	public void addTagPreparedNoWork(String accountNo, Timestamp trandate, String transactionID, String tag) {
		String tagSet = " tags + {'" + tag + "'} ";
		logger.info("writing addTag update statement with tags set to "+ tagSet);
		ResultSetFuture rs = this.session.executeAsync(this.addTransactionTag.bind(tagSet,accountNo,trandate,transactionID));

		//  set tags = ? where account_no = ? and tranPostDt = ? and tranId = ?";
	}
        public void addCustChange(String accountNo,String custid, String last_update) {
		logger.info("writing addCustChange update statement");
		ResultSetFuture rs = this.session.executeAsync(this.addCustomerChange.bind(accountNo,custid,last_update));
	}
	public void addTag(String accountNo, String trandate, String transactionID, String tag, String operation) {
		String cql = "update " + transactionTable + " set tags = tags " + operation + "{'" + tag
				+ "'} where account_no = '" + accountNo + "' and tranPostDt = '" + trandate
		+ "'	 and tranId = '" + transactionID + "'";
		logger.info("writing addTag update statement with cql =" + cql);
		ResultSet rs = this.session.execute(cql);

		//  set tags = ? where account_no = ? and tranPostDt = ? and tranId = ?";
	}
	public void insertCustomer(Customer customer) {		
		customerMapper.saveAsync(customer);
		
		long total = count.incrementAndGet();

		if (total % 10000 == 0) {
			logger.info("Total customers processed : " + total);
		}
	}
	
	public Customer getCustomer(String customerId){
		return customerMapper.get(customerId);
	}

	public List<Customer> getCustomerListFromIDs(List<String> custIDs) {
		List<Customer> customerList = new ArrayList<Customer>();
		for (String custid: custIDs) {
			customerList.add(getCustomer(custid));
		}
		return customerList;
	};

	public List<Customer> getCustomerByPhone(String phoneString) {

		String cql = "select * from bank.customer where solr_query = '{!tuple}phone_numbers.phone_number:" + phoneString + "'";

		ResultSet resultSet = this.session.execute(cql);

		return processCustResultSet(resultSet);
	}

    public List<Customer> getCustomerByFullNamePhone(String Fullname, String phoneString) {

        String cql = "select * from bank.customer where solr_query = " +
                "'full_name:" + Fullname + " AND {!tuple}phone_numbers.phone_number:" + phoneString + "'";

        ResultSet resultSet = this.session.execute(cql);

        return processCustResultSet(resultSet);
    }

    public List<Customer> getCustomerByEmail(String emailString) {

        String cql = "select * from bank.customer where solr_query = '{!tuple}email_address.email_address:" + emailString + "'";

        ResultSet resultSet = this.session.execute(cql);

        return processCustResultSet(resultSet);
    }

	private Customer	rowToCustomer(Row row) {
		Customer c = new Customer();

		c.setCustomerId(row.getString("customer_id"));
		c.setAddress_line1(row.getString("address_line1"));
		c.setAddress_line2(row.getString("address_line2"));
		c.setAddress_type(row.getString("address_type"));
		c.setbill_pay_enrolled(row.getString("bill_pay_enrolled"));
		c.setCity(row.getString("city"));
		c.setCountry_code(row.getString("country_code"));
		c.setcreated_by(row.getString("created_by"));
		c.setcreated_datetime(row.getTimestamp("created_datetime"));
		c.setcustomer_nbr(row.getString("customer_nbr"));
		c.setcustomer_origin_system(row.getString("customer_origin_system"));
		c.setcustomer_status(row.getString("customer_status"));
		c.setcustomer_type(row.getString("customer_type"));
		c.setdate_of_birth(row.getString("date_of_birth"));
		c.setEmail_address(row.getList("email_address",Email.class));
		c.setFirstName(row.getString("first_name"));
		c.setfull_name(row.getString("full_name"));
		c.setgender(row.getString("gender"));
		c.setgovernment_id(row.getString("government_id"));
		c.setgovernment_id_type(row.getString("government_id_type"));
		c.setLast_name(row.getString("last_name"));
		c.setlast_updated(row.getTimestamp("last_updated"));
		c.setlast_updated_by(row.getString("last_updated_by"));
		c.setmiddle_name(row.getString("middle_name"));
		c.setPhone_numbers(row.getList("phone_numbers",Phone.class));
		c.setprefix(row.getString("prefix"));
		c.setquery_helper_column(row.getString("query_helper_column"));
		c.setstate_abbreviation(row.getString("state_abbreviation"));
		c.setzipcode(row.getString("zipcode"));
		c.setzipcode4(row.getString("zipcode4"));
		c.setCustaccounts(row.getSet("custaccounts",String.class));
		return c;

	}
	private Transaction rowToTransaction(Row row) {

		Transaction t = new Transaction();


		t.setAccountNo(row.getString("account_no"));
		t.setMerchant(row.getString("merchantname"));
		t.setTransactionId(row.getString("tranid"));
		t.setTransactionTime(row.getTimestamp("tranPostDt"));
		t.setTags(row.getSet("tags", String.class));
		t.setcardNum(row.getString("cardNum"));
		t.setaccountType(row.getString("account_type"));
		t.setamountType(row.getString("amount_type"));
		t.setAddress_type(row.getString("address_type"));
		t.setAmount(row.getDouble("amount"));
		t.setFirst_name(row.getString("first_name"));
		t.setFull_name(row.getString("full_name"));
		t.setLast_name(row.getString("last_name"));
		t.setmiddle_name(row.getString("middle_name"));
		t.setCity(row.getString("city"));
		t.setCountry_code(row.getString("country_code"));
		t.setCountry_name(row.getString("country_name"));
		t.setAddress_line1(row.getString("address_line1"));
		t.setAddress_line2(row.getString("address_line2"));
		t.setstate_abbreviation(row.getString("state_abbreviation"));
		t.setzipcode(row.getString("zipcode"));
		t.setzipcode4(row.getString("zipcode4"));
		t.setmerchantCtgyDesc(row.getString("merchantCtgyDesc"));
		t.setmerchantCtygCd(row.getString("merchantCtygCd"));
		t.setorigTranAmt(row.getString("origTranAmt"));
		t.setreferenceKeyType(row.getString("referenceKeyType"));
		t.setreferenceKeyValue(row.getString("referenceKeyValue"));
		t.settranAmt(row.getDouble("tranAmt"));
		t.settranCd(row.getString("tranCd"));
		t.settranDescription(row.getString("tranDescription"));
		t.settranExpDt(row.getString("tranExpDt"));
		t.settranInitDt(row.getTimestamp("tranInitDt"));
		t.settranStat(row.getString("tranStat"));
		t.settranType(row.getString("tranType"));
		t.settransRsnCd(row.getString("transRsnCd"));
		t.settransRsnDesc(row.getString("transRsnDesc"));
		t.settransRsnType(row.getString("transRsnType"));
		t.settransRespCd(row.getString("transRespCd"));
		t.settransRsnDesc(row.getString("transRespDesc"));
		t.settransRespType(row.getString("transRespType"));
		t.setBucket(row.getString("bucket"));
		t.setCustomers(row.getSet("customers", String.class));
		t.setLocation(row.getString("location"));

		return t;
	}

	public List<Transaction> getLatestTransactionsForCCNoTagsAndDate(String acountNo, Set<String> tags, DateTime from,
			DateTime to) {
		ResultSet resultSet = this.session.execute(getLatestTransactionByCCno.bind(acountNo, from.toDate(), to.toDate()));
		return processResultSet(resultSet, tags);
	}

	public List<Transaction> getTransactionsForCCNoTagsAndDate(String acountNo, Set<String> tags, DateTime from, DateTime to) {
		ResultSet resultSet = this.session.execute(getTransactionBetweenTimes.bind(acountNo, from.toDate(), to.toDate()));

		return processResultSet(resultSet, tags);
	}

	public List<Transaction> getTransactionsSinceTime(String acountNo, DateTime from) {
		ResultSet resultSet = this.session.execute(getTransactionSinceTime.bind(acountNo, from.toDate()));

		return processResultSet(resultSet, null);
	}

	private List<Transaction> processResultSet(ResultSet resultSet, Set<String> tags) {
		List<Row> rows = resultSet.all();
		List<Transaction> transactions = new ArrayList<Transaction>();

		for (Row row : rows) {

			Transaction transaction = rowToTransaction(row);

			if (tags != null && tags.size() != 0) {
				Iterator<String> iter = tags.iterator();

				// Check to see if any of the search tags are in the tags of the
				// transaction.
				while (iter.hasNext()) {
					String tag = iter.next();

					if (transaction.getTags().contains(tag)) {
						transactions.add(transaction);
						break;
					}
				}
			} else {
				transactions.add(transaction);
			}
		}
		return transactions;
	}

	private List<Customer> processCustResultSet(ResultSet resultSet) {
		List<Row> rows = resultSet.all();
		List<Customer> customers = new ArrayList<Customer>();

		for (Row row : rows) {

			Customer customer = rowToCustomer(row);
			customers.add(customer);
		}
		return customers;
	}
	public List<Account> getCustomerAccounts(String customerId) {
		ResultSet results = session.execute(getCustomerAccounts.bind(customerId));
		
		Result<Account> accounts = accountMapper.map(results);
		
		return accounts.all();
	}
}
