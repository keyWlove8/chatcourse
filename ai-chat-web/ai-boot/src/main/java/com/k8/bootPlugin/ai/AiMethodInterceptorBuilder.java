package com.k8.bootPlugin.ai;

import com.k8.bootPlugin.proxy.MethodInterceptor;

/**
 * 正常来说是每个服务在生成代理类的时候都会创建一个拦截器实例
 * 每一个拦截器会生成该服务对应的方法映射表，不同服务是不同的，所以一定不能使用同一个
 * @Author: k8
 * @CreateTime: 2025-09-22
 * @Version: 1.0
 */
public interface AiMethodInterceptorBuilder {
    MethodInterceptor build(AiProxyStarter aiProxyStarter);
}
