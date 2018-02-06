package com.by122006library.Utils;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.by122006library.Activity.BaseActivity;
import com.by122006library.Functions.mLog;
import com.by122006library.Interface.ThreadStyle;
import com.by122006library.MyException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by admin on 2017/2/27.
 */

public class ThreadUtils {
    /**
     * 设置当前界面类，如果使用默认的UI线程处理逻辑，设置当前界面后可以正确的使用ui线程
     */
    public static void setThisAct(Activity thisAct) {
        ThreadUtils.thisAct = thisAct;
    }

    public static Activity getThisAct() {
        return thisAct;
    }

    private static Activity thisAct = null;

    /**
     * 设置UI线程处理逻辑类，你可以自行处理UI事件
     *
     * @param uiThreadAct
     */
    @Deprecated
    public static void setUiThreadAct(UiThreadAct uiThreadAct) {
        ThreadUtils.uiThreadAct = uiThreadAct;
    }

    @Deprecated
    private static UiThreadAct uiThreadAct = new UiThreadAct();

    public static void runOnUiThread(Runnable runnable) throws MyException {
//        uiThreadAct.runUITask(runnable);
        UiHandle.run(runnable);
    }
    protected static class UiHandle extends Handler {
        static UiHandle handle;
        UiHandle() {
            super(Looper.getMainLooper());
        }
        protected static UiHandle getHandler(){
            if (handle == null) {
                synchronized (UiHandle.class) {
                    if (handle == null) {
                        handle = new UiHandle();
                    }
                }
            }
            return handle;
        }
        @Override
        public void handleMessage(Message msg) {
            ((Runnable)msg.obj).run();
        }
        public static void run(Runnable runnable){
            Message message=new Message();
            message.obj=runnable;
            getHandler().sendMessage(message);
        }
    }

    public static boolean isUIThread() {
        return Looper.myLooper() == Looper.getMainLooper();

    }

    public static boolean isBGThread() {
        return Looper.myLooper() != Looper.getMainLooper();
    }

    /**
     * 该方法用于标记调用方法运行于主线程中<p>
     * 没有实际调用意义，无论是否会调用均会生效<p>
     *
     */
    public static void toUiThread(){

    }

    /**
     * 该方法用于标记调用方法运行于后台线程中<p>
     * 没有实际调用意义，无论是否会调用均会生效<p>
     *
     */
    public static void toBgThread(){

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
    @Deprecated
    public static class UiThreadAct {
        public void runUITask(Runnable runnable) {
            Activity activity = thisAct;
            if (thisAct == null) {
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
                        try {
                            runOnUiThread(runnable);
                        } catch (MyException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }
}
