package com.k8.bootPlugin.ai.message;

import lombok.Data;

import java.util.List;

@Data
public class MessageSession {
    private boolean canRefreshSystemMessage;
    private FullMessage userMessage;
    private FullMessage systemMessage;
    private FullMessage aiMessage;
    private String memoryId;
}
