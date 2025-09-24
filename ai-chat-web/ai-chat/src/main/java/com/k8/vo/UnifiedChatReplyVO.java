package com.k8.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 统一聊天回复VO
 * @Author: k8
 * @CreateTime: 2025-09-23
 * @Version: 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UnifiedChatReplyVO {
    private String reply;                  // AI回复的文本（与前端兼容）
    private String audioUrl;               // AI回复的语音URL（可选）
    private String transcribedText;        // 转录的文本（语音模式时使用）
    private String memoryId;               // 记忆ID
    private Long timestamp;                // 时间戳
    private String messageType;            // 消息类型：text, voice, image等
}
