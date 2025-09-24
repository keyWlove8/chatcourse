package com.k8.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.k8.entity.MultiChatMessage;
import org.apache.ibatis.annotations.*;

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
    @Select("SELECT * FROM multi_chat_message WHERE memory_id = #{memoryId} ORDER BY timestamp ASC")
    List<MultiChatMessage> selectChatMessagesByMemoryId(@Param("memoryId") String memoryId);

    /**
     * 根据memoryId删除聊天消息
     */
    @Delete("DELETE FROM multi_chat_message WHERE memory_id = #{memoryId}")
    int deleteByMemoryId(@Param("memoryId") String memoryId);
}
