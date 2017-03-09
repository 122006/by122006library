package com.by122006.modelprojectby122006;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.by122006library.Activity.BaseActivity;

public class TestActivity extends BaseActivity {
    static int count;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout layout=new LinearLayout(this);
        TextView tv=new TextView(this);
        tv.setText("TestActivity");
        TextView tv2=new TextView(this);
        tv.setText(getTaskId()+"");
        layout.addView(tv);
        layout.addView(tv2);
        tv.setClickable(true);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(TestActivity.this,TestActivity.class);
                startActivity(intent);
            }
        });
        setContentView(layout);
    }
}
