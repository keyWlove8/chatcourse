package com.k8.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 音色信息实体类
 * @Author: k8
 * @CreateTime: 2025-09-23
 * @Version: 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("voice_info")
public class VoiceInfo {
    
    /**
     * 音色ID
     */
    @TableId
    private String id;
    
    /**
     * 声音码/标识
     */
    @TableField("voice_code")
    private String voiceCode;
    
    /**
     * 声音描述
     */
    @TableField("description")
    private String description;
    
    /**
     * 声音性别 (male/female/neutral)
     */
    @TableField("gender")
    private String gender;
    
    /**
     * 声音名称
     */
    @TableField("name")
    private String name;
    
    /**
     * 语言 (zh-CN/en-US/ja-JP等)
     */
    @TableField("language")
    private String language;
    
    /**
     * 创建时间
     */
    @TableField("create_time")
    private Long createTime;
    
    /**
     * 创建者ID
     */
    @TableField("creator_id")
    private String creatorId;
    
    /**
     * 是否启用 (0-禁用, 1-启用)
     */
    @TableField("is_enabled")
    private Integer isEnabled;
}
