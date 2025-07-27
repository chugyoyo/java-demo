package com.chugyoyo.checker;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE) // 只能用在类上
@Retention(RetentionPolicy.SOURCE) // 源码级别保留
public @interface SpringCodeCheck {
}
