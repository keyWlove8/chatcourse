package com.k8.controller;

import com.k8.dto.KnowledgeCreateDTO;
import com.k8.result.Result;
import com.k8.service.KnowledgeService;
import com.k8.vo.FileUploadVO;
import com.k8.vo.KnowledgeVO;
import jakarta.annotation.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.k8.result.Result.success;

/**
 * @Author: k8
 * @CreateTime: 2025-09-23
 * @Version: 1.0
 */
@RequestMapping("/knowledge")
@RestController
public class KnowledgeController {

    @Resource
    private KnowledgeService knowledgeService;

    /**
     * 获取知识库列表
     */
    @GetMapping("/list")
    public ResponseEntity<Result<List<KnowledgeVO>>> getKnowledgeList() {
        List<KnowledgeVO> list = knowledgeService.getKnowledgeList();
        return ResponseEntity.ok(success(list));
    }

    /**
     * 创建知识库
     */
    @PostMapping("/create")
    public ResponseEntity<Result<KnowledgeVO>> createKnowledge(@RequestBody KnowledgeCreateDTO createDTO) {
        KnowledgeVO knowledgeVO = knowledgeService.createKnowledge(createDTO);
        return ResponseEntity.ok(success(knowledgeVO));
    }

    /**
     * 上传知识库文件
     */
    @PostMapping("/upload")
    public ResponseEntity<Result<FileUploadVO>> uploadKnowledgeFile(
            @RequestParam("knowledgeId") String knowledgeId,
            @RequestParam("file") MultipartFile file) {
        FileUploadVO uploadVO = knowledgeService.uploadKnowledgeFile(knowledgeId, file);
        return ResponseEntity.ok(success(uploadVO));
    }

    /**
     * 删除知识库（扩展接口）
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Result<Void>> deleteKnowledge(@PathVariable("id") String id) {
        knowledgeService.deleteKnowledge(id);
        return ResponseEntity.ok(success());
    }
}
