package com.k8.config;

import com.k8.chat.memory.MapperMemoryStore;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: k8
 * @CreateTime: 2025-09-23
 * @Version: 1.0
 */
@Configuration
public class ChatMemoryConfig {
    private static int max_messages = 20;


    @Bean
    public ChatMemoryProvider localChatMemoryProvider() {
        return memoryId -> MessageWindowChatMemory.builder()
                .chatMemoryStore(chatMemoryStore())
                .id(memoryId)
                .maxMessages(max_messages)
                .build();
    }

    @Bean
    public ChatMemoryStore chatMemoryStore(){
        return new MapperMemoryStore();
    }

}
