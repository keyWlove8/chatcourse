package com.k8.vo;

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
public class ChatDetailVO {
    private String memoryId;
    private List<ChatMessageVO> messages;
    private Long createTime;
}
