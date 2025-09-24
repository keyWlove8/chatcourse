package com.k8.bootPlugin.ai.store;

import com.k8.bootPlugin.ai.message.FullMessage;

import java.util.List;

/**
 * @Author: k8
 * @CreateTime: 2025-09-23
 * @Version: 1.0
 */
public interface ChatMemoryStore {
    void addMessage(FullMessage message, String memoryId);

    void addAllMessage(List<FullMessage> fullMessages, String memoryId);

    List<FullMessage> getMemoryFullMessages(String memoryId);

    int getMemorySize();
}
