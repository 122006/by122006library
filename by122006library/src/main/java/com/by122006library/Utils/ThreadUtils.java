package com.by122006library.Utils;

import android.os.Looper;

import com.by122006library.Activity.BaseActivity;
import com.by122006library.MyException;

/**
 * Created by admin on 2017/2/27.
 */

public class ThreadUtils {
    public static void runOnUiThread(Runnable runnable) throws MyException {
        BaseActivity activity=BaseActivity.getTopActivity();
        if (  runnable!=null&& activity!=null){
            activity.runOnUiThread(runnable);
        }
    }
    public static boolean isUIThread(){
        return Looper.myLooper() == Looper.getMainLooper();
    }
    public static boolean isBGThread(){
        return Looper.myLooper() != Looper.getMainLooper();
    }
}
