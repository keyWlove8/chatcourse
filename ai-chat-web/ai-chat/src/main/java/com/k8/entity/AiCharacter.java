package com.k8.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * AI角色实体
 * @Author: k8
 * @CreateTime: 2025-09-23
 * @Version: 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("ai_character")
public class AiCharacter {
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    
    @TableField("name")
    private String name;                    // 角色名称
    
    @TableField("description")
    private String description;             // 角色描述
    
    @TableField("personality")
    private String personality;             // 性格特征
    
    @TableField("background_story")
    private String backgroundStory;         // 背景故事
    
    @TableField("speaking_style")
    private String speakingStyle;           // 说话风格
    
    @TableField("avatar_url")
    private String avatarUrl;               // 头像URL
    
    @TableField("system_prompt")
    private String systemPrompt;            // 系统提示词
    
    @TableField("creator_id")
    private String creatorId;               // 创建者ID
    
    @TableField("is_public")
    private Boolean isPublic;               // 是否公开
    
    @TableField("popularity_score")
    private Integer popularityScore;        // 热度评分
    
    @TableField("created_time")
    private Long createdTime;               // 创建时间
    
    @TableField("updated_time")
    private Long updatedTime;               // 更新时间
    
    @TableField("voice_id")
    private String voiceId;                 // 关联的音色ID
}
