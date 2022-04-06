package com.hua.community.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用来标识一个访问路径是否需要登录才能访问
 * @create 2022-03-27 14:00
 */
@Target(ElementType.METHOD) //限定注释使用位置，java.lang annotation ElementType枚举类指定 表示此注解可以在方法上使用
@Retention(RetentionPolicy.RUNTIME) //java.lang.annotation.RetentionPolicy枚举类指定，表示此注解仅在运行时生效
public @interface LoginRequired {
}
