package com.k8.chat.mcp.voice.properties;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("k8.ars")
@Data
public class AsrProperties {
    String modelName;
    String apiKey;
    String url;
}
