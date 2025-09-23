package com.k8.vo;

import lombok.Data;

/**
 * 登录成功返回数据
 */
@Data
public class LoginVO {
    private Long expiresIn;
    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private UserVO userInfo;
}
    
