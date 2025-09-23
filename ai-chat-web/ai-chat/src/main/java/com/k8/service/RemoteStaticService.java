package com.k8.service;

import com.k8.param.ImageContextPara;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Author: k8
 * @CreateTime: 2025-08-28
 * @Version: 1.0
 */
public interface RemoteStaticService {
    String sendImageAndGetUrl(ImageContextPara imageContextPara);
    
    /**
     * 上传图片文件并获取URL (MultipartFile版本)
     * @param file 图片文件
     * @return 图片文件访问URL
     */
    String sendImageAndGetUrl(MultipartFile file);
    
    /**
     * 上传音频文件并获取URL
     * @param audioData 音频字节数组
     * @return 音频文件访问URL
     */
    String uploadAudio(byte[] audioData);
}
