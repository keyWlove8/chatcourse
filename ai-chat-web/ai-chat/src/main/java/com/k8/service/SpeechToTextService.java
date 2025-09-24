package com.k8.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * 语音转文本服务接口
 * @Author: k8
 * @CreateTime: 2025-09-23
 * @Version: 1.0
 */
public interface SpeechToTextService {
    
    /**
     * 将音频文件转换为文本
     * @param audioFile 音频文件
     * @return 转换后的文本
     */
    String convert(MultipartFile audioFile);
    
    /**
     * 将音频字节数组转换为文本
     * @param audioData 音频数据
     * @param format 音频格式
     * @return 转换后的文本
     */
    String convert(byte[] audioData, String format);
}


