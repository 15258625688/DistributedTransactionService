package com.tcp.netty.entity;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;

public class Transaction {

    public Transaction(String parentTransactionId) {
	this(parentTransactionId, DistributedTransactionConstant.TRANSACTION_WAIT);
    }

    public Transaction(String parentTransactionId, int status) {
	this.transactionId = UUID.randomUUID().toString();
	this.parentTransactionId = parentTransactionId;
	this.transactionStatus = status;
	this.createTime = new Date();
	if (parentTransactionId == null)
	    this.type = DistributedTransactionConstant.CREATE_TRANSACTION;
	else
	    this.type = DistributedTransactionConstant.ADD_TRANSACTION;
    }

    public Transaction(int status) {
	this(null, status);
    }

    public Transaction() {
	this(null, DistributedTransactionConstant.TRANSACTION_WAIT);
    }

    private String parentTransactionId;
    private String transactionId;
    private Map<String, Transaction> childTransactions;
    private int transactionStatus;
    private Date createTime;
    private int type;

    public String getParentTransactionId() {
	return parentTransactionId;
    }

    public void setParentTransactionId(String parentTransactionId) {
	this.parentTransactionId = parentTransactionId;
    }

    public String getTransactionId() {
	return transactionId;
    }

    public void setTransactionId(String transactionId) {
	this.transactionId = transactionId;
    }

    public Map<String, Transaction> getChildTransactions() {
	return childTransactions;
    }

    public void setChildTransactions(Map<String, Transaction> childTransactions) {
	this.childTransactions = childTransactions;
    }

    public int getTransactionStatus() {
	return transactionStatus;
    }

    public void setTransactionStatus(int transactionStatus) {
	this.transactionStatus = transactionStatus;
    }

    public Date getCreateTime() {
	return createTime;
    }

    public void setCreateTime(Date date) {
	this.createTime = date;
    }

    public int getType() {
	return type;
    }

    public void setType(int type) {
	this.type = type;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((transactionId == null) ? 0 : transactionId.hashCode());
	return result;
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	Transaction other = (Transaction) obj;
	if (transactionId == null) {
	    if (other.transactionId != null)
		return false;
	} else if (!transactionId.equals(other.transactionId))
	    return false;
	return true;
    }

    @Override
    public String toString() {
	return JSONObject.toJSONString(this, SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue,
		SerializerFeature.WriteDateUseDateFormat);
    }
}
