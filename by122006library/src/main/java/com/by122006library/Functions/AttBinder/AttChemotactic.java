package com.by122006library.Functions.AttBinder;

import com.by122006library.Functions.CycleTask.CycleTask;
import com.by122006library.Interface.UIThread;

import static com.by122006library.Functions.CycleTask.CycleTask.ImmediatelyRun;

/**
 * 时间属性趋值器，修改原有变化方向，以规定速度进行趋于0~1的调整<p>多个绑定器只有第一个为参照<p> 会解除原有的
 * <p>
 * Created by admin on 2017/6/15.
 */

public class AttChemotactic {
    public static void chemotactic(long maxTime, AttBinder... binders) {
        final AttBinder binder = binders[0];
        TimeAtt timeAtt = binder.getTimeAtt();
        double per = binder.getCachePer();
        long restTime = 0;

        if (per < 0.5) {
            restTime = (long) (per * maxTime);
        } else {
            restTime = (long) ((1 - per) * maxTime);
        }
        if (restTime == 0) return;
        if (timeAtt != null) timeAtt.cycleTask.unRegister();

        if (CycleTask.indexOfTag(binder).size() == 0)
            new CycleTask(ImmediatelyRun, TimeAtt.CYCLETIME, (int) (restTime / TimeAtt.CYCLETIME)+1) {
                double oPer;

                CycleTask init(double oPer) {
                    this.oPer = oPer;
                    return this;
                }

                @Override
                @UIThread
                public void doCycleAction(int haveCycleCount) {
                    double per = 0;

                    try {
                        if (oPer < 0.5) {
                            per = oPer - haveCycleCount * oPer / (cycleNum-1);
                        } else {
                            per = oPer + haveCycleCount * (1 - oPer) / (cycleNum-1);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

//                    mLog.i("oPer=%f , haveCycleCount=%d ,cycleCount=%d ,",oPer,haveCycleCount,cycleNum);
                    binder.fluctuation(per);
                }

//                @Override
//                public void endCycleAction() throws MyException {
//                    super.endCycleAction();
//                    binder.fluctuation(oPer>0.5?1:0);
//                }
            }.init(per).register(binder);
    }

}
