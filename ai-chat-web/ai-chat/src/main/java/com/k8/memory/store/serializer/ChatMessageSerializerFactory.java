package com.k8.memory.store.serializer;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ToolExecutionResultMessage;

import java.util.HashMap;
import java.util.Map;

/**
 * ChatMessage序列化器工厂
 * 根据消息类型提供对应的序列化器
 * 
 * @Author: k8
 * @CreateTime: 2025-08-28
 * @Version: 1.0
 */
public class ChatMessageSerializerFactory {
    
    private static final Map<Class<? extends ChatMessage>, ChatMessageSerializer> SERIALIZERS = new HashMap<>();
    
    static {
        // 注册所有序列化器
        SERIALIZERS.put(SystemMessage.class, new SystemMessageSerializer());
        SERIALIZERS.put(UserMessage.class, new UserMessageSerializer());
        SERIALIZERS.put(AiMessage.class, new AiMessageSerializer());
        SERIALIZERS.put(ToolExecutionResultMessage.class, new ToolExecutionResultMessageSerializer());
    }
    
    /**
     * 根据消息类型获取对应的序列化器
     * 
     * @param messageType 消息类型Class
     * @return 对应的序列化器
     * @throws IllegalArgumentException 如果找不到对应的序列化器
     */
    public static ChatMessageSerializer getSerializer(Class<? extends ChatMessage> messageType) {
        ChatMessageSerializer serializer = SERIALIZERS.get(messageType);
        if (serializer == null) {
            throw new IllegalArgumentException("未找到消息类型 " + messageType.getSimpleName() + " 的序列化器");
        }
        return serializer;
    }
    
    /**
     * 根据消息对象获取对应的序列化器
     * 
     * @param message 消息对象
     * @return 对应的序列化器
     */
    public static ChatMessageSerializer getSerializer(ChatMessage message) {
        return getSerializer(message.getClass());
    }
    
    /**
     * 检查是否支持指定的消息类型
     * 
     * @param messageType 消息类型Class
     * @return 是否支持
     */
    public static boolean isSupported(Class<? extends ChatMessage> messageType) {
        return SERIALIZERS.containsKey(messageType);
    }
}
