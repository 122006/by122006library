package com.by122006.modelprojectby122006;

import android.os.Bundle;
import android.widget.TextView;

import com.by122006.annotation.Attribute;
import com.by122006.annotation.Subclass;
import com.by122006library.Activity.BaseActivity;


public class MainActivity extends BaseActivity {
    public boolean FLAG_ACT_FULLSCREEN = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView textView = new TextView(this);
        textView.setText("123");
        setContentView(textView);
    }
}
