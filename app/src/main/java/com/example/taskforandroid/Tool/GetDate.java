package com.example.taskforandroid.Tool;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class GetDate {

    public static String time(){

        Date date = new Date();
        SimpleDateFormat dateFormat= new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(date);

    }


    public static int maxdays(String start_time,String predict_time) throws ParseException {
        //设置转换的日期格式
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        //开始时间
        Date startDate = sdf.parse(start_time);
        //结束时间
        Date endDate = sdf.parse(predict_time);

        //得到相差的天数 betweenDate
        int betweenDate = (int)(endDate.getTime() - startDate.getTime())/(60*60*24*1000);

        return betweenDate;
    }

    public static int newdays(String start_time) throws ParseException {
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        cal.setTime(sdf.parse(start_time));
        long time1 = cal.getTimeInMillis();
        cal.setTime(sdf.parse(time()));
        long time2 = cal.getTimeInMillis();
        long between_days=(time2-time1)/(1000*3600*24);

        return Integer.parseInt(String.valueOf(between_days));
    }



}
