package com.k8.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.k8.enums.KChatMessageType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @Author: k8
 * @CreateTime: 2025-08-28
 * @Version: 1.0
 */
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Data
@TableName("multi_chat_message")
public class MultiChatMessage {
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    
    @TableField("memory_id")
    private String memoryId;
    
    @TableField("chat_message_type")
    private KChatMessageType chatMessageType;
    
    @TableField("real_chat_message")
    private String realChatMessage;
    
    @TableField("contents_metadata")
    private String contentsMetadata;
    
    @TableField("knowledge_id")
    private String knowledgeId;
    
    @TableField("timestamp")
    private Long timestamp;
    
    @TableField("user_id")
    private String userId;
    
    @TableField("detail_id")
    private String detailId;


}
