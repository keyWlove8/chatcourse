package com.k8.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 文件上传返回数据
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileUploadVO {
    private String fileId;  // 文件ID
    private String fileName;  // 文件名（原始文件名）
    private String knowledgeId;  // 所属知识库ID
}
    
