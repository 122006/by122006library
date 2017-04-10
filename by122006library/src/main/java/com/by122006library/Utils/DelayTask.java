package com.by122006library.Utils;

import com.by122006library.MyException;

/**
 * Created by admin on 2017/4/10.
 */

public abstract class DelayTask extends CycleTask{
    /**
     * @param daleyTime 首次延迟时间
     */
    public DelayTask(long daleyTime) {
        super(daleyTime, 0, 1);
    }
}
