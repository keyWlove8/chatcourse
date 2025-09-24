package com.k8.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: k8
 * @CreateTime: 2025-09-23
 * @Version: 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("chat_history")
public class ChatHistory {
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    
    @TableField("memory_id")
    private String memoryId;
    
    @TableField("last_question")
    private String lastQuestion;
    
    @TableField("last_answer")
    private String lastAnswer;
    
    @TableField("last_time")
    private Long lastTime;
    
    @TableField("message_count")
    private Integer messageCount;
    
    @TableField("creator_id")
    private String creatorId;
}
