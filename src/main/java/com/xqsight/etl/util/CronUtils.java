package com.xqsight.etl.util;


import org.quartz.TriggerUtils;
import org.quartz.impl.triggers.CronTriggerImpl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 〈一句话功能简述〉<br>
 * 〈功能详细描述〉
 *
 * @author ganggang.wang
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public class CronUtils {

    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private static SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd");

    private static List<Date> parseTimeStamp(String cron) {
        try {
            CronTriggerImpl cronTriggerImpl = new CronTriggerImpl();
            cronTriggerImpl.setCronExpression(cron);
            Calendar calendar = Calendar.getInstance();
            Date now = calendar.getTime();
            calendar.add(Calendar.DATE, 2); //调度时间周期不能大于这里的值
            List<Date> dates = TriggerUtils.computeFireTimesBetween(
                    cronTriggerImpl, null, now, calendar.getTime());
            return dates;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getEndTime(String cron) {
        return dateFormat.format(System.currentTimeMillis());
    }

    public static String getEndTime() {
        return dateFormat.format(System.currentTimeMillis());
    }

    public static String getStartTime(String cron) {
        List<Date> dates = parseTimeStamp(cron);
//        for(Date date:dates){
//            System.out.println(dateFormat.format(date));
//        }
        Date curTime = dates.get(0);
        Date aftTime = dates.get(1);
        long timeDiff = aftTime.getTime() - curTime.getTime();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(curTime);
        calendar.add(Calendar.MILLISECOND, -(int) timeDiff);
        return dateFormat.format(calendar.getTime());
    }

    public static Long getDiffTime(String cron) {
        List<Date> dates = parseTimeStamp(cron);
        long data1 = dates.get(0).getTime();
        long data2 = dates.get(1).getTime();
        return (data2 - data1) / 1000 - 120; //注意：调度周期不能低于120秒即2分钟
    }

    public static String getDayBefore(int dayAgo) {
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, -dayAgo);
        date = calendar.getTime();
        return dateFormat1.format(date) + " 00:00:00";
    }

    public static String getDayBefore(String dateTime, int dayAgo) {
        Date date = new Date();
        try {
            date = dateFormat.parse(dateTime);
        } catch (Exception e) {
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, dayAgo);
        date = calendar.getTime();
        return dateFormat.format(date);
    }

    public static void main(String[] args) {
        String cron = "0 0 * * * ?";
        System.out.println(getStartTime(cron));
        System.out.println(getEndTime(cron));
    }

}
