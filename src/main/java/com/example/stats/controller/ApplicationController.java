package com.example.stats.controller;

import java.io.InputStream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.stats.FileAccessReceiver;
import com.example.stats.config.ApplicationSchedule;
import com.example.stats.dbms.AuditAggregator;
import com.example.stats.dbms.AuditTableCreateTask;
import com.example.stats.model.FileQueue;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/rest/file_access")
public class ApplicationController {
	
	private FileAccessReceiver receiver;
	private AuditAggregator aggregator;
	private AuditTableCreateTask repository;
	private ApplicationSchedule scheduler;
	
	public ApplicationController(FileQueue fileQueue, @Value("${auditor.receive-dir}") String dirPath, AuditAggregator aggregator, AuditTableCreateTask repository, ApplicationSchedule scheduler) {
		this.receiver = new FileAccessReceiver(dirPath, fileQueue);
		this.aggregator = aggregator;
		this.repository = repository;
		this.scheduler = scheduler;
	}
	
	@PostMapping
	@RequestMapping("/{userId}")
	public boolean reportCryptoAudit(@PathVariable String userId, MultipartFile audit) {
		log.debug("receive [{}]", userId);
		
		try {
			InputStream is = audit.getInputStream();
			receiver.receive(is);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	@PutMapping
	@RequestMapping("/aggregate")
	public ResponseEntity<Void> aggregate(String tag) {
		log.debug("aggregate [{}]", tag);
		aggregator.aggregate(tag);
		return ResponseEntity.ok().build();
	}
	
	@PutMapping
	@RequestMapping("/aggregate/cron")
	public ResponseEntity<Void> changeAggregationCron(String expression) {
		log.debug("change aggregate cron to [{}]", expression);
		scheduler.registerSchedule(aggregator, expression);
		return ResponseEntity.ok().build();
	}
	
	@PostMapping
	@RequestMapping("/repository")
	public ResponseEntity<Void> repository(String tag) {
		repository.createTables(tag);
		return ResponseEntity.ok().build();
	}
	
}
