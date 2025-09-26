package com.k8.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: k8
 * @CreateTime: 2025-09-23
 * @Version: 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TokenRefreshVO {
    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private Long expiresIn;
}
