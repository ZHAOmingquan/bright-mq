package com.mzy.brightmq.annotation;

import com.mzy.brightmq.beans.enums.DBEnum;

import java.lang.annotation.*;

/**
 *  com.mzy.brightmq.annotation
 * @author BrightSpring
 *10
 */
@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DS {
    DBEnum type() default DBEnum.DEFAULT_DB;
}
