package com.k8.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.k8.entity.KnowledgeInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author: k8
 * @CreateTime: 2025-08-29
 * @Version: 1.0
 */
@Mapper
public interface KnowledgeMapper extends BaseMapper<KnowledgeInfo> {
    
    /**
     * 添加知识库
     */
    int addKnowledge(@Param("knowledgeInfo") KnowledgeInfo knowledgeInfo);

    /**
     * 统计知识库数量
     */
    int count();

    /**
     * 获取知识库列表
     */
    List<KnowledgeInfo> list();

    /**
     * 根据ID删除知识库
     */
    boolean deleteById(@Param("knowledgeId") String knowledgeId);

    /**
     * 根据ID查询知识库
     */
    KnowledgeInfo selectById(@Param("knowledgeId") String knowledgeId);
}
