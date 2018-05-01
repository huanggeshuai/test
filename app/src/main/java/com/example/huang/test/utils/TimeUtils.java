package com.example.huang.test.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by huang on 2018/4/19.
 */

public class TimeUtils {
    public static String ConvertHHMMSS(String time) {
        long ss = Long.parseLong(time);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return sdf.format(ss);
    }
    public static int TimeDifference  (String opentime,String closetime) throws ParseException {
     //   long from = Long.parseLong(opentime);
     //   long to = Long.parseLong(closetime);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        Date date1 = sdf.parse(opentime);
        Date date2 = sdf.parse(closetime);
        long from=date1.getTime();
        long to=date2.getTime();
        int hours = (int) ((to - from)/(1000 * 60 * 60));
        return hours;
    }
    public static String ConvertYYMMDD(Date date) {
        DateFormat dateFormat = new SimpleDateFormat();
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(date);
    }
    public static boolean Camplare_Time(String time1,String time2) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date1=sdf.parse(time1);
        Date date2=sdf.parse(time2);
        if(date1.getTime()>date2.getTime()){
            return true;
        }else
            return false;
    }
    public static String getNowtime(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date d = new Date();
       return sdf.format(d);
    }
   public static int TimeDifferencebyyear(String time1,String time2) throws ParseException {  //活动开始时间-活动截止时间
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date1=sdf.parse(time1);
        Date date2=sdf.parse(time2);
        int day=0;
        day=(int) (date2.getTime()-date1.getTime());
        day=day/(3600*1000);
        return day;
    }
    public static Date getNow() {  //活动开始时间-活动截止时间

        return new Date();
    }
}
