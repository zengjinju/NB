package com.zjj.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by admin on 17/2/22.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Component
public @interface IgnoreCheckAnnotation {
    //是否需要忽略check,默认不忽略
    boolean isIgnore() default false;
}
