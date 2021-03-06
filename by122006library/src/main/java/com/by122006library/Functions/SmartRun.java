package com.by122006library.Functions;

import android.support.annotation.Nullable;

import com.by122006library.Interface.Async;
import com.by122006library.Interface.BGThread;
import com.by122006library.Interface.DefaultThread;
import com.by122006library.Interface.ThreadStyle;
import com.by122006library.Interface.Trace2;
import com.by122006library.Interface.UIThread;
import com.by122006library.MyException;
import com.by122006library.Utils.StringUtils;
import com.by122006library.Utils.ThreadUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 智能运行线程
 */
public abstract class SmartRun {
    final static String[] ignoreMethods = new String[]{
            "toString", "wait", "getClass", "hashCode", "notify", "notifyAll"
    };
    static ExpMap<Object, SmartRun> staticMap;
    ArrayList<Method> methodList;
    HashMap<Method, ThreadStyle.Style> changeThreadStyleMap;
    HashMap<Method, Boolean> changeThreadAsnycMap;

    final public static <T> void removeCache(T target) {
        if (staticMap == null) {
            staticMap = new ExpMap<>();
        }
        if (staticMap.containsKey(target)) {
            staticMap.remove(target);
        }
    }
    final public static <T> boolean sPrepare(@Nullable T target, @Nullable Object... parameter) {

        StackTraceElement[] stackTraceElement = Thread.currentThread().getStackTrace();

        if (staticMap == null) {
            staticMap = new ExpMap<>();
        }
        SmartRun smartRun;
        if (!staticMap.containsKey(target)) {
            smartRun = new SmartRun() {
                @Override
                public void action() {

                }
            };
            staticMap.put(target, smartRun);
        } else {
            smartRun = staticMap.get(target);
        }
        if (smartRun.methodList == null) {
            smartRun.methodList = new ArrayList<>();
            Class clazz1 = smartRun.getClass();
            Method[] methods1 = clazz1.getMethods();
            for (Method m : methods1) {
//                mLog.i("含有的方法：" + m.getName());
                boolean ifIgnore = false;
                for (String ignore : ignoreMethods) {
                    if (ignore.equals(m.getName())) {
                        ifIgnore = true;
                        break;
                    }
                }
                if (!ifIgnore)
                    smartRun.methodList.add(m);
            }
            Class clazz2 = target instanceof Class? (Class) target :target.getClass();
            Method[] methods2 = clazz2.getMethods();
            for (Method m : methods2) {
//                if (m.getName().contains("$")) continue;
                for (Method m2 : methods1) {
                    if (!m.getName().equals(m2.getName())) continue;
                    if (m.getParameterTypes().length != m2.getParameterTypes().length) continue;
                    Class[] classes1 = m.getParameterTypes();
                    Class[] classes2 = m2.getParameterTypes();
                    boolean ifConflict = false;
                    if (classes1.length == 0) {
                        ifConflict = true;
                        for (Method m3 : Object.class.getMethods()) {
                            if (!m.getName().equals(m3.getName())) continue;
                            if (m.getParameterTypes().length != m3.getParameterTypes().length) continue;
                            ifConflict = false;
                            break;
                        }
                    } else {
                        for (int i = 0; i < classes2.length; i++) {
                            if (classes1[i].getClass().toString().contains("java.lang.") && classes1[i].getClass()
                                    .toString()
                                    .toLowerCase().contains(classes2[i].toString())) {
                                continue;
                            }
                            if (classes2[i].getClass().toString().contains("java.lang.") && classes2[i].getClass()
                                    .toString()
                                    .toLowerCase().contains(classes1[i].toString())) {
                                continue;
                            }
                            if (!classes2[i].isAssignableFrom(classes1[i].getClass())) {
                                ifConflict = true;
                                for (Method m3 : Object.class.getMethods()) {
                                    Class[] classes3 = m3.getParameterTypes();
                                    if (!m.getName().equals(m3.getName())) continue;
                                    if (m.getParameterTypes().length != m3.getParameterTypes().length) continue;
                                    if (classes1.length == 0) {
                                        ifConflict = false;
                                    } else {
                                        for (int j = 0; j < classes3.length; j++) {
                                            if (classes2[i].getClass().equals(classes3[j].getClass())) {
                                                ifConflict = false;
                                                continue;
                                            }
                                        }
                                    }
                                    if (!ifConflict) break;
                                }
                                if (ifConflict) break;
                            }
                        }
                    }
                    if (ifConflict)
                        mLog.e("不确定方法警告:SmartRun所在运行类(" +( target instanceof Class? (Class) target :target.getClass()).getName() + ")含有：与SmartRun本身的同名同参数冲突方法("
                                + m.getName() + ")，请重命名并解决冲突");
                }
                smartRun.methodList.add(m);
            }
        }
//        for (int i = 0; i < stackTraceElement.length; i++) {
//            StackTraceElement st = stackTraceElement[i];
//              mLog.i(st.getMethodName());
//        }
        String methodName = smartRun.getMethodNameFromStackTrace(stackTraceElement);
        String lastMethodName = smartRun.getLastMethodNameFromStackTrace(stackTraceElement);
//        mLog.i("methodName=" + methodName);
//        mLog.i("lastMethodName=" + lastMethodName);
        boolean boo = !(lastMethodName.equals("chooseThreadRun") || lastMethodName.equals("invoke"));
        methodName = (methodName.equals("chooseThreadRun") || methodName.equals("invoke")) ? lastMethodName :
                methodName;
//        mLog.array(lastMethodName,methodName);
//        if (boo) mLog.i("=============\n正常转切换用反射打开 " + methodName + "(参数量" + parameter.length + ")");
//        else mLog.i("正在运行的是反射方法 " + methodName + "(参数量" + parameter.length + ")");
//        mLog.i("boo=" + boo);
        if (boo)
            smartRun.chooseThreadRun(target, methodName, parameter);

        return boo;


    }

