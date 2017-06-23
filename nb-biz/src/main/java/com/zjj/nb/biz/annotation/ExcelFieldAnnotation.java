package com.zjj.nb.biz.annotation;

import java.lang.annotation.*;

/**
 * Created by jinju.zeng on 2017/6/13.
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target({ElementType.FIELD})
public @interface ExcelFieldAnnotation {
    /**
     * excel文件中的字段名
     * @return
     */
    String ExcelField() default "";
}
