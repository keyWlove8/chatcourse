package com.k8.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class KnowledgeVO {
    private String id;
    private String name;
    private LocalDateTime createTime;
}
    
