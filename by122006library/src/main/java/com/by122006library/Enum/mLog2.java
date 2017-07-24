package com.by122006library.Enum;

import android.util.Log;

import com.by122006library.Functions.mLog;

/**
 * Created by admin on 2017/7/24.
 */
public class mLog2{
    public static int i(String tag, String msg) {
        mLog.I(tag,msg);
        return 0;
    }

    public static int e(String tag, String msg) {
        mLog.E(tag,msg);
        return 0;
    }

    public static int w(String tag, String msg) {
        mLog.I(tag,"xx:"+msg);
        mLog.W(tag,msg);
        return 0;
    }

    public static int v(String tag, String msg) {
        mLog.V(tag,msg);
        return 0;
    }

    public static int d(String tag, String msg) {
        mLog.D(tag,msg);
        return 0;
    }
}