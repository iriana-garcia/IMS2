package com.ghw.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class DateUtil {

	public static Integer timeMinutes = 60;
	public static Integer timeMinutesMin = 60;

	public static Date convertUTCtoESTEDT(Date date) {

		// Coordinated Universal Time is 4 hours ahead of Eastern Time
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.setTimeZone(TimeZone.getDefault());
		calendar.add(Calendar.HOUR_OF_DAY, -getNumber());

		return calendar.getTime();
	}

	public static Integer getNumber() {
		return TimeZone.getDefault().getDisplayName(false, TimeZone.SHORT)
				.equals("EST") ? 4 : 5;
	}

	public static Date convertESTEDTtoUTC(Date date) {
		// Coordinated Universal Time is 4 hours ahead of Eastern Time
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
		calendar.add(Calendar.HOUR_OF_DAY, getNumber());

		return calendar.getTime();
	}

	public static Date processStartDate(Date startDate) {
		Calendar start = Calendar.getInstance();
		start.setTime(startDate);
		start.set(Calendar.SECOND, 0);
		start.set(Calendar.MILLISECOND, 0);
		// add one minute to the last date
		start.add(Calendar.MINUTE, 1);
		return start.getTime();

	}

	public static Date processEndDate() {
		Calendar end = Calendar.getInstance();
		end.set(Calendar.SECOND, end.getMaximum(Calendar.SECOND));
		end.add(Calendar.MINUTE, -timeMinutes);
		end.set(Calendar.MILLISECOND, end.getMaximum(Calendar.MILLISECOND));

		return end.getTime();

	}

	public static Date getEndOfDay(Date date) {
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		calendar.set(Calendar.MILLISECOND, 999);

		return calendar.getTime();

	}

	public static Date getIniOfDay(Date date) {
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 000);

		return calendar.getTime();

	}
	
	

	public static String dateFormatMinutes(Date date) {

		return date != null ? (new SimpleDateFormat("E, M-d-yy h:mm a"))
				.format(date) : null;
	}
	
	public static String dateFormatSeconds(Date date) {

		return date != null ? (new SimpleDateFormat("E, M-d-yy h:mm:ss a"))
				.format(date) : null;
	}

}
