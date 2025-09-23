package com.k8.bootPlugin.ai;

import com.k8.bootPlugin.ai.message.AiContext;
import com.k8.bootPlugin.ai.store.ChatMemoryStore;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Author: k8
 * @CreateTime: 2025-09-22
 * @Version: 1.0
 */
@Data
@Accessors(chain = true)
public class AiProxyStarter {
    private AiContext aiContext;
    private ChatMemoryStore chatMemoryStore;
}
