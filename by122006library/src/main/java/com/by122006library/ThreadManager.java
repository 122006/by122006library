package com.by122006library;

import com.by122006library.Utils.ThreadUtils;

/**
 * Created by admin on 2017/7/7.
 */

public class ThreadManager {
    public static ThreadManager instance=null;
    private ThreadManager(){

    }
    public static ThreadManager getInstance(){
        if(instance==null){
            synchronized (ThreadManager.class){
                if(instance==null){
                    instance=new ThreadManager();
                }
            }
        }
        return instance;
    }


    public boolean check(String str){
        if (ThreadUtils.isBGThread()&&str.toLowerCase().contains("bg")) return true;
        if (ThreadUtils.isUIThread()&&str.toLowerCase().contains("ui")) return true;
        if (ThreadUtils.isBGThread()&&str.toLowerCase().contains("async")) return true;
        return false;
    }

    public void postUIThread(Runnable runnable){
        try {
            ThreadUtils.runOnUiThread(runnable);
        } catch (MyException e) {
            e.printStackTrace();
        }
    }
    public void postBGThread(Runnable runnable){
        try {
            ThreadUtils.runOnUiThread(runnable);
        } catch (MyException e) {
            e.printStackTrace();
        }
    }
}
