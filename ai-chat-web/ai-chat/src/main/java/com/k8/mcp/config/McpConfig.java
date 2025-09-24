package com.k8.mcp.config;

import com.k8.mcp.SearchMCPClient;
import com.k8.mcp.properties.McpProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: k8
 * @CreateTime: 2025-09-23
 * @Version: 1.0
 */
@Configuration
@EnableConfigurationProperties(McpProperties.class)
public class McpConfig {
    @Bean
    public SearchMCPClient searchMCPClient(){
        return new SearchMCPClient();
    }
}
