package com.k8.chat.tools.image;

import com.k8.bootPlugin.annotation.MemoryP;
import com.k8.bootPlugin.annotation.UserMessageContentP;
import com.k8.bootPlugin.annotation.SystemMessage;
import com.k8.bootPlugin.annotation.ToolService;


/**
 * @Author: k8
 * @CreateTime: 2025-09-23
 * @Version: 1.0
 */

@ToolService(modelName = "qwen-vl-plus", interceptorBuilder = "aiImageMethodInterceptorBuilder", chatMemoryStore = "chatMemoryStore1")
public interface ImageService {
    @SystemMessage("你是一个图像处理的工具，需要根据用户问题处理图片")
    String chatImage(@UserMessageContentP(type = "text") String userMessage, @UserMessageContentP(type = "image") String base64, @MemoryP String memoryId);
}
