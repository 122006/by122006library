package com.by122006library.Functions.AttBinder;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.CallSuper;
import android.support.annotation.FloatRange;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.by122006library.Functions.SmartRun;
import com.by122006library.Functions.mLog;
import com.by122006library.Interface.UIThread;
import com.by122006library.Utils.RunLogicUtils;
import com.by122006library.Utils.ThreadUtils;

/**
 * Created by admin on 2017/5/15.
 */

public class ViewAtt extends Att {
    AttStyle attStyle = AttStyle.Other;
    public View view;

    public ViewAtt(View v, double max, double min) {
        super(max, min);
        if(v==null) mLog.e("传入参数v为null值");
        this.view = v;
    }

    public ViewAtt(View v, AttStyle attStyle, double max, double min) {
        super(max, min);
        if(v==null) mLog.e("传入参数v为null值");
        this.view = v;
        this.attStyle = attStyle;
//        measureAtt();
//        setAttNum(attNum);
    }
    protected double attNum =-1;
    @Override
    public double measureAtt() {
        if(view==null) return -1;
        switch (attStyle) {
            case Other:
                attNum = -1;
                break;
            case Height:
                attNum = view.getHeight();
                break;
            case Width:
                attNum = view.getWidth();
                break;
            case Alpha:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    attNum = view.getAlpha();
                } else attNum = view.getBackground().getAlpha();
                break;
            case LeftMargin:
                ViewGroup.LayoutParams params = view.getLayoutParams();
                if (!(params instanceof LinearLayout.MarginLayoutParams)) {
                    if (RunLogicUtils.getHereRunTimes(500) < 2)
                        mLog.e(String.format("动画控件所在父控件不支持'%s'属性", attStyle.name()));
                    break;
                }
                attNum=((LinearLayout.MarginLayoutParams) params).leftMargin;
                break;
            case RightMargin:
                params = view.getLayoutParams();
                if (!(params instanceof LinearLayout.MarginLayoutParams)) {
                    if (RunLogicUtils.getHereRunTimes(500) < 2)
                        mLog.e(String.format("动画控件所在父控件不支持'%s'属性", attStyle.name()));
                    break;
                }
                attNum=((LinearLayout.MarginLayoutParams) params).rightMargin;
                break;
            case TopMargin:
                params = view.getLayoutParams();
                if (!(params instanceof LinearLayout.MarginLayoutParams)) {
                    if (RunLogicUtils.getHereRunTimes(500) < 2)
                        mLog.e(String.format("动画控件所在父控件不支持'%s'属性", attStyle.name()));
                    break;
                }
                attNum=((LinearLayout.MarginLayoutParams) params).topMargin;

                break;
            case BottomMargin:
                params = view.getLayoutParams();
                if (!(params instanceof LinearLayout.MarginLayoutParams)) {
                    if (RunLogicUtils.getHereRunTimes(500) < 2)
                        mLog.e(String.format("动画控件所在父控件不支持'%s'属性", attStyle.name()));
                    break;
                }
                attNum=((LinearLayout.MarginLayoutParams) params).bottomMargin;

                break;
            case Left:
                attNum = view.getLeft();
                break;
            case Right:
                attNum = view.getRight();
                break;
            case Top:
                attNum = view.getTop();
                break;
            case Bottom:
                attNum = view.getBottom();
                break;
            case ScrollX:
                attNum = view.getScrollX();
                break;
            case ScrollY:
                attNum = view.getScrollY();
                break;
            case TranslationY:
                attNum = view.getTranslationY();
                break;
            case TranslationX:
                attNum = view.getTranslationX();
                break;
        }

        return attNum;
    }

    @Override
    public ViewAtt setAttNum(double num) {
        if(!ThreadUtils.isUIThread()) return this;
        if(view==null) return this;
        ViewGroup.LayoutParams params = view.getLayoutParams();
        if(num==attNum||Math.abs(num-attNum)<=0.00001)return this;
        attNum=num;
            switch (attStyle) {
                case Other:
                    break;
                case Height:
                    if (params == null) {return this;}
                    params.height = (int) num;
                    try {
                        view.getLayoutParams().height= (int) num;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case Width:
                    if (params == null) return this;
                    params.width = (int) num;
                    view.setLayoutParams(params);
                    break;
                case Alpha:
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                        view.setAlpha((float) num);
                    } else {
                        Drawable drawable = view.getBackground();
                        drawable.setAlpha((int) (num * 255));
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            view.setBackground(drawable);
                        } else view.setBackgroundDrawable(drawable);
                    }
                    break;
                case LeftMargin:
                    if (params == null) return this;
                    if (!(params instanceof LinearLayout.MarginLayoutParams)) {
                        if (RunLogicUtils.getHereRunTimes(500) < 2)
                            mLog.e(String.format("动画控件所在父控件不支持'%s'属性", attStyle.name()));
                        return this;
                    }
                    ((LinearLayout.MarginLayoutParams) params).leftMargin = (int) num;
                    break;
                case RightMargin:
                    if (params == null) return this;
                    if (!(params instanceof LinearLayout.MarginLayoutParams)) {
                        if (RunLogicUtils.getHereRunTimes(500) < 2)
                            mLog.e(String.format("动画控件所在父控件不支持'%s'属性", attStyle.name()));
                        return this;
                    }
                    ((LinearLayout.MarginLayoutParams) params).rightMargin = (int) num;
                    break;
                case TopMargin:
                    if (params == null) return this;
                    if (!(params instanceof LinearLayout.MarginLayoutParams)) {
                        if (RunLogicUtils.getHereRunTimes(500) < 2)
                            mLog.e(String.format("动画控件所在父控件不支持'%s'属性", attStyle.name()));
                        return this;
                    }
                    ((LinearLayout.MarginLayoutParams) params).topMargin = (int) num;
                    break;
                case BottomMargin:
                    if (params == null) return this;
                    if (!(params instanceof LinearLayout.MarginLayoutParams)) {
                        if (RunLogicUtils.getHereRunTimes(500) < 2)
                            mLog.e(String.format("动画控件所在父控件不支持'%s'属性", attStyle.name()));
                        return this;
                    }
                    ((LinearLayout.MarginLayoutParams) params).bottomMargin = (int) num;
                    break;
                case Left:
                    view.setLeft((int) num);
                    break;
                case Right:
                    view.setRight((int) num);
                    break;
                case Top:
                    view.setTop((int) num);
                    break;
                case Bottom:
                    view.setBottom((int) num);
                    break;
                case ScrollX:
                    view.scrollTo((int) num, view.getScrollY());
                    break;
                case ScrollY:
                    view.scrollTo(view.getScrollX(),(int) num );
                    break;
                case TranslationX:
                    view.setTranslationX((float) num);
                    break;
                case TranslationY:
                    view.setTranslationY((float) num);
                    break;
            }
        return this;
    }

    @Override
    public double transform(@FloatRange(from=0,to=1)double per) {
        return per;
    }

    @Override
    public void setOutSideListener() {

    }

    public enum AttStyle {
        Other, Height, Width/**
* 0~1
*/
, Alpha,LeftMargin,RightMargin,TopMargin,BottomMargin, Left, Right, Top, Bottom, ScrollX, ScrollY,TranslationX,TranslationY
    }

    public void remove(){
        super.remove();
        view=null;
    }

    @Override
    public void beBinded() {
        super.beBinded();
//        measureAtt();
//        mLog.i(attStyle.toString()+"   "+attNum);
    }

}
