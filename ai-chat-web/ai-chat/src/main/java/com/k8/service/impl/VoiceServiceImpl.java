package com.k8.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.k8.dto.VoiceCreateDTO;
import com.k8.entity.VoiceInfo;
import com.k8.mapper.VoiceMapper;
import com.k8.service.VoiceService;
import com.k8.vo.VoiceQueryVO;
import com.k8.vo.VoiceVO;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.UUID;

/**
 * 音色服务实现类
 * @Author: k8
 * @CreateTime: 2025-09-23
 * @Version: 1.0
 */
@Service
public class VoiceServiceImpl implements VoiceService {
    
    @Resource
    private VoiceMapper voiceMapper;
    
    @Override
    public VoiceVO createVoice(VoiceCreateDTO createDTO, String creatorId) {
        // 验证必填字段
        if (!StringUtils.hasText(createDTO.getVoiceCode()) || 
            !StringUtils.hasText(createDTO.getName()) ||
            !StringUtils.hasText(createDTO.getGender())) {
            throw new RuntimeException("音色码、名称和性别不能为空");
        }
        
        // 检查音色码是否已存在
        LambdaQueryWrapper<VoiceInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(VoiceInfo::getVoiceCode, createDTO.getVoiceCode());
        if (voiceMapper.selectCount(queryWrapper) > 0) {
            throw new RuntimeException("音色码已存在");
        }
        
        // 创建音色实体
        VoiceInfo voiceInfo = new VoiceInfo();
        BeanUtils.copyProperties(createDTO, voiceInfo);
        voiceInfo.setId(UUID.randomUUID().toString());
        voiceInfo.setCreateTime(System.currentTimeMillis());
        voiceInfo.setCreatorId(creatorId);
        voiceInfo.setIsEnabled(1); // 默认启用
        
        // 保存到数据库
        voiceMapper.insert(voiceInfo);
        
        // 返回VO
        return convertToVO(voiceInfo);
    }
    
    @Override
    public List<VoiceVO> getAllVoices() {
        List<VoiceInfo> voiceInfos = voiceMapper.selectAllVoices();
        return voiceInfos.stream()
                .map(this::convertToVO)
                .toList();
    }
    
    @Override
    public VoiceVO getVoiceById(String id) {
        VoiceInfo voiceInfo = voiceMapper.selectById(id);
        if (voiceInfo == null) {
            throw new RuntimeException("音色不存在");
        }
        return convertToVO(voiceInfo);
    }
    
    @Override
    public VoiceVO updateVoice(String id, VoiceCreateDTO updateDTO) {
        VoiceInfo voiceInfo = voiceMapper.selectById(id);
        if (voiceInfo == null) {
            throw new RuntimeException("音色不存在");
        }
        
        // 检查音色码是否与其他记录冲突
        if (StringUtils.hasText(updateDTO.getVoiceCode()) && 
            !updateDTO.getVoiceCode().equals(voiceInfo.getVoiceCode())) {
            LambdaQueryWrapper<VoiceInfo> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(VoiceInfo::getVoiceCode, updateDTO.getVoiceCode());
            queryWrapper.ne(VoiceInfo::getId, id);
            if (voiceMapper.selectCount(queryWrapper) > 0) {
                throw new RuntimeException("音色码已存在");
            }
        }
        
        // 更新字段
        BeanUtils.copyProperties(updateDTO, voiceInfo, "id", "createTime", "creatorId", "isEnabled");
        
        // 保存更新
        voiceMapper.updateById(voiceInfo);
        
        return convertToVO(voiceInfo);
    }
    
    @Override
    public boolean deleteVoice(String id) {
        VoiceInfo voiceInfo = voiceMapper.selectById(id);
        if (voiceInfo == null) {
            throw new RuntimeException("音色不存在");
        }
        
        return voiceMapper.deleteById(id) > 0;
    }
    
    @Override
    public boolean toggleVoiceStatus(String id, boolean enabled) {
        VoiceInfo voiceInfo = voiceMapper.selectById(id);
        if (voiceInfo == null) {
            throw new RuntimeException("音色不存在");
        }
        
        voiceInfo.setIsEnabled(enabled ? 1 : 0);
        return voiceMapper.updateById(voiceInfo) > 0;
    }
    
    @Override
    public List<VoiceQueryVO> getEnabledVoices() {
        return voiceMapper.selectEnabledVoices();
    }
    
    @Override
    public List<VoiceQueryVO> getEnabledVoicesByGender(String gender) {
        return voiceMapper.selectEnabledVoicesByGender(gender);
    }
    
    @Override
    public List<VoiceQueryVO> getEnabledVoicesByLanguage(String language) {
        return voiceMapper.selectEnabledVoicesByLanguage(language);
    }
    
    @Override
    public List<VoiceQueryVO> getEnabledVoicesByGenderAndLanguage(String gender, String language) {
        return voiceMapper.selectEnabledVoicesByGenderAndLanguage(gender, language);
    }
    
    @Override
    public List<VoiceQueryVO> searchVoices(String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return getEnabledVoices();
        }
        return voiceMapper.searchVoices(keyword);
    }
    
    /**
     * 转换为VO对象
     */
    private VoiceVO convertToVO(VoiceInfo voiceInfo) {
        VoiceVO voiceVO = new VoiceVO();
        BeanUtils.copyProperties(voiceInfo, voiceVO);
        return voiceVO;
    }
}
