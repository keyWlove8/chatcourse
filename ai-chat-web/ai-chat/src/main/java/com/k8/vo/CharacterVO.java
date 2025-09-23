package com.k8.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 角色VO
 * @Author: k8
 * @CreateTime: 2025-01-01
 * @Version: 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CharacterVO {
    private String id;                      // 角色ID
    private String name;                    // 角色名称
    private String description;             // 角色描述
    private String personality;             // 性格特征
    private String backgroundStory;         // 背景故事
    private String speakingStyle;           // 说话风格
    private String avatarUrl;               // 头像URL
    private String systemPrompt;            // 系统提示词
    private String creatorId;               // 创建者ID
    private Boolean isPublic;               // 是否公开
    private Integer popularityScore;        // 热度评分
    private Long createdTime;               // 创建时间
    private Long updatedTime;               // 更新时间
}
