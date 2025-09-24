package com.k8.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.k8.dto.CharacterCreateDTO;
import com.k8.dto.CharacterQueryDTO;
import com.k8.result.Result;
import com.k8.service.CharacterService;
import com.k8.service.RemoteStaticService;
import com.k8.vo.CharacterVO;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 角色控制器
 * @Author: k8
 * @CreateTime: 2025-09-23
 * @Version: 1.0
 */
@RequestMapping("/character")
@RestController
public class CharacterController {

    @Resource
    private CharacterService characterService;

    @Resource
    private RemoteStaticService remoteStaticService;

    /**
     * 创建角色
     */
    @PostMapping("/create")
    public Result<CharacterVO> createCharacter(@RequestBody CharacterCreateDTO createDTO) {
        CharacterVO character = characterService.createCharacter(createDTO);
        return Result.success(character);
    }

    /**
     * 分页查询角色
     */
    @PostMapping("/page")
    public Result<IPage<CharacterVO>> getCharacterPage(@RequestBody CharacterQueryDTO queryDTO) {
        IPage<CharacterVO> page = characterService.getCharacterPage(queryDTO);
        return Result.success(page);
    }

    /**
     * 根据ID获取角色
     */
    @GetMapping("/{id}")
    public Result<CharacterVO> getCharacterById(@PathVariable String id) {
        CharacterVO character = characterService.getCharacterById(id);
        return Result.success(character);
    }

    /**
     * 更新角色
     */
    @PutMapping("/{id}")
    public Result<CharacterVO> updateCharacter(@PathVariable String id, @RequestBody CharacterCreateDTO updateDTO) {
        CharacterVO character = characterService.updateCharacter(id, updateDTO);
        return Result.success(character);
    }

    /**
     * 删除角色
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteCharacter(@PathVariable String id) {
        characterService.deleteCharacter(id);
        return Result.success();
    }

    /**
     * 增加角色热度
     */
    @PostMapping("/{id}/popularity")
    public Result<Void> increasePopularity(@PathVariable String id) {
        characterService.increasePopularity(id);
        return Result.success();
    }

    /**
     * 上传角色头像
     */
    @PostMapping("/upload-avatar")
    public Result<String> uploadAvatar(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return Result.fail("文件不能为空");
            }
            
            // 检查文件类型
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return Result.fail("只支持图片文件");
            }
            
            // 检查文件大小 (5MB)
            if (file.getSize() > 5 * 1024 * 1024) {
                return Result.fail("文件大小不能超过5MB");
            }
            
            // 通过RemoteStaticService上传到ai-static服务
            String avatarUrl = remoteStaticService.sendImageAndGetUrl(file);
            return Result.success(avatarUrl);
            
        }  catch (Exception e) {
            return Result.fail("头像上传失败: " + e.getMessage());
        }
    }
}
