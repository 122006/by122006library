package com.by122006library.Control;

import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by admin on 2016/12/20.
 */

public class ViewControl {

    public static void setShowTextPopup(View v, final String text) {
        v.setClickable(true);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout popuplayout = new LinearLayout(v.getContext());
                popuplayout.setBackgroundColor(0xffdd8c00);
                TextView tv = new TextView(v.getContext());
                tv.setText(text);
                tv.setPadding(5, 5, 5, 5);
                tv.setTextColor(Color.WHITE);
                popuplayout.addView(tv);
                PopupWindow popup = new PopupWindow();
                popup.setOutsideTouchable(true);
                popup.setContentView(popuplayout);
                popup.update();
                popup.setWidth(-2);
                popup.setHeight(-2);
                popup.setBackgroundDrawable(new BitmapDrawable());
                popup.setFocusable(false);
                popup.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                int[] location = new int[2];
                popup.showAtLocation(v.getRootView(), Gravity.LEFT | Gravity.TOP,
                        (int) (location[0]), (int) (location[1] + v
                                .getHeight()));

            }
        });
    }

}
