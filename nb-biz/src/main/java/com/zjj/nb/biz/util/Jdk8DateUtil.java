package com.zjj.nb.biz.util;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;

/**
 * @author zengjinju
 * @date 2019/3/22 下午4:13
 */
public class Jdk8DateUtil {
	private static final DateTimeFormatter YYYY_MM_DD = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	private static final DateTimeFormatter YYYY_MM_DD_HH_mm_ss = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	public static String parseDateToString(Date date , String pattern){
		try {
			Instant instant = date.toInstant();
			ZoneId zoneId = ZoneId.systemDefault();
			LocalDateTime localDateTime = instant.atZone(zoneId).toLocalDateTime();
			return localDateTime.format(DateTimeFormatter.ofPattern(pattern));
		}catch (Exception e){

		}
		return null;
	}

	public static Date parseYYYYMMDDHHmmss2Time(String str) {
		try {
			LocalDateTime localDateTime = LocalDateTime.parse(str, YYYY_MM_DD_HH_mm_ss);
			ZoneId zoneId = ZoneId.systemDefault();
			ZonedDateTime zdt = localDateTime.atZone(zoneId);
			return Date.from(zdt.toInstant());
		}catch (Exception e){
			e.printStackTrace();
		}
		return null;
	}

	public static Date parseYYYYMMDD2Time(String str){
		try{
			LocalDate localDate = LocalDate.parse(str,YYYY_MM_DD);
			ZoneId zoneId = ZoneId.systemDefault();
			Instant instant = localDate.atStartOfDay().atZone(zoneId).toInstant();
			return Date.from(instant);
		}catch (Exception e){
			e.printStackTrace();
		}
		return null;
	}

	public static Date getFirstDayOfMonth(){
		LocalDate firstDay = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());
		ZoneId zoneId = ZoneId.systemDefault();
		Instant instant = firstDay.atStartOfDay().atZone(zoneId).toInstant();
		return Date.from(instant);
	}

	public static void main(String[] args){
		System.out.println(parseYYYYMMDDHHmmss2Time("2019-11-17 11:11:11"));
		System.out.println(parseYYYYMMDD2Time("2019-11-16"));
		System.out.println(getFirstDayOfMonth());
		System.out.println(parseDateToString(new Date(),"yyyy-MM-dd"));
	}
}

