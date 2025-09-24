package com.k8.bootPlugin.ai.context.builder;



import com.k8.bootPlugin.ai.message.AiContext;
import com.k8.bootPlugin.ai.AiMethodContext;
import com.k8.bootPlugin.annotation.MemoryP;
import com.k8.bootPlugin.annotation.UserMessageContentP;
import com.k8.bootPlugin.annotation.SystemMessage;
import com.k8.bootPlugin.annotation.V;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * @Author: k8
 * @CreateTime: 2025-09-23
 * @Version: 1.0
 */
public class DefaultAiMethodContextBuilder implements AiMethodContextBuilder {
    @Override
    public AiMethodContext build(AiContext aiContext, Method method) {
        SystemMessage smAn = method.getAnnotation(SystemMessage.class);
        AiMethodContext aiMethodContext = new AiMethodContext();
        aiMethodContext.setApiKey(aiContext.getApiKey())
                .setModelName(aiContext.getModelName())
                .setSourceSystemMessage(smAn.value());
        Parameter[] parameters = method.getParameters();
        int index = 0;
        int memoryIndex = -1;
        for (Parameter parameter : parameters) {
            if (parameter.isAnnotationPresent(UserMessageContentP.class)){
                UserMessageContentP mp = parameter.getAnnotation(UserMessageContentP.class);
                aiMethodContext.addUserContentMapping(mp.type(),index);
            }else if (parameter.isAnnotationPresent(MemoryP.class)){
                if (!String.class.equals(parameter.getType())){
                    throw new IllegalArgumentException("type of memoryId is not String.");
                }
                memoryIndex = index;
            }else if (parameter.isAnnotationPresent(V.class)){
                V v = parameter.getAnnotation(V.class);
                if (!String.class.equals(parameter.getType())) throw new IllegalArgumentException("type of @V is not String.");
                aiMethodContext.addSystemVMapping(v.name(), index);
            }
            index++;
        }
        aiMethodContext.setMemoryIdIndex(memoryIndex);
        return aiMethodContext;
    }

}
