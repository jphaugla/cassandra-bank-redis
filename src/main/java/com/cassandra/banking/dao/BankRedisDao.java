package com.cassandra.banking.dao;
import com.cassandra.banking.model.Transaction;
import com.cassandra.banking.webservice.BankingWS;
import com.cassandra.banking.model.Customer;
import io.redisearch.Query;

import io.redisearch.client.Client;
import io.redisearch.Document;
import io.redisearch.SearchResult;
import io.redisearch.client.IndexDefinition;
import org.apache.commons.lang3.StringUtils;
import io.redisearch.Schema;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.*;
import redis.clients.jedis.exceptions.JedisConnectionException;
import redis.clients.jedis.exceptions.JedisDataException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class BankRedisDao  {
    private String hostname;
    private Integer portNo;
    private Client custClient;
    private Client transClient;
    private JedisPool jedisPool;

    private static final Logger logger = LoggerFactory.getLogger(BankingWS.class);

    public Client getClient(String index_name) {
        Client returnClient = null;
        if(index_name == "customer") {
            returnClient = custClient;
        }
        else if (index_name == "transaction") {
            returnClient = transClient;
        }
        return returnClient;
    }

    public JedisPool createJedisPool() {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(128);
        jedisPool = new JedisPool(poolConfig, hostname, portNo);
        return jedisPool;
    }
    public Client createClient(String index_name) {
        Client returnClient = new io.redisearch.client.Client(index_name, jedisPool);
        if(index_name == "customer") {
            custClient = returnClient;
        }
        else if (index_name == "transaction") {
            transClient = returnClient;
        }
        return returnClient;
    }

    public void setHost(String hostname_in, Integer portNo_in)   {
        hostname=hostname_in;
        portNo = portNo_in;
    }

    public boolean checkConnection(String index_name) {
        boolean flag = true;
        try {
            getClient(index_name).getInfo();
        } catch (JedisConnectionException je) {
            flag = false;
        } catch (JedisDataException jex) {
            // index not present or some other data exception :-(
        }
        return flag;
    }

    public boolean createTransactionSchema() {
        logger.warn("entering createTransactionSchema");
        Schema schema = new Schema().addTagField("merchantctgydesc")
                .addTextField("merchantctygcd", 1.0)
                .addTextField("merchantname", 1.0)
                .addTextField("cardNum", 1.0)
                .addTagField("tags")
                .addTextField("bucket", 1.0)
                .addTextField("account_no",1.0)
                .addTextField("transactionID", 1.0)
                .addSortableNumericField("unixTime")
                .addTextField("postDate",1.0);
        transClient = createClient("transaction");
        transClient.dropIndex(true);
        String prefix = "transaction:";
        io.redisearch.client.IndexDefinition def = new IndexDefinition().setPrefixes(prefix);
        return(transClient.createIndex(schema, Client.IndexOptions.defaultOptions().setDefinition(def)));
    }
    public int addTransactionDocument(Transaction transaction)
    {
        // logger.warn("entering addTransactionDocument" + transaction.getTransactionId());
        Date transactimeDate = transaction.getTransactionTime();
        long unixtime = transactimeDate.getTime();
        String displayDate = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(transactimeDate);
        Map<String, String> fields = new HashMap<>();
        fields.put("merchantctygdesc", transaction.getmerchantCtgyDesc());
        fields.put("merchantctygcd", transaction.getmerchantCtygCd());
        fields.put("merchantname", transaction.getMerchant());
        fields.put("cardNum", transaction.getcardNum()   );
        fields.put("tags", transaction.retrieveStringTags());
        fields.put("bucket", transaction.getBucket());
        fields.put("account_no", transaction.getAccountNo());
        fields.put("unixTime", String.valueOf(unixtime));
        fields.put("transactionID", transaction.getTransactionId().toString());
        fields.put("postDate", displayDate);
        String docID = "transaction:" + transaction.getAccountNo() + ":" + unixtime + ":" + transaction.getTransactionId().toString();
        // transClient.addDocument(new Document(docID, fields));
        try(Jedis conn = transClient.connection()) {
            conn.hmset(docID, fields);
        }

        return 1;
    }
    public int updateTransactionTag(String transaction, String newTag)
    {
        // logger.warn("entering addTransactionDocument" + transaction.getTransactionId());
       // Map<String, Object> fields = new HashMap<>();
        // fields.put("tags", newTag);
        // transClient.updateDocument(transaction, 1.0, fields);
        try(Jedis conn = transClient.connection()) {
            conn.hset(transaction, "tags", newTag);
        }

        return 1;
    }

    public Map<String, Object> indexInfo(String indexName)  throws redis.clients.jedis.exceptions.JedisDataException {
        return(createClient(indexName).getInfo());
    }

    public boolean createCustomerSchema() {
        logger.warn("entering createCustomerSchema");
        Schema schema = new Schema().addTextField("city", 1.0)
                .addTextField("state_abbreviation", 1.0)
                .addTextField("email_address", 1.0)
                .addTextField("last_name", 1.0)
                .addTextField("first_name", 1.0)
                .addTextField("zipcode", 1.0)
                .addTextField("phone",1.0);
        String prefix = "customer:";
        custClient = createClient("customer");
        custClient.dropIndex(true);
        io.redisearch.client.IndexDefinition def = new IndexDefinition().setPrefixes(prefix);
        //   this is creating the index returning the index
        return(custClient.createIndex(schema, Client.IndexOptions.defaultOptions().setDefinition(def)));
    }
    public boolean validateIndexSchema(String index_name) {
        List b = (List) getClient("customer").getInfo().get("fields");
        assert StringUtils.equals(new String((byte[]) ((List) b.get(0)).get(0)), "id");
        return StringUtils.equals((CharSequence) getClient(index_name).getInfo().get("index_name"), index_name);
    }
    public int addCustomerDocument(Customer customer)
    {
     //   logger.warn("entering addCustomerDocument");
        Map<String, String> fields = new HashMap<>();
        fields.put("city", customer.getCity());
        fields.put("state_abbreviation", customer.getstate_abbreviation());
        fields.put("email_address", customer.retrieveStringOfEmails()   );
        fields.put("last_name", customer.getLast_name());
        fields.put("first_name", customer.getFirstName());
        fields.put("zipcode", customer.getzipcode());
        fields.put("phone", customer.retrieveStringOfPhones());
        fields.put("full_name", customer.getfull_name());
        String customerID = "customer:" + customer.getCustomerId();
        // custClient.addDocument(new Document(customer.getCustomerId(), fields), new AddOptions());
        try(Jedis conn2 = custClient.connection()) {
            conn2.hmset(customerID, fields);
        }
       // Map<String, Object> info = custClient.getInfo();
        return 1;
    }

    public List<String> returnCustomerIDsfromQuery(String QueryString) {
        Query q = new Query (QueryString);
        setHost("localhost",6379);
        SearchResult searchResult = createClient("customer").search(q);
        List<String> custidList = new ArrayList<String>();
        Long numDocs = searchResult.totalResults;
        logger.warn("after numdocs=" + numDocs.toString());
        List<Document> ldoc = searchResult.docs;
        for(Document document : ldoc) {
            custidList.add(document.getId());
            logger.warn("adding to custid list string=" + document.getId());
        }
        logger.warn("before return");
        return custidList;
    }

    public List<String> getCustomerIdsbyPhone(String phoneString) {
        logger.warn("in bankredisdao.getCustomerIdsbyPhone with phone=" + phoneString);
        return (returnCustomerIDsfromQuery("@phone:" + phoneString));
    }

    public List<String> getCustomerIdsbyStateCity(String state, String city) {
        logger.warn("in bankredisdao.getCustomerIdsbyState with state=" + state + " and city=" + city);
        return (returnCustomerIDsfromQuery("@state_abbreviation:" + state +  " @city:" + city));
    }

    public List<String> getCustomerByFullNamePhone(String fullName, String phone) {
        logger.warn("in bankredisdao.getCustomerByFullNamePhone with fullName=" + fullName + " and phone=" + phone);
        return (returnCustomerIDsfromQuery("@full_name:" + fullName + " @phone:" + phone));
    }
    public List<String> getCustomerIdsbyEmail(String email) {
        logger.warn("in bankredisdao.getCustomerIdsbyEmail with email=" + email );
        return (returnCustomerIDsfromQuery("@email_address:" + email.replace("@", "\\@")));
    }
    public List<String> getCustomerIdsbyZipcodeLastname(String zipcode, String last_name) {
        logger.warn("in bankredisdao.getCustomerIdsbyZipcodeLastname with zipcode=" + zipcode + " last_name=" + last_name);
        return (returnCustomerIDsfromQuery("@zipcode:" + zipcode + " @last_name:" + last_name));
    }

    public String getDateToFromQueryString(String to, String from) throws ParseException {
        Date toDate = new SimpleDateFormat("MM/dd/yyyy").parse(to);
        Date fromDate = new SimpleDateFormat("MM/dd/yyyy").parse(from);
        Long toUnix = toDate.getTime();
        Long fromUnix = fromDate.getTime();
        return " @unixTime:[" + fromUnix + " " + toUnix + "]";
    }

    public String getDateFullDayQueryString(String stringDate) throws ParseException {
        Date inDate = new SimpleDateFormat("MM/dd/yyyy").parse(stringDate);
        Long inUnix = inDate.getTime();
        //  since the transaction ID is also in the query can take a larger reach around the date column
        Long startUnix = inUnix - 86400*1000;
        Long endUnix = inUnix + 86400*1000;
        return " @unixTime:[" + startUnix + " " + endUnix + "]";
    }

    public List<String> getMerchantTransactions(String merchant, String account, String to, String from) throws ParseException {
        String preamble="bankredisdao.getTransactionMerchantCateg with ";
        String tofromQuery = getDateToFromQueryString(to, from);
        logger.warn(preamble + "merchantname=" + merchant + " account=" + account + " to=" + to + " from=" + from);
        String Q="@merchantname:" + merchant + " @account_no:" + account + tofromQuery;
        logger.warn("in bankredisdao.getTransactionMerchantCateg with Q=" + Q);
        return (returnTransactionsfromIDs(Q));
    }

    public List<String> addTag(String accountNo, String trandate, String transactionID, String tag, String operation) throws ParseException {
        String preamble="bankredisdao.addTag with ";
        logger.warn(preamble + "accountNo=" + accountNo + " trandate=" + trandate + " tranID=" +
                        transactionID + " tag=" + tag +  " operation=" + operation);
        String tofromQuery = getDateFullDayQueryString(trandate);
        String Q="@account_no:" + accountNo + " @transactionID:" + transactionID + tofromQuery;
        List<String> transactionList = returnTransactionsfromIDs(Q);
        for(String transaction : transactionList) {
            updateTransactionTag(transaction, tag);
        }
        return (transactionList);
    }

    public List<String> getCreditCardTransactions(String creditCard, String account, String to, String from) throws ParseException {
        String preamble="bankredisdao.getCreditCardTransactions with ";
        String tofromQuery = getDateToFromQueryString(to, from);
        logger.warn(preamble + "creditCard=" + creditCard + " account=" + account + " to=" + to + " from=" + from);
        String Q="@cardNum:" + creditCard + " @account_no:" + account + tofromQuery;
        logger.warn("in bankredisdao.getCreditCardTransactions with Q=" + Q);
        return (returnTransactionsfromIDs(Q));
    }

    public List<String> returnTransactionsfromIDs(String QueryString) {
        logger.warn("in bankredisdao.returnTransactionsfromIDs QueryString=" + QueryString);

        Query q = new Query (QueryString);
        setHost("localhost",6379);
        SearchResult searchResult = createClient("transaction").search(q);
        List<String> transIdList = new ArrayList<String>();
        Long numDocs = searchResult.totalResults;
        logger.warn("in bankredisdao.returnTransactionsfromIDs after numdocs=" + numDocs.toString());
        List<Document> ldoc = searchResult.docs;
        String documentID;
        for(Document document : ldoc) {
            documentID = document.getId();
            transIdList.add(documentID);
            logger.warn("adding to transaction list string=" + documentID);
        }
        logger.warn("before return");
        return transIdList;
    }

}
