package com.k8.chat.mcp.voice.config;


import com.k8.chat.mcp.voice.properties.AsrProperties;
import com.k8.chat.mcp.voice.properties.TtsProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({AsrProperties.class, TtsProperties.class})
public class VoiceMcpConfig {
}
