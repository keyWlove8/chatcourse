package com.k8.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.k8.enums.KChatMessageType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("context_message")
public class ContextMessage {
    @TableField("item_index")
    private Integer itemIndex;

    @TableField("memory_id")
    private String memoryId;

    @TableField("chat_message_type")
    private KChatMessageType chatMessageType;

    @TableField("contents_metadata")
    private String contentsMetadata;

    @TableField("timestamp")
    private Long timestamp;

    @TableField("knowledge_id")
    private String knowledgeId;
}
