package com.k8.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 角色查询DTO
 * @Author: k8
 * @CreateTime: 2025-09-23
 * @Version: 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CharacterQueryDTO {
    private String keyword;                 // 角色名搜索关键词
    private Boolean isPublic;               // 是否公开
    private String creatorId;               // 创建者ID
    private Integer pageNum = 1;            // 页码
    private Integer pageSize = 10;          // 每页大小
}