    final private String getMethodNameFromStackTrace(StackTraceElement[] stackTraceElement) {
        String methodName = "";
        for (int i = 0; i < stackTraceElement.length; i++) {
            StackTraceElement st = stackTraceElement[i];
            if (st.isNativeMethod()) continue;
            if (st.getMethodName().contains("$")) continue;
            if (st.getMethodName().equals("prepare")) continue;
            if (st.getMethodName().equals("sPrepare")) continue;
            boolean ifhave = false;
            for (Method m : (ArrayList<Method>)methodList.clone()) {
                if (m.getName().equals(st.getMethodName())) ifhave = true;
            }

//            mLog.i("含有的方法：" + st.getMethodName() + "  have=" + ifhave);
            if (ifhave) {

                methodName = st.getMethodName();
                break;
            }
        }
//        mLog.i("调用方法：" + methodName);
        return methodName;
    }

    final private String getLastMethodNameFromStackTrace(StackTraceElement[] stackTraceElement) {
        String lastMethodName = "";
        boolean getNextOne = false;
        for (int i = 0; i < stackTraceElement.length; i++) {
            StackTraceElement st = stackTraceElement[i];
            if (st.isNativeMethod()) continue;
            if (st.getMethodName().contains("$")) continue;
            if (st.getMethodName().equals("prepare")) continue;
            if (st.getMethodName().equals("sPrepare")) continue;
            boolean ifhave = false;
            for (Method m : (ArrayList<Method>)methodList.clone()) {
                if (m.getName().equals(st.getMethodName())) ifhave = true;
            }
//            mLog.i("含有的方法：" + st.getMethodName() + "  have=" + ifhave);
            if (ifhave) {
                if (!getNextOne) {
                    getNextOne = true;
                } else {
                    lastMethodName = st.getMethodName();
                    break;
                }

            }
        }
//        mLog.i("上一个调用的方法：" + lastMethodName);
        return lastMethodName;
    }

    @ThreadStyle(style = ThreadStyle.Style.Default)
    public abstract void action();

    final public void start() {

        chooseThreadRun("action");
    }

    final public boolean prepare(Object... parameter) {

        StackTraceElement[] stackTraceElement = Thread.currentThread().getStackTrace();
        String methodName = getMethodNameFromStackTrace(stackTraceElement);
        String lastMethodName = getLastMethodNameFromStackTrace(stackTraceElement);
//        mLog.i("lastMethodName="+lastMethodName);
        boolean boo = !(lastMethodName.equals("chooseThreadRun") || lastMethodName.equals("invoke"));
        methodName = (methodName.equals("chooseThreadRun") || methodName.equals("invoke")) ? lastMethodName :
                methodName;
//        if (boo) mLog.i("=============\n正常转切换用反射打开 "+methodName+"(参数量"+parameter.length+")");
//        else mLog.i("正在运行的是反射方法 "+methodName+"(参数量"+parameter.length+")");
//        mLog.i("boo="+boo);
        if (boo)
            chooseThreadRun(methodName, parameter);

        return boo;


    }


