package com.by122006.annotation;

/**
 * Created by admin on 2017/6/6.
 */

public @interface Attribute {
    public String name();
    public Class type();
    public String defaultValue();
}

