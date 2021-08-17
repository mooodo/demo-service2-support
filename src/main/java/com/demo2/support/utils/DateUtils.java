/* 
 * Created by 2018年9月9日
 */
package com.demo2.support.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * The utility of the date.
 * @author fangang
 */
public class DateUtils {
	private static final String dateFormat = "yyyy-MM-dd HH:mm:ss";
	/**
	 * @param date
	 * @return 将Date转换为Calendar
	 */
	public static Calendar getCalendar(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar;
	}

	/**
	 * @return 获取当前时间的Calendar
	 */
	public static Calendar getCalendar() {
		Calendar calendar = Calendar.getInstance();
		return calendar;
	}
	
	/**
	 * @return 获取当前时间
	 */
	public static Date getNow() {
		return getCalendar().getTime();
	}

	/**
	 * @param string
	 * @param format
	 * @return 将日期时间字符串按照某种格式转换为日期时间
	 */
	public static Date getDate(String string, String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		try {
			return sdf.parse(string);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @param string
	 * @return 将UTC日期时间字符串转换为日期时间
	 */
	public static Date getDateForUTC(String string) {
		string = string.replace("Z", " UTC");
		String format = "yyyy-MM-dd'T'HH:mm:ss.SSS Z";
		return DateUtils.getDate(string, format);
	}
	
	/**
	 * @param string
	 * @return 将日期时间字符串按照默认格式转换为日期时间
	 */
	public static Date getDate(String string) {
		return getDate(string, dateFormat);
	}
}
