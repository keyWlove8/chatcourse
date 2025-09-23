package com.k8.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.k8.entity.ChatDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @Author: k8
 * @CreateTime: 2025-08-28
 * @Version: 1.0
 */
@Mapper
public interface ChatDetailMapper extends BaseMapper<ChatDetail> {

    /**
     * 根据memoryId查询聊天详情
     */
    ChatDetail getChatDetailByMemoryId(@Param("memoryId") String memoryId);

    /**
     * 根据memoryId删除聊天详情
     */
    boolean deleteByMemoryId(@Param("memoryId") String memoryId);
}
