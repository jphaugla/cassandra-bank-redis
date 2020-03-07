package com.cassandra.demo.utils;

public interface KillableRunner extends Runnable {

	public void shutdown();
}
