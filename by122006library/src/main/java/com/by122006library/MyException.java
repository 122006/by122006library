package com.by122006library;


import com.by122006library.Activity.BaseActivity;
import com.by122006library.Functions.mLog;
import com.by122006library.View.mToast;

/**
 * Created by admin on 2017/2/6.
 */

public class MyException extends Exception {
    public MyException(String message) {
        super(message);
        String tag = mLog.generateTag(mLog.getCallerStackTraceElement());
        mLog.e("===="+tag+"\nException:"+message);
    }

    public static void show(Exception e) {
        try {
            mToast.getInstance(BaseActivity.getTopActivity()).show(e.getMessage());
        } catch (MyException e1) {
        }
    }
}
