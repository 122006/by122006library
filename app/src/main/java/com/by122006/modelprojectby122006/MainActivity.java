package com.by122006.modelprojectby122006;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.by122006library.Activity.BaseActivity;
import com.by122006library.Functions.SmartRun;
import com.by122006library.Interface.BGThread;
import com.by122006library.Interface.DefaultThread;
import com.by122006library.Interface.UIThread;
import com.by122006library.Utils.ThreadUtils;
import com.by122006library.Utils.ViewUtils;


public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout);

    }

}
