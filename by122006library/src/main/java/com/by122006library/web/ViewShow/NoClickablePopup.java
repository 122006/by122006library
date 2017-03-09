package com.by122006library.web.ViewShow;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.by122006library.item.ColorStyle;
import com.by122006library.View.CustomPopWindow;
import com.by122006library.R;

/**
 * Created by admin on 2016/12/20.
 * <p>
 * 不会响应触摸状态的覆盖提示类
 */

public class NoClickablePopup implements com.by122006library.web.ViewShow.ViewShow {
    @Nullable
    CustomPopWindow popup;
    @Nullable
    private View v;
    private View mloading_v;
    private View merror_v;
    private Context context;

    NoClickablePopup(Activity act) {
        if (act != null) v = act.getWindow().getDecorView();
        context = v.getContext();
    }

    NoClickablePopup(@NonNull View v) {
        this.v = v;
        context = v.getContext();
    }

    public View getMerror_v() {
        return merror_v;
    }

    public void setMerror_v(View merror_v) {
        this.merror_v = merror_v;
    }

    public View getMloading_v() {
        return mloading_v;
    }

    public void setMloading_v(View mloading_v) {
        this.mloading_v = mloading_v;
    }

    @Override
    public void showLoading(String str, @Nullable ColorStyle style) {
        if(style==null) style=ColorStyle.getInitData();
        if (v == null) return;
        if (!v.isShown()) return;
        View showv = null;
        if (mloading_v != null)
            showv = LayoutInflater.from(context).inflate(R.layout.popup_web_default_loadingview, null);
        else showv = mloading_v;
        if (showv instanceof ViewGroup) {
            TextView tv = (TextView) ((ViewGroup) showv).findViewWithTag("msg");
            if (tv != null) tv.setText(str);
        }
        popup = new CustomPopWindow.PopupWindowBuilder(context)
                .setView(showv)//显示的布局，还可以通过设置一个View
                //     .size(600,400) //设置显示的大小，不设置就默认包裹内容
                .setFocusable(true)//是否获取焦点，默认为ture
                .setOutsideTouchable(false)//是否PopupWindow 以外触摸dissmiss
                .create()//创建PopupWindow
                .showAtLocation(v, Gravity.CENTER, 0, 0);
    }

    /**
     * 展示出错提示
     *
     * @param error
     * @param style
     */
    @Deprecated
    @Override
    public void showError(String error, ColorStyle style) {
        dismiss();
//        if (v == null) return;
//        if (!v.isShown()) return;
//        popup = new CustomPopWindow.PopupWindowBuilder(context)
//                .setView(merror_v == null ? null : merror_v)//显示的布局，还可以通过设置一个View
//                //     .size(600,400) //设置显示的大小，不设置就默认包裹内容
//                .setFocusable(true)//是否获取焦点，默认为ture
//                .setOutsideTouchable(true)//是否PopupWindow 以外触摸dissmiss
//                .create()//创建PopupWindow
//                .showAsDropDown(v, 0, 10);

//        CustomDialog.Builder dialog=new CustomDialog.Builder();
//                dialog.setMessage(error).setTitle("网络出错")
    }

    @Override
    public void dismiss() {
        if (popup != null) popup.dismiss();
    }
}
