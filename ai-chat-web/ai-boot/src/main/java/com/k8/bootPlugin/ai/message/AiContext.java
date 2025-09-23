package com.k8.bootPlugin.ai.message;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

/**
 * @Author: k8
 * @CreateTime: 2025-09-22
 * @Version: 1.0
 */
@Data
@AllArgsConstructor
public class AiContext {
    private String apiKey;
    private String modelName;
    private Map<String,String> extendProperties;
}
