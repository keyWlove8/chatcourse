package com.k8.bootPlugin.test.param;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: k8
 * @CreateTime: 2025-09-23
 * @Version: 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImageContextPara {
    private String fileName;
    private byte[] data;
}
