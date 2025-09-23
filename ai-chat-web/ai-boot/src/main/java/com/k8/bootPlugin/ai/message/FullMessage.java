package com.k8.bootPlugin.ai.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: k8
 * @CreateTime: 2025-09-22
 * @Version: 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FullMessage implements Serializable {
    private MessageRole messageRole;
    private List<Content> contents;
}
