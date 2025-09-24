package com.k8.dto;

import com.k8.enums.KChatMessageType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: k8
 * @CreateTime: 2025-09-23
 * @Version: 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageDTO {
    private String memoryId;
    private KChatMessageType chatMessageType;
    private String realChatMessage;
    private String knowledgeId;
    private String messageText;
}
