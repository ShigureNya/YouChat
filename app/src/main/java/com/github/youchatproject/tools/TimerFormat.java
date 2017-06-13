package com.github.youchatproject.tools;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 作者： guhaoran
 * 创建于： 2017/6/6
 * 包名： com.github.youchatproject.tools
 * 文档描述：时间工具类
 */
public class TimerFormat {
    /**
     * [获得当前时间]
     * @return 当前时间对象
     */
    public static Calendar getCurrentDay(){
        Date date = new Date();
        Calendar currentDate = Calendar.getInstance();
        currentDate.setTime(date);
        return currentDate;
    }

    /**
     * @return 普通按指定字符串输出的文本日期
     */
    public static String getSimpleCurrentDate(){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date = new Date();
        String dateStr = format.format(date);
        return dateStr;
    }
    /**
     * 时间是什么时间段
     * @param time 时间
     * @return
     */
    public static String formatDateTime(String time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        if(time==null ||"".equals(time)){
            return "";
        }
        Date date = null;
        try {
            date = format.parse(time);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Calendar current = Calendar.getInstance();

        Calendar today = Calendar.getInstance();    //今天

        today.set(Calendar.YEAR, current.get(Calendar.YEAR));
        today.set(Calendar.MONTH, current.get(Calendar.MONTH));
        today.set(Calendar.DAY_OF_MONTH,current.get(Calendar.DAY_OF_MONTH));
        //  Calendar.HOUR——12小时制的小时数 Calendar.HOUR_OF_DAY——24小时制的小时数
        today.set( Calendar.HOUR_OF_DAY, 0);
        today.set( Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);

        Calendar yesterday = Calendar.getInstance();    //昨天

        yesterday.set(Calendar.YEAR, current.get(Calendar.YEAR));
        yesterday.set(Calendar.MONTH, current.get(Calendar.MONTH));
        yesterday.set(Calendar.DAY_OF_MONTH,current.get(Calendar.DAY_OF_MONTH)-1);
        yesterday.set( Calendar.HOUR_OF_DAY, 0);
        yesterday.set( Calendar.MINUTE, 0);
        yesterday.set(Calendar.SECOND, 0);

        current.setTime(date);

        Calendar afterYesterday = Calendar.getInstance();    //前天

        afterYesterday.set(Calendar.YEAR, current.get(Calendar.YEAR));
        afterYesterday.set(Calendar.MONTH, current.get(Calendar.MONTH));
        afterYesterday.set(Calendar.DAY_OF_MONTH,current.get(Calendar.DAY_OF_MONTH)-2);
        afterYesterday.set( Calendar.HOUR_OF_DAY, 0);
        afterYesterday.set( Calendar.MINUTE, 0);
        afterYesterday.set(Calendar.SECOND, 0);

        if(current.after(today)){
            //今天
            return time.split(" ")[1];
        }else if(current.before(today) && current.after(yesterday)){
            return "昨天 "+time.split(" ")[1];
        }else{
            int index = time.indexOf("-")+1;
            return time.substring(index, time.length());
        }
    }
}
