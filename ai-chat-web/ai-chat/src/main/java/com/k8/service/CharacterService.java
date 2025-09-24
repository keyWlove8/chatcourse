package com.k8.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.k8.dto.CharacterCreateDTO;
import com.k8.dto.CharacterQueryDTO;
import com.k8.entity.AiCharacter;
import com.k8.vo.CharacterVO;

/**
 * 角色服务接口
 * @Author: k8
 * @CreateTime: 2025-09-23
 * @Version: 1.0
 */
public interface CharacterService {
    
    /**
     * 创建角色
     */
    CharacterVO createCharacter(CharacterCreateDTO createDTO);
    
    /**
     * 分页查询角色
     */
    IPage<CharacterVO> getCharacterPage(CharacterQueryDTO queryDTO);
    
    /**
     * 根据ID获取角色
     */
    CharacterVO getCharacterById(String characterId);
    
    /**
     * 更新角色
     */
    CharacterVO updateCharacter(String characterId, CharacterCreateDTO updateDTO);
    
    /**
     * 删除角色
     */
    boolean deleteCharacter(String characterId);
    
    /**
     * 增加角色热度
     */
    boolean increasePopularity(String characterId);
}
