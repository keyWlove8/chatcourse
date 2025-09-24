package com.k8.tools;

import dev.langchain4j.data.message.UserMessage;

/**
 * @Author: k8
 * @CreateTime: 2025-09-23
 * @Version: 1.0
 * 为了避免重复注入，所以需要使用适配器去进行调用下层tools
 */
public interface Chat2ImageAdapterService {
    String chatImage(String userMessage, String memoryId);
}
