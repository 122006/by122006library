package com.by122006.modelprojectby122006;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.by122006library.Functions.mLog;
import com.by122006library.Utils.ViewUtils;

/**
 * Created by admin on 2017/6/13.
 */

public class ViewTest extends FunctionActivity {
    String title = "控件调试器";
    int bgColor = 0x7f0a0071;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout layout = new LinearLayout(this);
        final TextView textView = new TextView(this);
        textView.setTextColor(Color.BLUE);
        textView.setText("显示的第一个TextView");
        textView.setTextSize(20);
        final TextView textView2 = new TextView(this);
        textView2.setText("渐隐覆盖替换对象");
        textView2.setTextColor(Color.RED);
        textView2.setTextSize(20);
        layout.addView(textView);
        setContentView(layout);
        layout.post(new Runnable() {
            @Override
            public void run() {
                ViewUtils.smoothReplace(textView, textView2, 200);
            }
        });
        layout.setClickable(true);
        layout.setBackgroundColor(0xff114332);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLog.i("textView2.getParent()=" + textView2.getParent());
                mLog.i("textView1.getParent()=" + textView.getParent());
                ViewUtils.smoothReplace_Smart(textView, textView2, 200);
            }
        });
    }
}
