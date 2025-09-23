package com.k8.file.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @Author: k8
 * @CreateTime: 2025-09-22
 * @Version: 1.0
 */
@ConfigurationProperties(prefix = "k8.static")
@Data
public class K8StaticProperties {
    
    /**
     * 存放文件的文件夹路径
     * 如果使用相对路径，将相对于 jar 包的位置
     */
    private String basePath = "./static-files";
    
    /**
     * 配置当前文件服务器能被访问到的地址
     * 用于生成文件的下载链接
     */
    private String host = "http://localhost:9000";
}
