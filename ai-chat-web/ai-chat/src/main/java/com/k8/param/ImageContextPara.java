package com.k8.param;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: k8
 * @CreateTime: 2025-08-11
 * @Version: 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImageContextPara {
    private String fileName;
    //todo 目前上传图片的模型还是用的base64，没有使用url，因为目前训练的数据和图片只有一张
    //如果后续暴露了图片的静态服务地址，那么可以怎么处理？目前是可以的，可以改为url
    private byte[] data;
}
