package com.k8.bootPlugin.ai;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @Author: k8
 * @CreateTime: 2025-09-22
 * @Version: 1.0
 */

public class AiMethodContext {
    private String apiKey;
    private String modelName;
    private FullMessage systemMessage;
    /**
     * key = type
     * value = index
     */
    private Map<String,Integer> messageMapping;
    private static  ThreadLocal<FullMessage> localUserMessages = new ThreadLocal<>();
    private ChatMemoryStore chatMemoryStore;
    private boolean useMemory = false;
    private int memoryIdIndex;
    private String memoryId;

    public AiMethodContext addMessage(String type, Integer argsIndex){
        if (messageMapping == null){
            messageMapping = new HashMap<>();
        }
        messageMapping.put(type,argsIndex);
        return this;
    }
    public int messageSize(){
        return messageMapping == null ? 0 : messageMapping.size();
    }

    public List<FullMessage> getFullMessages(){
        List<FullMessage> result = new LinkedList<>();
        result.add(systemMessage);
        List<FullMessage> memoryMessages = chatMemoryStore.getMemoryFullMessages(memoryId);
        result.addAll(memoryMessages);
        result.add(localUserMessages.get());
        return result;
    }

    public void clear(){
        localUserMessages.remove();
    }

    /**
     * 这里其实可以不clear，因为这是static修饰的，只有实例级别的需要强制清除避免oom
     * @param args
     */
    public void setArgs(Object[] args) {
        List<Content> contents = new LinkedList<>();
        messageMapping.forEach((type,index)->{
            Object value = args[index];
            if (!(value == null || !value.getClass().equals(String.class) || value.toString().isEmpty())){
                contents.add(new Content(type,value.toString()));
            }
        });
        FullMessage fullMessage = new FullMessage(MessageRole.user,contents);
        localUserMessages.set(fullMessage);
        this.memoryId = args[memoryIdIndex].toString();
    }

    public void memory(Object result) {
        if (!useMemory || chatMemoryStore == null) return;
        String assistMessage = result.toString();
        FullMessage assistFullMessage = new FullMessage(MessageRole.assistant,List.of(new Content("text",assistMessage)));
        FullMessage userFullMessages = localUserMessages.get();
        chatMemoryStore.addAllMessage(List.of(userFullMessages,assistFullMessage),memoryId);
    }
    public AiMethodContext setSystemMessage(List<String> systemMessages){
        List<Content> contents = systemMessages.stream()
                .map(message -> {
                    return new Content("text", message);
                }).toList();
        this.systemMessage = new FullMessage(MessageRole.system,contents);
        return this;
    }

    public AiMethodContext setMemoryIdIndex(int memoryIdIndex) {
        this.memoryIdIndex = memoryIdIndex;
        useMemory = true;
        return this;
    }

    public AiMethodContext setApiKey(String apiKey) {
        this.apiKey = apiKey;
        return this;
    }

    public AiMethodContext setModelName(String modelName) {
        this.modelName = modelName;
        return this;
    }


    public void setChatMemoryStore(ChatMemoryStore chatMemoryStore) {
        this.chatMemoryStore = chatMemoryStore;
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getModelName() {
        return modelName;
    }

    public String getMemoryId() {
        return memoryId;
    }
}
