package com.by122006library.Functions.AttBinder;

import android.support.annotation.CallSuper;

import com.by122006library.Utils.RunLogicUtils;

import java.util.ArrayList;

/**
 * Created by admin on 2017/5/15.
 */

public abstract class Att {
    ArrayList<AttBinder> binders = new ArrayList<>();
    double attNum;
    double max, min;


    /**
     * @param max 最大范围， >=max的事件不会被运行
     * @param min 最小范围， <min的事件不会被运行
     */
    public Att(double max, double min) {
        this.max = Math.max(max, min);
        this.min = Math.min(max, min);
        setListener();
    }

    protected void addBinder(AttBinder binder) {
        binders.add(binder);
    }

    /**
     * 测量该att的数据
     *
     * @return
     */
    public abstract double measureAtt();

    /**
     * 返回缓存中的该项属性
     */
    public double getAttNum() {
        return attNum;
    }

    /**
     * 设置该项属性
     *
     * @param num 需要设置的属性
     */
    public abstract void setAttNum(double num);

    /**
     * 返回缓存中的该项属性
     */
    public double getPer() {
        return (attNum - min) / (max - min);
    }

    public void setPer(double per) {
        setAttNum(per * (max - min) + min);
    }

    /**
     * 对百分比数据值进行特征转化，默认返回per即可
     *
     * @param per 原始的百分比
     * @return 转化后的百分比
     */
    public abstract double transform(double per);

    /**
     * 设定属性变化的监听器<p>你需要在监听器的最后调用fluctuation(num)方法</>
     */
    public abstract void setListener();

    /**
     * 发生数据修改，默认方法测量属性并缓存，发送至AttBinder
     */
    public void fluctuation() {
        measureAtt();
        fluctuation(getAttNum());
    }

    /**
     * 发生数据修改，将监听时获取的数据缓存，发送至AttBinder
     */
    public void fluctuation(double num) {
        attNum = num;
        if (num < min || num >= max) {
            if (RunLogicUtils.getHereRunTimes(500) <= 1) {
                if(num < min &&useMinBorder()) num=min;else
                if(num >= max &&useMaxBorder()) num=max;else
                return;
            } else
                return;
        }
        for (AttBinder binder : binders) {
            binder.change(this, (num - min) / (max - min));
        }
    }

    public boolean useMaxBorder() {
        return true;
    }

    public boolean useMinBorder() {
        return false;
    }

    @CallSuper
    public void remove(){
        for(AttBinder binder:(ArrayList<AttBinder>)binders){
            binder.atts.remove(this);
        }
    }

}
