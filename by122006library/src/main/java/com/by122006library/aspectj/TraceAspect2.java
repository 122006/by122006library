package com.by122006library.aspectj;

import com.by122006library.Functions.mLog;
import com.by122006library.Interface.NoProguard_All;
import com.by122006library.Utils.StopWatch2;
import com.safframework.log.L;
import com.safframework.tony.common.utils.Preconditions;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

/**
 * Created by admin on 2017/8/1.
 */
@Aspect
public class TraceAspect2 {
    private static final String POINTCUT_METHOD = "execution(@com.by122006library.Interface.Trace2 * *(..))";

    private static final String POINTCUT_CONSTRUCTOR = "execution(@com.by122006library.Interface.Trace2 *.new(..))";

    @Pointcut(POINTCUT_METHOD)
    public void methodAnnotatedWithTrace() {
    }

    @Pointcut(POINTCUT_CONSTRUCTOR)
    public void constructorAnnotatedTrace() {
    }

    @Around("methodAnnotatedWithTrace() || constructorAnnotatedTrace()")
    public Object traceMethod(final ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        String className = methodSignature.getDeclaringType().getSimpleName();
        String methodName = methodSignature.getName();
        final StopWatch2 stopWatch = new StopWatch2();
        stopWatch.start();
        Object result = joinPoint.proceed();
        stopWatch.stop();

        if (Preconditions.isBlank(className)) {
            className = "Anonymous class";
        }

        mLog.i(buildLogMessage(methodName, stopWatch.getTotalTimeMillis()));

        return result;
    }
    private static final int ns=1000*1000;

    /**
     * Create a log message.
     *
     * @param methodName A string with the method name.
     * @param methodDuration Duration of the method in milliseconds.
     * @return A string representing message.
     */
    private static String buildLogMessage(String methodName, long methodDuration) {
        if (methodDuration>10*ns){
            return String.format("%s() take %d ms", methodName,methodDuration/ns);
        }else if (methodDuration>ns){
            return String.format("%s() take %dms %dμs", methodName,methodDuration/ns,methodDuration%ns);
        }else{
            return String.format("%s() take %dμs", methodName,methodDuration%ns);
        }
    }
}
