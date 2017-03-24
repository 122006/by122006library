package com.by122006library.Utils;

import com.by122006library.Interface.BGThread;
import com.by122006library.Interface.DefaultThread;
import com.by122006library.Interface.ThreadStyle;
import com.by122006library.Interface.UIThread;
import com.by122006library.MyException;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;

/**
 * 循环任务<p>
 * <p>
 * Created by 122006 on 2017/2/17.
 */

public abstract class CycleTask {

    public static ArrayList<CycleTask> list = new ArrayList<>();
    //    public static ArrayList<CycleTask> removeList = new ArrayList<>();
//    public static ArrayList<CycleTask> addList = new ArrayList<>();
    public static boolean isRunning = false;


    public static Thread thread = new Thread(new Runnable() {


        @Override
        public void run() {
            while (isRunning) {
                threadWhileRun();
                try {
                    Thread.sleep(5);
                } catch (InterruptedException e) {
                }
            }
        }
    });
    static long lasttime = 0;
    public static long durtime = 0;
    public Object tag;
    public long daleyTime;
    public long cycleTime;
    public int cycleCount, cycleNum = 1;
    public long restTime;
    public ThreadStyle.Style runThreadStyle;

    public static void threadWhileRun() {
        durtime = System.currentTimeMillis() - lasttime;
        synchronized (list) {
            try {
                for (CycleTask c : (ArrayList<CycleTask>) list.clone()) {
                    c.run(durtime);
                }
            } catch (Exception e) {
            }
        }
        lasttime = System.currentTimeMillis();

    }
    final public static int ImmediatelyRun=-1;
    final public static int CircleForever=-1;
    /**
     * @param daleyTime 首次延迟时间
     * @param cycleTime 没次循环时间
     * @param cycleNum  循环次数 <=0时为无限循环
     */
    public CycleTask(long daleyTime, long cycleTime, int cycleNum) {
        this.daleyTime = daleyTime;
        if(daleyTime==-1) this.daleyTime=-cycleTime;
        this.cycleTime = cycleTime;
        this.cycleNum = cycleNum;
    }

    /**
     * 根据tag注销所有有关的定时器
     *
     * @param tag
     */
    public static void unregister(Object tag) {
        for (CycleTask item : (ArrayList<CycleTask>) list.clone()) {
//            if (item.tag == tag && !removeList.contains(item)) {
//                removeList.add(item);
//            }
            list.remove(item);
        }
    }

    public static void destroyTaskThread() {
        isRunning = false;
    }

    public void run(long durtime) throws MyException {
        if (cycleTime == 0) return;
        if (daleyTime > 0) daleyTime -= durtime;
        if (daleyTime > 0) return;

        restTime -= durtime;

        if (restTime <= 0) {
            restTime = cycleTime;
            if (runThreadStyle.equals(ThreadStyle.Style.BG)) {
                if (ThreadUtils.isUIThread()) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                doCycleAction(cycleCount);
                            } catch (MyException e) {

                            }
                        }
                    }).run();
                } else
                    doCycleAction(cycleCount);
            } else if (runThreadStyle.equals(ThreadStyle.Style.UI))
                ThreadUtils.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            doCycleAction(cycleCount);
                        } catch (MyException e) {
                            e.printStackTrace();
                        }
                    }
                });
            cycleCount++;
            if (cycleCount >= cycleNum && cycleNum > 0) unregister();
        }
    }

    /**
     * 注册并运行在内部线程中
     */
    public void registerOwnThreadTask(Object tag) {
        if (!isRunning) {
            isRunning = true;
            thread.start();
        }
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
        }
        register(tag);
    }

    /**
     * 注销该定时器
     */
    public void unregister() {
        if (list.contains(this)) list.remove(this);
    }

    /**
     * 注册该定时器
     *
     * @param tag 定时器的依附对象
     */
    public void register(Object tag) {
        this.tag = tag;

        Class clazz = this.getClass();
        try {
            runThreadStyle = ThreadStyle.Style.BG;
            Annotation[] annotation = clazz.getMethod("doCycleAction", int.class).getAnnotations();
            for (Annotation a : annotation) {
                if (a instanceof ThreadStyle) {
                    runThreadStyle = ((ThreadStyle) a).style();
                }
                if (a instanceof ThreadStyle) {
                    runThreadStyle = ((ThreadStyle) a).style();
                }
                if (a instanceof UIThread) {
                    runThreadStyle = ThreadStyle.Style.UI;
                }
                if (a instanceof BGThread) {
                    runThreadStyle = ThreadStyle.Style.BG;
                }
                if (a instanceof DefaultThread) {
                    runThreadStyle = ThreadStyle.Style.Default;
                }
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        if (!list.contains(this)) list.add(this);
    }

    public abstract void doCycleAction(int haveCycleCount) throws MyException;

}
