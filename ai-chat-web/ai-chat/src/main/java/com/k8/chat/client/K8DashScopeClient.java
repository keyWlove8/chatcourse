package com.k8.chat.client;

import okhttp3.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class K8DashScopeClient {
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String CONTENT_TYPE_HEADER = "Content-Type";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String APPLICATION_JSON = "application/json";
    private static final String CHARSET_UTF8 = "charset=utf-8";
    private final String apiKey;
    private final OkHttpClient client;
    private final String baseUrl;
    private final String endPoint;
    private K8DashScopeClient(String apiKey, String baseUrl, String endPoint, OkHttpClient client){
        this.apiKey = apiKey;
        this.client = client;
        this.endPoint = endPoint;
        this.baseUrl = baseUrl;
    }

    public String call(String bodyString){
        return call(bodyString, baseUrl + endPoint, apiKey);
    }

    public String call(String bodyString, String url, String apiKey) {
        RequestBody body = RequestBody.create(bodyString, MediaType.get(APPLICATION_JSON + "; " + CHARSET_UTF8));
        Request request = new Request.Builder()
                .url(url)
                .addHeader(AUTHORIZATION_HEADER, BEARER_PREFIX + apiKey)
                .addHeader(CONTENT_TYPE_HEADER, APPLICATION_JSON)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String errorBody = response.body() != null ? response.body().string() : "";
                throw new IOException("请求失败: " + response.code() + " " + response.message() + "\n" + errorBody);
            }

            return response.body().string();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static class DashScopeClientBuilder{
        private String apiKey;
        private String baseUrl;
        private String endPoint;

        private int connectTimeout = 5;
        private int readTimeout = 180;
        private int writeTimeout = 180;
        private int callTimeout = 360;
        public K8DashScopeClient build(){
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(connectTimeout, TimeUnit.SECONDS)      // 连接超时：30秒
                    .readTimeout(readTimeout, TimeUnit.SECONDS)        // 读取超时：120秒
                    .writeTimeout(writeTimeout, TimeUnit.SECONDS)        // 写入超时：60秒
                    .callTimeout(callTimeout, TimeUnit.SECONDS)        // 总调用超时：180秒
                    .build();
            return new K8DashScopeClient(apiKey, baseUrl, endPoint, client);
        }

        public DashScopeClientBuilder connectTimeout(int connectTimeout){
            this.callTimeout = connectTimeout;
            return this;
        }

        public DashScopeClientBuilder readTimeout(int readTimeout){
            this.readTimeout = readTimeout;
            return this;
        }

        public DashScopeClientBuilder writeTimeout(int writeTimeout){
            this.writeTimeout = writeTimeout;
            return this;
        }

        public DashScopeClientBuilder callTimeout(int callTimeout){
            this.callTimeout = callTimeout;
            return this;
        }

        public DashScopeClientBuilder apiKey(String apiKey){
            this.apiKey = apiKey;
            return this;
        }

        public DashScopeClientBuilder baseUrl(String baseUrl){
            this.baseUrl = baseUrl;
            return this;
        }
        public DashScopeClientBuilder endPoint(String endPoint){
            this.endPoint = endPoint;
            return this;
        }

        private boolean isEmpty(String value){
            return value == null || value.isEmpty();
        }
    }
}
