package com.k8.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

/**
 * 创建知识库请求参数
 */
@Data
public class KnowledgeCreateDTO {
    @NotBlank(message = "知识库名称不能为空")
    private String name;  // 知识库名称（需唯一）
    
    @NotBlank(message = "知识库描述不能为空")
    private String description;  // 知识库描述
}
    
