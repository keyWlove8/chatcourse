package com.k8.chat.mcp.voice.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("k8.tts")
@Data
public class TtsProperties {
    String modelName;
    String apiKey;
    String url;
}
