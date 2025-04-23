package com.example.stats.dbms;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.*;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.apache.ibatis.type.JdbcType;
import lombok.Setter;

public class StatsSqlSessionFactory {
	@Setter
	private SqlSessionFactory factory;
	
	public StatsSqlSessionFactory(DataSource dataSource) throws SQLException, IOException {
		TransactionFactory tf = new JdbcTransactionFactory();
		Environment env = new Environment("env", tf, dataSource);
		Configuration config = new Configuration(env);
		config.setMapUnderscoreToCamelCase(true);
		config.setJdbcTypeForNull(JdbcType.NULL);
		
		String mapperResource = "mappers/stats/file_access.xml";
		parseMapper(mapperResource, config);
		
		mapperResource = findMapperResource(dataSource.getConnection());
		parseMapper(mapperResource, config);
		
		factory = new SqlSessionFactoryBuilder().build(config);
	}
	
	private String findMapperResource(Connection conn) throws SQLException, IOException {
		String name = conn.getMetaData().getDatabaseProductName().toUpperCase();
		String path = null;
		if (name.contains("H2")) {
			path = "mappers/stats/h2/file_access.xml";
		}else if (name.contains("ORACLE")) {
			path = "mappers/stats/oracle/file_access.xml";
		}
		return path;
	}
	
	private void parseMapper(String resource, Configuration config) throws IOException {
		InputStream is = Resources.getResourceAsStream(resource);
		XMLMapperBuilder mapperBuilder = new XMLMapperBuilder(is, config, resource, config.getSqlFragments());
		mapperBuilder.parse();
	}
	
	public StatsSqlSession getSession() {
		SqlSession session = factory.openSession(ExecutorType.BATCH, false);
		return new StatsSqlSession(session);
	}
	
}
