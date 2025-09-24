package com.k8.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.k8.entity.KnowledgeInfo;
import org.apache.ibatis.annotations.*;

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
    @Insert("INSERT INTO knowledge_info (id, name, create_time, creator_id) VALUES (#{knowledgeInfo.id}, #{knowledgeInfo.name}, #{knowledgeInfo.createTime}, #{knowledgeInfo.creatorId})")
    int addKnowledge(@Param("knowledgeInfo") KnowledgeInfo knowledgeInfo);

    /**
     * 统计知识库数量
     */
    @Select("SELECT COUNT(*) FROM knowledge_info")
    int count();

    /**
     * 获取知识库列表
     */
    @Select("SELECT * FROM knowledge_info ORDER BY create_time DESC")
    List<KnowledgeInfo> list();

    /**
     * 根据ID删除知识库
     */
    @Delete("DELETE FROM knowledge_info WHERE id = #{knowledgeId}")
    boolean deleteById(@Param("knowledgeId") String knowledgeId);

    /**
     * 根据ID查询知识库
     */
    @Select("SELECT * FROM knowledge_info WHERE id = #{knowledgeId}")
    KnowledgeInfo selectById(@Param("knowledgeId") String knowledgeId);
}
