package com.k8.file.config;

import com.k8.auth.simple.filter.AuthFilter;
import com.k8.cache.TokenCache;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Undertow Filter配置类
 * 使用FilterRegistrationBean，但添加@Primary确保优先使用
 * @Author: k8
 * @CreateTime: 2025-09-23
 * @Version: 1.0
 */
@Configuration
public class UndertowFilterConfig {

    /**
     * 注册AuthFilter
     * 使用不同的Bean名称避免冲突
     */
    @Bean("undertowAuthFilterRegistration")
    public FilterRegistrationBean<AuthFilter> undertowAuthFilterRegistration(TokenCache tokenCache) {
        System.out.println("=== 正在注册AuthFilter (Undertow) ===");
        
        FilterRegistrationBean<AuthFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new AuthFilter(tokenCache));
        
        // 拦截所有路径
        registration.addUrlPatterns("/*");
        
        registration.setName("undertowAuthFilter");
        registration.setOrder(1);
        registration.setEnabled(true);
        
        System.out.println("=== AuthFilter注册完成 (Undertow) ===");
        return registration;
    }
}
