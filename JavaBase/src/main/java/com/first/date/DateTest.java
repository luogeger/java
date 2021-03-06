package com.first.date;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.junit.Test;
import org.springframework.util.StopWatch;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;


public class DateTest {


    @Test
    public void main1() {
        System.out.println(new Date());// Sat May 18 10:02:48 CST 2019
        System.out.println(System.currentTimeMillis());// 1558144706160
        System.out.println(DateFormatUtils.format(new Date(), "MM-dd"));// 07-11
        String format = DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss");
        System.out.println(DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));// 07-11
    }

    @Test
    public void main2() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        System.out.println(df.format(new Date()));// new Date()为获取当前系统时间
    }

    @Test
    public void main3() {
        Calendar beforeTime = Calendar.getInstance();
        System.out.println(beforeTime.getTime());// 当前时间

        beforeTime.add(Calendar.MINUTE, -5);
        System.out.println(beforeTime.getTime());// 5分钟之前的时间

        String before5 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(beforeTime.getTime());  // 前五分钟时间--String
        System.out.println(before5);
    }

    /**
     * LocalDateTime 比较
     */
    @Test
    public void main4() {
        System.out.println(LocalDate.parse("2020-09-02", DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        System.out.println(LocalDateTime.parse("2020-09-02 12:48:11", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        System.out.println("--------------------");

        //  String 转化为 Date
        String strDate = "2020-02-07 15:45:32";

        LocalDateTime now = LocalDateTime.now();
        //  2020-02-07T15:44:47.767
        System.out.println(now);

        LocalDateTime formatTime = LocalDateTime.parse(strDate, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        //  过期了吗？
        LocalDateTime nowTime = LocalDateTime.now();
        LocalDateTime before = nowTime.minusSeconds(30);
        boolean flag = before.isAfter(formatTime);
        System.out.println(flag);
    }


    /**
     * LocalDateTime转为String、TimeStamp、Long、Instant、 Date
     */
    @Test
    public void main5() {
        //LocalDateTime -> String
        String localDateTimeToString = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        System.out.println("LocalDateTime -> String:  " + localDateTimeToString);
        //LocalDateTime -> TimeStamp
        Timestamp localDateTimeToTimeStamp = Timestamp.valueOf(LocalDateTime.now());
        System.out.println("LocalDateTime -> TimeStamp:  " + localDateTimeToTimeStamp);
        //LocalDateTime -> Long
        Long localDateTimeToLong = Timestamp.valueOf(LocalDateTime.now()).getTime();
        System.out.println("LocalDateTime -> Long:  " + localDateTimeToLong);
        //LocalDateTime -> Instant
        Instant localDateTimeToInstant = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant();
        System.out.println("LocalDateTime -> Instant:  " + localDateTimeToInstant);
        //LocalDateTime -> Date
        Date LocalDateTimeToDate = Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant());
        System.out.println("LocalDateTime -> Date:  " + LocalDateTimeToDate);
    }

    /**
     * String转为LocalDateTime、 Date
     */
    @Test
    public void main6() {
        //String -> LocalDateTime
        LocalDateTime stringToLocalDateTime = LocalDateTime.parse("2018-03-11 15:30:11", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        System.out.println("String -> LocalDateTime:  " + stringToLocalDateTime);
        //String -> Date
        Date stringToDate = null;
        try {
            stringToDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2018-03-11 15:30:11");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        System.out.println("String -> Date:  " + stringToDate);
    }


    /**
     * Long转为LocalDateTime、 Date
     */
    @Test
    public void main7() {
        //Long -> LocalDateTime
        LocalDateTime longToLocalDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(1520754566856L), ZoneId.systemDefault());
        System.out.println("Long -> LocalDateTime:  " + longToLocalDateTime);
        //Long -> Date
        Date longToDate = new Date(1520754566856L);
        System.out.println("Long -> Date: " + longToDate);
    }


    /**
     * 线程睡眠， 执行耗时
     */
    @Test
    public void main8 () throws InterruptedException {
        StopWatch stopWatch = new StopWatch();
        //TimeUnit.SECONDS.sleep(3);

        for (int i = 0; i < 10000; i++) {
            System.out.println(i);
        }
//        long time = stopWatch.getTime();
//        System.out.println(time);
    }

    @Test
    public void main9 () throws InterruptedException {
        StopWatch stopWatch = new StopWatch("任务名称");

        // 任务1
        stopWatch.start("任务一");
        Thread.sleep(1000);
        stopWatch.stop();

        // 任务2
        stopWatch.start("任务一");
        Thread.sleep(2000);
        stopWatch.stop();

        StopWatch.TaskInfo[] taskInfo = stopWatch.getTaskInfo();
        System.out.println(stopWatch.getTotalTimeMillis());

        // 打印出耗时
        System.out.println(stopWatch.prettyPrint());
    }

}
