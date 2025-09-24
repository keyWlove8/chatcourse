package com.k8.exception;

import lombok.Getter;

/**
 * @Author: k8
 * @CreateTime: 2025-09-23
 * @Version: 1.0
 */
@Getter
public class BusinessException extends RuntimeException {
    private final Integer code; // 业务状态码
    private final String message; // 错误描述

    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }
}
