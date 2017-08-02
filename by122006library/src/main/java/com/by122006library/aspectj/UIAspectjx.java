package com.by122006library.aspectj;

import com.by122006library.Interface.NoProguard_All;
import com.by122006library.Utils.ThreadUtils;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

/**
 * Created by admin on 2017/7/31.
 */
@Aspect
public class UIAspectjx implements NoProguard_All {

    @Pointcut("execution(@com.by122006library.Interface.UIThread * *(..))||execution(@android.support.annotation.UiThread * *(..))")
    public void DebugToolMethod() {

    }

    @Around("DebugToolMethod()")
    public Object onDebugToolMethodBefore(final ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        if(!ThreadUtils.isUIThread()){
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
        }else
        return proceedingJoinPoint.proceed();
    }
}
