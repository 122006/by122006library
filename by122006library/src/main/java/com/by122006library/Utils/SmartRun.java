package com.by122006library.Utils;

import com.by122006library.Interface.Async;
import com.by122006library.Interface.BGThread;
import com.by122006library.Interface.DefaultThread;
import com.by122006library.Interface.ThreadStyle;
import com.by122006library.Interface.UIThread;
import com.by122006library.MyException;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * 智能运行线程
 */
public abstract class SmartRun {
    ArrayList<Method> methodList;

    @ThreadStyle(style = ThreadStyle.Style.Default)
    public abstract void action();

    final public void start() {
        choosThreadRun("action");
    }

    final public boolean prepare(Object... parameter) {
        StackTraceElement[] stackTraceElement = Thread.currentThread().getStackTrace();
        String methodName = "",lastMethodName="";
        boolean getNextOne = false;
        for (int i = 0; i < stackTraceElement.length; i++) {
            StackTraceElement st = stackTraceElement[i];
            if (st.isNativeMethod()) continue;
//            if (!st.getClassName().equals(getClass().getName())) continue;
            if (st.getMethodName().contains("$")) continue;
            if (st.getMethodName().equals("prepare")) continue;
            boolean ifhave = false;
            for (Method m : methodList) {
                if (m.getName().equals(st.getMethodName())) ifhave = true;
            }
            if (ifhave) {
//                mLog.i("含有的方法："+st.getMethodName());
                if (!getNextOne) {getNextOne = true;methodName = st.getMethodName();}
                else {
                    lastMethodName = st.getMethodName();
                    break;
                }

            }
        }
        mLog.i("调用方法："+methodName);
        mLog.i("上一个调用的方法："+lastMethodName);
        boolean boo = !lastMethodName.equals("choosThreadRun");

        if (boo) choosThreadRun(methodName, parameter);
        return boo;

    }

    final public void choosThreadRun(String methodName, final Object... parameter) {
        mLog.i(methodName+"");
        methodName = methodName.replace("(", "").replace(")", "");
        ThreadStyle.Style actionThreadStyle = ThreadStyle.Style.Default;
        boolean async = false;
        if (methodList == null) {
            methodList = new ArrayList<>();

            Class clazz = getClass();
            Method[] methods = clazz.getMethods();
            for (Method m : methods) {
                methodList.add(m);
            }
        }
        Method method = null;
        for (Method m : methodList) {
            if (!m.getName().equals(methodName)) continue;
            Class<?>[] parameterTypes = m.getParameterTypes();
            if (parameterTypes.length != parameter.length) continue;
            boolean ifthis = true;
            for (int i = 0; i < parameterTypes.length; i++) {
                if (!(parameter[i].getClass() == parameterTypes[i]))
                    ifthis = false;
            }
            if (ifthis) {
                method = m;
                break;
            }
        }
        if (method == null) {
            mLog.e("没有找到匹配的类 "+methodName+"("+parameter.toString()+")");
            return;
        }
        try {
            Annotation[] annotation = method.getAnnotations();
            for (Annotation a : annotation) {
                if (a instanceof ThreadStyle) {
                    actionThreadStyle = ((ThreadStyle) a).style();
                }
                if (a instanceof UIThread) {
                    actionThreadStyle = ThreadStyle.Style.UI;
                }
                if (a instanceof BGThread) {
                    actionThreadStyle = ThreadStyle.Style.BG;
                }
                if (a instanceof DefaultThread) {
                    actionThreadStyle = ThreadStyle.Style.Default;
                }
                if (a instanceof Async) {
                    async = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            mLog.e("没有发现" + methodName + "方法，请不要对SmartRun类进行混淆");
            return;
        }

        final boolean finalAsync = async;

        if (actionThreadStyle == ThreadStyle.Style.Default) {
            actionThreadStyle = ThreadUtils.getThreadStytle();
        }

        final Method finalMethod = method;
        if (actionThreadStyle == ThreadStyle.Style.BG && ThreadUtils.isBGThread()) {
            if (!finalAsync)
                invoke(method, parameter);

            else {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        invoke(finalMethod, parameter);

                    }
                }).start();
            }
            return;
        }
        if (actionThreadStyle == ThreadStyle.Style.UI && ThreadUtils.isUIThread()) {
            if (!finalAsync)
                invoke(method, parameter);

            else try {
                ThreadUtils.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        invoke(finalMethod, parameter);

                    }
                });
            } catch (MyException e) {
                e.printStackTrace();
            }
            return;
        }
        if (ThreadUtils.isUIThread() && actionThreadStyle == ThreadStyle.Style.BG) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    invoke(finalMethod, parameter);

                }
            }).start();
            return;
        }
        if (ThreadUtils.isBGThread() && actionThreadStyle == ThreadStyle.Style.UI) {
            try {
                ThreadUtils.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        invoke(finalMethod, parameter);

                    }
                });
            } catch (MyException e) {
                e.printStackTrace();
            }
            return;
        }


    }

    final private void invoke(Method method, Object... parameter) {
        mLog.i(ThreadUtils.getThreadStytle().toString());
        try {
            method.invoke(this, parameter);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            Throwable t = e.getTargetException();// 获取目标异常
            t.printStackTrace();
        }
    }
}
