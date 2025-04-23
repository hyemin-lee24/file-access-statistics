package com.example.stats.dbms;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.example.stats.FileAccessReader;
import com.example.stats.Repository;
import com.example.stats.model.FileQueue;
import com.example.stats.model.FileAccessLog;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FileAccessRepository extends Thread implements Repository, Closeable {


	private ExecutorService pool;
	private StatsSqlSessionFactory sessionFactory;
	private FileQueue fileQueue;

	public FileAccessRepository(FileQueue fileQueue, StatsSqlSessionFactory sessionFactory, int poolSize) {
		this.fileQueue = fileQueue;
		this.sessionFactory = sessionFactory;
		this.pool = Executors.newFixedThreadPool(poolSize);
	}

	@Override
	public void run() {
		log.debug("FileAccessSaveTaskPool start");

		while (!isInterrupted()) {
			String filePath = fileQueue.poll();
			if (filePath == null) {
				break;
			}
			save(filePath);
		}

		log.debug("FileAccessSaveTaskPool terminate");
	}

	@Override
	public void close() throws IOException {
		pool.shutdown();
		interrupt();
	}

	@Override
	public void save(String filePath) {
		StatsSqlSession session = sessionFactory.getSession();
		FileAccessSaveTask task = new FileAccessSaveTask(session, filePath);
		pool.submit(task);
	}

	public void createRepository(String tag) {
		StatsSqlSession session = sessionFactory.getSession();
		try {
			session.createFileAccessLogTable(tag);  // 파일 접근 로그/집계 테이블 생성
		} finally {
			session.close(true);
		}
		log.debug("Create [{}] file access tables", tag);
	}

	public void clearRepository(String tag) {
		StatsSqlSession session = sessionFactory.getSession();
		try {
			session.clearFileAccessAggTable(tag);
		} finally {
			session.close(true);
		}
		log.debug("Clear [{}] file access tables", tag);
	}

	class FileAccessSaveTask implements Runnable {
		private StatsSqlSession session;
		private File file;
		private boolean deleteAfterSave = true;

		public FileAccessSaveTask(StatsSqlSession session, String filePath) {
			this.session = session;
			this.file = new File(filePath);
		}

		@Override
		public void run() {
			try {
				save(file);
			} catch (Exception e) {
				log.error("Error saving file: {}", file.getName(), e);
			}
		}

		public void save(File file) throws Exception {
			FileAccessReader reader = null;
			FileAccessLog logEntry = null;
			int insertCount = 0;
			boolean success = true;

			try {
				reader = new FileAccessReader(file);
			} catch (FileNotFoundException e) {
				log.error("[{}] - {}", file.getAbsolutePath(), e.getMessage());
				throw e;
			}

			try {
				while ((logEntry = reader.readNext()) != null) {
					session.insertFileAccessLog(logEntry);
					insertCount++;
				}
				log.info("Saved file access log [{}] ({} records)", file.getName(), insertCount);

			} catch (Exception e) {
				log.error("Failed to save [file: {}, log: {}] - {}", file.getName(), logEntry, e.toString());
				success = false;
				throw e;
			} finally {
				reader.close();
				session.close(success);
			}

			if (deleteAfterSave) {
				file.delete();
			}
		}
	}
}
