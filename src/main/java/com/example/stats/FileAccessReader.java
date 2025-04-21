package com.example.stats;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import com.example.stats.model.FileAccessLog;

public class FileAccessReader implements Closeable {

	private final BufferedReader reader;

	public FileAccessReader(InputStream is) {
		this.reader = new BufferedReader(new InputStreamReader(is));
	}

	public FileAccessReader(File file) throws IOException {
		this.reader = new BufferedReader(new FileReader(file));
	}

	public FileAccessLog readNext() throws IOException {
		String line = reader.readLine();
		if (line != null && !line.isEmpty()) {
			return new FileAccessLog(line);
		}
		return null;
	}

	public List<FileAccessLog> readAll() throws IOException {
		List<FileAccessLog> result = new ArrayList<>();
		FileAccessLog log;
		while ((log = readNext()) != null) {
			result.add(log);
		}
		return result;
	}

	@Override
	public void close() {
		try {
			reader.close();
		} catch (IOException ignored) {}
	}
}