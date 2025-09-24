package com.k8.bootPlugin.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * @Author: k8
 * @CreateTime: 2025-09-23
 * @Version: 1.0
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ToolService {
    String modelName();

    /**
     * 用于配置model独一的key，如果不填写则使用modelName
     * @return
     */
    String modelKey() default "";


    String interceptorBuilder();

    String chatMemoryStore() default "";
}
