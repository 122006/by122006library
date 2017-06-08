package com.by122006library.Interface;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.SOURCE;

/**
 * 该方法不支持脱离框架调用<p>可能会导致抛出异常、后台报错、功能无法使用等情况<p> 如有需求请自行进行修改，或者联系作者进行第三方适配
 */
@Retention(SOURCE)
public @interface SpecialMethod {

}
