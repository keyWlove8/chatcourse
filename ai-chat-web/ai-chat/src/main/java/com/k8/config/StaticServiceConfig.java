package com.k8.config;

import com.k8.properties.StaticServiceProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 静态文件服务配置类
 * 
 * @Author: k8
 * @CreateTime: 2025-08-29
 * @Version: 1.0
 */
@Configuration
@EnableConfigurationProperties(StaticServiceProperties.class)
public class StaticServiceConfig {
}


