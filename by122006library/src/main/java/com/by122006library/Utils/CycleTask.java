package com.by122006library.Utils;

import com.by122006library.Interface.BGThread;
import com.by122006library.Interface.DefaultThread;
import com.by122006library.Interface.ThreadStyle;
import com.by122006library.Interface.UIThread;
import com.by122006library.MyException;

import java.lang.annotation.Annotation;
import java.util.ArrayList;

/**
 * 循环任务<p>
 * <p>
 * Created by 122006 on 2017/2/17.
 */

public abstract class CycleTask {

    final public static int ImmediatelyRun = -1;
    final public static int CircleForever = -1;
    /**
     * 使用不覆盖的单例事件，同tag必须唯一，否则不生效
     */
    final static public int SINGLETASK = 0x1;
    /**
     * 使用覆盖的单例事件，同tag必须唯一，否则覆盖已有的事件
     */
    final static public int SINGLETASK_COVER = 0x2;
    public static ArrayList<CycleTask> list = new ArrayList<>();
    //    public static ArrayList<CycleTask> removeList = new ArrayList<>();
//    public static ArrayList<CycleTask> addList = new ArrayList<>();
    public static boolean isRunning = false;
    static long durtime = 0;
    static long lasttime = 0;
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
    public Object tag;
    public long daleyTime;
    public long cycleTime;
    public int cycleCount, cycleNum = 1;
    public long restTime;
    public ThreadStyle.Style runThreadStyle;

    /**
     * @param daleyTime 首次延迟时间
     * @param cycleTime 每次循环时间
     * @param cycleNum  循环次数 <=0时为无限循环
     */
    public CycleTask(long daleyTime, long cycleTime, int cycleNum) {
        this.daleyTime = daleyTime;
        if (daleyTime == ImmediatelyRun) this.daleyTime = -cycleTime;
        this.cycleTime = cycleTime;
        this.cycleNum = cycleNum;

    }

    public static Thread getThread() {
        return thread;
    }

    public static void setThread(Thread thread) {
        CycleTask.thread = thread;
    }

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

    /**
     * 根据tag注销所有有关的定时器
     *
     * @param tag
     */
    public static void unRegister(Object tag) {
        if (tag == null) return;
        for (CycleTask item : (ArrayList<CycleTask>) list.clone()) {
            if (item.tag == null) continue;
            if (item.tag.equals(tag)) {
                list.remove(item);
            }

        }
    }

    public long mLastTime;

    public long mCurDurtime;

    public static void destroyTaskThread() {
        isRunning = false;
    }

    public void run(long durtime) throws MyException {
        if (cycleNum == 0) return;
        if (daleyTime > 0) daleyTime -= durtime;
        if (daleyTime > 0) return;

        restTime -= durtime;

        if (restTime <= 0) {

            mCurDurtime=System.currentTimeMillis()-mLastTime;


            restTime = cycleTime;
            if (runThreadStyle.equals(ThreadStyle.Style.BG)) {
                if (ThreadUtils.isUIThread()) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                doCycleAction(cycleCount);
                            } catch (MyException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                } else
                    try {
                        doCycleAction(cycleCount);
                    } catch (MyException e) {
                        e.printStackTrace();
                    }
            } else if (runThreadStyle.equals(ThreadStyle.Style.UI)) {

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
            }
            cycleCount++;
            mLastTime=System.currentTimeMillis();
            if (cycleCount >= cycleNum && cycleNum > 0) unRegister();
        }
    }

    /**
     * 注销该定时器
     */
    public void unRegister() {
        if (list.contains(this)) list.remove(this);
        if (list.size() == 0) destroyTaskThread();
    }

    /**
     * 注册该定时器
     *
     * @param tag 定时器的依附对象
     */
    public void register(Object tag) {
        register(tag, 0);
    }

    /**
     * 注册该定时器
     *
     * @param tag 定时器的依附对象
     */
    public void register(Object tag, int flag) {
        if (flag == SINGLETASK || flag == SINGLETASK_COVER) {
            boolean ifhave = false;
            for (CycleTask cycleTask : (ArrayList<CycleTask>) CycleTask.list.clone()) {
                if (cycleTask.equals(tag)) ifhave = true;
            }

            if (ifhave) {
                if (flag == SINGLETASK) return;
                else {
                    for (CycleTask cycleTask : (ArrayList<CycleTask>) CycleTask.list.clone()) {
                        list.remove(cycleTask);
                    }
                }
            }
        }
        if (!isRunning) {
            isRunning = true;
            thread.start();
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
            }
        }
        mLastTime=System.currentTimeMillis();
        StackTraceElement stackTraceElement = mLog.getCallerStackTraceElement();
        mLog.i("注册CycleTask 注册代码位置：" + mLog.generateTag(stackTraceElement));
        mLog.i("重复次数：" + (cycleNum == ImmediatelyRun ? "n+" : cycleNum) + " 次   ;循环周期：" + cycleTime + "ms   ;首次延迟时间："
                + (daleyTime <= 0 ? "立即" : daleyTime + "ms"));
        this.tag = tag;

        Class clazz = this.getClass();
        try {
            runThreadStyle = ThreadStyle.Style.BG;
            Annotation[] annotation = clazz.getMethod("doCycleAction", int.class).getAnnotations();
            for (Annotation a : annotation) {
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
