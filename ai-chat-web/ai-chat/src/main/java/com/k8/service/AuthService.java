package com.k8.service;


import com.k8.dto.LoginDTO;
import com.k8.vo.LoginVO;
import com.k8.vo.TokenRefreshVO;
import com.k8.vo.UserVO;

/**
 * 认证服务接口（登录、刷新Token等）
 */
public interface AuthService {

    LoginVO login(LoginDTO loginDTO);

    /**
     * 刷新Token
     * @param refreshToken 刷新token
     * @return 新的token信息
     */
    TokenRefreshVO refreshToken(String refreshToken);

    void logout();

    UserVO getCurrentUser();
}
    
