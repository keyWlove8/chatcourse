package com.k8.bootPlugin.ai;

import java.lang.reflect.Method;

/**
 * @Author: k8
 * @CreateTime: 2025-09-22
 * @Version: 1.0
 */
public interface AiMethodContextBuilder {
    AiMethodContext build(AiContext aiContext, Method method);
}
