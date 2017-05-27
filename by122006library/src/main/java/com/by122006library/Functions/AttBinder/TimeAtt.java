package com.by122006library.Functions.AttBinder;

import com.by122006library.Functions.CycleTask.CycleTask;
import com.by122006library.Interface.UIThread;
import com.by122006library.MyException;

import java.util.ArrayList;

/**
 * Created by 122006 on 2017/5/15.
 */

public class TimeAtt extends Att {
    final static private long CYCLETIME = 16;
    CycleTask cycleTask = null;
    ArrayList<TimeProgressAction> timeProgressActions;

    public TimeAtt(final long time) {
        super(0, time);
        cycleTask = new CycleTask(CycleTask.ImmediatelyRun, CYCLETIME, (int) (time / CYCLETIME + 1)) {
            @Override
            @UIThread
            public void doCycleAction(int haveCycleCount) throws MyException {
                fluctuation();
            }

            public void endCycleAction() {
                remove();
            }
        }.register(this);
        setAttProgressListener(new AttProgressListener() {
            @Override
            public void progress(double beforeAttNum, double nowAttNum, double nowAttPer) {
                for (TimeProgressAction timeProgressAction :(ArrayList<TimeProgressAction>) timeProgressActions.clone()) {
                    if (beforeAttNum != nowAttNum && ((timeProgressAction.getRunTime() < nowAttNum &&
                            timeProgressAction.getRunTime() >= beforeAttNum) || (timeProgressAction.getRunTime() >=
                            nowAttNum && timeProgressAction.getRunTime() < beforeAttNum))) {
                        timeProgressAction.action();
                    }
                }
                if (beforeAttNum != nowAttNum &&( (max <= nowAttNum &&
                        max > beforeAttNum) || (max <= nowAttNum &&
                        max > beforeAttNum))) {
                    remove();
                    cycleTask.unRegister();
                }

            }
        });
    }

    @Override
    public double measureAtt() {
        return cycleTask == null ? 0 : ((double) (cycleTask.cycleCount * cycleTask.cycleTime)) / max;
    }

    @Override
    public void setAttNum(double num) {

    }

    @Override
    public double transform(double per) {
        return per;
    }

    @Override
    public void setOutSideListener() {

    }

    public void remove() {
        super.remove();
        cycleTask.unRegister();
    }

    /**
     * 你应该使用addTimeProgressAction()来进行时间轴进度监听
     *
     * @param attProgressListener
     */
    @Deprecated
    final public void setAttProgressListener(AttProgressListener attProgressListener) {

    }

    public void addTimeProgressAction(TimeProgressAction timeProgressAction) {
        if (timeProgressAction == null) timeProgressActions = new ArrayList<TimeProgressAction>();
        timeProgressActions.add(timeProgressAction);
    }

    public abstract class TimeProgressAction {
        long runTime;

        TimeProgressAction(long runTime) {
            this.runTime = runTime;
        }

        public long getRunTime() {
            return runTime;
        }

        abstract void action();
    }

}
