package com.k8.mcp;


import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Author: k8
 * @CreateTime: 2025-09-23
 * @Version: 1.0
 */
public class SearchMCPClient {

    private final OkHttpClient client;


    public SearchMCPClient() {
        this.client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .build();
    }

    public String callTool(String content, String apiKey, String modelName) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        List<Message> messages = Collections.singletonList(
                new Message("user", content)
        );
        ChatRequest requestData = new ChatRequest(
                messages,
                modelName,
                "week"
        );

        // 将对象转换为JSON字符串
        String jsonBody = objectMapper.writeValueAsString(requestData);

        // 构建请求
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, jsonBody);

        Request request = new Request.Builder()
                .url("https://qianfan.baidubce.com/v2/ai_search/chat/completions")
                .post(body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + apiKey)
                .build();

        // 发送请求（注意：实际使用时需处理异常和关闭资源）

        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }

    static class ChatRequest {
        public List<Message> messages;
        public String search_source;
        public String search_recency_filter;

        public ChatRequest(List<Message> messages, String search_source, String search_recency_filter) {
            this.messages = messages;
            this.search_source = search_source;
            this.search_recency_filter = search_recency_filter;
        }
    }

    static class Message {
        public String role;
        public String content;

        public Message(String role, String content) {
            this.role = role;
            this.content = content;
        }
    }
}
