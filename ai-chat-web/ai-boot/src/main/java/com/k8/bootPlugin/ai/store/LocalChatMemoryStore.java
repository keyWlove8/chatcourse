package com.k8.bootPlugin.ai.store;

import com.k8.bootPlugin.ai.message.FullMessage;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @Author: k8
 * @CreateTime: 2025-09-23
 * @Version: 1.0
 */
public class LocalChatMemoryStore implements ChatMemoryStore {
    private ConcurrentHashMap<String,List<FullMessage>> cache = new ConcurrentHashMap<>();

    @Override
    public void addMessage(FullMessage fullMessage, String memoryId) {
        List<FullMessage> fullMessages = cache.computeIfAbsent(memoryId, (key) -> {
            return new CopyOnWriteArrayList<>();
        });
        fullMessages.add(fullMessage);
    }

    @Override
    public void addAllMessage(List<FullMessage> fullMessages, String memoryId) {
        List<FullMessage> oldFullMessages = cache.computeIfAbsent(memoryId, (key) -> {
            return new CopyOnWriteArrayList<>();
        });
        oldFullMessages.addAll(fullMessages);
    }

    @Override
    public List<FullMessage> getMemoryFullMessages(String memoryId) {
        List<FullMessage> fullMessages = cache.get(memoryId);
        return fullMessages == null ? Collections.emptyList() : fullMessages;
    }

    @Override
    public int getMemorySize() {
        return cache.size();
    }
}
