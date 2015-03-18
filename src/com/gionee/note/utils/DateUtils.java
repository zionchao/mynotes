package com.gionee.note.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {

	private static SimpleDateFormat defaultFormat = new SimpleDateFormat();
//	private static SimpleDateFormat YYYYMMDDFormat = new SimpleDateFormat("yyyyMMdd");

	

	/**
	 * 
	 * @param date
	 * @param pattern
	 * @return
	 */
	public static String format(Date date, String pattern) {
		if (date == null || pattern == null || "".equals(pattern)) {
			return "";
		}
		return new SimpleDateFormat(pattern).format(date);
	}

	
}
