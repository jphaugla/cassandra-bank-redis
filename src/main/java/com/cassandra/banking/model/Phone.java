package com.cassandra.banking.model;

import com.datastax.driver.mapping.annotations.UDT;

@UDT(name = "phone", keyspace = "bank")
public class Phone {
    private String phone_type;
    private String phone_number;
    public String getPhone_type() {
        return phone_type;
    }
    public void setPhone_type(String phone_type) {
        this.phone_type = phone_type;
    }
    public String getPhone_number() {
        return phone_number;
    }
    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }
}
