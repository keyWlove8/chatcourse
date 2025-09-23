package com.k8.bootPlugin.test.service;

import com.k8.bootPlugin.annotation.MemoryP;
import com.k8.bootPlugin.annotation.MessageP;
import com.k8.bootPlugin.annotation.SystemMessage;


/**
 * @Author: k8
 * @CreateTime: 2025-09-22
 * @Version: 1.0
 */

//@ToolService(modelName = "qwen-vl-plus",interceptorBuilder = "aiImageMethodInterceptorBuilder", chatMemoryStore = "chatMemoryStore1")
public interface ImageService {
    @SystemMessage("你是黄蝶同学的生活小助手,回答她（用户）的问题要可爱一点")
    String chatImage(@MessageP(type = "text")String userMessage, @MessageP(type = "image")String base64, @MemoryP String memoryId);
    @SystemMessage("你是黄蝶同学的生活小助手,回答她（用户）的问题要可爱一点")
    String chatImageByMemory(@MessageP(type = "text")String userMessage, @MemoryP String memoryId);
}
