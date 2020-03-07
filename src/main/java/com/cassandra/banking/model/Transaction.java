package com.cassandra.banking.model;

import java.util.Date;
import java.util.Set;

import com.datastax.driver.mapping.annotations.ClusteringColumn;
import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;

@Table(keyspace = "bank", name = "transaction")
public class Transaction {

	@PartitionKey
	@Column(name = "account_no")
	private String accountNo;

	@ClusteringColumn(0)
	@Column(name = "tranPostDt")
	private Date transactionTime;
	@ClusteringColumn(1)
	@Column(name = "tranId")
	private Integer transactionId;
	@Column(name = "account_type")
	private String accountType;
	@Column(name = "amount_type")
	private String amountType;

	private Double	amount;
	private String	cardNum;
	private String	merchantCtygCd;
	private String	merchantCtgyDesc;
	@Column(name = "merchantname")
	private String	merchant;
	private String	origTranAmt;
	private String	referenceKeyType;
	private String	referenceKeyValue;
	private double	tranAmt;
	private String	tranCd ;
	private String	tranDescription;
	private String	tranExpDt;
	private Date	tranInitDt;
	private String	tranStat;
	private String	tranType;
	private String transRsnCd;
	private String transRsnDesc;
	private String transRsnType;
	private String transRespCd;
	private String transRespDesc;
	private String transRespType;

	private String bucket = "1";
	private String location;
	private Set<String> customers;
	private Set<String> tags;

	public String getAccountNo() {
		return accountNo;
	}

	public void setAccountNo(String accountNo) {
		this.accountNo = accountNo;
	}

	public String getaccountType() {
		return accountType;
	}
	public void setaccountType(String accountType) {
		this.accountType = accountType;
	}

	public String getamountType() {
		return amountType;
	}
	public void setamountType(String amountType) {
		this.amountType = amountType;
	}
	public String getcardNum() {
		return cardNum;
	}
	public void setcardNum(String cardNum) {
		this.cardNum = cardNum;
	}
	public Date getTransactionTime() {
		return transactionTime;
	}

	public void setTransactionTime(Date transactionTime) {
		this.transactionTime = transactionTime;
	}

	public Integer getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(Integer transactionId) {
		this.transactionId = transactionId;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public Set<String> getCustomers() {
		return customers;
	}

	public void setCustomers(Set<String> customers) {
		this.customers = customers;
	}

	public Set<String> getTags() {
		return tags;
	}

	public void setTags(Set<String> tags) {
		this.tags = tags;
	}

	public String getBucket() {
		return bucket;
	}

	public void setBucket(String bucket) {
		this.bucket = bucket;
	}

	public enum Status {
		CHECK, APPROVED, DECLINED, CLIENT_APPROVED, CLIENT_DECLINED, CLIENT_APPROVAL, TIMEOUT
	}
	
	public String getmerchantCtygCd() {
		return this.merchantCtygCd;
	}
	public void setmerchantCtygCd(String merchantCtygCd) {
		this.merchantCtygCd = merchantCtygCd;
	}
	public String getmerchantCtgyDesc() {
		return this.merchantCtgyDesc;
	}
	public void setmerchantCtgyDesc(String merchantCtgyDesc) {
		this.merchantCtgyDesc = merchantCtgyDesc;
	}
	public String getMerchant() {
		return this.merchant;
	}
	public void setMerchant(String merchant) {
		this.merchant = merchant;
	}
	public String getorigTranAmt() {
		return this.origTranAmt;
	}
	public void setorigTranAmt(String origTranAmt) {
		this.origTranAmt = origTranAmt;
	}
	public String getreferenceKeyType() {
		return this.referenceKeyType;
	}
	public void setreferenceKeyType(String referenceKeyType) {
		this.referenceKeyType = referenceKeyType;
	}
	public String getreferenceKeyValue() {
		return this.referenceKeyValue;
	}
	public void setreferenceKeyValue(String referenceKeyValue) {
		this.referenceKeyValue = referenceKeyValue;
	}
	public Double gettranAmt() {
		return this.tranAmt;
	}
	public void settranAmt(Double tranAmt) {
		this.tranAmt = tranAmt;
	}
	public String gettranCd() {
		return this.tranCd;
	}
	public void settranCd(String tranCd) {
		this.tranCd = tranCd;
	}
	public String gettranDescription() {
		return this.tranDescription;
	}
	public void settranDescription(String tranDescription) {
		this.tranDescription = tranDescription;
	}
	public String gettranExpDt() {
		return this.tranExpDt;
	}
	public void settranExpDt(String tranExpDt) {
		this.tranExpDt = tranExpDt;
	}
	public Date gettranInitDt() {
		return this.tranInitDt;
	}
	public void settranInitDt(Date tranInitDt) {
		this.tranInitDt = tranInitDt;
	}
	public String gettranStat() {
		return this.tranStat;
	}
	public void settranStat(String tranStat) {
		this.tranStat = tranStat;
	}
	public String gettranType() {
		return this.tranType;
	}
	public void settranType(String tranType) {
		this.tranType = tranType;
	}
	public String gettransRsnCd() {
		return this.transRsnCd;
	}
	public void settransRsnCd(String transRsnCd) {
		this.transRsnCd = transRsnCd;
	}
	public String gettransRsnDesc() {
		return this.transRsnDesc;
	}
	public void settransRsnDesc(String transRsnDesc) {
		this.transRsnDesc = transRsnDesc;
	}
	public String gettransRsnType() {
		return this.transRsnType;
	}
	public void settransRsnType(String transRsnType) {
		this.transRsnType = transRsnType;
	}
	public String gettransRespCd() {
		return this.transRespCd;
	}
	public void settransRespCd(String transRespCd) {
		this.transRespCd = transRespCd;
	}
	public String gettransRespDesc() {
		return this.transRespDesc;
	}
	public void settransRespDesc(String transRespDesc) {
		this.transRespDesc = transRespDesc;
	}
	public String gettransRespType() {
		return this.transRespType;
	}
	public void settransRespType(String transRespType) {
		this.transRespType = transRespType;
	}
}
