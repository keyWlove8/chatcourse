package com.k8.file.controller;

import com.k8.file.vo.UploadVo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * @Author: k8
 * @CreateTime: 2025-09-22
 * @Version: 1.0
 */
@RestController
public class ImageUploadController {
    
    @Value("${k8.static.basePath}")
    String basePath;
    
    @Value("${k8.static.host}")
    String host;
    
    /**
     * 图片上传接口
     * 简化版本，不需要复杂的策略验证
     */
    @PostMapping(value = "/api/image/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public UploadVo uploadImage(@RequestParam("file") MultipartFile file,
                               @RequestParam(value = "category", defaultValue = "chat") String category) {
        try {
            if (file.isEmpty()) {
                throw new IllegalArgumentException("上传的文件不能为空");
            }
            
            // 生成唯一文件名（包含时间戳和随机UUID，确保唯一性）
            String fileName = generateUniqueFileName(file.getOriginalFilename());
            String storagePath = "images/" + category + "/" + getCurrentDatePath();
            String fullPath = storagePath + "/" + fileName;
            
            // 创建存储目录
            Path targetPath = Paths.get(basePath, fullPath);
            Files.createDirectories(targetPath.getParent());
            
            // 保存文件
            Files.copy(file.getInputStream(), targetPath);
            
            // 只返回路径，不包含host（前端会动态拼接）
            String accessPath = "/download/" + fullPath;
            
            // 返回结果
            UploadVo result = new UploadVo();
            result.setUri(accessPath);
            result.setStatus("OK");
            result.setFileName(fileName);
            result.setSize(file.getSize());
            result.setContentType(file.getContentType());
            result.setStoragePath(fullPath); // 添加存储路径，便于调试
            
            return result;
            
        } catch (Exception e) {
            throw new RuntimeException("图片上传失败: " + e.getMessage());
        }
    }
    
    /**
     * 直接上传接口（不需要策略验证）
     */
    @PostMapping(value = "/api/image/upload/direct", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public UploadVo uploadDirect(@RequestParam("file") MultipartFile file,
                                @RequestParam(value = "category", defaultValue = "chat") String category) {
        return uploadImage(file, category);
    }
    
    /**
     * 文件下载接口
     * 根据存储路径准确找到并返回文件
     */
    @GetMapping("/download/**")
    public void download(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String requestURI = request.getRequestURI();
        String[] split = requestURI.split("/download/");
        
        if (split.length < 2 || split[1].trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        
        String filePath = split[1];
        Path file = Paths.get(basePath, filePath);
        
        if (!Files.exists(file) || Files.isDirectory(file)) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        
        // 设置响应头
        response.setContentType(Files.probeContentType(file));
        response.setHeader("Content-Disposition", "inline; filename=\"" + file.getFileName().toString() + "\"");
        
        // 复制文件到响应流
        Files.copy(file, response.getOutputStream());
        response.flushBuffer();
    }
    
    /**
     * 生成唯一文件名
     * 格式：image_日期_时间戳_UUID.扩展名
     * 确保每次上传的文件名都是唯一的，且与文件夹结构对应
     */
    private String generateUniqueFileName(String originalFilename) {
        // 获取文件扩展名
        String extension = ".jpg"; // 默认扩展名
        if (originalFilename != null && originalFilename.contains(".")) {
            int lastDotIndex = originalFilename.lastIndexOf('.');
            if (lastDotIndex > 0) {
                extension = originalFilename.substring(lastDotIndex);
            }
        }
        
        // 获取当前日期（用于文件夹和文件名前缀）
        LocalDateTime now = LocalDateTime.now();
        String datePrefix = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        
        // 生成时间戳（时分秒）
        String timestamp = now.format(DateTimeFormatter.ofPattern("HHmmss"));
        
        // 生成UUID（去掉横线，缩短长度）
        String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        
        // 组合文件名：image_日期_时间戳_UUID.扩展名
        return "image_" + datePrefix + "_" + timestamp + "_" + uuid + extension;
    }
    
    /**
     * 获取当前日期路径
     * 格式：年/月/日
     */
    private String getCurrentDatePath() {
        LocalDateTime now = LocalDateTime.now();
        return String.format("%d/%02d/%02d", 
            now.getYear(), 
            now.getMonthValue(), 
            now.getDayOfMonth());
    }
}
