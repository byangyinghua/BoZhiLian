package cn.com.data_plus.bozhilian.util;

import android.util.Config;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TimeUtil {
    public static String getTime() {
        return new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
    }

    public static String getDate() {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
    }

    public static String getDate(long mills) {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date(mills));
    }

    static String getDateAndTime() {
        return getDateAndTime(new Date());
    }

    public static String getDateAndTime(long millis) {
        return getDateAndTime(new Date(millis));
    }

    private static String getDateAndTime(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return dateFormat.format(date);
    }

    public static long getMilliSecond(String date, String time) {
        Calendar calendar = Calendar.getInstance();
        try {
            Date dateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(date + " " + time);
            calendar.setTime(dateTime);
        } catch (ParseException e) {
            LogUtil.error("日期转换出错", e);
        }
        return calendar.getTimeInMillis();
    }

    public static long getMillitime(String time) {
        Calendar calendar = Calendar.getInstance();
        try {
            Date dateTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).parse(time);
            calendar.setTime(dateTime);
        } catch (ParseException e) {
            LogUtil.error("日期转换出错", e);
        }
        return calendar.getTimeInMillis();
    }

    public static long getMilliSecond(String date) {
        Calendar calendar = Calendar.getInstance();
        try {
            Date dateTime = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(date);
            calendar.setTime(dateTime);
        } catch (ParseException e) {
            LogUtil.error("日期转换出错", e);
        }
        return calendar.getTimeInMillis();
    }

    public static String getChineseDate() {
        return new SimpleDateFormat("EEEE\nyyyy年MM月dd日", Locale.getDefault()).format(new Date());
    }

    public static void delayExe(long seconds) {
        try {
            Thread.sleep(seconds);
        } catch (InterruptedException e) {
            LogUtil.error("延迟执行程序时出错");
        }
    }

    //判断时间是否相等
    public static boolean isSameDate(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);

        boolean isSameYear = cal1.get(Calendar.YEAR) == cal2
                .get(Calendar.YEAR);
        boolean isSameMonth = isSameYear
                && cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH);
        boolean isSameDate = isSameMonth
                && cal1.get(Calendar.DAY_OF_MONTH) == cal2
                .get(Calendar.DAY_OF_MONTH);

        return isSameDate;
    }


    /**
     * 判断当前时间是否在[startTime, endTime]区间，注意时间格式要一致
     *
     * @param nowTime   当前时间
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return
     * @author jqlin
     */
    public static boolean isEffectiveDate(Date nowTime, Date startTime, Date endTime) {
        if (nowTime.getTime() == startTime.getTime()
                || nowTime.getTime() == endTime.getTime()) {
            return true;
        }

        Calendar date = Calendar.getInstance();
        date.setTime(nowTime);

        Calendar begin = Calendar.getInstance();
        begin.setTime(startTime);

        Calendar end = Calendar.getInstance();
        end.setTime(endTime);

        if (date.after(begin) && date.before(end)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 判断某一时间是否在一个区间内
     *
     * @param sourceTime 时间区间,半闭合,如[10:00-20:00)
     * @param curTime    需要判断的时间 如10:00
     * @return
     * @throws IllegalArgumentException
     */
    public static boolean isInTime(String sourceTime, String curTime) {
        if (sourceTime == null || !sourceTime.contains("-") || !sourceTime.contains(":")) {
            throw new IllegalArgumentException("Illegal Argument arg:" + sourceTime);
        }
        if (curTime == null || !curTime.contains(":")) {
            throw new IllegalArgumentException("Illegal Argument arg:" + curTime);
        }
        String[] args = sourceTime.split("-");
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        try {
            long now = sdf.parse(curTime).getTime();
            long start = sdf.parse(args[0]).getTime();
            long end = sdf.parse(args[1]).getTime();
            if (args[1].equals("00:00")) {
                args[1] = "24:00";
            }
            if (end < start) {
                if (now >= end && now < start) {
                    return false;
                } else {
                    return true;
                }
            } else {
                if (now >= start && now < end) {
                    return true;
                } else {
                    return false;
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Illegal Argument arg:" + sourceTime);
        }

    }

}
