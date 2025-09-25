package com.k8.controller;

import com.k8.dto.VoiceCreateDTO;
import com.k8.result.Result;
import com.k8.service.VoiceService;
import com.k8.vo.VoiceQueryVO;
import com.k8.vo.VoiceVO;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 音色管理控制器
 * @Author: k8
 * @CreateTime: 2025-09-23
 * @Version: 1.0
 */
@RestController
@RequestMapping("/voice")
public class VoiceController {
    
    @Resource
    private VoiceService voiceService;
    
    /**
     * 创建音色（管理员功能）
     */
    @PostMapping("/create")
    public Result<VoiceVO> createVoice(@RequestBody VoiceCreateDTO createDTO, 
                                      @RequestHeader("userId") String userId) {
        try {
            VoiceVO voiceVO = voiceService.createVoice(createDTO, userId);
            return Result.success(voiceVO);
        } catch (Exception e) {
            return Result.fail(400, e.getMessage());
        }
    }
    
    /**
     * 获取所有音色（管理员功能）
     */
    @GetMapping("/list")
    public Result<List<VoiceVO>> getAllVoices() {
        try {
            List<VoiceVO> voices = voiceService.getAllVoices();
            return Result.success(voices);
        } catch (Exception e) {
            return Result.fail(400, e.getMessage());
        }
    }
    
    /**
     * 根据ID获取音色详情（管理员功能）
     */
    @GetMapping("/{id}")
    public Result<VoiceVO> getVoiceById(@PathVariable String id) {
        try {
            VoiceVO voiceVO = voiceService.getVoiceById(id);
            return Result.success(voiceVO);
        } catch (Exception e) {
            return Result.fail(400, e.getMessage());
        }
    }
    
    /**
     * 更新音色（管理员功能）
     */
    @PutMapping("/{id}")
    public Result<VoiceVO> updateVoice(@PathVariable String id, @RequestBody VoiceCreateDTO updateDTO) {
        try {
            VoiceVO voiceVO = voiceService.updateVoice(id, updateDTO);
            return Result.success(voiceVO);
        } catch (Exception e) {
            return Result.fail(400, e.getMessage());
        }
    }
    
    /**
     * 删除音色（管理员功能）
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteVoice(@PathVariable String id) {
        try {
            boolean success = voiceService.deleteVoice(id);
            return success ? Result.success() : Result.fail(400, "删除失败");
        } catch (Exception e) {
            return Result.fail(400, e.getMessage());
        }
    }
    
    /**
     * 启用/禁用音色（管理员功能）
     */
    @PutMapping("/{id}/toggle")
    public Result<Void> toggleVoiceStatus(@PathVariable String id, @RequestParam boolean enabled) {
        try {
            boolean success = voiceService.toggleVoiceStatus(id, enabled);
            return success ? Result.success() : Result.fail(400, "操作失败");
        } catch (Exception e) {
            return Result.fail(400, e.getMessage());
        }
    }
    
    /**
     * 查询所有启用的音色（用户功能）
     */
    @GetMapping("/enabled")
    public Result<List<VoiceQueryVO>> getEnabledVoices() {
        try {
            List<VoiceQueryVO> voices = voiceService.getEnabledVoices();
            return Result.success(voices);
        } catch (Exception e) {
            return Result.fail(400, e.getMessage());
        }
    }
    
    /**
     * 根据性别查询启用的音色（用户功能）
     */
    @GetMapping("/enabled/gender/{gender}")
    public Result<List<VoiceQueryVO>> getEnabledVoicesByGender(@PathVariable String gender) {
        try {
            List<VoiceQueryVO> voices = voiceService.getEnabledVoicesByGender(gender);
            return Result.success(voices);
        } catch (Exception e) {
            return Result.fail(400, e.getMessage());
        }
    }
    
    /**
     * 搜索音色（用户功能）
     */
    @GetMapping("/search")
    public Result<List<VoiceQueryVO>> searchVoices(@RequestParam(required = false) String keyword) {
        try {
            List<VoiceQueryVO> voices = voiceService.searchVoices(keyword);
            return Result.success(voices);
        } catch (Exception e) {
            return Result.fail(400, e.getMessage());
        }
    }
}
