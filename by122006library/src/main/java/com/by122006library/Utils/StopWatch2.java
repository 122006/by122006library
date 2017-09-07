package com.by122006library.Utils;



/**
 * 重写Tony Shen 的StopWatch2类<p>
 * （原有时间为私有且不能使用反射故重写整个类不适用继承）<p>
 * Created by 122006 on 2017/8/1.
 */

public class StopWatch2 {
    private long startTime;
    private long endTime;
    private long elapsedTime;

    public StopWatch2() {
    }

    private void reset() {
        startTime = 0;
        endTime = 0;
        elapsedTime = 0;
    }

    public void start() {
        reset();
        startTime = System.nanoTime();
    }

    public void stop() {
        if (startTime != 0) {
            endTime = System.nanoTime();
            elapsedTime = endTime - startTime;
        } else {
            reset();
        }
    }

    public long getTotalTimeMillis() {
        return (elapsedTime != 0) ? endTime - startTime: 0;
    }
}
