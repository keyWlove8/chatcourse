package com.k8.file.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 文件上传响应对象
 * 
 * @Author: k8
 * @CreateTime: 2025-09-23
 * @Version: 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UploadVo implements Serializable {
    
    /**
     * 文件访问 URI
     * 前端可以直接使用这个 URL 访问文件
     */
    private String uri;
    
    /**
     * 上传状态
     * 成功返回 "OK"，失败返回 "FAIL"
     */
    private String status;
    
    /**
     * 文件名
     * 包含扩展名的完整文件名
     */
    private String fileName;
    
    /**
     * 文件大小（字节）
     */
    private Long size;
    
    /**
     * 文件内容类型
     * 例如：image/jpeg, image/png
     */
    private String contentType;
    
    /**
     * 文件存储路径
     * 相对于 basePath 的路径，便于调试和管理
     */
    private String storagePath;
}
