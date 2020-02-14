package com.datastax.banking.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;


import com.datastax.driver.mapping.annotations.*;




@Table(keyspace = "bank", name = "customer")
public class Customer {
	@PartitionKey
	@Column(name="customer_id")
	private String customerId;
	private String address_line1;
	private String address_line2;
	private String address_type;
	private String bill_pay_enrolled;
	private String city;
	private String country_code;
	private String created_by;
	private Date created_datetime;
	private String customer_nbr;
	private String customer_origin_system;
	private String customer_status;
	private String customer_type;
	private String date_of_birth;
	@Frozen
	private List<Email> email_address;
	@Column(name="first_name")
	private String firstName;
	private String full_name;
	private String gender;
	private String government_id;
	private String government_id_type;
	private String last_name;
	private Date last_updated;
	private String last_updated_by;
	private String middle_name;
	@Frozen
    private List<Phone> phone_numbers;
	private String prefix;
	private String query_helper_column;
	private String state_abbreviation;
	private String zipcode;
	private String zipcode4;
	private Set<String> custaccounts;

	public String getCustomerId() {
		return customerId;
	}
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}
	public String getAddress_line1() {
		return address_line1;
	}
	public void setAddress_line1(String addressline1) {
		this.address_line1 = addressline1;
	}
	public String getAddress_line2() {
		return address_line2;
	}
	public void setAddress_line2(String addressline2) {
		this.address_line2 = addressline2;
	}
	public String getAddress_type() {
		return address_type;
	}
	public void setAddress_type(String addresstype) {
		this.address_type = addresstype;
	}
	public String getbill_pay_enrolled() {
		return bill_pay_enrolled;
	}
	public void setbill_pay_enrolled(String billPayEnrolled) {
		this.bill_pay_enrolled = billPayEnrolled;
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
	public String getFirstName() {
		return firstName;
	}
	public String getcreated_by() {
		return created_by;
	}
	public void setcreated_by(String createdBy) {
		this.created_by = createdBy;
	}
	public Date getcreated_datetime() {
		return created_datetime;
	}
	public void setcreated_datetime(Date createdDatetime) {
		this.created_datetime = createdDatetime;
	}
	public void setFirstName(String first) {
		this.firstName = first;
	}
	public String getLast_name() {
		return last_name;
	}
	public void setLast_name(String last) {
		this.last_name = last;
	}
	public List<Email> getEmail_address() {
		return email_address;
	}
	public void initializeEmail() {
		this.email_address = new ArrayList<Email>();
	}
	public void initializePhone() {
		this.phone_numbers = new ArrayList<Phone>();
	}
	public void setEmail_address(List<Email> email_list) {
		this.email_address = email_list;
	}
	public List<Phone> getPhone_numbers() {
		return phone_numbers;
	}
	public void setPhone_numbers(List<Phone> phone_numbers) {
		this.phone_numbers = phone_numbers;
	}
	public void addPhone (String phone_number,String phone_type) {
		Phone phone = new Phone();
		phone.setPhone_number(phone_number);
		phone.setPhone_type(phone_type);
		this.phone_numbers.add(phone);
	}
	public void addEmail (String email_address,String email_type, String email_status) {
		Email email = new Email();
		email.setEmail_address(email_address);
		email.setEmail_type(email_type);
		email.setEmail_status(email_status);
		this.email_address.add(email);
	}
	public void addEmail (Email email) {

		this.email_address.add(email);
	}

	public String getcustomer_nbr() {
		return this.customer_nbr;
	}
	 public void setcustomer_nbr(String input_cust) {
		this.customer_nbr = input_cust;
	}
	public String getcustomer_origin_system() {
		return this.customer_origin_system;
	}
	public void setcustomer_origin_system(String originSystem) {
		this.customer_origin_system = originSystem;
	}
	public String getcustomer_status() {
		return this.customer_status;
	}
	public void setcustomer_status(String custStatus) {
		this.customer_status = custStatus;
	}
	public String getcustomer_type() {
		return this.customer_type;
	}
	public void setcustomer_type(String custType) {
		this.customer_type = custType;
	}
	public String getdate_of_birth() {
		return this.date_of_birth;
	}
	public void setdate_of_birth(String dob) {
		this.date_of_birth = dob;
	}
	public String getfull_name() {
		return this.full_name;
	}
	public void setfull_name(String fullName) {
		this.full_name = fullName;
	}
	public String getgender() {
		return this.gender;
	}
	public void setgender(String gender) {
		this.gender = gender;
	}
	public String getgovernment_id() {
		return this.government_id;
	}
	public void setgovernment_id(String government_id) {
		this.government_id = government_id;
	}
	public String getgovernment_id_type() {
		return this.government_id_type;
	}
	public void setgovernment_id_type(String government_id_type) {
		this.government_id_type = government_id_type;
	}
	public Date getlast_updated() {
		return this.last_updated;
	}
	public void setlast_updated(Date last_updated) {
		this.last_updated = last_updated;
	}
	public String getlast_updated_by() {
		return this.last_updated_by;
	}
	public void setlast_updated_by(String updatedBy) {
		this.last_updated_by = updatedBy;
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
	public String getquery_helper_column() {
		return this.query_helper_column;
	}
	public void setquery_helper_column(String query_helper_column) {
		this.query_helper_column = query_helper_column;
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
	public Set<String> getCustaccounts() {
		return this.custaccounts;
	}
	public void setCustaccounts(Set<String> accountList) {
		this.custaccounts = accountList;
	}
}