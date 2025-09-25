package com.k8.vo;

import lombok.Data;

/**
 * 音色信息VO
 * @Author: k8
 * @CreateTime: 2025-09-23
 * @Version: 1.0
 */
@Data
public class VoiceVO {
    
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
     * 模型URL
     */
    private String modelUrl;
    
    /**
     * 模型名称
     */
    private String modelName;
    
    /**
     * 创建时间
     */
    private Long createTime;
    
    /**
     * 创建者ID
     */
    private String creatorId;
    
    /**
     * 是否启用 (0-禁用, 1-启用)
     */
    private Integer isEnabled;
    
    /**
     * 性别显示名称
     */
    public String getGenderDisplay() {
        switch (gender) {
            case "male": return "男声";
            case "female": return "女声";
            case "neutral": return "中性";
            default: return "未知";
        }
    }
}