    final private Method getMethodByParams(String methodName, Object... parameter) {
        Method method = null;
        Method method_canable = null;
        for (Method m : (ArrayList<Method>)methodList.clone()) {
            if (!m.getName().equals(methodName)) continue;
            if (method_canable == null) method_canable = m;
            Class<?>[] parameterTypes = m.getParameterTypes();
            if (parameterTypes.length != parameter.length) continue;
            boolean ifthis = true;
            for (int i = 0; i < parameterTypes.length; i++) {
                if (parameter[i] == null) {
//                    mLog.s("传入参数发现null，已忽略该位置匹配，可能结果会产生错误");
                    continue;
                }
                if (parameter[i].getClass().toString().contains("java.lang.") && parameter[i].getClass().toString()
                        .toLowerCase().contains(parameterTypes[i].toString())) {
                    continue;
                }
                if (!parameterTypes[i].isAssignableFrom(parameter[i].getClass())) {
                    ifthis = false;
                    break;
                }

            }
            if (ifthis) {
                method = m;
                break;
            }
        }
//        mLog.i("类参数量：" + parameter.length);
        if (method == null) {
            mLog.e("没有找到匹配的方法 " + methodName + "(参数数： " + parameter.length + " )");
            if (method_canable == null) {
                return method;
            } else {
                mLog.e("发现同名方法 " + method_canable.getName() + "( 参数数：" + method_canable.getParameterTypes().length +
                        " )  是否调用时未传入方法所有参数！");
                return method_canable;
            }
        }
        return method;
    }

    final public void setMethodThread(ThreadStyle.Style style, Boolean asnyc, Object... parameter) {
        StackTraceElement[] stackTraceElement = Thread.currentThread().getStackTrace();
        String methodName = getMethodNameFromStackTrace(stackTraceElement);

        Method method = getMethodByParams(methodName, parameter);

//        mLog.i("调用方法：" + method.getName());
        changeThreadStyleMap.put(method, style);
        changeThreadAsnycMap.put(method, asnyc);
    }

    final public <T> void chooseThreadRun(String methodName, final Object... parameter) {
        chooseThreadRun(this, methodName, parameter);
    }

