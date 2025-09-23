package com.k8.bootPlugin.ai;

import com.k8.bootPlugin.ai.message.Content;
import com.k8.bootPlugin.ai.message.FullMessage;
import com.k8.bootPlugin.ai.message.MessageRole;
import com.k8.bootPlugin.ai.message.MessageSession;
import com.k8.bootPlugin.ai.message.dymic.ExpressionUtil;
import com.k8.bootPlugin.ai.message.dymic.ParsedExpression;
import com.k8.bootPlugin.ai.store.ChatMemoryStore;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * @Author: k8
 * @CreateTime: 2025-09-22
 * @Version: 1.0
 */

public class AiMethodContext {
    private String apiKey;
    private String modelName;
    private ParsedExpression sourceSystemMessage;
    private static ThreadLocal<MessageSession> SESSION_LOCAL = new ThreadLocal<>();
    /**
     * key = type
     * value = index
     */
    private Map<String, Integer> messageMapping;
    private ChatMemoryStore chatMemoryStore;
    private boolean useMemory = false;
    private int memoryIdIndex;
    private Map<String, Integer> systemVMapping;

    public void addUserContentMapping(String type, Integer argsIndex) {
        if (messageMapping == null) {
            messageMapping = new HashMap<>();
        }
        messageMapping.put(type, argsIndex);
    }

    public void addSystemVMapping(String name, Integer argsIndex) {
        if (systemVMapping == null) {
            systemVMapping = new HashMap<>();
        }
        systemVMapping.put(name, argsIndex);
    }

    public List<FullMessage> getFullMessages() {
        MessageSession messageSession = SESSION_LOCAL.get();
        String memoryId = messageSession.getMemoryId();
        List<FullMessage> memoryMessages = chatMemoryStore.getMemoryFullMessages(memoryId);
        if (CollectionUtils.isEmpty(memoryMessages)) {
            memoryMessages = Collections.emptyList();
        }
        List<FullMessage> result = new LinkedList<>(memoryMessages);
        if (result.isEmpty() || messageSession.isCanRefreshSystemMessage()){
            result.add(messageSession.getSystemMessage());
        }
        result.add(messageSession.getUserMessage());
        return result;
    }


    /**
     * 这里其实可以不clear，因为这是static修饰的，只有实例级别的需要强制清除避免oom
     *
     * @param args
     */
    public MessageSession refreshArgs(Object[] args) {
        List<Content> contents = new LinkedList<>();
        messageMapping.forEach((type, index) -> {
            Object value = args[index];
            if (!(value == null || !value.getClass().equals(String.class) || value.toString().isEmpty())) {
                contents.add(new Content(type, value.toString()));
            }
        });
        MessageSession session = new MessageSession();
        FullMessage fullUserMessage = new FullMessage(MessageRole.user, contents);
        session.setUserMessage(fullUserMessage);
        String memoryId = String.valueOf(args[memoryIdIndex]);
        session.setMemoryId(memoryId);
        String systemMessage = null;
        if (sourceSystemMessage.canReplace()) {
            List<String> parameters = sourceSystemMessage.getParameters();
            Map<String, String> map = new HashMap<>();
            for (String name : parameters) {
                String replaceValue = "";
                if (systemVMapping.containsKey(name)) {
                    replaceValue = String.valueOf(args[systemVMapping.get(name)]);
                }
                map.put(name, replaceValue);
            }
            systemMessage  = ExpressionUtil.getValue(sourceSystemMessage, map);
        }else {
            systemMessage = sourceSystemMessage.getOriginalString();
        }
        FullMessage fullSystemMessage = new FullMessage(MessageRole.system, List.of(new Content("text", systemMessage)));
        session.setSystemMessage(fullSystemMessage);
        //todo 禁止刷新SystemMessage,这里可以用一个threadLocal缓存一下，但是在初始化的时候应该查询缓存一下
        session.setCanRefreshSystemMessage(false);
        SESSION_LOCAL.set(session);
        return session;
    }

    public void memory() {
        if (!useMemory || chatMemoryStore == null) return;
        MessageSession messageSession = SESSION_LOCAL.get();
        List<FullMessage> messages = new LinkedList<>();
        if (chatMemoryStore.getMemorySize() == 0 || messageSession.isCanRefreshSystemMessage()){
            messages.add(messageSession.getSystemMessage());
        }
        messages.add(messageSession.getUserMessage());
        messages.add(messageSession.getAiMessage());
        chatMemoryStore.addAllMessage(messages, messageSession.getMemoryId());
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

    public AiMethodContext setSourceSystemMessage(String sourceSystemMessage) {
        this.sourceSystemMessage = ExpressionUtil.parse(sourceSystemMessage);
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
    public void clear(){
        SESSION_LOCAL.remove();
    }
}
