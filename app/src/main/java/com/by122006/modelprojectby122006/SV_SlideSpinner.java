package com.by122006.modelprojectby122006;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.by122006library.Functions.mLog;
import com.by122006library.Utils.ViewUtils;
import com.by122006library.View.SlideSpinner;

import java.util.ArrayList;

/**
 * Created by admin on 2017/6/13.
 */

public class SV_SlideSpinner extends SV {
    String title = "平滑下拉";
    int bgColor = 0x7f0a0071;

    boolean FLAG_ACT_FULLSCREEN = false;
    boolean FLAG_ACT_NO_TITLE = false;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }
    SlideSpinner spinner;

    @Override
    public View putLayoutSpace(ViewGroup layout) {
        CardView cardView = new CardView(this);
        cardView.setContentPadding(30,0,30,0);
        cardView.setCardElevation(5);
        textView=new TextView(this);
        textView.setTextColor(Color.BLUE);
        textView.setWidth(500);
        textView.setText("待选择的选项");
        textView.setTextSize(20);
        cardView.addView(textView);
        String strs[]=new String[]{"待选择的选项","选项1","选项2","选项3"};
        spinner=SlideSpinner.ClickBy(textView).setEveryTime(500).setLinearColor
                (0xffe65100, 0xfffb8c00).setData(strs).setTextView(textView, 0)
                .setFistItemBgColor(0xff757575)
                .setUseCardView(false)
                .setSelectedListener(new MyOnItemSelectedListener());

        FrameLayout.LayoutParams params=new FrameLayout.LayoutParams(-2, -2);
        params.gravity= Gravity.CENTER;
        cardView.setLayoutParams(params);
        layout.addView(cardView);
        return cardView;
    }

    @Override
    public ArrayList<SMethod> specialMethods() {
        ArrayList<SMethod> list = new ArrayList<>();
        SMethod sMethod=SMethod.create(spinner,"setTextView",View.class,int.class);
        sMethod.setInitParams(textView,0);
        sMethod.setDescrite("设置附着TextView与默认选项");
        list.add(sMethod);
        sMethod=SMethod.create(spinner,"setEveryTime",long.class);
        sMethod.setInitParams(200);
        sMethod.setDescrite("设置控件变换时间");
        list.add(sMethod);
        sMethod=SMethod.create(spinner,"setFistItemBgColor",int.class);
        sMethod.setInitParams(Color.RED);
        sMethod.setDescrite("设置首选项颜色");
        list.add(sMethod);
        sMethod=SMethod.create(spinner,"setLinearColor",int.class,int.class);
        sMethod.setInitParams(Color.RED, Color.BLUE);
        sMethod.setDescrite("设置内容颜色为渐变色");
        list.add(sMethod);
        sMethod=SMethod.create(spinner,"showDropDown",View.class);
        sMethod.setInitParams(textView);
        sMethod.setDescrite("在某控件下显示列表");
        list.add(sMethod);
        sMethod=SMethod.create(spinner,"setWidth",int.class);
        sMethod.setInitParams(-2);
        sMethod.setDescrite("设置宽度");
        list.add(sMethod);
        sMethod=SMethod.create(spinner,"setItemHeight",int.class);
        sMethod.setInitParams(100);
        sMethod.setDescrite("设置单项高度");
        list.add(sMethod);
        sMethod=SMethod.create(spinner,"select",int.class);
        sMethod.setInitParams(0);
        sMethod.setDescrite("手选某控件");
        list.add(sMethod);
        return list;
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

    @Override
    public Object getFunctionObject() {
        return spinner;
    }

    @Override
    public void countViews(ArrayList<View> views){
        views.clear();
        ArrayList<View> views2=new ArrayList<View>();
        ViewUtils.countViews(views2,binding.space);
        ArrayList<View> views3=new ArrayList<View>();
        if(spinner!=null)ViewUtils.countViews(views3, (ViewGroup) spinner.getContentView());
        views.addAll(views2);
        views.addAll(views3);
    }


    @Override
    public String getFunctionEnName() {
        return "SlideSpinner";
    }

    @Override
    public String getFunctionChName() {
        return title;
    }

    @Override
    public String[] getFuncitonTags() {
        return new String[]{"原创", "链式", "常用", "高度自定义", "美观"};
    }

    @Override
    public String getFuncitonDescribe() {
        return "一个下拉控件，支持高度自定义其颜色大小首选功能等属性";
    }

    @Override
    public void clickItem() {
        startThisActivity(optContext());
    }
}
