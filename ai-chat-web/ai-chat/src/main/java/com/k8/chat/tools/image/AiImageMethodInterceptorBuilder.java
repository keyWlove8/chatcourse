package com.k8.chat.tools.image;


import com.k8.bootPlugin.ai.AiMethodInterceptorBuilder;
import com.k8.bootPlugin.ai.AiProxyStarter;
import com.k8.bootPlugin.proxy.MethodInterceptor;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicReference;

/**
 * @Author: k8
 * @CreateTime: 2025-09-23
 * @Version: 1.0
 */
@Component
public class AiImageMethodInterceptorBuilder implements AiMethodInterceptorBuilder {
    private AtomicReference<MethodInterceptor> methodInterceptorAtomicReference = new AtomicReference<>();
    @Override
    public MethodInterceptor build(AiProxyStarter aiProxyStarter) {
        if (methodInterceptorAtomicReference.getAcquire() != null){
            return methodInterceptorAtomicReference.get();
        }
        synchronized (this){
            if (methodInterceptorAtomicReference.getAcquire() != null){
                return methodInterceptorAtomicReference.get();
            }
            AiImageChatEnhancerInterceptor interceptor = new AiImageChatEnhancerInterceptor(aiProxyStarter);
            methodInterceptorAtomicReference.set(interceptor);
            return interceptor;
        }
    }
}
