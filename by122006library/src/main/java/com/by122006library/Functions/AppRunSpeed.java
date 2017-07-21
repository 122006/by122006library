package com.by122006library.Functions;

import com.by122006library.Utils.ReflectionUtils;
import com.me.weishu.epic.Hook;

import java.lang.reflect.Method;

/**
 * 软件运行速度
 * Created by 122006 on 2017/7/20.
 */

public class AppRunSpeed {
    private static float speed;

    final private void AppRunSpeed(){

    }
    public static float getSpeed() {
        return speed;
    }

    public static void setSpeed(float appRunSpeed) {
        speed = appRunSpeed;
        startTime=System.currentTimeMillis();
        try {
            Method method= ReflectionUtils.getMethod(AppRunSpeed.class,"currentTimeMillis");
            Method method2= ReflectionUtils.getMethod(System.class,"currentTimeMillis");
            Hook.hook(method2,method);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }
    public static long  startTime;
    public static  long currentTimeMillis(){
        mLog.mark();
        try {
            long nL=0l;
            long oL= (Long) Hook.callOrigin(System.class);
            nL= (long) ((oL-AppRunSpeed.startTime)* AppRunSpeed.getSpeed()+oL);
            return nL;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return startTime;
    }
}
