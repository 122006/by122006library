package com.by122006library.Functions.AttBinder;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.by122006library.Functions.mLog;
import com.by122006library.Utils.RunLogicUtils;

/**
 * Created by admin on 2017/5/15.
 */

public class ViewAtt extends Att {
    AttStyle attStyle = AttStyle.Other;
    View view;

    public ViewAtt(View v, int max, int min) {
        super(max, min);
        if(v==null) mLog.e("传入参数v为null值");
        this.view = v;
    }

    public ViewAtt(View v, AttStyle attStyle, int max, int min) {
        super(max, min);
        if(v==null) mLog.e("传入参数v为null值");
        this.view = v;
        this.attStyle = attStyle;
    }

    @Override
    public double measureAtt() {
        if(view==null) return -1;
        switch (attStyle) {
            case Other:
                return -1;
            case Height:
                return view.getHeight();
            case Width:
                return view.getWidth();
            case Alpha:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    return view.getAlpha();
                } else return view.getBackground().getAlpha();
            case Left:
                return view.getLeft();
            case Right:
                return view.getRight();
            case Top:
                return view.getTop();
            case Bottom:
                return view.getBottom();
            case ScrollX:
                return view.getScrollX();
            case ScrollY:
                return view.getScrollY();
        }

        return -1;
    }

    @Override
    public void setAttNum(double num) {
        if(view==null) return;
        ViewGroup.LayoutParams params = view.getLayoutParams();

        switch (attStyle) {
            case Other:
                break;
            case Height:
                if (params == null) return;
                params.height = (int) num;
                view.setLayoutParams(params);
                break;
            case Width:
                if (params == null) return;
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
            case Left:
                if (params == null) return;
                if (!(params instanceof LinearLayout.MarginLayoutParams)) {
                    if (RunLogicUtils.getHereRunTimes(500) < 2)
                        mLog.e(String.format("动画控件所在父控件不支持'%s'属性", attStyle.name()));
                    return;
                }
                ((LinearLayout.MarginLayoutParams) params).leftMargin = (int) num;
                break;
            case Right:
                if (params == null) return;
                if (!(params instanceof LinearLayout.MarginLayoutParams)) {
                    if (RunLogicUtils.getHereRunTimes(500) < 2)
                        mLog.e(String.format("动画控件所在父控件不支持'%s'属性", attStyle.name()));
                    return;
                }
                ((LinearLayout.MarginLayoutParams) params).rightMargin = (int) num;
                break;
            case Top:
                if (params == null) return;
                if (!(params instanceof LinearLayout.MarginLayoutParams)) {
                    if (RunLogicUtils.getHereRunTimes(500) < 2)
                        mLog.e(String.format("动画控件所在父控件不支持'%s'属性", attStyle.name()));
                    return;
                }
                ((LinearLayout.MarginLayoutParams) params).topMargin = (int) num;
                break;
            case Bottom:
                if (params == null) return;
                if (!(params instanceof LinearLayout.MarginLayoutParams)) {
                    if (RunLogicUtils.getHereRunTimes(500) < 2)
                        mLog.e(String.format("动画控件所在父控件不支持'%s'属性", attStyle.name()));
                    return;
                }
                ((LinearLayout.MarginLayoutParams) params).bottomMargin = (int) num;
                break;
            case ScrollX:
                view.scrollTo((int) num, view.getScrollY());
                break;
            case ScrollY:
                view.scrollTo(view.getScrollX(),(int) num );
                break;
        }
    }

    @Override
    public double transform(double per) {
        return per;
    }

    @Override
    public void setListener() {

    }

    public enum AttStyle {
        Other, Height, Width, Alpha, Left, Right, Top, Bottom, ScrollX, ScrollY
    }

    public void remove(){
        super.remove();
        view=null;
    }

}