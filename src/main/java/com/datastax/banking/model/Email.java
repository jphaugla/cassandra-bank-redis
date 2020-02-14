package com.datastax.banking.model;

import com.datastax.driver.mapping.annotations.UDT;

@UDT(name = "email", keyspace = "bank")
public class Email {
    private String email_type;
    private String email_address;
    private String email_status;
    public String getEmail_type() {
        return email_type;
    }
    public void setEmail_type(String email_type) {
        this.email_type = email_type;
    }
    public String getEmail_address() {
        return email_address;
    }
    public void setEmail_address(String email_address) {
        this.email_address = email_address;
    }
    public String getEmail_status() {
        return email_status;
    }
    public void setEmail_status(String email_status) {
        this.email_status = email_status;
    }
}