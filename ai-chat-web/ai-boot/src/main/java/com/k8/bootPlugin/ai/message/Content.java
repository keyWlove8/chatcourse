package com.k8.bootPlugin.ai.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: k8
 * @CreateTime: 2025-09-22
 * @Version: 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Content {
    private String type;
    private String message;
}
