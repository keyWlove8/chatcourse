package com.k8.service.impl;

import com.k8.chat.mcp.voice.AsrClient;
import com.k8.service.SpeechToTextService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 语音转文本服务实现
 * 使用百炼大模型进行语音识别
 * @Author: k8
 * @CreateTime: 2025-09-23
 * @Version: 1.0
 */
@Service
public class SpeechToTextServiceImpl implements SpeechToTextService {

    @Autowired
    private AsrClient asrClient;

    @Override
    public String convert(MultipartFile audioFile) {
        try {
            // 获取文件扩展名
            String originalFilename = audioFile.getOriginalFilename();
            String format = getAudioFormat(originalFilename);
            
            // 调用ASR客户端进行语音识别
            return asrClient.convert(audioFile.getBytes(), format);
            
        } catch (IOException e) {
            throw new RuntimeException("读取音频文件失败: " + e.getMessage(), e);
        }
    }

    @Override
    public String convert(byte[] audioData, String format) {
        try {
            // 调用ASR客户端进行语音识别
            return asrClient.convert(audioData, format);
        } catch (Exception e) {
            throw new RuntimeException("语音识别失败: " + e.getMessage(), e);
        }
    }

    /**
     * 根据文件名获取音频格式
     * 强制使用WAV格式，因为阿里云SDK不支持WebM
     */
    private String getAudioFormat(String filename) {
        if (filename == null) {
            return "wav";
        }
        
        String lowerFilename = filename.toLowerCase();
        if (lowerFilename.endsWith(".wav")) {
            return "wav";
        } else if (lowerFilename.endsWith(".pcm")) {
            return "pcm";
        } else if (lowerFilename.endsWith(".mp3")) {
            return "mp3";
        } else if (lowerFilename.endsWith(".ogg")) {
            return "ogg";
        } else if (lowerFilename.endsWith(".aac")) {
            return "aac";
        } else if (lowerFilename.endsWith(".webm")) {
            return "webm";
        } else if (lowerFilename.endsWith(".m4a")) {
            return "m4a";
        } else if (lowerFilename.endsWith(".amr")) {
            return "amr";
        } else {
            return "wav";
        }
    }
}


