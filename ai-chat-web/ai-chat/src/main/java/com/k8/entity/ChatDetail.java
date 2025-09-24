package com.k8.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author: k8
 * @CreateTime: 2025-09-23
 * @Version: 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("chat_detail")
public class ChatDetail {
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    
    @TableField("creator_id")
    private String creatorId;
    
    @TableField("memory_id")
    private String memoryId;          // 会话ID
    
    @TableField("create_time")
    private Long createTime;
}
