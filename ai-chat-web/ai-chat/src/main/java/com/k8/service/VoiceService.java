package com.k8.service;

import com.k8.dto.VoiceCreateDTO;
import com.k8.vo.VoiceQueryVO;
import com.k8.vo.VoiceVO;

import java.util.List;

/**
 * 音色服务接口
 * @Author: k8
 * @CreateTime: 2025-09-23
 * @Version: 1.0
 */
public interface VoiceService {
    
    /**
     * 创建音色（管理员功能）
     */
    VoiceVO createVoice(VoiceCreateDTO createDTO, String creatorId);
    
    /**
     * 获取所有音色（管理员功能）
     */
    List<VoiceVO> getAllVoices();
    
    /**
     * 根据ID获取音色详情（管理员功能）
     */
    VoiceVO getVoiceById(String id);
    
    /**
     * 更新音色（管理员功能）
     */
    VoiceVO updateVoice(String id, VoiceCreateDTO updateDTO);
    
    /**
     * 删除音色（管理员功能）
     */
    boolean deleteVoice(String id);
    
    /**
     * 启用/禁用音色（管理员功能）
     */
    boolean toggleVoiceStatus(String id, boolean enabled);
    
    /**
     * 查询所有启用的音色（用户功能）
     */
    List<VoiceQueryVO> getEnabledVoices();
    
    /**
     * 根据性别查询启用的音色（用户功能）
     */
    List<VoiceQueryVO> getEnabledVoicesByGender(String gender);
    
    /**
     * 搜索音色（用户功能）
     */
    List<VoiceQueryVO> searchVoices(String keyword);
}
