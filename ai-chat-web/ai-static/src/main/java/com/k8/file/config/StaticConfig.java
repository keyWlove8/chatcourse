package com.k8.file.config;

import com.k8.file.properties.K8StaticProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: k8
 * @CreateTime: 2025-09-23
 * @Version: 1.0
 */
@Configuration
@EnableConfigurationProperties(K8StaticProperties.class)
public class StaticConfig {
}
