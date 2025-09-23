package com.k8.bootPlugin.ai.springboot;

import com.k8.bootPlugin.ai.AiContext;
import com.k8.bootPlugin.ai.AiMethodInterceptorBuilder;
import com.k8.bootPlugin.ai.AiProxyStarter;
import com.k8.bootPlugin.ai.ChatMemoryStore;
import com.k8.bootPlugin.ai.impl.DefaultAiMethodContextBuilder;
import com.k8.bootPlugin.proxy.ProxyFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * @Author: k8
 * @CreateTime: 2025-09-22
 * @Version: 1.0
 */
public class AiProxyFactoryBean implements FactoryBean<Object>, ApplicationContextAware {
    private final Class<?> interfaceType;
    private final AiContext aiContext;
    private final ProxyFactory proxyFactory = new ProxyFactory();
    private ChatMemoryStore chatMemoryStore;

    private final AiMethodInterceptorBuilder interceptorBuilder;

    private ApplicationContext applicationContext;

    public AiProxyFactoryBean(Class<?> interfaceType, AiContext aiContext, AiMethodInterceptorBuilder interceptorBuilder, ChatMemoryStore chatMemoryStore) {
        this.interfaceType = interfaceType;
        this.aiContext = aiContext;
        this.interceptorBuilder = interceptorBuilder;
        this.chatMemoryStore = chatMemoryStore;
    }

    @Override
    public Object getObject() throws Exception {
        AiProxyStarter aiProxyStarter = new AiProxyStarter();
        aiProxyStarter.setAiContext(aiContext);
        aiProxyStarter.setChatMemoryStore(chatMemoryStore);
        return proxyFactory.buildProxy(interfaceType, interceptorBuilder.build(aiProxyStarter));
    }

    @Override
    public Class<?> getObjectType() {
        return interfaceType;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
