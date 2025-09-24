package com.k8.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.k8.entity.ChatDetail;
import org.apache.ibatis.annotations.*;

/**
 * @Author: k8
 * @CreateTime: 2025-09-23
 * @Version: 1.0
 */
@Mapper
public interface ChatDetailMapper extends BaseMapper<ChatDetail> {

    /**
     * 根据memoryId查询聊天详情
     */
    @Select("SELECT * FROM chat_detail WHERE memory_id = #{memoryId}")
    ChatDetail getChatDetailByMemoryId(@Param("memoryId") String memoryId);

    /**
     * 根据memoryId删除聊天详情
     */
    @Delete("DELETE FROM chat_detail WHERE memory_id = #{memoryId}")
    boolean deleteByMemoryId(@Param("memoryId") String memoryId);
}
