package com.by122006library.Functions.AttBinder;

import android.support.annotation.CallSuper;
import android.support.annotation.FloatRange;

import com.by122006library.Functions.mLog;
import com.by122006library.Utils.RunLogicUtils;

import java.util.ArrayList;

/**
 * Created by 122006 on 2017/5/15.
 */

public abstract class Att {
    protected AttProgressListener attProgressListener;
    protected boolean logProgress = false;
    public ArrayList<AttBinder> binders = new ArrayList<>();
    double attNum;

    public double getMax() {
        return max;
    }

    public void setMax(double max) {
        this.max = max;
    }

    public double getMin() {
        return min;
    }

    public void setMin(double min) {
        this.min = min;
    }

    public double max;
    public double min;
    String tag = "";
    /**
     * 倒序标志
     */
    protected boolean reverse = false;

    /**
     * Max\min的数据顺序会被自动纠正和忽略。<br> 如果该属性是反向变化,你需要在transform(per)方法里返回1-per(当然，你也可以调用setReverse()方法)<br> 在这里，Listener也会被初始化。
     *
     * @param max 最大范围， >=max的事件不会被运行
     * @param min 最小范围， &lt;min的事件不会被运行<br>
     */
    public Att(double max, double min) {
        this.max = Math.max(max, min);
        this.min = Math.min(max, min);
        setOutSideListener();
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
    public abstract Att setAttNum(double num);

    /**
     * 返回缓存中的该项属性
     */
    public double getPer() {
        double per=(attNum - getMin()) / (getMax() - getMin());
        if(per>1&&useMaxBorder()) per=1;
        if(per<0&&useMinBorder()) per=0;
        return per;
    }

    public void setPer(double per) {
        if(per>1&&useMaxBorder()) per=1;
        if(per<0&&useMinBorder()) per=0;
        setAttNum(per * (getMax() - getMin()) + getMin());
    }

    /**
     * 对百分比数据值进行特征转化，默认返回per即可<br> per范围为0-1
     *
     * @param per 原始的百分比
     * @return 转化后的百分比
     */
    public abstract double transform(@FloatRange(from = 0, to = 1) double per);

    /**
     * 设定外部监听器，该监听器应该能够对控件值进行修改<p>你需要在监听器的最后调用fluctuation(num)方法</>
     */
    public abstract void setOutSideListener();

    /**
     * 发生数据修改，默认方法测量属性并缓存，发送至AttBinder
     */
    public void fluctuation() {
        measureAtt();
        fluctuation(getAttNum());
    }

    /**
     * 发生数据修改，将监听时获取的数据缓存，发送至AttBinder
     * <p></>
     * 和Binder中方法不同，这里传入的是实际数据
     */
    public void fluctuation(double num) {
//        mLog.i("fluctuation=%f",getPer());
        attNum = num;
        if (num < getMin() || num >= getMax()) {
            if (RunLogicUtils.getHereRunTimes(500) <= 1) {
                if (num < getMin() && useMinBorder()) num = getMin();
                else if (num >= getMax() && useMaxBorder()) num = getMax();
                else
                    return;
            } else
                return;
        }
        for (AttBinder binder : binders) {
            binder.change(this, (num - getMin()) / (getMax() - getMin()));
        }
    }

    /**
     * 子类需要重写该方法<p> 以确定是否在超过Max越界时使用Max界限数据而不是直接抛弃
     */
    public boolean useMaxBorder() {
        return border;
    }

    public boolean isBorder() {
        return border;
    }

    public Att setBorder(boolean border) {
        this.border = border;
        return this;
    }

    boolean border=true;

    /**
     * 子类需要重写该方法<p> 以确定是否在超过Min越界时使用Min界限数据而不是直接抛弃
     */
    public boolean useMinBorder() {
        return border;
    }

    @CallSuper
    public void remove() {
        for (AttBinder binder : (ArrayList<AttBinder>) binders) {
            binder.atts.remove(this);
        }
    }

    /**
     * 设定进度监听器
     *
     * @param attProgressListener
     */
    public Att setAttProgressListener(AttProgressListener attProgressListener) {
        this.attProgressListener = attProgressListener;
        return this;
    }

    /**
     * 打开log显示
     */
    public Att logProgress(String tag) {
        logProgress = true;
        this.tag = tag;
        return this;
    }

    /**
     * 倒序
     */
    public Att setReverse() {
        reverse = true;
        return this;
    }

    @CallSuper
    public void  beBinded(){
//        measureAtt();
    };

}
