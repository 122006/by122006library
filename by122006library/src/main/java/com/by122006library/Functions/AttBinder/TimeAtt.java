package com.by122006library.Functions.AttBinder;

import com.by122006library.Functions.CycleTask.CycleTask;
import com.by122006library.Interface.UIThread;
import com.by122006library.MyException;

/**
 * Created by 122006 on 2017/5/15.
 */

public class TimeAtt extends Att{
    CycleTask cycleTask=null;
    final static private long CYCLETIME=16;
    public TimeAtt(long time) {
        super(0, time);
        cycleTask=new CycleTask(CycleTask.ImmediatelyRun,CYCLETIME, (int) (time/CYCLETIME+1)) {
            @Override
            @UIThread
            public void doCycleAction(int haveCycleCount) throws MyException {
                fluctuation();
            }
            public void endCycleAction(){
                remove();
            }
        }.register(this);
    }

    @Override
    public double measureAtt() {
        return cycleTask==null?0:((double) (cycleTask.cycleCount*cycleTask.cycleTime))/ max;
    }

    @Override
    public void setAttNum(double num) {

    }

    @Override
    public double transform(double per) {
        return per;
    }

    @Override
    public void setListener() {

    }
    public void remove(){
        super.remove();
        cycleTask.unRegister();
    }
}
