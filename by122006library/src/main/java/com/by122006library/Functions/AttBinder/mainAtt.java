package com.by122006library.Functions.AttBinder;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.CallSuper;
import android.support.annotation.FloatRange;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.by122006library.Functions.mLog;
import com.by122006library.Utils.RunLogicUtils;

/**
 * Created by admin on 2017/5/15.
 */

public abstract class MainAtt extends Att {

    /**
     * Max\min的数据顺序会被自动纠正和忽略。<br> 如果该属性是反向变化,你需要在transform(per)方法里返回1-per(当然，你也可以调用setReverse()方法)<br>
     *     在这里，Listener也会被初始化。
     *
     * @param max 最大范围， >=max的事件不会被运行
     * @param min 最小范围， &lt;min的事件不会被运行<br>
     */
    public MainAtt(double max, double min) {
        super(max, min);
    }

    @Override
    public double measureAtt() {
        return 0;
    }

    @Override
    public MainAtt setAttNum(double num) {
        return this;
    }

    @Override
    public double transform(@FloatRange(from = 0, to = 1) double per) {
        return per;
    }

    @Override
    public abstract void setOutSideListener() ;

    @Override
    public void beBinded()  {

    }
}
