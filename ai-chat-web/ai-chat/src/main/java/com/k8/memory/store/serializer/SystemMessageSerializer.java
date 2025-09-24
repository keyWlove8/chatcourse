package com.k8.memory.store.serializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;

/**
 * SystemMessage序列化器
 * 使用全参构造器保留所有元数据信息
 * 
 * @Author: k8
 * @CreateTime: 2025-09-23
 * @Version: 1.0
 */
public class SystemMessageSerializer implements ChatMessageSerializer {
    
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    
    @Override
    public String serialize(ChatMessage message) {
        if (!(message instanceof SystemMessage)) {
            throw new IllegalArgumentException("不支持的消息类型: " + message.getClass().getSimpleName());
        }
        
        SystemMessage systemMessage = (SystemMessage) message;
        
        try {
            // 创建包含所有信息的JSON对象
            ObjectNode jsonNode = OBJECT_MAPPER.createObjectNode();
            jsonNode.put("type", "SYSTEM");
            
            // 提取文本内容
            String text = systemMessage.text();
            if (text != null && !text.isEmpty()) {
                jsonNode.put("text", text);
            }
            
            return OBJECT_MAPPER.writeValueAsString(jsonNode);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("序列化SystemMessage失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    public ChatMessage deserialize(String json) {
        try {
            JsonNode jsonNode = OBJECT_MAPPER.readTree(json);
            
            // 从JSON重建SystemMessage
            if (jsonNode.has("text")) {
                return SystemMessage.from(jsonNode.get("text").asText());
            }
            
            // 最后的备选方案
            return SystemMessage.from("系统消息");
        } catch (Exception e) {
            throw new RuntimeException("反序列化SystemMessage失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    public Class<? extends ChatMessage> getSupportedType() {
        return SystemMessage.class;
    }
}
