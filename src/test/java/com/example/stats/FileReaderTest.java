package com.example.stats;

import java.io.File;
import java.io.IOException;

import com.example.stats.model.FileAccessLog;
import org.junit.jupiter.api.Test;

public class FileReaderTest {
	
	@Test
	public void testRead() throws IOException {
		File file = new File("audit/multi_log_sample2581913455433010667.log");
		FileAccessReader reader = new FileAccessReader(file);
		while (true) {
			FileAccessLog audit = reader.readNext();
			if (audit == null) {
				break;
			}
			System.out.println(audit);
		}
		reader.close();
	}

}
