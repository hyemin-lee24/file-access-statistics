package com.example.stats.dbms;

import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.SqlSession;

import com.example.stats.model.FileAccessLog;

public class StatsSqlSession {
	public static final int DEFAULT_BATCH_SIZE = 100;

	private SqlSession session;
	private int updateCnt;
	private int batchSize = DEFAULT_BATCH_SIZE;

	public StatsSqlSession(SqlSession session) {
		this.session = session;
	}

	public void insertFileAccessLog(FileAccessLog log) {
		session.insert("file_access.insertFileAccessLog", log);
		if (++updateCnt == batchSize) {
			session.flushStatements();
			updateCnt = 0;
		}
	}

	public void aggregate(String tag) {
		session.update("file_access.mergeFileAccessAggregation", tag);
		session.flushStatements();
		session.commit();
	}

	public void clearFileAccessLogTable(String tag) {
		session.delete("file_access.clearFileAccessLogTable", tag);
		session.flushStatements();
		session.commit();
	}

	public void clearFileAccessAggTable(String tag) {
		session.delete("file_access.clearFileAccessAggTable", tag);
		session.flushStatements();
		session.commit();
	}

	public void createFileAccessLogTable(String tag) {
		session.insert("file_access.createFileAccessLogTable", tag);
		session.flushStatements();
		session.commit();
	}

	public void createFileAccessAggTable(String tag) {
		session.insert("file_access.createFileAccessAggTable", tag);
		session.flushStatements();
		session.commit();
	}

	public void selectFileAccessLogs(String tag, ResultHandler<FileAccessLog> handler) {
		session.select("file_access.selectFileAccessLogs", tag, handler);
	}

	public void close(boolean success) {
		if (success) {
			session.flushStatements();
			session.commit();
		} else {
			session.rollback();
		}
		session.close();
	}
}
