package com.by122006library.Functions.AttBinder;

/**
 * 属性进度监听器
 * Created by admin on 2017/5/27.
 */

public interface AttProgressListener {
    /**
     * @param beforeAttNum 该次变化之前的测量数据
     * @param nowAttNum 该次变化之后的/当前的测量数据
     * @param nowAttPer 该次变化之后的/当前的百分比
     */
    void progress(double beforeAttNum,double nowAttNum,double nowAttPer);
}
