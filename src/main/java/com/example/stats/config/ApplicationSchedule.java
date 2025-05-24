package com.example.stats.config;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;

import com.example.stats.dbms.AuditAggregator;
import com.example.stats.dbms.AuditTableCreateTask;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class ApplicationSchedule {
	
	private ThreadPoolTaskScheduler scheduler;
	private Map<Runnable, ScheduledFuture<?>> futureMap = new HashMap<>();
	
	public ApplicationSchedule(ThreadPoolTaskScheduler scheduler, AuditAggregator aggregator, @Value("${auditor.agg-cycle}") String aggCron, AuditTableCreateTask tableCreateTask) {
		this.scheduler = scheduler;
		
		registerSchedule(aggregator, aggCron);
		registerSchedule(tableCreateTask, "* * * L * *");
	}
	
	public void registerSchedule(Runnable task, String cronExp) {
		ScheduledFuture<?> future = futureMap.get(task);
		if (future != null) {
			future.cancel(false);
		}
		futureMap.put(task, scheduler.schedule(task, new CronTrigger(cronExp)));
		log.info("Register [{}] task scheduled [{}]", task.getClass().getSimpleName(), cronExp);
	}

}
