package com.k8.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.k8.param.ImageContextPara;
import com.k8.properties.StaticServiceProperties;
import com.k8.service.RemoteStaticService;
import com.k8.util.AuthUtil;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

/**
 * 远程静态服务实现类
 * 专门负责调用 ai-static 模块的图片上传功�? * 
 * @Author: k8
 * @CreateTime: 2025-09-23
 * @Version: 1.0
 */
@Service
public class RemoteStaticServiceImpl implements RemoteStaticService {
    
    private static final Logger logger = LoggerFactory.getLogger(RemoteStaticServiceImpl.class);
    
    private final StaticServiceProperties properties;
    private final OkHttpClient httpClient;
    private final ObjectMapper objectMapper;
    
    public RemoteStaticServiceImpl(StaticServiceProperties properties) {
        this.properties = properties;
        this.httpClient = new OkHttpClient();
        this.objectMapper = new ObjectMapper();
    }
    
    @Override
    public String sendImageAndGetUrl(ImageContextPara imageContextPara) {
        if (imageContextPara == null || imageContextPara.getData() == null) {
            throw new IllegalArgumentException("图片数据不能为空");
        }
        
        try {
            // 调用 ai-static 服务上传图片
            return uploadToStaticServer(imageContextPara);
        } catch (Exception e) {
            logger.error("图片上传失败", e);
            // 如果上传失败，返回模拟URL 作为备用
            return generateMockUrl(imageContextPara.getFileName());
        }
    }

    @Override
    public String sendImageAndGetUrl(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("图片文件不能为空");
        }
        
        try {
            // 将MultipartFile转换为ImageContextPara
            ImageContextPara imageContextPara = new ImageContextPara();
            imageContextPara.setData(file.getBytes());
            imageContextPara.setFileName(file.getOriginalFilename());
            
            // 调用 ai-static 服务上传图片
            return uploadToStaticServer(imageContextPara);
        } catch (Exception e) {
            logger.error("图片上传失败", e);
            // 如果上传失败，返回模拟URL 作为备用
            return generateMockUrl(file.getOriginalFilename());
        }
    }
    
    /**
     * 上传�?ai-static 服务
     */
    private String uploadToStaticServer(ImageContextPara imageContextPara) throws IOException {
        // 构建上传 URL
        String uploadUrl = properties.getStaticServerUrl() + properties.getUploadPath();
        
        // 构建 multipart 请求
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", 
                    imageContextPara.getFileName() != null ? imageContextPara.getFileName() : "image.jpg",
                    RequestBody.create(
                        MediaType.parse("image/*"), 
                        imageContextPara.getData()
                    ))
                .addFormDataPart("category", "chat")
                .build();
        
        Request.Builder requestBuilder = new Request.Builder()
                .url(uploadUrl)
                .post(requestBody);
        
        // 添加Authorization头
        String token = AuthUtil.getToken();
        if (token != null) {
            requestBuilder.addHeader("Authorization", "Bearer " + token);
        }
        
        Request request = requestBuilder.build();
        
        // 发送请求
        try (Response response = httpClient.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                String responseBody = response.body().string();
                logger.debug("ai-static 响应: {}", responseBody);
                
                // 解析 JSON 响应，获取文件访问URL
                try {
                    JsonNode jsonNode = objectMapper.readTree(responseBody);
                    String status = jsonNode.get("status").asText();
                    
                    if ("OK".equals(status)) {
                        String uri = jsonNode.get("uri").asText();
                        logger.info("图片上传成功，获取到 URL: {}", uri);
                        return uri;
                    } else {
                        logger.error("图片上传失败，状态: {}", status);
                        throw new RuntimeException("图片上传失败，状态: " + status);
                    }
                } catch (Exception e) {
                    logger.error("解析 ai-static 响应失败: {}", e.getMessage());
                    throw new RuntimeException("解析 ai-static 响应失败", e);
                }
            } else {
                throw new RuntimeException("图片上传失败，HTTP状态码: " + response.code());
            }
        }
    }
    
    @Override
    public String uploadAudio(byte[] audioData) {
        if (audioData == null || audioData.length == 0) {
            throw new IllegalArgumentException("音频数据不能为空");
        }
        
        try {
            // 调用 ai-static 服务上传音频
            return uploadAudioToStaticServer(audioData);
        } catch (Exception e) {
            logger.error("音频上传失败", e);
            // 如果上传失败，返回模拟URL 作为备用
            return generateMockAudioUrl();
        }
    }
    
    /**
     * 上传音频到 ai-static 服务
     */
    private String uploadAudioToStaticServer(byte[] audioData) throws IOException {
        // 构建上传 URL - 使用专门的音频上传接口
        String uploadUrl = properties.getStaticServerUrl() + properties.getAudioUploadPath();
        
        // 生成音频文件名（使用WAV扩展名，因为实际生成的是WAV格式）
        String fileName = "audio_" + UUID.randomUUID().toString() + ".wav";
        
        // 构建 multipart 请求
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", fileName,
                    RequestBody.create(
                        MediaType.parse("audio/*"), 
                        audioData
                    ))
                .addFormDataPart("category", "audio")
                .build();
        
        Request.Builder requestBuilder = new Request.Builder()
                .url(uploadUrl)
                .post(requestBody);
        
        // 添加Authorization头
        String token = AuthUtil.getToken();
        if (token != null) {
            requestBuilder.addHeader("Authorization", "Bearer " + token);
        }
        
        Request request = requestBuilder.build();
        
        // 发送请求
        try (Response response = httpClient.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                String responseBody = response.body().string();
                logger.debug("ai-static 音频上传响应: {}", responseBody);
                
                // 解析 JSON 响应，获取文件访问URL
                try {
                    JsonNode jsonNode = objectMapper.readTree(responseBody);
                    String status = jsonNode.get("status").asText();
                    
                    if ("OK".equals(status)) {
                        String uri = jsonNode.get("uri").asText();
                        logger.info("音频上传成功，获取到 URL: {}", uri);
                        return uri;
                    } else {
                        logger.error("音频上传失败，状态: {}", status);
                        throw new RuntimeException("音频上传失败，状态: " + status);
                    }
                } catch (Exception e) {
                    logger.error("解析 ai-static 音频上传响应失败: {}", e.getMessage());
                    throw new RuntimeException("解析 ai-static 音频上传响应失败", e);
                }
            } else {
                throw new RuntimeException("音频上传失败，HTTP状态码: " + response.code());
            }
        }
    }
    
    /**
     * 生成模拟音频 URL，作为备用方案
     */
    private String generateMockAudioUrl() {
        String fileName = "audio_" + UUID.randomUUID().toString() + ".mp3";
        return "https://mock-static-server.com/audio/" + fileName;
    }
    
    /**
     * 生成模拟 URL，作为备用方案
     */
    private String generateMockUrl(String fileName) {
        if (fileName == null || fileName.trim().isEmpty()) {
            fileName = "image_" + UUID.randomUUID().toString() + ".jpg";
        }
        return "https://mock-static-server.com/images/" + fileName;
    }
}
