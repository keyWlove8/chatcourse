package com.k8.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

/**
 * 刷新Token请求参数
 */
@Data
public class RefreshTokenDTO {
    @NotBlank(message = "刷新Token不能为空")
    private String refreshToken;
}
