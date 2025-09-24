package com.k8.memory.store;

import com.k8.dto.ChatMessageDTO;
import com.k8.enums.KChatMessageType;
import com.k8.memory.store.serializer.ChatMessageSerializer;
import com.k8.memory.store.serializer.ChatMessageSerializerFactory;
import com.k8.service.ChatService;
import com.k8.util.LocalUtil;
import com.k8.vo.LangChainMessageVO;
import dev.langchain4j.data.message.*;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;

/**
 * @Author: k8
 * @CreateTime: 2025-08-28
 * @Version: 1.0
 */
@Slf4j
public class MapperMemoryStore implements ChatMemoryStore {
    @Resource
    @Lazy
    ChatService chatService;


    @Override
    public List<ChatMessage> getMessages(Object memoryId) {
        String memoryIds = String.valueOf(memoryId);

        // 从chatService获取存储的消息
        List<LangChainMessageVO> messages = chatService.getLangChainMessagesByMemoryId(memoryIds);
        if (CollectionUtils.isEmpty(messages)) {
            return Collections.emptyList();
        }

        // 将存储的消息还原为LangChain4j消息
        List<ChatMessage> result = messages.stream()
                .map(message -> {
                    try {
                        // 使用专门的序列化器进行反序列化
                        Class<? extends ChatMessage> clazz = message.getChatMessageType().getMessageClass();
                        ChatMessageSerializer serializer = ChatMessageSerializerFactory.getSerializer(clazz);
                        return serializer.deserialize(message.getRealChatMessage());
                    } catch (Exception e) {
                        log.error("序列化失败：" + message);
                        return null;
                    }
                })
                .filter(msg -> msg != null) // 过滤掉反序列化失败的消息
                .toList();
        return result;
    }


    @Override
    public void updateMessages(Object memoryId, List<dev.langchain4j.data.message.ChatMessage> messages) {
        String memoryIds = String.valueOf(memoryId);
        if (CollectionUtils.isEmpty(messages)) {
            return;
        }
        String knowId = LocalUtil.getRagId();
        
        // 直接取最后一条消息（新消息）进行添加
        dev.langchain4j.data.message.ChatMessage lastMessage = messages.get(messages.size() - 1);
        try {
            // 使用专门的序列化器，保留所有元数据信息
            ChatMessageSerializer serializer = ChatMessageSerializerFactory.getSerializer(lastMessage);
            String realMessage = serializer.serialize(lastMessage);
            String messageText = "";
            if (lastMessage instanceof UserMessage userMessage){
                messageText = userMessage.singleText();
            }else if (lastMessage instanceof SystemMessage systemMessage){
                messageText = systemMessage.text();
            }else if (lastMessage instanceof AiMessage aiMessage){
                messageText = aiMessage.text();
            }else if (lastMessage instanceof ToolExecutionResultMessage toolExecutionResultMessage){
                messageText = toolExecutionResultMessage.text();
            }
            // 创建DTO，存储转换后的内容
            ChatMessageDTO chatMessageDTO = new ChatMessageDTO(
                    memoryIds, KChatMessageType.getType(lastMessage.type().messageClass()), realMessage, knowId, messageText
            );
            
            // 添加新消息
            chatService.addMessage(memoryIds, chatMessageDTO);
            
        } catch (Exception e) {
            // 记录错误但不抛出异常，避免影响LangChain4j的正常流程
            log.error("序列化或保存消息失败: " + e.getMessage() + ", 消息类型: " + lastMessage.getClass().getSimpleName());
        }
    }


    @Override
    public void deleteMessages(Object memoryId) {
        String memoryIds = String.valueOf(memoryId);
        try {
            boolean deleted = chatService.deleteByMemoryId(memoryIds);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

