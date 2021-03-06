package com.by122006.modelprojectby122006;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;

import com.anupcowkur.reservoir.Reservoir;
import com.by122006library.Functions.mLog;
import com.by122006library.View.TopBar;
import com.taobao.android.dexposed.DexposedBridge;
import com.taobao.android.dexposed.XC_MethodHook;

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
//        mLog.autoReplaceLog("widve");
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





//        class ThreadMethodHook extends XC_MethodHook{
//            @Override
//            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//                super.beforeHookedMethod(param);
//                Thread t = (Thread) param.thisObject;
//                mLog.i("thread:" + t + ", started..");
//
//            }
//
//            @Override
//            protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
//                super.afterHookedMethod(param);
//                Thread t = (Thread) param.thisObject;
//                mLog.i( "thread:" + t + ", exit..");
//            }
//        }
//
//        DexposedBridge.hookAllConstructors(Thread.class, new XC_MethodHook() {
//            @Override
//            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                super.afterHookedMethod(param);
//                Thread thread = (Thread) param.thisObject;
//                Class<?> clazz = thread.getClass();
//                if (clazz != Thread.class) {
//                    mLog.i( "found class extend Thread:" + clazz);
//                    DexposedBridge.findAndHookMethod(clazz, "run", new ThreadMethodHook());
//                }
//                mLog.i( "Thread: " + thread.getName() + " class:" + thread.getClass() +  " is created.");
//            }
//        });
//        DexposedBridge.findAndHookMethod(Thread.class, "run", new ThreadMethodHook());
    }


}
