package com.by122006library.Functions.AttBinder;

import android.support.annotation.FloatRange;

import com.by122006library.Functions.CycleTask.CycleTask;
import com.by122006library.Functions.mLog;
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
        cycleTask = new CycleTask(CycleTask.ImmediatelyRun, CYCLETIME, (int) (time / CYCLETIME )) {
            @Override
            @UIThread
            public void doCycleAction(int haveCycleCount) throws MyException {
                try {
                    fluctuation();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            public void endCycleAction() {
                try {
                    setAttNum(time);
                    if(getPer()<1)fluctuation(time);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.register(this,-1);
        setAttProgressListener(new AttProgressListener() {
            double beforeAttNum = 0;

            @Override
            public void progress(double beforeAttNum, double nowAttNum, double nowAttPer) {
                beforeAttNum = this.beforeAttNum;
                if (beforeAttNum == nowAttNum && beforeAttNum != 0) return;
//                mLog.i("beforeAttNum= %f ,nowAttNum = %f ", beforeAttNum, nowAttNum);
                if(timeProgressActions!=null)
                for (TimeProgressAction timeProgressAction : (ArrayList<TimeProgressAction>) timeProgressActions
                        .clone()) {
                    long actionTime = timeProgressAction.getRunTime();
                    if ((!reverse&&nowAttNum >= actionTime &&beforeAttNum<actionTime)||(reverse&&nowAttNum < actionTime &&beforeAttNum>=actionTime)){
                        timeProgressAction.event();
                    }
                }
                if ((reverse && nowAttNum == min) || (!reverse && nowAttNum == max)) {
                    remove();
                    cycleTask.unRegister();
                }
                this.beforeAttNum = nowAttNum;

            }
        });
    }

    @Override
    public double measureAtt() {
        return attNum = (cycleTask == null ? 0 : ((double) (cycleTask.cycleCount * cycleTask.cycleTime)));
    }

    @Override
    public double getAttNum() {
        return attNum;
    }

    @Override
    public void setAttNum(double num) {

    }

    @Override
    public double transform(@FloatRange(from = 0, to = 1) double per) {
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
    final public Att setAttProgressListener(AttProgressListener attProgressListener) {
        this.attProgressListener = attProgressListener;
        return this;
    }

    public void addTimeProgressAction(TimeProgressAction timeProgressAction) {
        if (timeProgressActions == null) timeProgressActions = new ArrayList<TimeProgressAction>();
        timeProgressActions.add(timeProgressAction);
    }

    public static abstract class TimeProgressAction {
        long runTime;

        public TimeProgressAction(long runTime) {
            this.runTime = runTime;
        }

        public long getRunTime() {
            return runTime;
        }

        public abstract void event();
    }

}
