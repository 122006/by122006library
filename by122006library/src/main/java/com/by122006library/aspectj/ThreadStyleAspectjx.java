package com.by122006library.aspectj;

import com.by122006library.Interface.Async;
import com.by122006library.Interface.NoProguard_All;
import com.by122006library.Interface.ThreadStyle;
import com.by122006library.Utils.ThreadUtils;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;

/**
 * Created by admin on 2017/7/31.
 */
@Aspect
public class ThreadStyleAspectjx implements NoProguard_All {

    @Pointcut("execution(@com.by122006library.Interface.ThreadStyle * *(..))")
    public void DebugToolMethod() {
    }

    @Around("DebugToolMethod()")
    public Object onDebugToolMethodBefore(final ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) proceedingJoinPoint.getSignature();
        Method method = signature.getMethod();

        ThreadStyle threadStyle = method.getAnnotation(ThreadStyle.class);


        if (ThreadUtils.isUIThread() && threadStyle.style() == ThreadStyle.Style.UI) {
            return proceedingJoinPoint.proceed();
        }
        if (ThreadUtils.isUIThread() && threadStyle.style() == ThreadStyle.Style.BG) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        proceedingJoinPoint.proceed();
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                    }
                }
            }).start();
            return null;
        }
        if (ThreadUtils.isBGThread() && threadStyle.style() == ThreadStyle.Style.UI) {
            ThreadUtils.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        proceedingJoinPoint.proceed();
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                    }
                }
            });
            return null;
        }
        Async async = method.getAnnotation(Async.class);
        if (ThreadUtils.isBGThread() && threadStyle.style() == ThreadStyle.Style.BG) {
            if (async != null) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            proceedingJoinPoint.proceed();
                        } catch (Throwable throwable) {
                            throwable.printStackTrace();
                        }
                    }
                }).start();
                return null;
            }

            return proceedingJoinPoint.proceed();
        }
        return null;
    }
}
