package com.k8.bootPlugin.proxy;

import java.lang.reflect.Method;

/**
 * @Author: k8
 * @CreateTime: 2025-09-23
 * @Version: 1.0
 */
public interface MethodInterceptor {
    Object process(Object proxy, Method method, Object[] args);


    /**
     * 对拦截器进行初始化，准备参数之类的
     */
    void init(Class<?> interfaceClazz);
}
