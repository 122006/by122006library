package com.by122006library.Utils;

import android.util.Log;


import com.by122006library.BuildConfig;
import com.by122006library.Functions.mLog;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by 122006 on 2017/2/23.
 */

public class DebugUtils {
    public static HashMap<String, ArrayList<HashMap<String, Object>>> runningDurtimeData = new HashMap<String, ArrayList<HashMap<String, Object>>>();

    /**
     * 显示方法中某代码段运行时间<p>
     * 非Debug模式不会被运行
     * <p>
     * Created by 122006 on 2017/2/23.
     */
    public static void runningDurtime() {
        if (!BuildConfig.DEBUG) return;
        StackTraceElement caller = mLog.getCallerStackTraceElement();
        String tag = caller.getClassName() + "." + caller.getMethodName();
        ArrayList<HashMap<String, Object>> list = runningDurtimeData.get(tag);
        if (list == null) {
            list = new ArrayList<HashMap<String, Object>>();
            runningDurtimeData.put(tag, list);
        }
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("linenum", caller.getLineNumber());
        map.put("time", System.currentTimeMillis());
        boolean needClear = false;
        int minLineNumber = caller.getLineNumber();
        for (HashMap<String, Object> m : (ArrayList<HashMap<String, Object>>) list.clone()) {
            if (((Integer) m.get("linenum")) < minLineNumber) {
                minLineNumber = (Integer) m.get("linenum");
            }
        }
        int complexNum = 1;
        for (HashMap<String, Object> m : (ArrayList<HashMap<String, Object>>) list.clone()) {
            if (((Integer) m.get("linenum")) == caller.getLineNumber()) {
                complexNum++;
            }
        }
        if (complexNum > 1 && minLineNumber == caller.getLineNumber()) {
            needClear = true;
        }
        long lastCycleTime = 0, firstCycleTime = 0;
        if (complexNum > 1) {
            for (HashMap<String, Object> m : (ArrayList<HashMap<String, Object>>) list.clone()) {
                if (((Integer) m.get("linenum")) == caller.getLineNumber()) {
                    if (firstCycleTime == 0) firstCycleTime = (long) m.get("time");
                    lastCycleTime = (long) m.get("time");
                }
            }
        }


        if (needClear) list.clear();
        list.add(map);
        if (list.size() == 1) {
            Log.i(mLog.generateTag(caller), "============性能断点工具=============");
        }
        String text = mLog.generateTag(caller) + "  (" + list.size() + ") ：[";
        try {
            text += "" + "use:" + (System.currentTimeMillis() - (long) list.get(list.size() - 2).get("time")+"ms ");
        } catch (Exception e) {
            text += "  ;" + "use:-1"+"ms ";
        }
        try {
            text += "  ;" + "all:" + (System.currentTimeMillis() - (long) list.get(0).get("time"))+"ms ";
        } catch (Exception e) {
            text += "  ;" + "all:-1"+"ms ";
        }
        text += " ]";
        if (complexNum > 1)
            text += "  ;" + " cycle：[ (" + complexNum + ")  first:" + (System.currentTimeMillis() - firstCycleTime) + "ms  last:" + (System.currentTimeMillis() - lastCycleTime) + "ms ]";

        Log.i(mLog.generateTag(caller), text);


    }


}
