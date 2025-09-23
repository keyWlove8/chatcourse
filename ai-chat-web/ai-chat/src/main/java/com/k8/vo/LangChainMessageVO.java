package com.k8.vo;

import com.k8.enums.KChatMessageType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: k8
 * @CreateTime: 2025-08-28
 * @Version: 1.0
 * 专门用于大模型上下文的VO类，包含完整的LangChain4j消息信息
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LangChainMessageVO {
    private KChatMessageType chatMessageType;
    private String realChatMessage;
}
