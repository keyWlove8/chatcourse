package com.k8.memory.store.serializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import dev.langchain4j.data.message.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

/**
 * UserMessage序列化器
 * 使用全参构造器保留所有元数据信息，包括contents
 *
 * @Author: k8
 * @CreateTime: 2025-09-23
 * @Version: 1.0
 */
public class UserMessageSerializer implements ChatMessageSerializer {


    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public String serialize(ChatMessage message) {
        if (!(message instanceof UserMessage)) {
            throw new IllegalArgumentException("不支持的消息类型: " + message.getClass().getSimpleName());
        }

        UserMessage userMessage = (UserMessage) message;
        TextContent textContent = (TextContent) userMessage.contents().get(0);
        try {
            // 创建包含所有信息的JSON对象
            ObjectNode jsonNode = OBJECT_MAPPER.createObjectNode();
            jsonNode.put("name", userMessage.name());
            jsonNode.put("contents", textContent.text());
            return OBJECT_MAPPER.writeValueAsString(jsonNode);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("序列化UserMessage失败: " + e.getMessage(), e);
        }
    }





    public ChatMessage deserialize(String json) {
        try {
            JsonNode jsonNode = OBJECT_MAPPER.readTree(json);
            String name = jsonNode.get("name").asText();
            String contents = jsonNode.get("contents").asText();
            List<Content> contentList = new ArrayList<>();
            contentList.add(new TextContent(contents));
            return new UserMessage(name, contentList);
        } catch (Exception e) {
            throw new RuntimeException("反序列化UserMessage失败: " + e.getMessage(), e);
        }
    }

    @Override
    public Class<? extends ChatMessage> getSupportedType() {
        return UserMessage.class;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    static class Node {
        String type;
        Object value;
    }
}
