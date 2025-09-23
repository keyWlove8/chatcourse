package com.k8.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

/**
 * 登录请求参数
 */
@Data
public class LoginDTO {
    @NotBlank(message = "用户名不能为空")
    private String username;
    
    @NotBlank(message = "密码不能为空")
    private String password;
}
    
