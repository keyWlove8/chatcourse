package com.k8.service.ai;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import dev.langchain4j.service.spring.AiService;
import dev.langchain4j.service.spring.AiServiceWiringMode;

/**
 * 动态聊天服务 - 支持动态系统提示词
 * @Author: k8
 * @CreateTime: 2025-01-01
 * @Version: 1.0
 */
@AiService(wiringMode = AiServiceWiringMode.EXPLICIT,
        chatModel = "openAiChatModel",
        chatMemoryProvider = "localChatMemoryProvider",
        contentRetriever = "contentRetriever2",
        tools = {"searchToolServiceImpl", "chat2ImageAdapterService"}
)
public interface DynamicChatService {
    /**
     * 动态聊天接口，支持自定义系统提示词
     * @param userMessage 用户消息
     * @param memoryId 记忆ID
     * @param systemPrompt 系统提示词
     * @return AI回复
     */
    @SystemMessage("你现在在进行角色扮演，按照以下对人物的介绍回答用户的消息：{{systemPrompt}}")
    String chat(@UserMessage("用户发出的消息") String userMessage,
                @MemoryId String memoryId,
                @V("systemPrompt") String systemPrompt);
}
