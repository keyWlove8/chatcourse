package com.k8.chat.mcp.voice.config;


import com.k8.chat.mcp.voice.properties.ToTextMcpProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(ToTextMcpProperties.class)
public class VoiceMcpConfig {
}
