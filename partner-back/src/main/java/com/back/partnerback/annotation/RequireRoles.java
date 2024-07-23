package com.back.partnerback.annotation;

import com.back.partnerback.constant.StatusConstant;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author yang
 * @create 2024-05-12 17:57
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD,ElementType.TYPE})
public @interface RequireRoles {

    String[] value() default {StatusConstant.ADMIN_ROLE};

}