    final public <T> void chooseThreadRun(final T target, String methodName, final Object... parameter) {
//        mLog.i("chooseThreadRun：" + parameter.length);
        if (methodName == null || methodName.length() == 0) {
            mLog.e("SmartRun 方法分析异常   "+target.getClass().toString());
            mLog.e(mLog.getCallerLocation());
            try {
                mLog.e(StringUtils.getStringFromArray(Thread.currentThread().getStackTrace()));
            }catch (Exception e){
                e.printStackTrace();
            }

            return;
        }
        methodName = methodName.replace("(", "").replace(")", "");
        ThreadStyle.Style actionThreadStyle = ThreadStyle.Style.Default;
        boolean async = false;
        if (methodList == null) {
            methodList = new ArrayList<>();

            Class clazz = getClass();
            Method[] methods = clazz.getMethods();
            for (Method m : methods) {
//                mLog.i("含有的方法：" + m.getName());
                methodList.add(m);
            }
        }
        Method method = getMethodByParams(methodName, parameter);
        try {
            Annotation[] annotation = method.getAnnotations();
            if (annotation != null)
                for (Annotation a : annotation) {
                    if (a instanceof ThreadStyle) {
                        actionThreadStyle = ((ThreadStyle) a).style();
                    }
                    String annotationName = a.annotationType().getSimpleName();
                    if (a instanceof UIThread || annotationName.toLowerCase().equals("com.by122006library.Interface.UIThread".toLowerCase())) {
                        actionThreadStyle = ThreadStyle.Style.UI;
                    }
                    if (a instanceof BGThread || annotationName.toLowerCase().equals("com.by122006library.Interface.BGThread".toLowerCase())) {
                        actionThreadStyle = ThreadStyle.Style.BG;
                    }
                    if (a instanceof DefaultThread || annotationName.toLowerCase().equals("DefaultThread".toLowerCase
                            ())) {
                        actionThreadStyle = ThreadStyle.Style.Default;
                    }
                    if (a instanceof Async) {
                        async = true;
                    }
                }
        } catch (Exception e) {
            e.printStackTrace();
            mLog.e("没有发现" + methodName + "方法，请不要对SmartRun类进行混淆或需要检查是否已传递该方法所有参数");
            return;
        }
        if (changeThreadStyleMap != null && changeThreadStyleMap.containsKey(method)) {
            actionThreadStyle = changeThreadStyleMap.get(method);
        }
        if (changeThreadAsnycMap != null && changeThreadAsnycMap.containsKey(method)) {
            async = changeThreadAsnycMap.get(method);
        }
        final boolean finalAsync = async;

        if (actionThreadStyle == ThreadStyle.Style.Default) {
            actionThreadStyle = ThreadUtils.getThreadStytle();
        }

        final Method finalMethod = method;
        if (actionThreadStyle == ThreadStyle.Style.BG && ThreadUtils.isBGThread()) {
            if (!finalAsync)
                invoke(target, method, parameter);

            else {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        invoke(target, finalMethod, parameter);

                    }
                }).start();
            }
            return;
        }
        if (actionThreadStyle == ThreadStyle.Style.UI && ThreadUtils.isUIThread()) {
            if (!finalAsync)
                invoke(target, method, parameter);

            else try {
                ThreadUtils.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        invoke(target, finalMethod, parameter);

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
                    invoke(target, finalMethod, parameter);

                }
            }).start();
            return;
        }
        if (ThreadUtils.isBGThread() && actionThreadStyle == ThreadStyle.Style.UI) {
            try {
                ThreadUtils.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        invoke(target, finalMethod, parameter);

                    }
                });
            } catch (MyException e) {
                e.printStackTrace();
            }
            return;
        }
    }

    final public void invoke(final Method method, final Object... parameter) {
        invoke(false, method, parameter);
    }

    final public void invoke(boolean haveException, final Method method, final Object... parameter) {
        invoke(this, haveException, method, parameter);
    }

    final public <T> void invoke(@Nullable T target, final Method method, final Object... parameter) {
        invoke(target, false, method, parameter);
    }

    final public <T> void invoke(@Nullable final T target, boolean haveException, final Method method, final Object...
            parameter) {
//        mLog.i(ThreadUtils.getThreadStytle().toString() + "线程中运行方法：" + method.getName() + "(参数量：" + parameter.length
//                + ")" + Thread.currentThread().toString());
        try {
            method.invoke(target, parameter);
            if (haveException) {
                mLog.i("(" + method.getName() + ") " + "由线程错误引发的代码错误已修复，该方法已被正确运行完成，且在该次实例中将不会再次出现。\n建议开发者进行代码修复");
                if (changeThreadStyleMap == null) changeThreadStyleMap = new HashMap<>();
                changeThreadStyleMap.put(method, ThreadUtils.getThreadStytle());
            }
//            mLog.i("方法：" + method.getName() + "(参数量：" + parameter.length
//                    + ") 成功运行在" + ThreadUtils.getThreadStytle().toString() + "线程");
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            try {
                throw new Exception(e.getTargetException());// 获取目标异常
            } catch (Exception e1) {
                if (haveException) {
                    mLog.e("(" + method.getName() + ") " + "代码错误：线程错误且尝试修复失败");
                    return;
                }
                if (e1.getMessage().contains("CalledFromWrongThreadException")) {
                    mLog.e("(" + method.getName() + ") " + "代码错误：不能在子线程中更新UI");
                    mLog.e("(" + method.getName() + ") " + "正在切换线程并重启方法以尝试修复");
                    try {
                        ThreadUtils.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                invoke(target, true, method, parameter);
                            }
                        });
                    } catch (MyException e2) {
                        e2.printStackTrace();
                    }
                    return;
                }
                if (e1.getMessage().contains("NetworkOnMainThreadException")) {
                    mLog.e("(" + method.getName() + ") " + "代码错误：不能在主线程中进行网络连接");
                    mLog.e("(" + method.getName() + ") " + "正在切换线程并重启方法以尝试修复");
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            invoke(target, true, method, parameter);
                        }
                    }).start();
                    return;
                }

            } finally {
                if (ThreadUtils.isBGThread()) Thread.currentThread().interrupt();
            }

        }
    }

    public void sleep(long sleeptime) {
        if (ThreadUtils.isUIThread()) mLog.e("请避免在UI线程进行sleep操作");
        try {
            Thread.sleep(sleeptime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
