package com.example.stats.dbms;

import com.example.stats.Aggregator;
import com.example.stats.model.AuditTag;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AuditAggregator implements Runnable, Aggregator {
	
	private StatsSqlSessionFactory sessionFactory;
	
	public AuditAggregator(StatsSqlSessionFactory factory) {
		this.sessionFactory = factory;
	}
	
	@Override
	public void run() {
		AuditTag tag = AuditTag.now();
		try {
			aggregate(tag.toString());
		} catch (Exception e) {
		}

		tag.subtractPeriod();
		try {
			aggregate(tag.toString());
		} catch (Exception e) {
		}
	}
	
	@Override
	public void aggregate(String tag) {
		StatsSqlSession session = sessionFactory.getSession();
		try {
			session.aggregate(tag);
			session.close(true);
			log.info("Aggregate {} crypto audit data.", tag);
		} catch (Exception e) {
			session.close(false);
			log.error("Fail to aggregate [{}] crypto audit data - {}", tag.toString(), e.toString());
			throw e;
		}
	}
	
	public void createTable(String tag) {
		StatsSqlSession session = sessionFactory.getSession();
		session.createFileAccessAggTable(tag);
		session.close(true);
		log.debug("Create [{}] aggregation table", tag);
	}
	
	public void clearTable(String tag) {
		StatsSqlSession session = sessionFactory.getSession();
		session.clearFileAccessAggTable(tag);
		session.close(true);
		log.debug("Clear [{}] aggregation table", tag);
	}
}
