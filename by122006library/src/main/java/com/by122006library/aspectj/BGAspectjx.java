package com.by122006library.aspectj;

import com.by122006library.Interface.Async;
import com.by122006library.Interface.NoProguard_All;
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
public class BGAspectjx implements NoProguard_All{

    @Pointcut("execution(@com.by122006library.Interface.BGThread * *(..))")
    public void DebugToolMethod() {
    }

    @Around("DebugToolMethod()")
    public Object onDebugToolMethodBefore(final ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) proceedingJoinPoint.getSignature();
        Method method = signature.getMethod();

        Async async = method.getAnnotation(Async.class);
        if(ThreadUtils.isUIThread()||async!=null){
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
        }else
        return proceedingJoinPoint.proceed();
    }
}
