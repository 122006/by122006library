package com.by122006.modelprojectby122006;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.by122006library.Utils.ViewUtils;
import com.by122006library.View.PullDownTopbarMenu;
import com.by122006library.View.SlideSpinner;

import java.util.ArrayList;

/**
 * Created by admin on 2017/6/13.
 */

public class SV_PullDownTopbarMenu extends SV {
    String title = "下拉型Topbar菜单";
    int bgColor = 0x7f0a0071;

    boolean FLAG_ACT_FULLSCREEN = false;
    boolean FLAG_ACT_NO_TITLE = false;
    PullDownTopbarMenu pullDownTopbarMenu;
    TextView topView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getTopBar().setTitle(getFunctionEnName());

    }

    @Override
    public View putLayoutSpace(ViewGroup layout) {
        topView = new TextView(this);
        topView.setWidth(800);
        topView.setText("模拟的TopBar等任意控件");
        topView.setBackgroundColor(Color.BLACK);
        topView.setTextColor(Color.WHITE);
        PullDownTopbarMenu.Builder builder = new PullDownTopbarMenu.Builder(this);
        TextView textView2 = new TextView(this);
        textView2.setText("这里是菜单内容，可以在此显示任意自定义布局。\neg 多个CardView进行筛选、附属功能、详情界面");
        textView2.setGravity(Gravity.CENTER);
        textView2.setTextSize(20);
        builder.setContextLayout(textView2);
        builder.setPullSpeed(300);
        builder.setTopView(topView);
        topView.setClickable(true);
        topView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pullDownTopbarMenu.scrollToSide(300);
            }
        });

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(-2, -2);
        params.gravity = Gravity.CENTER;
        topView.setLayoutParams(params);

        layout.addView(topView);
        pullDownTopbarMenu = builder.build().show();

        return pullDownTopbarMenu.getContentView();
    }

    @Override
    public ArrayList<SMethod> specialMethods() {
        ArrayList<SMethod> list = new ArrayList<>();
        SMethod method = SMethod.create(pullDownTopbarMenu, "scrollToSide", long.class);
        method.setDescrite("滑动切换");
        method.setInitParams(300);
        list.add(method);
        SMethod method2 = SMethod.create(pullDownTopbarMenu, "scrollToClose", long.class);
        method2.setDescrite("滑动关闭");
        method2.setInitParams(300);
        list.add(method2);
        SMethod method3 = SMethod.create(pullDownTopbarMenu, "scrollToOpen", long.class);
        method3.setDescrite("滑动打开");
        method3.setInitParams(300);
        list.add(method3);
        SMethod method4 = SMethod.create(pullDownTopbarMenu.getBuilder(), "useSliderView", boolean.class);
        method4.setDescrite("设置是否使用滑动条");
        method4.setInitParams(true);
        list.add(method4);
        SMethod method5 = SMethod.create(pullDownTopbarMenu.getBuilder(), "build");
        method5.setDescrite("以当前Build构建，或者更新数据");
        list.add(method5);
        return list;
    }

    @Override
    public void countViews(ArrayList<View> views) {
        views.clear();
        ArrayList<View> views2 = new ArrayList<View>();
        ViewUtils.countViews(views2, binding.space);
        ArrayList<View> views3 = new ArrayList<View>();
        if (pullDownTopbarMenu != null) ViewUtils.countViews(views3, (ViewGroup) pullDownTopbarMenu.getContentView());
        views.addAll(views2);
        views.addAll(views3);
    }

    @Override
    public String getFunctionEnName() {
        return "PullDownTopbarMenu";
    }

    @Override
    public String getFunctionChName() {
        return title;
    }

    @Override
    public String[] getFuncitonTags() {
        return new String[]{"原创", "下拉", "悬浮窗", "链式", "美观"};
    }

    @Override
    public String getFuncitonDescribe() {
        return "显示一个附属于TopBar等控件的下拉列表，用于显示一些信息或者界面选项";
    }

    @Override
    public void clickItem() {
        startThisActivity(optContext());
    }

    @Override
    public Object getFunctionObject() {
        return pullDownTopbarMenu;
    }

    public class MyOnItemSelectedListener implements SlideSpinner.OnItemSelectedListener {
        @Override
        public void onItemSelected(SlideSpinner slideSpinner, TextView textView, String[] data, String
                chooseStr, int chooseIndex) {
            textView.setTextColor(chooseIndex == 0 ? Color.BLACK : Color.WHITE);
            textView.setBackgroundColor(chooseIndex == 0 ? Color.WHITE : 0xffff5722);
            textView.setText(chooseStr);

//            saveViewBitmap(spinner.getContentView());

        }

        @Override
        public void onNoChoose(SlideSpinner slideSpinner, TextView textView, String[] data) {
            textView.setText("");
        }

    }
}
