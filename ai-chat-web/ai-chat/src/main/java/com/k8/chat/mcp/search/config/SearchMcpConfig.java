package com.k8.chat.mcp.search.config;

import com.k8.chat.mcp.search.SearchMCPClient;
import com.k8.chat.mcp.search.properties.SearchMcpProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: k8
 * @CreateTime: 2025-09-23
 * @Version: 1.0
 */
@Configuration
@EnableConfigurationProperties(SearchMcpProperties.class)
public class SearchMcpConfig {
    @Bean
    public SearchMCPClient searchMCPClient(){
        return new SearchMCPClient();
    }
}
