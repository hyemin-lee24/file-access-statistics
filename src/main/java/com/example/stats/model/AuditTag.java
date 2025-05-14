package com.example.stats.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class AuditTag {
	private SimpleDateFormat sdf = new SimpleDateFormat("yyMM");
	private Calendar calendar = Calendar.getInstance();
	
	public static AuditTag now() {
		return new AuditTag();
	}
	
	public AuditTag(String tag) {
		try {
			calendar.setTime(sdf.parse(tag));
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}
	
	public AuditTag(Date date) {
		calendar.setTime(date);
	}
	
	public void addPeriod() {
		calendar.add(Calendar.MONTH, 1);
	}
	
	public void subtractPeriod() {
		calendar.add(Calendar.MONTH, -1);
	}
	
	public Date getDate() {
		return calendar.getTime();
	}
	
	@Override
	public String toString() {
		return sdf.format(calendar.getTime());
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof AuditTag)) {
			return false;
		}
		
		AuditTag prepared = (AuditTag) obj;
		return this.toString().equals(prepared.toString());
	}
}
