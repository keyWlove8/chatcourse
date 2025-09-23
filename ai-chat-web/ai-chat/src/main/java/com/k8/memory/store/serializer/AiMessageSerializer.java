package com.k8.memory.store.serializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.agent.tool.ToolExecutionRequest;

import java.util.List;

/**
 * AiMessage序列化器
 * 使用全参构造器保留所有元数据信息，包括toolExecutionRequests
 * 
 * @Author: k8
 * @CreateTime: 2025-08-28
 * @Version: 1.0
 */
public class AiMessageSerializer implements ChatMessageSerializer {
    
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    
    @Override
    public String serialize(ChatMessage message) {
        if (!(message instanceof AiMessage)) {
            throw new IllegalArgumentException("不支持的消息类型: " + message.getClass().getSimpleName());
        }
        
        AiMessage aiMessage = (AiMessage) message;
        
        try {
            // 创建包含所有信息的JSON对象
            ObjectNode jsonNode = OBJECT_MAPPER.createObjectNode();
            jsonNode.put("type", "AI");
            
            // 提取文本内容
            String text = aiMessage.text();
            if (text != null && !text.isEmpty()) {
                jsonNode.put("text", text);
            }
            
            // 手动序列化工具执行请求列表，避免Jackson序列化问题
            List<ToolExecutionRequest> toolExecutionRequests = aiMessage.toolExecutionRequests();
            if (toolExecutionRequests != null && !toolExecutionRequests.isEmpty()) {
                ArrayNode toolRequestsArray = jsonNode.putArray("toolExecutionRequests");
                for (ToolExecutionRequest request : toolExecutionRequests) {
                    ObjectNode requestNode = OBJECT_MAPPER.createObjectNode();
                    // 手动提取ToolExecutionRequest的属性
                    if (request.id() != null) {
                        requestNode.put("id", request.id());
                    }
                    if (request.name() != null) {
                        requestNode.put("name", request.name());
                    }
                    if (request.arguments() != null) {
                        requestNode.put("arguments", request.arguments());
                    }
                    toolRequestsArray.add(requestNode);
                }
            }
            
            return OBJECT_MAPPER.writeValueAsString(jsonNode);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("序列化AiMessage失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    public ChatMessage deserialize(String json) {
        try {
            JsonNode jsonNode = OBJECT_MAPPER.readTree(json);
            
            // 从JSON重建AiMessage
            String text = null;
            if (jsonNode.has("text")) {
                text = jsonNode.get("text").asText();
            }
            
            List<ToolExecutionRequest> toolExecutionRequests = null;
            if (jsonNode.has("toolExecutionRequests")) {
                ArrayNode toolRequestsArray = (ArrayNode) jsonNode.get("toolExecutionRequests");
                toolExecutionRequests = new java.util.ArrayList<>();
                for (JsonNode requestNode : toolRequestsArray) {
                    // 手动重建ToolExecutionRequest对象
                    String id = requestNode.has("id") ? requestNode.get("id").asText() : null;
                    String name = requestNode.has("name") ? requestNode.get("name").asText() : null;
                    String arguments = requestNode.has("arguments") ? requestNode.get("arguments").asText() : null;
                    
                    // 使用ToolExecutionRequest的静态工厂方法重建对象
                    ToolExecutionRequest request = ToolExecutionRequest.builder()
                            .id(id != null ? id : "unknown_id")
                            .name(name != null ? name : "unknown_tool")
                            .arguments(arguments != null ? arguments : "{}")
                            .build();
                    toolExecutionRequests.add(request);
                }
            }
            
            // 使用全参构造器重建对象
            if (toolExecutionRequests != null && !toolExecutionRequests.isEmpty()) {
                if (text != null && !text.isEmpty()) {
                    return AiMessage.from(text, toolExecutionRequests);
                } else {
                    return AiMessage.from(toolExecutionRequests);
                }
            } else if (text != null && !text.isEmpty()) {
                return AiMessage.from(text);
            } else {
                // 最后的备选方案
                return AiMessage.from("AI消息");
            }
        } catch (Exception e) {
            throw new RuntimeException("反序列化AiMessage失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    public Class<? extends ChatMessage> getSupportedType() {
        return AiMessage.class;
    }
}
