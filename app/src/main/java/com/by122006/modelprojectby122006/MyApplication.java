package com.by122006.modelprojectby122006;

import android.app.Application;
import android.util.Log;

import com.anupcowkur.reservoir.Reservoir;
import com.by122006library.Functions.mLog;
import com.by122006library.View.TopBar;

import java.io.IOException;

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
        mLog.autoReplaceLog("widve");
        mLog.more(Log.INFO,"test","dadsa=21afasdfdasffffffffffffffffffffffffffffffffffaaaaaaaaaaaaaaaaaaaaaaeeeeeeeeeeeeeeeeeeeeeeeeeeeewwwwwwwwwwwwwwwwwwssssssssssssssssssssssssfffffffffffffffffffffffffaaaaaaaaaaaaaaaaaaaaaaaaaaaaaawwwwwwwwwwwwwwwwwwwww311","dadsa=21311","dadsa=21311");
//        com.by122006.modelprojectby122006.SmartRun2Mapping.doMapping();
//        com.by122006library.Activity.SmartRun2Mapping.doMapping();
//        AppRunSpeed.setSpeed(2);
    }


}
