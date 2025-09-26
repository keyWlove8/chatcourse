package com.k8.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.k8.entity.VoiceInfo;
import com.k8.vo.VoiceQueryVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 音色信息Mapper
 * @Author: k8
 * @CreateTime: 2025-09-23
 * @Version: 1.0
 */
@Mapper
public interface VoiceMapper extends BaseMapper<VoiceInfo> {
    
    /**
     * 查询所有启用的音色（用于用户选择）
     */
    @Select("SELECT id, voice_code, description, gender, name, language FROM voice_info WHERE is_enabled = 1 ORDER BY create_time DESC")
    List<VoiceQueryVO> selectEnabledVoices();
    
    /**
     * 根据性别查询启用的音色
     */
    @Select("SELECT id, voice_code, description, gender, name, language FROM voice_info WHERE is_enabled = 1 AND gender = #{gender} ORDER BY create_time DESC")
    List<VoiceQueryVO> selectEnabledVoicesByGender(@Param("gender") String gender);
    
    /**
     * 根据语言查询启用的音色
     */
    @Select("SELECT id, voice_code, description, gender, name, language FROM voice_info WHERE is_enabled = 1 AND language = #{language} ORDER BY create_time DESC")
    List<VoiceQueryVO> selectEnabledVoicesByLanguage(@Param("language") String language);
    
    /**
     * 根据性别和语言查询启用的音色
     */
    @Select("SELECT id, voice_code, description, gender, name, language FROM voice_info WHERE is_enabled = 1 AND gender = #{gender} AND language = #{language} ORDER BY create_time DESC")
    List<VoiceQueryVO> selectEnabledVoicesByGenderAndLanguage(@Param("gender") String gender, @Param("language") String language);
    
    /**
     * 根据关键词搜索音色
     */
    @Select("SELECT id, voice_code, description, gender, name, language FROM voice_info WHERE is_enabled = 1 AND (name LIKE CONCAT('%', #{keyword}, '%') OR description LIKE CONCAT('%', #{keyword}, '%')) ORDER BY create_time DESC")
    List<VoiceQueryVO> searchVoices(@Param("keyword") String keyword);
    
    /**
     * 查询所有音色（管理员用）
     */
    @Select("SELECT * FROM voice_info ORDER BY create_time DESC")
    List<VoiceInfo> selectAllVoices();
    
    /**
     * 根据ID查询音色
     */
    @Select("SELECT * FROM voice_info WHERE id = #{id}")
    VoiceInfo selectById(@Param("id") String id);
    
    /**
     * 根据ID查询音色（简化版，用于角色关联）
     */
    @Select("SELECT id, voice_code, description, gender, name, language FROM voice_info WHERE id = #{id}")
    VoiceQueryVO selectVoiceQueryById(@Param("id") String id);
}
