package com.k8.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.k8.entity.MultiChatMessage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author: k8
 * @CreateTime: 2025-08-29
 * @Version: 1.0
 */
@Mapper
public interface ChatMessageMapper extends BaseMapper<MultiChatMessage> {
    
    /**
     * 根据memoryId查询聊天消息列表
     */
    List<MultiChatMessage> selectChatMessagesByMemoryId(@Param("memoryId") String memoryId);

    /**
     * 根据memoryId删除聊天消息
     */
    int deleteByMemoryId(@Param("memoryId") String memoryId);
}
