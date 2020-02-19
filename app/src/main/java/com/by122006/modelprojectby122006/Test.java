package com.by122006.modelprojectby122006;

import java.util.concurrent.CountDownLatch;

/**
 * Created by admin on 2018/2/6.
 */

public class Test{

    CountDownLatch countDownLatch;
    Object result;
    private Object o1;
    private int o3;
    private Object o2;

    Throwable e;


    public void run() throws Exception{
        result = null;


        countDownLatch.countDown();

        for (int i = 0; i < 3; i++) {
            int x = 1;

        }
        int v = 3;

        if (v+3 == 6) {
            throw new RuntimeException("Out");
        }
        if (v+2 == 5) {
            throw new Exception("");
        }

        {
            int x = 2;

        }


    }
}
