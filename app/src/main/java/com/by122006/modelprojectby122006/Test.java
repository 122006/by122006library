package com.by122006.modelprojectby122006;

import com.by122006library.ThreadManager;
import com.by122006library.Utils.ThreadUtils;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created by admin on 2018/2/6.
 */

public class Test implements Runnable {

    CountDownLatch countDownLatch;
    Object result;
    private Object o1;
    private int o3;
    private Object o2;

    @Override
    public void run() {
        result=null;
        countDownLatch.countDown();
    }
    public Object action(Object o1,Object o2,int o3){
        this.o1=o1;
        this.o2=o2;
        this.o3=o3;
        if (countDownLatch==null)
        countDownLatch =new CountDownLatch(1);
        ThreadManager.postUIThread(this);
        try {
            countDownLatch.await(1000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static void test(String s,int i){
        new Test().action(null,s,i);
    }


    public static void test_static(String var0, int var1) {
        if(ThreadUtils.isBGThread()) {
        } else {
            Object var10001 = new Test() {
                CountDownLatch countDownLatch;
                private MainActivity obj;
                private String o0;
                private int o1;

                public void run() {
//                    MainActivity.test_static(o0, o1);
                    if (countDownLatch!=null)
                    countDownLatch.countDown();
                }

                public Object action(MainActivity var1, String var2, int var3) {
                    obj = var1;
                    o0 = var2;
                    o1 = var3;
                    countDownLatch = new CountDownLatch(1);
                    ThreadManager.postBGThread(this);

                    try {
                        countDownLatch.await(2000L, TimeUnit.MILLISECONDS);
                    } catch (InterruptedException var6) {
                        var6.printStackTrace();
                    }
                    return null;
                }
            }.action((MainActivity)null, var0, var1);
        }

    }
}
