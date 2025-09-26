package com.k8.chat.mcp.voice;


import com.k8.chat.client.K8DashScopeClient;
import com.k8.chat.mcp.voice.properties.TtsProperties;

// 语音合成
public class TtsClient {

    private final K8DashScopeClient client;
    private final TtsProperties ttsProperties;

    public TtsClient(TtsProperties ttsProperties) {
        this.client = new K8DashScopeClient.DashScopeClientBuilder()
                .build();
        this.ttsProperties = ttsProperties;
    }

    public String convert(String bodyString) {
        return client.call(bodyString, ttsProperties.getUrl(), ttsProperties.getApiKey());
    }
}
