package com.example.stats.model;

import java.io.File;
import java.util.concurrent.ConcurrentLinkedQueue;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class FileQueue extends ConcurrentLinkedQueue<String> {
	
	private static final long serialVersionUID = 1L;
	
	public FileQueue(String dirPath) {
		File auditFileDir = new File(dirPath);
		if (!auditFileDir.exists()) {
			auditFileDir.mkdirs();
		}
		
		if (!auditFileDir.isDirectory()) {
			throw new IllegalArgumentException("[" + auditFileDir.getAbsolutePath() + "] is not directory");
		}
		
		File[] auditFiles = auditFileDir.listFiles();
		
		for (File file : auditFiles) {
			offer(file.getAbsolutePath());
		}
	}

	@Override
	public synchronized boolean offer(String fileName) {
		boolean result = super.offer(fileName);
		notify();
		return result; 
	}
	
	@Override
	public synchronized String poll() {
		try {
			if (isEmpty()) {
				wait();
			}
			return super.poll();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			return null;
		}
	}

}
