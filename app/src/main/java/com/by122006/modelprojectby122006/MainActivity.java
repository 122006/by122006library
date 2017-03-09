package com.by122006.modelprojectby122006;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.by122006library.Activity.BaseActivity;
import com.by122006library.Utils.ViewUtils;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout layout=new LinearLayout(this);
        layout.setGravity(Gravity.CENTER);
        TextView tv=new TextView(this);
        tv.setText("TestActivity");
        layout.addView(tv);
        final TextView tv2=new TextView(this);
        tv2.setText(getTaskId()+"");
        layout.addView(tv2);
        tv.setClickable(true);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
//                Intent intent=new Intent(MainActivity.this,TestActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
//                startActivity(intent);
                ViewUtils.introduceView(v,"这个是展示的控件",true, new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        ViewUtils.introduceView(tv2,"这个是第二个展示的控件",false, new PopupWindow.OnDismissListener() {
                            @Override
                            public void onDismiss() {

                            }
                        });
                    }
                });
            }
        });
        setContentView(layout);
//        startService(new Intent(this, PopupTaskControlService.class));



    }

}
