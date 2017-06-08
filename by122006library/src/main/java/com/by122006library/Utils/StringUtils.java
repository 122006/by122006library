package com.by122006library.Utils;

import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by admin on 2017/2/21.
 */

public class StringUtils {
    /**
     * 将字符串转成MD5值(默认为UFT-8编码)
     *
     * @param string
     * @return
     */
    public static String stringToMD5(String string) {
        byte[] hash;

        try {
            hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }

        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10)
                hex.append("0");
            hex.append(Integer.toHexString(b & 0xFF));
        }

        return hex.toString();
    }
    /**
     * 大额int数字转为String
     *
     * @return 万亿为单位的String
     */
    public static String getLargerNumString(int money) {
        String a = null;
        if (money > 9999 && money < 99999999) {
            a = money / 10000 + "万";
        }
        if (money > 99999999) {
            a = money / 100000000 + "亿";
        }
        if (money < 9999) {
            a = money + "";
        }
        return a;
    }

    public static String getStringFromArray(Object[] array){
        String s="";
        for (Object o:array){
            String str=o==null?"null":o.toString();
            str+=","+(str.length()>10?"\n":"");}
        return s;
    }
    public static int[] getWH(String str, @Nullable Paint paint) {
        Rect rect = new Rect();
        if (paint == null) {
            paint = new Paint();
        }
        paint.getTextBounds(str, 0, str.length(), rect);
        return new int[]{rect.width(), rect.height()};
    }

    private static boolean isNumeric(String tag, int startIndex) {
        int length = tag.length();
        if (length == startIndex) {
            return false; // no numerals
        }
        for (int i = startIndex; i < length; i++) {
            if (!Character.isDigit(tag.charAt(i))) {
                return false;
            }
        }
        return true;
    }
    /**
     * @param regex
     * 正则表达式字符串
     * @param str
     * 要匹配的字符串
     * @return 如果str 符合 regex的正则表达式格式,返回true, 否则返回 false;
     */
    public static boolean match(String regex, String str) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }
    /**
     * 正则部分匹配
     *
     * @param regex
     * 正则表达式字符串
     * @param str
     * 要匹配的字符串
     * @return 如果str 符合 regex的正则表达式格式,返回true, 否则返回 false;
     */
    public static boolean matchPart(String regex, String str) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        return matcher.find();
    }

    public static String toLowerCaseFirstOne(String s) {
        if (Character.isLowerCase(s.charAt(0)))
            return s;
        else
            return (new StringBuilder()).append(Character.toLowerCase(s.charAt(0))).append(s.substring(1)).toString();
    }

    //首字母转大写
    public static String toUpperCaseFirstOne(String s) {
        if (Character.isUpperCase(s.charAt(0)))
            return s;
        else
            return (new StringBuilder()).append(Character.toUpperCase(s.charAt(0))).append(s.substring(1)).toString();
    }
}
