package com.by122006library;

import com.by122006library.Utils.ThreadUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by admin on 2017/7/7.
 */

public class ThreadManager {
    public static ThreadManager instance = null;

    private ThreadManager() {

    }

    public static ThreadManager getInstance() {
        if (instance == null) {
            synchronized (ThreadManager.class) {
                if (instance == null) {
                    instance = new ThreadManager();
                }
            }
        }
        return instance;
    }


    public boolean check(String str) {
        if (ThreadUtils.isBGThread() && str.toLowerCase().contains("bg")) return true;
        if (ThreadUtils.isUIThread() && str.toLowerCase().contains("ui")) return true;
        if (ThreadUtils.isBGThread() && str.toLowerCase().contains("async")) return true;
        return false;
    }

    public void postUIThread(Runnable runnable) {
        try {
            ThreadUtils.runOnUiThread(runnable);
        } catch (MyException e) {
            e.printStackTrace();
        }
    }

    public void postBGThread(Runnable runnable) {
        new Thread(runnable).start();
    }

    public void postUIThread(final Object obj, final String methodName, final Object... objects) {
        try {
            ThreadUtils.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Class clazz = (obj instanceof Class) ? ((Class) obj) : obj.getClass();
                        Method[] methods = clazz.getMethods();
                        for (Method method : methods) {
                            if (!method.getName().equals(methodName)) continue;
                            try {
                                method.invoke(obj, objects);
                                break;
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            } catch (IllegalArgumentException e) {
                                e.printStackTrace();
                            } catch (InvocationTargetException e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (MyException e) {
            e.printStackTrace();
        }
    }

    public void postBGThread(final Object obj, final String methodName, final Object... objects) {
        try {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Class clazz = (obj instanceof Class) ? ((Class) obj) : obj.getClass();
                    Method[] methods = clazz.getMethods();
                    for (Method method : methods) {
                        if (!method.getName().equals(methodName)) continue;

                        try {
                            method.invoke(obj, objects);
                            break;
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (IllegalArgumentException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
