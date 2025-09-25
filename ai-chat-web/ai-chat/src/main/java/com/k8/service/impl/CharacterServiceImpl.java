package com.k8.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.k8.dto.CharacterCreateDTO;
import com.k8.dto.CharacterQueryDTO;
import com.k8.entity.AiCharacter;
import com.k8.mapper.CharacterMapper;
import com.k8.mapper.VoiceMapper;
import com.k8.service.CharacterService;
import com.k8.util.AuthUtil;
import com.k8.vo.CharacterVO;
import com.k8.vo.VoiceQueryVO;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 角色服务实现
 * @Author: k8
 * @CreateTime: 2025-09-23
 * @Version: 1.0
 */
@Service
public class CharacterServiceImpl implements CharacterService {

    @Resource
    private CharacterMapper characterMapper;
    
    @Resource
    private VoiceMapper voiceMapper;

    @Override
    public CharacterVO createCharacter(CharacterCreateDTO createDTO) {
        String userId = AuthUtil.getUserId();
        
        // 验证音色是否存在（如果提供了音色ID）
        if (StringUtils.hasText(createDTO.getVoiceId())) {
            VoiceQueryVO voice = voiceMapper.selectVoiceQueryById(createDTO.getVoiceId());
            if (voice == null) {
                throw new RuntimeException("音色不存在");
            }
        }
        
        AiCharacter character = new AiCharacter();
        BeanUtils.copyProperties(createDTO, character);
        character.setCreatorId(userId);
        character.setPopularityScore(0);
        character.setCreatedTime(System.currentTimeMillis());
        character.setUpdatedTime(System.currentTimeMillis());
        
        characterMapper.insert(character);
        
        return convertToVO(character);
    }

    @Override
    public IPage<CharacterVO> getCharacterPage(CharacterQueryDTO queryDTO) {
        Page<AiCharacter> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        
        // 使用QueryWrapper构建查询条件
        QueryWrapper<AiCharacter> queryWrapper = new QueryWrapper<>();
        
        // 角色名模糊搜索
        if (StringUtils.hasText(queryDTO.getKeyword())) {
            queryWrapper.like("name", queryDTO.getKeyword());
        }
        
        // 公开性筛选
        if (queryDTO.getIsPublic() != null) {
            queryWrapper.eq("is_public", queryDTO.getIsPublic());
        }
        
        // 创建者筛选
        if (StringUtils.hasText(queryDTO.getCreatorId())) {
            queryWrapper.eq("creator_id", queryDTO.getCreatorId());
        }
        
        // 排序：有搜索词时按热度排序，否则按时间排序
        if (StringUtils.hasText(queryDTO.getKeyword())) {
            queryWrapper.orderByDesc("popularity_score", "created_time");
        } else {
            queryWrapper.orderByDesc("created_time");
        }
        
        IPage<AiCharacter> characterPage = characterMapper.selectPage(page, queryWrapper);
        
        return characterPage.convert(this::convertToVO);
    }

    @Override
    public CharacterVO getCharacterById(String characterId) {
        AiCharacter character = characterMapper.selectById(characterId);
        return character != null ? convertToVO(character) : null;
    }

    @Override
    public CharacterVO updateCharacter(String characterId, CharacterCreateDTO updateDTO) {
        String userId = AuthUtil.getUserId();
        
        // 检查权限（只能修改自己创建的角色）
        AiCharacter existingCharacter = characterMapper.selectById(characterId);
        if (existingCharacter == null) {
            throw new RuntimeException("角色不存在");
        }
        if (!userId.equals(existingCharacter.getCreatorId())) {
            throw new RuntimeException("无权限修改此角色");
        }
        
        // 验证音色是否存在（如果提供了音色ID）
        if (StringUtils.hasText(updateDTO.getVoiceId())) {
            VoiceQueryVO voice = voiceMapper.selectVoiceQueryById(updateDTO.getVoiceId());
            if (voice == null) {
                throw new RuntimeException("音色不存在");
            }
        }
        
        BeanUtils.copyProperties(updateDTO, existingCharacter);
        existingCharacter.setUpdatedTime(System.currentTimeMillis());
        
        characterMapper.updateById(existingCharacter);
        
        return convertToVO(existingCharacter);
    }

    @Override
    public boolean deleteCharacter(String characterId) {
        String userId = AuthUtil.getUserId();
        
        // 检查权限
        AiCharacter character = characterMapper.selectById(characterId);
        if (character == null) {
            throw new RuntimeException("角色不存在");
        }
        if (!userId.equals(character.getCreatorId())) {
            throw new RuntimeException("无权限删除此角色");
        }
        
        return characterMapper.deleteById(characterId) > 0;
    }

    @Override
    public boolean increasePopularity(String characterId) {
        AiCharacter character = characterMapper.selectById(characterId);
        if (character != null) {
            character.setPopularityScore(character.getPopularityScore() + 1);
            character.setUpdatedTime(System.currentTimeMillis());
            return characterMapper.updateById(character) > 0;
        }
        return false;
    }

    private CharacterVO convertToVO(AiCharacter character) {
        CharacterVO vo = new CharacterVO();
        BeanUtils.copyProperties(character, vo);
        
        // 加载关联的音色信息
        if (StringUtils.hasText(character.getVoiceId())) {
            try {
                VoiceQueryVO voice = voiceMapper.selectVoiceQueryById(character.getVoiceId());
                vo.setVoice(voice);
            } catch (Exception e) {
                // 如果音色不存在或查询失败，不设置音色信息
                vo.setVoice(null);
            }
        }
        
        return vo;
    }
}
