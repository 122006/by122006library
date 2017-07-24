package com.by122006.modelprojectby122006;

import android.app.Application;
import android.util.Log;

import com.anupcowkur.reservoir.Reservoir;
import com.by122006library.Enum.mLog2;
import com.by122006library.Functions.mLog;
import com.by122006library.Utils.ReflectionUtils;
import com.by122006library.View.TopBar;
import com.me.weishu.epic.Hook;

import java.io.IOException;
import java.lang.reflect.Method;

/**
 * Created by admin on 2017/6/15.
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        try {
            Reservoir.init(this, 512 * 1024);
        } catch (IOException e) {
            e.printStackTrace();
        }
        TopBar.setDefaultTopBarHeightdp(-2);
                try {
            Log.e("x", "e");
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Log.d("x", "d");
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Log.v("x", "v");
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            Log.i("x", "i");
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            Log.w("x", "w");
        } catch (Exception e) {
            e.printStackTrace();
        }
        mLog.autoReplaceLog("widve");
//        com.by122006.modelprojectby122006.SmartRun2Mapping.doMapping();
//        com.by122006library.Activity.SmartRun2Mapping.doMapping();
//        AppRunSpeed.setSpeed(2);
//        m("","");
//        try {
//            Method m_o = ReflectionUtils.getDeclaredMethod(Log.class, "i",String.class,String.class);
//            Method m_n = ReflectionUtils.getDeclaredMethod(MyApplication.class, "m",String.class,String.class);
//            Hook.hook(m_o,m_n);
//        } catch (NoSuchMethodException e) {
//            e.printStackTrace();
//        }
//        m("","");

    }


}
