package com.k8.bootPlugin.ai;

import com.k8.bootPlugin.ai.springboot.AiServiceRegistry;
import com.k8.bootPlugin.ai.springboot.properties.AiProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: k8
 * @CreateTime: 2025-09-22
 * @Version: 1.0
 */
@Configuration
@EnableConfigurationProperties(AiProperties.class)
public class AiAutoConfiguration {
    @Bean
    @ConditionalOnBean(AiMethodInterceptorBuilder.class)
    public AiServiceRegistry aiServiceRegistry() {
        return new AiServiceRegistry();
    }
}
