package com.by122006library.Utils;

/**
 * Created by admin on 2017/4/1.
 */

public class TimeUtils {
    /**
     * 计算某一时间距离当前时间的长度
     *
     * @param t
     * @return
     */
    public static String getStandardDate(long t) {

        long time = System.currentTimeMillis() - t;
        String str= getDiffTimeString(time);
        if (!str.equals("即将")) {
            str+="前";
        }else{
            str=str.replace("即将","刚刚");
        }
        return str;
    }

    /**
     *
     * 将时间间隔转化为String
     *
     * @param time
     * @return
     */
    public static String getDiffTimeString(long time) {
        StringBuffer sb = new StringBuffer();
        long mill = (long) Math.ceil(time / 1000);//秒前

        long minute = (long) Math.ceil(time / 60 / 1000.0f);// 分钟前

        long hour = (long) Math.ceil(time / 60 / 60 / 1000.0f);// 小时

        long day = (long) Math.ceil(time / 24 / 60 / 60 / 1000.0f);// 天前

        if (day - 1 > 0) {
            sb.append(day + "天");
        } else if (hour - 1 > 0) {
            if (hour >= 24) {
                sb.append("1天");
            } else {
                sb.append(hour - 1 + "小时");
            }
        } else if (minute - 1 > 0) {
            if (minute == 60) {
                sb.append("1小时");
            } else {
                sb.append(minute - 1 + "分钟");
            }
        } else if (mill - 1 > 0) {
            if (mill == 60) {
                sb.append("1分钟");
            } else {
                sb.append(mill - 1 + "秒");
            }
        } else {
            sb.append("即将");
        }

        return sb.toString();
    }
}
