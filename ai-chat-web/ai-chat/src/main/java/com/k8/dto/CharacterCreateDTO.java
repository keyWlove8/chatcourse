package com.k8.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 角色创建DTO
 * @Author: k8
 * @CreateTime: 2025-01-01
 * @Version: 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CharacterCreateDTO {
    private String name;                    // 角色名称
    private String description;             // 角色描述
    private String personality;             // 性格特征
    private String backgroundStory;         // 背景故事
    private String speakingStyle;           // 说话风格
    private String avatarUrl;               // 头像URL
    private String systemPrompt;            // 系统提示词
    private Boolean isPublic;               // 是否公开
}
