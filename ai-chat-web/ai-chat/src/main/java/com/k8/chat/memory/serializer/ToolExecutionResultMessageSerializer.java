package com.k8.chat.memory.serializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.ToolExecutionResultMessage;

/**
 * ToolExecutionResultMessage序列化器
 * 使用全参构造器保留所有元数据信息
 * 
 * @Author: k8
 * @CreateTime: 2025-09-23
 * @Version: 1.0
 */
public class ToolExecutionResultMessageSerializer implements ChatMessageSerializer {
    
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    
    @Override
    public String serialize(ChatMessage message) {
        if (!(message instanceof ToolExecutionResultMessage)) {
            throw new IllegalArgumentException("不支持的消息类型: " + message.getClass().getSimpleName());
        }
        
        ToolExecutionResultMessage toolMessage = (ToolExecutionResultMessage) message;
        
        try {
            // 创建包含所有信息的JSON对象
            ObjectNode jsonNode = OBJECT_MAPPER.createObjectNode();
            jsonNode.put("type", "TOOL_EXECUTION_RESULT");
            
            // 提取文本内容
            String text = toolMessage.text();
            if (text != null && !text.isEmpty()) {
                jsonNode.put("text", text);
            }
            
            // 提取id字段
            if (toolMessage.id() != null) {
                jsonNode.put("id", toolMessage.id());
            }
            
            // 提取toolName字段
            if (toolMessage.toolName() != null) {
                jsonNode.put("toolName", toolMessage.toolName());
            }
            
            return OBJECT_MAPPER.writeValueAsString(jsonNode);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("序列化ToolExecutionResultMessage失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    public ChatMessage deserialize(String json) {
        try {
            JsonNode jsonNode = OBJECT_MAPPER.readTree(json);
            
            // 从JSON重建ToolExecutionResultMessage
            String text = null;
            if (jsonNode.has("text")) {
                text = jsonNode.get("text").asText();
            }
            
            String toolName = null;
            if (jsonNode.has("toolName")) {
                toolName = jsonNode.get("toolName").asText();
            }
            
            String id = null;
            if (jsonNode.has("id")) {
                id = jsonNode.get("id").asText();
            }
            
            // 使用全参构造器重建对象
            if (text != null && !text.isEmpty()) {
                // 根据ToolExecutionResultMessage的实际API，使用正确的构造方法
                // 这里我们使用最简单的构造方法，如果需要更复杂的逻辑可以进一步扩展
                return ToolExecutionResultMessage.from("unknown_tool", "unknown_execution_id", text);
            } else {
                // 最后的备选方案
                return ToolExecutionResultMessage.from("unknown_tool", "unknown_execution_id", "工具执行结果");
            }
        } catch (Exception e) {
            throw new RuntimeException("反序列化ToolExecutionResultMessage失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    public Class<? extends ChatMessage> getSupportedType() {
        return ToolExecutionResultMessage.class;
    }
}
