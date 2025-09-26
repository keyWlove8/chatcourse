package com.k8.chat.mcp.voice;

import com.alibaba.dashscope.audio.asr.recognition.Recognition;
import com.alibaba.dashscope.audio.asr.recognition.RecognitionParam;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.fasterxml.jackson.databind.JsonNode;
import com.k8.chat.mcp.voice.properties.AsrProperties;
import com.k8.util.JsonUtil;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * 阿里云实时语音识别客户端
 * 使用官方Java SDK，支持直接传入本地文件路径
 * @Author: k8
 * @CreateTime: 2025-01-20
 * @Version: 1.0
 */
@Component
public class AsrClient {
    private final AsrProperties asrProperties;

    public AsrClient(AsrProperties asrProperties) {
        this.asrProperties = asrProperties;
    }

    /**
     * 将音频数据转换为文本
     * @param data 音频数据字节数组
     * @param format 音频格式 (webm, wav, mp3等)
     * @return 识别出的文本
     */
    public String convert(byte[] data, String format) {
        try {
            // 根据格式处理，优先使用WAV
            if ("wav".equalsIgnoreCase(format)) {
                return recognizeWithSdk(data, "wav");
            } else {
                // 对于非WAV格式，尝试直接使用（可能会失败）
                return recognizeWithSdk(data, format);
            }
        } catch (Exception e) {
            throw new RuntimeException("语音识别失败", e);
        }
    }
    
    


    /**
     * 使用阿里云SDK进行识别（直接使用二进制数据）
     */
    private String recognizeWithSdk(byte[] audioData, String format) throws ApiException, NoApiKeyException, InputRequiredException, IOException {
        // 创建识别参数
        RecognitionParam param = RecognitionParam.builder()
                .model(asrProperties.getModelName()) // 使用配置文件中的模型名
                .format(format) // 音频格式
                .sampleRate(16000) // 采样率
                .apiKey(asrProperties.getApiKey()) // 设置API Key
                .build();

        // 创建识别对象
        Recognition recognition = new Recognition();

        // 创建临时文件用于SDK调用
        Path tempFile = null;
        try {
            tempFile = Files.createTempFile("asr_audio_", "." + format);
            Files.write(tempFile, audioData);
            
            File audioFile = tempFile.toFile();
            
            // 直接调用识别接口（同步调用，传入File对象）
            // 根据源码，这个方法返回String，包含JSON格式的识别结果
            String result = recognition.call(param, audioFile);
            
            // 解析JSON结果
            return parseRecognitionResult(result);
            
        } catch (IOException e) {
            throw new RuntimeException("文件操作失败", e);
        } catch (Exception e) {
            throw new RuntimeException("SDK调用失败", e);
        } finally {
            if (tempFile != null) {
                Files.deleteIfExists(tempFile);
            }
        }
    }
    
    /**
     * 解析识别结果JSON
     */
    private String parseRecognitionResult(String jsonResult) {
        try {
            // 根据源码，返回的JSON格式为：{"sentences": [...]}
            // 我们需要提取sentences数组中的文本
            JsonNode jsonNode = JsonUtil.parse(jsonResult);
            JsonNode sentences = jsonNode.get("sentences");
            
            if (sentences == null || !sentences.isArray()) {
                throw new RuntimeException("无效的识别结果格式");
            }
            
            StringBuilder result = new StringBuilder();
            for (com.fasterxml.jackson.databind.JsonNode sentence : sentences) {
                com.fasterxml.jackson.databind.JsonNode textNode = sentence.get("text");
                if (textNode != null) {
                    String text = textNode.asText();
                    if (text != null && !text.trim().isEmpty()) {
                        if (result.length() > 0) {
                            result.append(" ");
                        }
                        result.append(text.trim());
                    }
                }
            }
            
            return result.toString();
            
        } catch (Exception e) {
            throw new RuntimeException("解析识别结果失败: " + e.getMessage(), e);
        }
    }
}