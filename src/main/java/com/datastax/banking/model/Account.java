package com.datastax.banking.model;

import java.util.Date;
import java.util.List;

import com.datastax.driver.mapping.annotations.ClusteringColumn;
import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;

@Table(keyspace = "bank", name = "account")
public class Account {
	
	@PartitionKey
	@Column(name="customer_id")
	private String customerId;

	@ClusteringColumn
	@Column(name="account_no")
	private String accountNo;
	
	@Column(name="account_type")
	private String accountType;

	private String account_origin_system;
	private String account_status;
	private String address_line1;
	private String address_line2;
	private String address_type;
	private String city;
	private String country_code;
	private String country_name;
	private String created_by;
	private Date created_datetime;
	@Column(name="customer_nbr")
	private String customerNbr;
	private String date_of_birth;
	private String first_name;
	private String full_name;
	private String government_id;
	private String government_id_type;
	private String last_name;
	private Date last_updated;
	private String last_updated_by;
	private String middle_name;
	private List<Phone> phone_numbers;
	private String prefix;
	private String state_abbreviation;
	private String zipcode;
	private String zipcode4;

	public String getCustomerId() {
		return customerId;
	}
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}
	public String getAccount_origin_system() {
		return account_origin_system;
	}
	public void setAccount_origin_system(String account_origin_system) {
		this.account_origin_system = account_origin_system;
	}
	public String getAccount_status() {
		return account_status;
	}
	public void setAccount_status(String account_status) {
		this.account_status = account_status;
	}
	public String getCreated_by() {
		return created_by;
	}
	public void setCreated_by(String created_by) {
		this.created_by = created_by;
	}
	public Date getCreated_datetime() {
		return created_datetime;
	}
	public void setCreated_datetime(Date created_datetime) {
		this.created_datetime = created_datetime;
	}
	public Date getlast_updated() {
		return this.last_updated;
	}
	public void setLast_updated(Date last_updated) {
		this.last_updated = last_updated;
	}
	public String getlast_updated_by() {
		return this.last_updated_by;
	}
	public void setlast_updated_by(String updatedBy) {
		this.last_updated_by = updatedBy;
	}
	public String getAccountType() {
		return accountType;
	}
	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}
	public String getAccountNo() {
		return accountNo;
	}
	public void setAccountNo(String accountNo) {
		this.accountNo = accountNo;
	}

	// customer columns
	public String getAddress_line1() {
		return address_line1;
	}
	public void setAddress_line1(String address_line1) {
		this.address_line1 = address_line1;
	}
	public String getAddress_line2() {
		return address_line2;
	}
	public void setAddress_line2(String address_line2) {
		this.address_line2 = address_line2;
	}
	public String getAddress_type() {
		return address_type;
	}
	public void setAddress_type(String address_type) {
		this.address_type = address_type;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getCountry_code() {
		return country_code;
	}
	public void setCountry_code(String country_code) {
		this.country_code = country_code;
	}
	public String getCountry_name() {
		return country_name;
	}
	public void setCountry_name(String country_name) {
		this.country_name = country_name;
	}
	public String getDate_of_birth() {
		return date_of_birth;
	}
	public void setDate_of_birth(String date_of_birth) {
		this.date_of_birth = date_of_birth;
	}
	public String getFull_name() {
		return full_name;
	}
	public void setFull_name(String full_name) {
		this.full_name = full_name;
	}
	public String getGovernment_id() {
		return government_id;
	}
	public void setGovernment_id(String government_id) {
		this.government_id = government_id;
	}
	public String getGovernment_id_type() {
		return government_id_type;
	}
	public void setGovernment_id_type(String government_id_type) {
		this.government_id_type = government_id_type;
	}
	public String getFirst_name() {
		return first_name;
	}
	public void setFirst_name(String first) {
		this.first_name = first;
	}
	public String getLast_name() {
		return last_name;
	}
	public void setLast_name(String last_name) {
		this.last_name = last_name;
	}
	public String getmiddle_name() {
		return this.middle_name;
	}
	public void setmiddle_name(String middleName) {
		this.middle_name = middleName;
	}
	public String getprefix() {
		return this.prefix;
	}
	public void setprefix(String prefix) {
		this.prefix = prefix;
	}
	public String getstate_abbreviation() {
		return this.state_abbreviation;
	}
	public void setstate_abbreviation(String state_abbreviation) {
		this.state_abbreviation = state_abbreviation;
	}
	public String getzipcode() {
		return this.zipcode;
	}
	public void setzipcode(String zipcode) {
		this.zipcode = zipcode;
	}
	public String getZipcode4() {
		return this.zipcode4;
	}
	public void setzipcode4(String zipcode4) {
		this.zipcode4 = zipcode4;
	}
	public String getCustomerNbr() { return customerNbr; }
	public void setCustomerNbr(String customerNbr) { this.customerNbr = customerNbr; }
	public void addPhone (String phone_number,String phone_type) {
		Phone phone = new Phone();
		phone.setPhone_number(phone_number);
		phone.setPhone_type(phone_type);
		this.phone_numbers.add(phone);
	}

	public List<Phone> getPhone_numbers() {
		return phone_numbers;
	}
	public void setPhone_numbers(List<Phone> phone_numbers) {
		this.phone_numbers = phone_numbers;
	}
	public void defineAllCustomerColumns(Customer customer) {
		this.customerNbr = customer.getcustomer_nbr();
		this.customerId = customer.getCustomerId();
		this.phone_numbers = customer.getPhone_numbers();
		this.first_name = customer.getFirstName();
		this.last_name = customer.getLast_name();
		this.account_origin_system = customer.getcustomer_origin_system();
		this.address_line1 = customer.getAddress_line1();
		this.address_line2 = customer.getAddress_line2();
		this.city = customer.getCity();
		this.full_name = customer.getfull_name();
		this.address_type = customer.getAddress_type();
		this.country_code = customer.getCountry_code();
		this.government_id = customer.getgovernment_id();
		this.government_id_type = customer.getgovernment_id_type();
		this.state_abbreviation = customer.getstate_abbreviation();
		this.zipcode = customer.getzipcode();
		this.zipcode4 = customer.getZipcode4();
		this.middle_name = customer.getmiddle_name();
		this.prefix = customer.getprefix();
		this.country_code = customer.getCountry_code();
		this.date_of_birth = customer.getdate_of_birth();
	}


}

