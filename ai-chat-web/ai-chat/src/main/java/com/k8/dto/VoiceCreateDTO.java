package com.k8.dto;

import lombok.Data;

/**
 * 音色创建DTO
 * @Author: k8
 * @CreateTime: 2025-09-23
 * @Version: 1.0
 */
@Data
public class VoiceCreateDTO {
    
    /**
     * 声音码/标识
     */
    private String voiceCode;
    
    /**
     * 声音描述
     */
    private String description;
    
    /**
     * 声音性别 (male/female/neutral)
     */
    private String gender;
    
    /**
     * 声音名称
     */
    private String name;
    
    /**
     * 模型URL
     */
    private String modelUrl;
    
    /**
     * 模型名称
     */
    private String modelName;
    
    /**
     * API密钥
     */
    private String apiKey;
}
