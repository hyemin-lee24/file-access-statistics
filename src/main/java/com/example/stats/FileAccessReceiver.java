package com.example.stats;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Queue;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FileAccessReceiver {

	private String dirPath;
	private Queue<String> filePathQueue;

	public FileAccessReceiver(String dirPath, Queue<String> filePathQueue) {
		File dir = new File(dirPath);

		if (!dir.exists()) {
			dir.mkdirs();
		}

		this.dirPath = dirPath;
		this.filePathQueue = filePathQueue;
	}

	public void receive(InputStream is) throws IOException {
		ZipEntry entry;

		try (ZipInputStream zis = new ZipInputStream(is)) {
			while ((entry = zis.getNextEntry()) != null) {
				if (entry.isDirectory()) {
					continue;
				}

				try {
					// Add timestamp suffix to source filename (for collision prevention)
					String fileName = Paths.get(entry.getName()).getFileName().toString();
					String timestampedName = System.currentTimeMillis() + "_" + fileName;
					Path path = Paths.get(dirPath, timestampedName);

					Files.copy(zis, path);
					filePathQueue.offer(path.toString());

					log.info("File Save Completed: {}", path);

				} catch (FileAlreadyExistsException e) {
					log.warn("File Exists: {}", e.toString());
				}
			}
		}
	}
}
