package com.k8.chatApi.image;

import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversation;
import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversationParam;
import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversationResult;
import com.alibaba.dashscope.common.MultiModalMessage;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.alibaba.dashscope.exception.UploadFileException;
import com.k8.bootPlugin.ai.AiMethodContext;
import com.k8.bootPlugin.ai.Content;
import com.k8.bootPlugin.ai.FullMessage;
import com.k8.bootPlugin.ai.MessageRole;


import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author: k8
 * @CreateTime: 2025-07-28
 * @Version: 1.0
 */
public class ImageChatUtil {
    private static MultiModalConversation conv = new MultiModalConversation();
    public static String chatByLocal(AiMethodContext aiMethodContext) throws IOException, NoApiKeyException, UploadFileException {
        List<FullMessage> fullMessages = aiMethodContext.getFullMessages();
        ArrayList<MultiModalMessage> messages = fullMessages.stream()
                .map(fullMessage -> {
                    Role role = getRole(fullMessage.getMessageRole());
                    List<Content> contents = fullMessage.getContents();
                    List<Map<String, Object>> list = contents.stream()
                            .map(content -> {
                                return Collections.singletonMap(content.getType(), (Object) content.getMessage());
                            }).toList();
                    return MultiModalMessage.builder().role(role.getValue())
                            .content(list)
                            .build();
                }).collect(Collectors.toCollection(ArrayList::new));
        MultiModalConversationParam param = MultiModalConversationParam.builder()
                .apiKey(aiMethodContext.getApiKey())
                .model(aiMethodContext.getModelName())
                .messages(messages)
                .build();
        MultiModalConversationResult result = conv.call(param);
        return result.getOutput().getChoices().get(0).getMessage().getContent().get(0).get("text").toString();
    }

    private static Role getRole(MessageRole role) {
        switch (role){
            case assistant -> {
                return Role.ASSISTANT;
            }
            case system -> {
                return Role.SYSTEM;
            }default -> {
                return Role.USER;
            }
        }
    }
}
