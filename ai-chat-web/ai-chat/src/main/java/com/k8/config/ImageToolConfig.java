package com.k8.config;

import com.k8.bootPlugin.ai.store.ChatMemoryStore;
import com.k8.bootPlugin.ai.store.LocalChatMemoryStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: k8
 * @CreateTime: 2025-08-06
 * @Version: 1.0
 */
@Configuration
public class ImageToolConfig {
    @Bean
    public ChatMemoryStore chatMemoryStore1(){
        return new LocalChatMemoryStore();
    }
}
