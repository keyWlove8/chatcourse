package com.k8.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.k8.entity.ChatHistory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author: k8
 * @CreateTime: 2025-08-29
 * @Version: 1.0
 */
@Mapper
public interface ChatHistoryMapper extends BaseMapper<ChatHistory> {
    
    /**
     * 根据用户ID查询聊天历史列表
     */
    List<ChatHistory> selectHistoryByUserId(@Param("userId") String userId);

    /**
     * 根据memoryId删除聊天历史
     */
    boolean deleteHistoryByMemoryId(@Param("memoryId") String memoryId);

    /**
     * 根据memoryId查询聊天历史
     */
    ChatHistory getChatHistoryByMemoryId(@Param("memoryId") String memoryId);
}
