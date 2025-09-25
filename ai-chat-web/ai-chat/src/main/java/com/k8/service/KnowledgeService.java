package com.k8.service;

import com.k8.chat.rag.EmbeddingManger;
import com.k8.dto.KnowledgeCreateDTO;
import com.k8.entity.KnowledgeInfo;
import com.k8.vo.FileUploadVO;
import com.k8.vo.KnowledgeVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 知识库服务接口
 */
public interface KnowledgeService {
    KnowledgeVO createKnowledge(KnowledgeCreateDTO createDTO);

    List<KnowledgeVO> getKnowledgeList();

    FileUploadVO uploadKnowledgeFile(String knowledgeId, MultipartFile file);

    void deleteKnowledge(String knowledgeId);

    void addEmbeddingManger(EmbeddingManger embeddingManger);

    KnowledgeInfo getKnowledgeById(String knowledgeId);
}
    
