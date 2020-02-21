package com.datastax.banking.dao;
import com.datastax.banking.model.Transaction;
import com.datastax.banking.webservice.BankingWS;
import io.redisearch.Query;
import io.redisearch.client.AddOptions;
import io.redisearch.client.Client;
import io.redisearch.Document;
import io.redisearch.SearchResult;
import com.datastax.banking.model.Customer;
import org.apache.commons.lang3.StringUtils;
import io.redisearch.Schema;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    public Client createClient(String index_name) {
        Client returnClient = new io.redisearch.client.Client(index_name, hostname, portNo);
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
        if (portNo>0) {
            setHost("localhost",6379);
            createClient("transaction").dropIndex(true);
        }
        Schema schema = new Schema().addTagField("merchantctgydesc")
                .addTextField("merchantctygcd", 1.0)
                .addTextField("merchantname", 1.0)
                .addTextField("cardNum", 1.0)
                .addTextField("tags", 1.0)
                .addTextField("bucket", 1.0)
                .addTextField("account_no",1.0)
                .addSortableNumericField("unixTime")
                .addTextField("postDate",1.0);
        //   this is creating the index returning the index
        return createClient("transaction").createIndex(schema, io.redisearch.client.Client.IndexOptions.Default());
    }
    public int addTransactionDocument(Transaction transaction)
    {
        // logger.warn("entering addTransactionDocument" + transaction.getTransactionId());
        Date transactimeDate = transaction.getTransactionTime();
        long unixtime = transactimeDate.getTime();
        String displayDate = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(transactimeDate);
        Map<String, Object> fields = new HashMap<>();
        fields.put("merchantctygdesc", transaction.getmerchantCtgyDesc());
        fields.put("merchantctygcd", transaction.getmerchantCtygCd());
        fields.put("merchantname", transaction.getMerchant());
        fields.put("cardNum", transaction.getcardNum()   );
        fields.put("tags", transaction.getTags());
        fields.put("bucket", transaction.getBucket());
        fields.put("account_no", transaction.getAccountNo());
        fields.put("unixTime", unixtime);
        fields.put("postDate", displayDate);
        String docID = transaction.getAccountNo() + ":" + unixtime + ":" + transaction.getTransactionId().toString();
        transClient.addDocument(new Document(docID, fields), new AddOptions());
        // Map<String, Object> info = custClient.getInfo();
        return 1;
    }

    public boolean createCustomerSchema() {
        logger.warn("entering createCustomerSchema");
        if (portNo>0) {
            setHost("localhost",6379);
            createClient("customer").dropIndex(true);
        }
        Schema schema = new Schema().addTextField("city", 1.0)
                .addTextField("state_abbreviation", 1.0)
                .addTextField("email_address", 1.0)
                .addTextField("last_name", 1.0)
                .addTextField("first_name", 1.0)
                .addTextField("zipcode", 1.0)
                .addTextField("phone",1.0);
        //   this is creating the index returning the index
        return createClient("customer").createIndex(schema, io.redisearch.client.Client.IndexOptions.Default());
    }
    public boolean validateIndexSchema(String index_name) {
        List b = (List) getClient("customer").getInfo().get("fields");
        assert StringUtils.equals(new String((byte[]) ((List) b.get(0)).get(0)), "id");
        return StringUtils.equals((CharSequence) getClient(index_name).getInfo().get("index_name"), index_name);
    }
    public int addCustomerDocument(Customer customer)
    {
     //   logger.warn("entering addCustomerDocument");
        Map<String, Object> fields = new HashMap<>();
        fields.put("city", customer.getCity());
        fields.put("state_abbreviation", customer.getstate_abbreviation());
        fields.put("email_address", customer.retrieveStringOfEmails()   );
        fields.put("last_name", customer.getLast_name());
        fields.put("first_name", customer.getFirstName());
        fields.put("zipcode", customer.getzipcode());
        fields.put("phone", customer.retrieveStringOfPhones());
        fields.put("full_name", customer.getfull_name());
        custClient.addDocument(new Document(customer.getCustomerId(), fields), new AddOptions());
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
    public List<String> getMerchantTransactions(String merchant, String account, String to, String from) throws ParseException {
        String preamble="in bankredisdao.getTransactionMerchantCateg with ";
        Date toDate = new SimpleDateFormat("MM/dd/yyyy").parse(to);
        Date fromDate = new SimpleDateFormat("MM/dd/yyyy").parse(from);
        Long toUnix = toDate.getTime();
        Long fromUnix = fromDate.getTime();
        logger.warn(preamble + "merchantname=" + merchant + " account=" + account + " to=" + to + " from=" + from);
        String Q="@merchantname:" + merchant + " @account_no:" + account + " @unixTime:[" + fromUnix + " " + toUnix + "]";
        logger.warn("in bankredisdao.getTransactionMerchantCateg with Q=" + Q);
        return (returnTransactionsfromIDs(Q));
    }
    public List<String> returnTransactionsfromIDs(String QueryString) {
        Query q = new Query (QueryString);
        setHost("localhost",6379);
        SearchResult searchResult = createClient("transaction").search(q);
        List<String> transIdList = new ArrayList<String>();
        Long numDocs = searchResult.totalResults;
        logger.warn("in bankredisdao.returnTransactionsfromIDs after numdocs=" + numDocs.toString());
        List<Document> ldoc = searchResult.docs;
        for(Document document : ldoc) {
            transIdList.add(document.getId());
            logger.warn("adding to transaction list string=" + document.getId());
        }
        logger.warn("before return");
        return transIdList;
    }

}
