package com.k8.chat.memory.serializer;

import dev.langchain4j.data.message.ChatMessage;

/**
 * LangChain4j消息序列化器接口
 * 用于序列化和反序列化不同类型的ChatMessage
 * 
 * @Author: k8
 * @CreateTime: 2025-09-23
 * @Version: 1.0
 */
public interface ChatMessageSerializer {
    
    /**
     * 序列化ChatMessage为JSON字符串
     * 保留所有元数据信息
     * 
     * @param message 要序列化的消息
     * @return JSON字符串
     */
    String serialize(ChatMessage message);
    
    /**
     * 从JSON字符串反序列化为ChatMessage
     * 使用全参构造器重建完整对象
     * 
     * @param json JSON字符串
     * @return 重建的ChatMessage对象
     */
    ChatMessage deserialize(String json);
    
    /**
     * 获取此序列化器支持的消息类型
     * 
     * @return 支持的消息类型Class
     */
    Class<? extends ChatMessage> getSupportedType();
}
