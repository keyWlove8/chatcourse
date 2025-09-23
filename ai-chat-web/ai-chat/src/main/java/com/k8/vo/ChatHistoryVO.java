package com.k8.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: k8
 * @CreateTime: 2025-08-28
 * @Version: 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatHistoryVO {
    private String memoryId;
    private String title;
    private String lastQuestion;
    private String lastAnswer;
    private Long lastTime;
    private Integer messageCount;
}
