package com.logicaldoc.util.time;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.junit.Test;

import com.logicaldoc.util.time.TimeDiff.TimeField;

import junit.framework.TestCase;

public class TimeDiffTest extends TestCase {
	
	@Test
	public void testGetTimeDifference() throws ParseException {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SS");
		
		Date date1=df.parse("1978-02-14 12:50:13.05");
		Date date2=df.parse("1978-02-15 14:51:14.08");
		
		assertEquals(1L,TimeDiff.getTimeDifference(date1, date2, TimeField.DAY));
		assertEquals(2L,TimeDiff.getTimeDifference(date1, date2, TimeField.HOUR));
		assertEquals(1L,TimeDiff.getTimeDifference(date1, date2, TimeField.MINUTE));
		assertEquals(1L,TimeDiff.getTimeDifference(date1, date2, TimeField.SECOND));
		assertEquals(3L,TimeDiff.getTimeDifference(date1, date2, TimeField.MILLISECOND));
	}
	
	@Test
	public void testPrintDuration() throws ParseException {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SS");
		
		Date date1=df.parse("1978-02-14 12:50:13.05");
		Date date2=df.parse("1978-02-15 14:51:14.08");
		
		assertEquals("26:01:01.003",TimeDiff.printDuration(date1, date2));
	}
}