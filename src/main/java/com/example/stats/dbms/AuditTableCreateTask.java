package com.example.stats.dbms;

import com.example.stats.model.AuditTag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AuditTableCreateTask implements Runnable {
	
	private FileAccessRepository repository;
	private AuditAggregator aggregator;
	
	public AuditTableCreateTask(FileAccessRepository repository, AuditAggregator aggregator) {
		this.repository = repository;
		this.aggregator = aggregator;
		prepareInitTables();
	}
	
	private void prepareInitTables() {
		AuditTag tag = AuditTag.now();
		try {
			createTables(tag.toString());

			tag.subtractPeriod();
			createTables(tag.toString());
		} catch (Exception e) {
			log.error("Error while creating audit tables ({})", tag, e);
			// 감사기록 중 에러가 발생하여도 운영에 지장이 없도록 예외는 던지지 않는다.
		}
	}

	@Override
	public void run() {
		AuditTag tag = AuditTag.now();
		tag.addPeriod();
		createTables(tag.toString());
	}
	
	public void createTables(String tag) {
		repository.createRepository(tag);
		aggregator.createTable(tag);
	}
}
