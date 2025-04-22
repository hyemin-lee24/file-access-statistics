package com.example.stats;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import com.example.stats.model.FileQueue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class FileReceiverTest {
	
	private static final String CRYPTO_AUDIT_DIR_PATH = "audit";
	private static final String SAMPLE_AUDIT_DIR_PATH = "sample";
	
	private FileAccessReceiver receiver;
	
	@BeforeEach
	public void init() {
		FileQueue queue = new FileQueue(CRYPTO_AUDIT_DIR_PATH);
		receiver = new FileAccessReceiver(CRYPTO_AUDIT_DIR_PATH, queue);
	}
	
	@Test
	public void testReceive() {
		File sampleDir = new File(SAMPLE_AUDIT_DIR_PATH);
		File[] samples = sampleDir.listFiles();
		FileInputStream is = null;

		long start = System.nanoTime();
		System.out.println("test start -- " + start);
		for (File sample : samples) {
			try {
				is = new FileInputStream(sample);
				receiver.receive(is);
				sample.delete();
			} catch (IOException e) {
				System.out.println(e.getMessage());
			}
		}
		
		long end = System.nanoTime();
		long result = TimeUnit.SECONDS.convert((end-start), TimeUnit.NANOSECONDS);
		System.out.println("  test end -- " + end + " (" + result + "sec.)");
		
	}

}
