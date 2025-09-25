package com.k8.service.impl;


import com.k8.chat.rag.EmbeddingManger;
import com.k8.dto.KnowledgeCreateDTO;
import com.k8.entity.KnowledgeInfo;
import com.k8.exception.BusinessException;
import com.k8.mapper.KnowledgeMapper;
import com.k8.service.KnowledgeService;
import com.k8.service.UserService;
import com.k8.util.AuthUtil;
import com.k8.vo.FileUploadVO;
import com.k8.vo.KnowledgeVO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * 知识库服务实现类（示例）
 */
@Component
public class KnowledgeServiceImpl implements KnowledgeService {
    @Resource
    private UserService userService;

    private EmbeddingManger embeddingManger;

    @Resource
    private KnowledgeMapper knowledgeMapper;

    @Override
    public synchronized KnowledgeVO createKnowledge(KnowledgeCreateDTO createDTO) {
        String userId = AuthUtil.getUserId();
        if (!userService.isAdmin(userId)) {
            throw new BusinessException(400, "");
        }
        String id = UUID.randomUUID().toString();
        KnowledgeInfo knowledgeInfo = new KnowledgeInfo();
        knowledgeInfo.setCreatorId(userId);
        knowledgeInfo.setId(id);
        knowledgeInfo.setName(createDTO.getName());
        LocalDateTime createTime = LocalDateTime.now();
        knowledgeInfo.setCreateTime(createTime);
        embeddingManger.createNewContainer(knowledgeInfo);
        knowledgeMapper.insert(knowledgeInfo);
        return new KnowledgeVO(id, createDTO.getName(), createTime);
    }

    @Override
    public List<KnowledgeVO> getKnowledgeList() {
        List<KnowledgeInfo> knowledgeInfos = knowledgeMapper.list();
        List<KnowledgeVO> knowledgeVOS = knowledgeInfos.stream()
                .map(knowledgeInfo -> {
                    return new KnowledgeVO(knowledgeInfo.getId(), knowledgeInfo.getName(), knowledgeInfo.getCreateTime());
                }).toList();
        return knowledgeVOS;
    }

    @Override
    public synchronized FileUploadVO uploadKnowledgeFile(String knowledgeId, MultipartFile file) {
        try {
            embeddingManger.uploadKnowledge(getKnowledgeById(knowledgeId), file.getOriginalFilename(), file.getInputStream());
            return new FileUploadVO(knowledgeId, file.getOriginalFilename(), knowledgeId);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public synchronized void deleteKnowledge(String knowledgeId) {
        knowledgeMapper.deleteById(knowledgeId);
    }

    @Override
    public synchronized void addEmbeddingManger(EmbeddingManger embeddingManger) {
        this.embeddingManger = embeddingManger;
    }

    public KnowledgeInfo getKnowledgeById(String knowledgeId) {
        return knowledgeMapper.selectById(knowledgeId);
    }

}
    
