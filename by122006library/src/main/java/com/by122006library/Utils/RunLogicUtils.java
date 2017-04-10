package com.by122006library.Utils;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 运行逻辑类
 * <p>
 * Created by admin on 2017/4/7.
 */

public class RunLogicUtils {
    private static HashMap<String, ArrayList<Long>> mHereRunTimes = new HashMap<>();

    public static int getHereRunTimes(long nearTime) {
        StackTraceElement caller = mLog.getCallerStackTraceElement();
        String tag = caller.getClassName() + "." + caller.getMethodName() + "." + caller.getLineNumber();
        if (!mHereRunTimes.containsKey(tag)) {
            mHereRunTimes.put(tag, new ArrayList<Long>());
        }
        ArrayList<Long> map = mHereRunTimes.get(tag);
        map.add(System.currentTimeMillis());
        int count = 0;
        for (Long time : (ArrayList<Long>) map.clone()) {
            if (System.currentTimeMillis() - time <= nearTime) count++;
        }
        return count;
    }

    public static void clearHereRunTimes() {
        StackTraceElement caller = mLog.getCallerStackTraceElement();
        String tag = caller.getClassName() + "." + caller.getMethodName() + "." + caller.getLineNumber();
        mHereRunTimes.remove(tag);
    }
    public static void clearAllRunTimes() {
        mHereRunTimes.clear();
    }
}
