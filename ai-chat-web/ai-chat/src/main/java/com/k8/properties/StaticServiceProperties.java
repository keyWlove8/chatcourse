package com.k8.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 静态文件服务配置属性
 * 
 * @Author: k8
 * @CreateTime: 2025-09-23
 * @Version: 1.0
 */
@ConfigurationProperties(prefix = "k8.static-service")
@Data
public class StaticServiceProperties {
    
    /**
     * ai-static 服务的地址
     * 例如：http://localhost:9000
     */
    private String staticServerUrl = "http://localhost:9000";
    
    /**
     * 图片上传接口路径
     * 例如：/api/image/upload/direct
     */
    private String uploadPath = "/api/image/upload/direct";
}
