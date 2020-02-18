package com.datastax.banking.service;

import java.util.concurrent.BlockingQueue;

import com.datastax.banking.dao.BankDao;
import com.datastax.banking.model.Transaction;
import com.datastax.demo.utils.KillableRunner;

class TransactionWriter implements KillableRunner {

	private volatile boolean shutdown = false;
	private BankDao dao;
	private BlockingQueue<Transaction> queue;

	public TransactionWriter(BankDao dao, BlockingQueue<Transaction> queue) {
		this.dao = dao;
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
