package com.example.stats.model;

import java.sql.Timestamp;
import java.util.Calendar;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@NoArgsConstructor
public class FileAccessLog {

	public static final String FIELD_DELIMITER = ":";

	private String userId;
	private String policyId;
	private String filePath;
	private String accessType;     // "UPLOAD" / "DOWNLOAD"
	private String accessResult;   // "S" or "F"
	private String failReason;
	private Timestamp accessTime;
	private String deviceCode;

	public FileAccessLog(String line) {
		String[] tokens = line.split(FIELD_DELIMITER);
		int i = 0;
		this.userId = tokens[i++].trim();
		this.policyId = tokens[i++];
		this.filePath = tokens[i++];
		this.accessType = tokens[i++];
		this.accessResult = tokens[i++];
		this.failReason = tokens[i++];
		this.accessTime = new Timestamp(Long.parseLong(tokens[i++]));
		this.deviceCode = tokens[i++];
	}

	public boolean isSuccess() {
		return "S".equalsIgnoreCase(accessResult);
	}

	public boolean isUpload() {
		return "UPLOAD".equalsIgnoreCase(accessType);
	}

	public String getDirectoryPath() {
		if (filePath == null) return "UNKNOWN";
		int idx = filePath.lastIndexOf('/');
		return idx > 0 ? filePath.substring(0, idx) : "/";
	}

	public String getFileExtension() {
		if (filePath == null || !filePath.contains(".")) return "NONE";
		return filePath.substring(filePath.lastIndexOf('.') + 1);
	}

	public int getDay() {
		Calendar cal = Calendar.getInstance();
		cal.setTime(accessTime);
		return cal.get(Calendar.DAY_OF_MONTH);
	}

	public int getHour() {
		Calendar cal = Calendar.getInstance();
		cal.setTime(accessTime);
		return cal.get(Calendar.HOUR_OF_DAY);
	}
}

