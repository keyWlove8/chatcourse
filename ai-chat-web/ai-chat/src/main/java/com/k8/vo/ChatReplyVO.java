package com.k8.vo;

import lombok.Data;

/**
 * 聊天回复返回数据
 */
@Data
public class ChatReplyVO {
    private String reply;             // AI回复内容
    private String memoryId;          // 会话ID
    private Long timestamp;
}
    
