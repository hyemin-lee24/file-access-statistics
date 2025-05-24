package com.example.stats.config;

import java.io.IOException;
import java.sql.SQLException;

import javax.sql.DataSource;

import com.example.stats.dbms.FileAccessRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.stats.dbms.AuditAggregator;
import com.example.stats.dbms.StatsSqlSessionFactory;
import com.example.stats.dbms.AuditTableCreateTask;
import com.example.stats.model.FileQueue;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

@Configuration
public class ApplicationConfiguration {
	
	@Bean
	public FileQueue queue(@Value("${auditor.receive-dir}") String dirPath) {
		return new FileQueue(dirPath);
	}
	
	@Bean 
	public DataSource dataSource(@Value("${spring.datasource.url}") String url,
								@Value("${spring.datasource.username}") String username,
								@Value("${spring.datasource.password}") String password) {
		HikariConfig config = new HikariConfig();
		config.setJdbcUrl(url);
		config.setUsername(username);
		config.setPassword(password);
		config.setMaximumPoolSize(10);
		return new HikariDataSource(config);
	}
	
	@Bean
	public StatsSqlSessionFactory factory(DataSource dataSource) throws SQLException, IOException {
		return new StatsSqlSessionFactory(dataSource);
	}
	
	@Bean
	public FileAccessRepository repository(FileQueue queue, StatsSqlSessionFactory repositoryFactory, @Value("${auditor.pool-size}") int poolSize) {
		FileAccessRepository pool = new FileAccessRepository(queue, repositoryFactory, poolSize);
		pool.start();
		return pool;
	}
	
	@Bean
	public AuditAggregator aggregator(StatsSqlSessionFactory repositoryFactory) {
		return new AuditAggregator(repositoryFactory);
	}
	
	@Bean
	public AuditTableCreateTask tableCreateTask(FileAccessRepository repository, AuditAggregator aggregator) {
		return new AuditTableCreateTask(repository, aggregator);
	}
	
}
