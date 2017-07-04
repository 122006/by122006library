package com.by122006library.Functions.CycleTask;

import com.by122006library.Functions.mLog;
import com.by122006library.Interface.Async;
import com.by122006library.Interface.BGThread;
import com.by122006library.Interface.DefaultThread;
import com.by122006library.Interface.ThreadStyle;
import com.by122006library.Interface.UIThread;
import com.by122006library.MyException;
import com.by122006library.Utils.ThreadUtils;

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
    private static int tick=13;


    static long durtime = 0;
    static long lasttime = 0;
    public static Thread thread = new Thread(new Runnable() {


        @Override
        public void run() {
            while (isRunning) {
                threadWhileRun();
                try {
                    Thread.sleep(tick);
                } catch (InterruptedException e) {
                }
            }
        }
    });
    public Object tag;
    public long delayTime;
    public long cycleTime;
    public int cycleCount, cycleNum = 1;
    public long restTime;
    public ThreadStyle.Style runThreadStyle;
    public boolean async = false;
    public long mLastTime;//上次运行时间
    public long mCurDurtime;//真实运行时间

    private boolean log = true;
    volatile private TaskState state = TaskState.Create;

    /**
     * @param delayTime 首次延迟时间
     * @param cycleTime 每次循环时间
     * @param cycleNum  循环次数 <=0时为无限循环
     */
    public CycleTask(long delayTime, long cycleTime, int cycleNum) {
        this.delayTime = delayTime;
        if (delayTime == ImmediatelyRun) this.delayTime = -cycleTime;
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
//        durtime+=2;
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
            if(item==null){
                if(item.log)mLog.isNull(item);
                return;
            }
            if (item.tag == null) continue;
            if (item.tag.equals(tag)) {
                list.remove(item);
            }

        }
    }

    public static ArrayList<CycleTask> indexOfTag(Object tag) {
        ArrayList<CycleTask> list = new ArrayList<CycleTask>();
        for (CycleTask item : (ArrayList<CycleTask>) list.clone()) {
            if (item.tag == null) continue;
            if (item.tag.equals(tag)) {
                list.add(item);
            }

        }
        return list;
    }

    public static void destroyTaskThread() {
        isRunning = false;
    }

    public CycleTask setLog(Boolean boo) {
        log = boo;
        return this;
    }

    public void run(long durtime) throws MyException {
//        mLog.i(durtime);
        if (cycleNum == 0) return;
        if (delayTime > 0) {
            if(getState()!=TaskState.Running)setState(getState()==TaskState.Register||getState()==TaskState.Delay?TaskState.Delay:TaskState.Wait);
            delayTime -= durtime;
        }
        if (delayTime > 0) return;
        restTime -= durtime;
//        mLog.array(Thread.currentThread().toString(),getState());

        if (restTime <= 0) {

            final int fCycleCount = cycleCount;
            setState(TaskState.Running);
            cycleCount++;
            mCurDurtime = System.currentTimeMillis() - mLastTime;

            restTime = cycleCount*cycleTime+startTime+ delayTime -System.currentTimeMillis();
            try {
                if (runThreadStyle.equals(ThreadStyle.Style.BG)) {
                    if (ThreadUtils.isUIThread() && !async) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    setState(TaskState.Running);
//                                    mLog.array(Thread.currentThread().toString(),getState());
                                    doCycleAction(fCycleCount);
                                    setState(TaskState.Wait);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    } else {
                        try {
                            setState(TaskState.Running);
//                            mLog.array(Thread.currentThread().toString(),getState());
                            doCycleAction(fCycleCount);
                            setState(TaskState.Wait);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    }
                } else if (runThreadStyle.equals(ThreadStyle.Style.UI)) {

                    ThreadUtils.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                setState(TaskState.Running);
                                doCycleAction(fCycleCount);
                                setState(TaskState.Wait);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            } catch (MyException e) {
                e.printStackTrace();
            }

            mLastTime = System.currentTimeMillis();
            if (cycleCount >= cycleNum && cycleNum > 0) unRegister();
        }
    }

    /**
     * 注销该定时器
     */
    public void unRegister() {
        if (list.contains(this)) {
            try {
                endCycleAction();
            } catch (MyException e) {
                e.printStackTrace();
            }
            list.remove(this);
            setState(TaskState.Unregister);
        }else{
            if(log)mLog.e("不能注销循环任务 注销位置："+mLog.getCallerLocation());
        }
//        if (list.size() == 0) destroyTaskThread();
    }

    /**
     * 注册该定时器
     *
     * @param tag 定时器的依附对象
     */
    public CycleTask register(Object tag) {
        register(tag, -1);
        return this;
    }

    public TaskState getState() {
        return state;
    }

    public void setState(TaskState state) {
        this.state = state;
    }
    long startTime=0;
    /**
     * 注册该定时器
     *
     * @param tag  定时器的依附对象
     * @param flag 1 :SINGLETASK 如果tag不唯一则不生效<p>2 :SINGLETASK_COVER 一定生效且覆盖同名tag事件<p>-1 :非单例模式
     * @return
     */
    public CycleTask register(Object tag, int flag) {
        if (flag == SINGLETASK || flag == SINGLETASK_COVER) {
            boolean ifhave = false;
            for (CycleTask cycleTask : (ArrayList<CycleTask>) CycleTask.list.clone()) {
                if (cycleTask == null) continue;
                if (cycleTask.equals(tag)) ifhave = true;
            }

            if (ifhave) {
                if (flag == SINGLETASK) return this;
                else {
                    for (CycleTask cycleTask : (ArrayList<CycleTask>) CycleTask.list.clone()) {
                        if (cycleTask.equals(tag)) list.remove(cycleTask);
                    }
                }
            }
        }
        if (!isRunning) {
            isRunning = true;
            try {
                thread.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
            }
        }
        startTime=System.currentTimeMillis();
        mLastTime = System.currentTimeMillis();
        if (log) {
            mLog.i("注册CycleTask 注册代码位置：" + mLog.getCallerLocation(1));
            mLog.i("重复次数：%s 次   ;循环周期：%dms   ;首次延迟时间：%s", (cycleNum == ImmediatelyRun ? "n+" : cycleNum), cycleTime,
                    (delayTime <= 0 ? "立即" : delayTime + "ms"));
        }
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
                if (a instanceof Async) {
                    async = true;
                }

            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        if (!list.contains(this)) {
            list.add(this);
            setState(TaskState.Register);
        }
        return this;
    }

    /**
     * 本次循环结束后立刻进行下次任务，同时可运行回合数+1(额外运行，不占用原本次数)<p>用于出错后重试<p>必须等待本次循环结束之后才会进行重试</p>
     */
    public void again() {
        restTime = 0;
        startTime-=cycleTime;
        if (cycleNum > 0) cycleNum++;
    }

    public abstract void doCycleAction(int haveCycleCount) throws MyException;

    public void endCycleAction() throws MyException {

    }

    public enum TaskState {
        Create, Register, Delay, Wait, Running, Unregister
    }


}
