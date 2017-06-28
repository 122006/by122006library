package com.by122006.modelprojectby122006;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.by122006library.Functions.ViewsReplace;
import com.by122006library.Functions.mLog;
import com.by122006library.Utils.ViewUtils;

import java.util.ArrayList;

/**
 * Created by admin on 2017/6/13.
 */

public class SV_ViewsReplace extends SV {
    String title = "控件替换";
    int bgColor = 0x7f0a0071;

    boolean FLAG_ACT_FULLSCREEN = false;
    boolean FLAG_ACT_NO_TITLE = false;
    private TextView textView, textView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View putLayoutSpace(final ViewGroup layout) {
        textView = new TextView(this);
        textView.setTextColor(Color.BLUE);
        textView.setText("显示的第一个TextView");
        textView.setTextSize(20);
        textView2 = new TextView(this);
        textView2.setText("渐隐覆盖替换对象");
        textView2.setTextColor(Color.RED);
        textView2.setTextSize(20);
        layout.addView(textView);
        layout.post(new Runnable() {
            @Override
            public void run() {
                ViewUtils.smoothReplace(textView, textView2, 200);
            }
        });
        layout.setClickable(true);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLog.i("textView2.getParent()=" + textView2.getParent());
                mLog.i("textView1.getParent()=" + textView.getParent());
//                new DelayTask(100) {
//                    @Override
//                    public void doCycleAction(int haveCycleCount) throws MyException {
//                        if(haveCycleCount==0) saveViewBitmap(layout);
//                    }
//                }.register(this);
                ViewUtils.smoothReplace_Smart(textView, textView2, 200);

            }
        });
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(-2, -2);
        params.gravity = Gravity.CENTER;
        textView.setLayoutParams(params);
        textView2.setLayoutParams(params);
        return textView;
    }

    @Override
    public ArrayList<SMethod> specialMethods() {
        ArrayList<SMethod> list = new ArrayList<>();
        SMethod method = SMethod.create(ViewUtils.class, "smoothReplace_Smart", View.class, View.class, long.class);
        method.setInitParams(textView, textView2, 200);
        method.setDescrite("智能切换两个控件的渐隐");
        list.add(method);
        method = SMethod.create(ViewUtils.class, "smoothReplace", View.class, View.class, long.class);
        method.setInitParams(textView, textView2, 200);
        method.setDescrite("控件1渐隐切换至控件2");
        list.add(method);
        method = SMethod.create(ViewUtils.class, "smoothReplace", View.class, View.class, long.class);
        method.setInitParams(textView2, textView, 200);
        method.setDescrite("控件2渐隐切换至控件1");
        list.add(method);

        method = SMethod.debug();
        method.setDescrite("重置控件1控件2");
        method.setListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewGroup group=getViewGroup();
                group.removeAllViews();
                putLayoutSpace(getViewGroup());
                showSpecialMethods();
            }
        });
        list.add(method);


        return list;
    }

    @Override
    public Object getFunctionObject() {
        return ViewsReplace.class;
    }

    @Override
    public String getFunctionEnName() {
        return "smoothReplace()";
    }

    @Override
    public String getFunctionChName() {
        return "平滑替换";
    }

    @Override
    public String[] getFuncitonTags() {
        return new String[]{"原创", "小工具", "常用", "一行代码", "框架"};
    }

    @Override
    public String getFuncitonDescribe() {
        return "一句代码实现两个控件的渐隐替换，替换之后新的控件会保存原有的位置信息";
    }

    @Override
    public void clickItem() {
        startThisActivity(optContext());
    }

    @Override
    public void countViews(ArrayList<View> views) {
        views.clear();
        views.add(textView);
        views.add(textView2);
    }
}
