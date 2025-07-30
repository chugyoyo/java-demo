package com.chugyoyo.tool.javabase.annotation.runtime;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({
        ElementType.TYPE,
        ElementType.METHOD
})
@Retention(RetentionPolicy.RUNTIME)
public @interface PrintLog {

    Level level() default Level.INFO; //

    enum Level {
        INFO, DEBUG, ERROR
    }
}
