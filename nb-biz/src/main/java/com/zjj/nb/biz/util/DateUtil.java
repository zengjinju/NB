package com.zjj.nb.biz.util;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Hours;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by jinju.zeng on 2017/6/23.
 */
public class DateUtil {

    private static final String PATTERT_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * 判断当前日期是否在两个日期之间
     *
     * @param start
     * @param end
     * @return
     */
    public static Boolean between(Date start, Date end) {
        if (start == null) {
            throw new RuntimeException("判断当前日期是否在两个日期之间时，开始时间不能为空");
        }
        if (end == null) {
            throw new RuntimeException("判断当前日期是否在两个日期之间时，结束时间不能为空");
        }
        DateTime beforeDate = new DateTime(start.getTime());
        DateTime afterDate = new DateTime(end.getTime());
        DateTime now = DateTime.now();
        if (beforeDate.isBefore(now) && afterDate.isAfter(now)) {
            return true;
        }
        return false;
    }

    /**
     * 将日期转换成String默认格式(yyyy-MM-dd HH:mm:ss)
     *
     * @param date
     * @return
     */
    public static String parseDateToString(Date date) {
        return parseDateToString(date, PATTERT_FORMAT);
    }

    public static String parseDateToString(Date date, String format) {
        if (date == null) {
            throw new RuntimeException("日期转换时，不能为空");
        }
        if (format == null || "".equals(format)) {
            //使用默认的日期格式
            format = PATTERT_FORMAT;
        }
        return new SimpleDateFormat(format).format(date);
    }

    /**
     * 计算两个日期之间相差的天数
     *
     * @param var1
     * @param var2
     * @return
     */
    public static int days(Date var1, Date var2) {
        DateTime time1 = new DateTime(var1.getTime());
        DateTime time2 = new DateTime(var2.getTime());
        return Days.daysBetween(time1, time2).getDays();
    }

    public static int hours(Date var1, Date var2) {
        DateTime time1 = new DateTime(var1.getTime());
        DateTime time2 = new DateTime(var2.getTime());
        return Hours.hoursBetween(time1, time2).getHours();
    }

    public static void main(String[] args) {
        Date t1 = null, t2 = null;
        try {
            t1 = new SimpleDateFormat(PATTERT_FORMAT).parse("2017-05-17 12:00:00");
            t2 = new SimpleDateFormat(PATTERT_FORMAT).parse("2017-06-24 12:00:00");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int n = days(t2, t1);
    }
}