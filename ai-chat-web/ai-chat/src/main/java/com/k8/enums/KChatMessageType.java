package com.k8.enums;

import dev.langchain4j.data.message.*;

/**
 * @Author: k8
 * @CreateTime: 2025-09-23
 * @Version: 1.0
 */
public enum KChatMessageType {
    /**
     * A message from the system, typically defined by a developer.
     */
    SYSTEM(SystemMessage.class),

    /**
     * A message from the user.
     */
    USER(UserMessage.class),

    /**
     * A message from the AI.
     */
    AI(AiMessage.class),

    /**
     * A message from a tool.
     */
    TOOL_EXECUTION_RESULT(ToolExecutionResultMessage.class);

    private final Class<? extends ChatMessage> messageClass;

    KChatMessageType(Class<? extends ChatMessage> messageClass) {
        this.messageClass = messageClass;
    }


    public Class<? extends ChatMessage> getMessageClass() {
        return messageClass;
    }

    public static KChatMessageType getType(Class<? extends ChatMessage> clazz){
        if (clazz.equals(SystemMessage.class)){
            return SYSTEM;
        }
        if (clazz.equals(UserMessage.class)){
            return USER;
        }
        if (clazz.equals(AiMessage.class)){
            return AI;
        }
        if (clazz.equals(ToolExecutionResultMessage.class)){
            return TOOL_EXECUTION_RESULT;
        }
        throw new IllegalArgumentException("无该类型");
    }
}
