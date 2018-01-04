package com.by122006library.Functions;

import android.content.Context;

import com.by122006library.Utils.ReflectionUtils;
import com.me.weishu.epic.Hook;

import java.lang.reflect.Method;
import java.util.ResourceBundle;

/**
 * 软件运行速度
 * Created by 122006 on 2017/7/20.
 */

public class AppRunSpeed {
    private static long LastNowTime;
    /**
     * 2^n 0:1 1:2 2:4 4:8
     */
    private static int TimeSpeedMove;
    private static long LastNowTrueTime;
    /**
     * 是否启用加速
     */
    private static boolean FasterGame;

    public synchronized static long getTime() {
        int TimeSpeedMove=0;
        TimeSpeedMove = Math.max(1, AppRunSpeed.TimeSpeedMove);
        if(!FasterGame) TimeSpeedMove=0;
        if (LastNowTime == 0l) {
            LastNowTime = System.currentTimeMillis();
            LastNowTrueTime=LastNowTime;
        } else {
            long dur = (System.currentTimeMillis() - LastNowTrueTime);
            int pdur = (int) (dur << TimeSpeedMove);
            if (pdur == 0) return LastNowTime;
            else{
                LastNowTrueTime=System.currentTimeMillis();
                LastNowTime = LastNowTime + pdur;}
        }
        return LastNowTime;
    }
    public static long getChangedTime(long time) {
        int TimeSpeedMove=0;
        TimeSpeedMove = Math.max(1, AppRunSpeed.TimeSpeedMove);
        if(!FasterGame) TimeSpeedMove=0;
        if (LastNowTime == 0l) {
            return time;
        } else {
            return time >> TimeSpeedMove;
        }
    }

    public static void setTimeSpeedMove(Context context, int timeSpeedMove) {
        getTime();
        TimeSpeedMove = timeSpeedMove;
    }
}
