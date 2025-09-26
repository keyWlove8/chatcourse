package com.k8.vo;

import lombok.Data;

/**
 * 音色查询VO（简化版，用于用户选择）
 * @Author: k8
 * @CreateTime: 2025-09-23
 * @Version: 1.0
 */
@Data
public class VoiceQueryVO {
    
    /**
     * 音色ID
     */
    private String id;
    
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
     * 语言
     */
    private String language;
    

}
