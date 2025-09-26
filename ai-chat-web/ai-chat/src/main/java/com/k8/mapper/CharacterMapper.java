package com.k8.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.k8.entity.AiCharacter;
import org.apache.ibatis.annotations.Mapper;

/**
 * 角色Mapper
 * @Author: k8
 * @CreateTime: 2025-01-01
 * @Version: 1.0
 */
@Mapper
public interface CharacterMapper extends BaseMapper<AiCharacter> {
}
