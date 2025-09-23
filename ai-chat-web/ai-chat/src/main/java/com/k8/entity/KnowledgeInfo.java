package com.k8.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @Author: k8
 * @CreateTime: 2025-08-28
 * @Version: 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("knowledge_info")
public class KnowledgeInfo {
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    
    @TableField("name")
    private String name;
    
    @TableField("create_time")
    private LocalDateTime createTime;
    
    @TableField("creator_id")
    private String creatorId;
}
