package com.cassandra.banking.service;

import java.util.concurrent.BlockingQueue;

import com.cassandra.banking.dao.BankDao;
import com.cassandra.banking.dao.BankRedisDao;
import com.cassandra.banking.model.Transaction;
import com.cassandra.demo.utils.KillableRunner;


class TransactionWriter implements KillableRunner {

	private volatile boolean shutdown = false;
	private BankDao dao;
	private BankRedisDao redisDao;
	private BlockingQueue<Transaction> queue;


	public TransactionWriter(BankDao dao, BankRedisDao rdao, BlockingQueue<Transaction> queue) {
		this.dao = dao;
		this.redisDao = rdao;
		this.queue = queue;
	}

	@Override
	public void run() {
		Transaction transaction;
		while(!shutdown){				
			transaction = queue.poll(); 
			
			if (transaction!=null){
				try {
					this.dao.insertTransactionAsync(transaction);
					this.redisDao.addTransactionDocument(transaction);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}				
		}				
	}
	
	@Override
    public void shutdown() {
		while(!queue.isEmpty())
			
		shutdown = true;
    }
}
