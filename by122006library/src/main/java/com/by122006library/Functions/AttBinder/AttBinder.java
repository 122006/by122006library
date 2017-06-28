package com.by122006library.Functions.AttBinder;

import android.support.annotation.FloatRange;

import com.by122006library.Functions.CycleTask.CycleTask;
import com.by122006library.Functions.mLog;
import com.by122006library.Utils.RunLogicUtils;

import java.util.ArrayList;

/**
 * 主类 用于存放其绑定的属性值 Created by admin on 2017/5/15.
 */

public class AttBinder {
    ArrayList<Att> atts = new ArrayList<>();
    Att nowChangingAtt = null;
    long protectChangedTime = 0;
    private AttProgressListener attProgressListener;
    public boolean logProgress = false;

    public double getCachePer() {
        return cachePer;
    }

    public void setCachePer(double cachePer) {
        this.cachePer = cachePer;
    }

    double cachePer=0;

    public AttBinder startWithDuringTime(long time){
        TimeAtt timeAtt=new TimeAtt(time);
        bind(timeAtt);
        return this;
    }

    public AttBinder bind(Att... atts2) {
        for (Att att : atts2) {
            atts.add(att);
            att.addBinder(this);
            att.beBinded();
        }
        return this;

    }
    int minRefreshTime=16;

    /**
     * 设置刷新的最小间隔时间(ms)重复操作将会被抛出
     * <br>但是你可以通过fluctuation进行强制更新，为0/1时也会无视该规则
     * @param time
     * @return
     */
    public AttBinder setMinRefreshTime(int time){
        minRefreshTime=time;
        return this;
    }
    private long lastChangeTime;
    /**
     * 这里是数据修改的入口方法 (但是不会修改本身)
     */
    protected void change(Att from, @FloatRange(from = 0, to = 1) double per) {
        double beforePer=getCachePer();
        setCachePer(per);
        if (logProgress) mLog.i("========当前总进度：" + per * 100);
        if (nowChangingAtt != from) {
            if (System.currentTimeMillis() < protectChangedTime) return;
        } else {
            protectChangedTime = System.currentTimeMillis() + 3;
        }
        if(System.currentTimeMillis()-lastChangeTime<minRefreshTime&&per!=1&&per!=0){ return;}
        lastChangeTime=System.currentTimeMillis();
        for (Att att : (ArrayList<Att>) atts.clone()) {
            double before = att.attNum;
            double p=0;
            if (att != from) {
                if (per > 1) per = 1;
                if (per < 0) per = 0;
                p = att.transform(per);
                if (att.reverse) p = 1f - p;
                if (p == att.getPer()) continue;
                if (att.logProgress) mLog.i(String.format("%s 属性值 = %f", att.tag, p));
                att.setPer(p);
            }
            if (att.attProgressListener != null) {
                att.attProgressListener.progress(before, att.attNum, p);
            }
        }
        if (attProgressListener != null) attProgressListener.progress(beforePer,per,per);
    }

    /**
     * 发生数据修改(强制更新所有的数据，不推荐，请使用单独属性的监听器)
     *
     * @param per 当前数据百分比（0-1）
     */
    public void fluctuation(double per) {
        double beforePer=getCachePer();
        setCachePer(per);
        if(logProgress)mLog.i("=========强制更新数据！  per=%f",per);
        for (Att att : (ArrayList<Att>) atts.clone()) {
//            if (per == 0 || per == 1) return;
            if (per > 1&&att.useMaxBorder()) per = 1;
            if (per < 0&&att.useMinBorder()) per = 0;
            double p = att.transform(per);
            if (att.reverse) p = 1f - p;
            att.setPer(p);
            double before = att.attNum;
            if (att.logProgress) mLog.i(String.format("%s 属性值 = %f", att.tag, p));
            att.attNum = att.min + p * (att.max - att.min);
            if (att.attProgressListener != null) {
                att.attProgressListener.progress(before, att.attNum, p);
            }
        }
        if (attProgressListener != null) attProgressListener.progress(beforePer,per,per);
    }

    public void destroy() {
        for (Att att : (ArrayList<Att>) atts.clone()) {
            att.remove();
        }
        CycleTask.unRegister(this);
    }

    /**
     * 设定进度监听器
     *
     * @param attProgressListener
     */
    public void setAttProgressListener(AttProgressListener attProgressListener) {
        this.attProgressListener = attProgressListener;
    }

    /**
     * 打开log显示
     */
    public void logProgress() {
        logProgress = true;
    }

    public TimeAtt getTimeAtt(){
        for (Att att:atts){
            if (att instanceof TimeAtt) return (TimeAtt) att;
        }
        return null;
    }

}
