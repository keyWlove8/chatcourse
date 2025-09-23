package com.k8.file.handler;

import com.k8.file.vo.UploadVo;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * 全局异常处理器
 * 
 * @Author: k8
 * @CreateTime: 2025-08-29
 * @Version: 1.0
 */
@ControllerAdvice
public class StaticExceptionHandler {
    
    @ExceptionHandler(RuntimeException.class)
    public UploadVo handleException(RuntimeException ex) {
        UploadVo result = new UploadVo();
        result.setStatus("FAIL");
        result.setUri("");
        result.setFileName("");
        result.setSize(0L);
        result.setContentType("");
        result.setStoragePath("");
        return result;
    }
}
