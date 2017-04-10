package com.by122006library.Utils;

import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Created by admin on 2017/2/16.
 */

public class MathUtils  {

    /**
     * 值域映射
     *
     * @param data 需要被定位的原始数据
     * @param dataRangeStart 原始数据域开始值
     * @param dataRangeEnd 原始数据域结束值
     * @param rangeStart 结果域开始值
     * @param rangeEnd 结果域结束值
     * @return
     */
    public static  int mappingRange(int data,int dataRangeStart,int dataRangeEnd,int rangeStart,int rangeEnd){
        double per=((double) (data-dataRangeStart))/(dataRangeEnd-dataRangeStart);
        int re= (int) (per*(rangeEnd-rangeStart)+rangeStart);
        return re;
    }

    /**
     * 获得范围内随机值
     *
     * @param rangeStart
     * @param rangeEnd
     * @return
     */
    public  static int getRandomNumInRange(int rangeStart,int rangeEnd ){
        double v = Math.random() * (rangeEnd - rangeStart) + rangeStart;
        return (int) v;
    }

    /**
     * 获得一定范围内的合法值
     *
     * @param num
     * @param minnum
     * @param maxnum
     * @return
     */
    public static int getLegalNumInRange(int num, int minnum, int maxnum) {
        return Math.max(minnum, Math.min(num, maxnum));

    }

     public static long getLegalNumInRange(int num, long minnum, long maxnum) {
        return Math.max(minnum, Math.min(num, maxnum));

    }
    public static double getLegalNumInRange(int num, double minnum, double maxnum) {
        return Math.max(minnum, Math.min(num, maxnum));

    }

    /**
     *
     * 获得最大公约数
     *
     * @param m
     * @param n
     * @return
     */
    public static int divisor(int m,int n) {
        if (m % n == 0) {
            return n;
        } else {
            return divisor(n,m % n);
        }
    }
    public static float countDistance_2(float x, float y, float f, float g) {
        return (x - f) * (x - f) + (y - g) * (y - g);
    }

    /**
     *
     * 判断一个概率为per的时间是否会发生
     * </p></>以per为参数（0-100），<per 返回true视为发生。
     *
     * @param per
     * @return
     */
    public static boolean randomHappened(int per){
        return Math.random()*100<per;
    }

    /**
     * 返回onum与addnum之和，保证返回的数据在maxnum和minnum之间（maxnum 和 minnum自动识别判断大小）
     *
     *
     *
     * @param onum
     * @param addnum
     * @param maxnum
     * @param minnum
     * @return
     */
    public static int safeChangeNum(int onum,int addnum,int maxnum,int minnum){
        onum+=addnum;
        if(maxnum<minnum) {
            int x=minnum;
            minnum=maxnum;
            maxnum=x;
        }
        if (onum>maxnum) return maxnum;
        if (onum<minnum) return minnum;
        return onum;
    }


    public static <T> T randomArrayData(T... data){
        return data[(int) (Math.random()*data.length)];
    }
    public static <T> T randomArrayData(ArrayList<T> data){
        return data.get((int) (Math.random()*data.size()));
    }
}
