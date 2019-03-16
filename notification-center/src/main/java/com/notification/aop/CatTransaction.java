package com.notification.aop;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author:youb
 * @date:2018/11/15
 * @desc:主要用来在cat中产生消息
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface CatTransaction {
    /**
     * 消息标题
     *
     * @return
     */
    String title() default "";

    /**
     *
     * @return
     */
    String projectName() default "";
}
