package com.k8.bootPlugin.ai.impl;



import com.k8.bootPlugin.ai.AiContext;
import com.k8.bootPlugin.ai.AiMethodContext;
import com.k8.bootPlugin.ai.AiMethodContextBuilder;
import com.k8.bootPlugin.annotation.MemoryP;
import com.k8.bootPlugin.annotation.MessageP;
import com.k8.bootPlugin.annotation.SystemMessage;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;

/**
 * @Author: k8
 * @CreateTime: 2025-09-22
 * @Version: 1.0
 */
public class DefaultAiMethodContextBuilder implements AiMethodContextBuilder {
    @Override
    public AiMethodContext build(AiContext aiContext, Method method) {
        SystemMessage smAn = method.getAnnotation(SystemMessage.class);
        AiMethodContext aiMethodContext = new AiMethodContext();
        aiMethodContext.setApiKey(aiContext.getApiKey())
                .setModelName(aiContext.getModelName())
                .setSystemMessage(Arrays.stream(smAn.value()).toList());
        Parameter[] parameters = method.getParameters();
        int index = 0;
        int memoryIndex = -1;
        for (Parameter parameter : parameters) {
            if (parameter.isAnnotationPresent(MessageP.class)){
                MessageP mp = parameter.getAnnotation(MessageP.class);
                aiMethodContext.addMessage(mp.type(),index);
            }else if (parameter.isAnnotationPresent(MemoryP.class)){
                if (!String.class.equals(parameter.getType())){
                    throw new IllegalArgumentException("type of memoryId is not String.");
                }
                memoryIndex = index;
            }
            index++;
        }
        aiMethodContext.setMemoryIdIndex(memoryIndex);
        return aiMethodContext;
    }

}
