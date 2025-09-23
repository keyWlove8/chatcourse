package com.k8.bootPlugin.ai.springboot.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import java.util.Map;

/**
 * @Author: k8
 * @CreateTime: 2025-09-22
 * @Version: 1.0
 */
@Data
@ConfigurationProperties(prefix = "k8.ai")
public class AiProperties {
    private String basePackage;
    private String globalApiKey;
    @NestedConfigurationProperty
    private Map<String,String> modelKeyToApiKey;
    @NestedConfigurationProperty
    private Map<String,String> extendPropertiesKey;
}
