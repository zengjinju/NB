package com.zjj.nb.biz.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author zengjinju
 * @date 2019/3/22 下午4:13
 */
public class Jdk8DateUtil {
	private static final String PATTERT_FORMAT= "yyyy-MM-dd HH:mm:ss";

	public static String parseDateToString(LocalDateTime date , String pattern){
		return date.format(DateTimeFormatter.ofPattern(pattern));
	}

	public static LocalDateTime parseStr2Time(String str,String pattern){
		return LocalDateTime.parse(str,DateTimeFormatter.ofPattern(pattern));
	}
}

