package com.k8.mcp.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @Author: k8
 * @CreateTime: 2025-09-23
 * @Version: 1.0
 */
@ConfigurationProperties("k8.mcp.search")
@Data
public class McpProperties {
    String modelName;
    String apiKey;
}
