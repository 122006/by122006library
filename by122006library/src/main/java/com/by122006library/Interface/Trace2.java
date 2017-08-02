package com.by122006library.Interface;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.METHOD;

/**
 * Created by admin on 2017/8/1.
 */

@Target({METHOD, CONSTRUCTOR})
@Retention(RetentionPolicy.CLASS)
public @interface Trace2 {
}
