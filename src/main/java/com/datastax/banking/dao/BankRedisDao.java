package com.datastax.banking.dao;
import com.sun.istack.Pool;
import io.redisearch.client.AddOptions;
import io.redisearch.client.Client;
import io.redisearch.Document;
import com.datastax.banking.model.Customer;
import org.apache.commons.lang3.StringUtils;
import io.redisearch.Schema;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.exceptions.JedisConnectionException;
import redis.clients.jedis.exceptions.JedisDataException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BankRedisDao  {
    private String hostname;
    private Integer portNo;
    private Client custClient;

    public Client getClient(String index_name) {
        return custClient;
    }
    public Client createClient(String index_name) {
        custClient = new io.redisearch.client.Client(index_name, hostname, portNo);
        return custClient;
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

    public boolean createCustomerSchema() {
        Schema schema = new Schema().addTextField("city", 1.0)
                .addTextField("state_abbreviation", 1.0)
                .addTextField("email_address", 1.0)
                .addTextField("last_name", 1.0)
                .addTextField("first_name", 1.0)
                .addTextField("zipcode", 1.0);
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
        Map<String, Object> fields = new HashMap<>();
        fields.put("city", customer.getCity());
        fields.put("state_abbreviation", customer.getstate_abbreviation());
        fields.put("email_address", customer.getEmail_address());
        fields.put("last_name", customer.getLast_name());
        fields.put("first_name", customer.getFirstName());
        fields.put("zipcode", customer.getzipcode());
        custClient.addDocument(new Document(customer.getCustomerId(), fields), new AddOptions());
        Map<String, Object> info = custClient.getInfo();
        return Integer.parseInt((String) info.get("num_docs"));
    }

}
