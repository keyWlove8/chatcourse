package com.k8.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.k8.entity.ChatHistory;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * @Author: k8
 * @CreateTime: 2025-09-23
 * @Version: 1.0
 */
@Mapper
public interface ChatHistoryMapper extends BaseMapper<ChatHistory> {
    
    /**
     * 根据用户ID查询聊天历史列表
     */
    @Select("SELECT * FROM chat_history WHERE creator_id = #{userId} ORDER BY last_time DESC")
    List<ChatHistory> selectHistoryByUserId(@Param("userId") String userId);

    /**
     * 根据memoryId删除聊天历史
     */
    @Delete("DELETE FROM chat_history WHERE memory_id = #{memoryId}")
    boolean deleteHistoryByMemoryId(@Param("memoryId") String memoryId);

    /**
     * 根据memoryId查询聊天历史
     */
    @Select("SELECT * FROM chat_history WHERE memory_id = #{memoryId}")
    ChatHistory getChatHistoryByMemoryId(@Param("memoryId") String memoryId);
}
