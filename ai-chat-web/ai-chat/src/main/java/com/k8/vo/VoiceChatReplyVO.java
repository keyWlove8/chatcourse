package com.k8.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 语音聊天回复VO
 * @Author: k8
 * @CreateTime: 2025-01-01
 * @Version: 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VoiceChatReplyVO {
    private String transcribedText;        // 转录的文本（用户的问题）
    private String aiReply;                // AI回复的文本
    private String audioUrl;               // AI回复的语音URL
    private String memoryId;               // 记忆ID
    private Long timestamp;                // 时间戳
}
