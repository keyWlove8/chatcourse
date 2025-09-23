package com.k8.bootPlugin.test.imageChat;

import com.alibaba.dashscope.exception.NoApiKeyException;
import com.alibaba.dashscope.exception.UploadFileException;
import com.k8.bootPlugin.ai.AbstractAiMethodInterceptor;
import com.k8.bootPlugin.ai.AiContext;
import com.k8.bootPlugin.ai.AiMethodContext;
import com.k8.bootPlugin.ai.AiProxyStarter;

import java.io.IOException;
import java.lang.reflect.Method;

/**
 * @Author: k8
 * @CreateTime: 2025-09-22
 * @Version: 1.0
 */
public class AiImageChatEnhancerInterceptor extends AbstractAiMethodInterceptor {
    private final Class<?> returnType;
    public AiImageChatEnhancerInterceptor(AiProxyStarter aiProxyStarter) {
        super(aiProxyStarter);
        try {
            Method method = ImageChatUtil.class.getMethod("chatByLocal", AiMethodContext.class);
            this.returnType = method.getReturnType();
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void doInit(AiContext aiContext) {

    }


    @Override
    public boolean filterMethod(Method method) {
        return !method.getReturnType().isAssignableFrom(returnType);
    }

    @Override
    public Object doProcess(AiMethodContext aiMethodContext) {
        try {
            return ImageChatUtil.chatByLocal(aiMethodContext);
        } catch (IOException | NoApiKeyException | UploadFileException e) {
            throw new RuntimeException(e);
        }
    }
}
