package com.demo2.support.utils;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Test;

public class DateUtilsTest {

	@Test
	public void testForDate() {
		String string = "2020-01-01";
		String format = "yyyy-MM-dd";
		Date date = DateUtils.getDate(string, format);
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String dateString = formatter.format(date);
		assertThat(dateString, equalTo(string));
	}
	
	@Test
	public void testForDateTime() {
		String string = "2020-01-01 23:59:59";
		String format = "yyyy-MM-dd HH:mm:ss";
		Date date = DateUtils.getDate(string, format);
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateString = formatter.format(date);
		assertThat(dateString, equalTo(string));
	}
	
	@Test
	public void testForUTC() {
		String string = "1979-09-30T16:00:00.000Z";
		Date date = DateUtils.getDateForUTC(string);
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateString = formatter.format(date);
		assertThat(dateString, equalTo("1979-10-01 00:00:00"));
	}

}
