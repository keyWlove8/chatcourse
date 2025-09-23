package com.k8.properties;

import io.pinecone.clients.Index;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @Author: k8
 * @CreateTime: 2025-08-29
 * @Version: 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties("pinecone")
public class PineconeProperties {
    String apiKey;
    String projectId;
    String nameSpace;
    String metadataTextKey;
    String metric;
    Integer dimension;
    String cloud;
    String region;
}
