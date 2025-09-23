package com.k8.bootPlugin.ai;

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
