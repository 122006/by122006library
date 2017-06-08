package com.by122006library.Utils;

import android.app.Activity;
import android.os.Looper;

import com.by122006library.Activity.BaseActivity;
import com.by122006library.Interface.ThreadStyle;
import com.by122006library.MyException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by admin on 2017/2/27.
 */

public class ThreadUtils {
    public static void runOnUiThread(Runnable runnable) throws MyException {
        Activity activity = null;
        try {
            activity = BaseActivity.getTopActivity();
        } catch (MyException e) {
            e.printStackTrace();
        }
        try {
            if (activity == null) activity = BaseActivity.getTopOutActivity();
        } catch (MyException e) {
            e.printStackTrace();
        }
        if (runnable != null) {
            if (activity != null) {
                activity.runOnUiThread(runnable);
                RunLogicUtils.clearMethodRunTimes();
            } else {
                if (RunLogicUtils.getHereRunTimes(1000) < 10) {
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    runOnUiThread(runnable);
                }
            }
        }

    }

    public static boolean isUIThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }

    public static boolean isBGThread() {
        return Looper.myLooper() != Looper.getMainLooper();
    }

    public static ThreadStyle.Style getThreadStytle() {
        return isUIThread() ? ThreadStyle.Style.UI : ThreadStyle.Style.BG;
    }

    public static Thread getCurrentActivityThread() {
        Class<?> activityThread = null;
        try {
            activityThread = Class.forName("android.app.ActivityThread");
            Method currentActivityThread = activityThread.getDeclaredMethod("currentActivityThread");
            currentActivityThread.setAccessible(true);
            //获取主线程对象
            Object activityThreadObject = currentActivityThread.invoke(null);
            return (Thread) activityThreadObject;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }
}
