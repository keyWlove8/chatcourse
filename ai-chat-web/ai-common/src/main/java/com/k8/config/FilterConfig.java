package com.k8.config;

import com.k8.auth.simple.filter.AuthFilter;
import com.k8.cache.TokenCache;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Filter配置类
 * 用于在Tomcat服务器上注册Filter（ai-chat使用）
 * @Author: k8
 * @CreateTime: 2025-09-23
 * @Version: 1.0
 */
@Configuration
public class FilterConfig {

    /**
     * 注册AuthFilter
     * 使用FilterRegistrationBean确保在Tomcat上正常工作
     * 
     * 注意：context-path为/，所以拦截路径直接使用/*
     */
    @Bean
    public FilterRegistrationBean<AuthFilter> authFilterRegistration(TokenCache tokenCache) {
        FilterRegistrationBean<AuthFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new AuthFilter(tokenCache));
        
        // 拦截所有路径，包括/api/*和/download/*
        registration.addUrlPatterns("/*");
        
        registration.setName("authFilter");
        registration.setOrder(1); // 设置优先级，数字越小优先级越高
        
        // 确保Filter在Tomcat上正确工作
        registration.setEnabled(true);
        
        return registration;
    }
}
