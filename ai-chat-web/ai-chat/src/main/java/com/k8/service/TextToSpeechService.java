package com.k8.service;

/**
 * 文本转语音服务接口
 * @Author: k8
 * @CreateTime: 2025-09-23
 * @Version: 1.0
 */
public interface TextToSpeechService {
    
    /**
     * 将文本转换为语音并上传到静态服务
     * @param text 要转换的文本
     * @return 语音文件的URL
     */
    String convertToSpeech(String text);
    
    /**
     * 将文本转换为语音字节数组
     * @param text 要转换的文本
     * @return 语音字节数组
     */
    byte[] convertToSpeechBytes(String text);
}
