package com.example.demo.annotation;

import java.lang.annotation.*;

/**
 * 权限验证注解
 * 用于Controller方法上，指定需要的权限编码
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequirePermission {
    /**
     * 需要的权限编码
     */
    String value();
    
    /**
     * 是否需要所有权限（多个权限时）
     */
    boolean requireAll() default false;
}
