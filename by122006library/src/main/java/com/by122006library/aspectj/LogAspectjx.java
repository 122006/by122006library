package com.by122006library.aspectj;

import com.by122006library.Functions.mLog;
import com.by122006library.Interface.NoProguard_All;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Pointcut;

/**
 * Created by admin on 2017/7/31.
 */
//@Aspect
@Deprecated
public class LogAspectjx implements NoProguard_All {

    @Pointcut("execution(* android.util.Log.*(java.lang.String,java.lang.String))")
    public void DebugToolMethod() {

    }

    @Around("DebugToolMethod()")
    public Object onDebugToolMethodBefore(final ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        mLog.i("Log has been hook!");
        String name = proceedingJoinPoint.getSignature().getName();
        String value=proceedingJoinPoint.getArgs()[1].toString();
        switch (name) {
            case "i":
                mLog.i(value);
                break;
            case "w":
                mLog.w(value);
                break;
            case "e":
                mLog.e(value);
                break;
            case "v":
                mLog.v(value);
                break;
            case "d":
                mLog.d(value);
                break;
            default:
                mLog.i(value);
                break;

        }
        return 0;
    }
}
