package com.k8.controller;


import com.k8.auth.simple.annotation.Auth;
import com.k8.dto.LoginDTO;
import com.k8.dto.RefreshTokenDTO;
import com.k8.result.Result;
import com.k8.service.AuthService;
import com.k8.vo.LoginVO;
import com.k8.vo.TokenRefreshVO;
import com.k8.vo.UserVO;
import jakarta.annotation.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @Author: k8
 * @CreateTime: 2025-08-27
 * @Version: 1.0
 */
@RequestMapping("/auth")
@RestController
public class AuthController{

    @Resource
    private AuthService authService;

    /**
     * 用户登录
     */
    @PostMapping("/login")
    @Auth(required = false)
    public ResponseEntity<Result<LoginVO>> login(@RequestBody LoginDTO loginDTO) {
        LoginVO loginVO = authService.login(loginDTO);
        return ResponseEntity.ok(Result.success(loginVO));
    }

    /**
     * 刷新Token
     */
    @PostMapping("/refresh")
    @Auth(required = false)
    public ResponseEntity<Result<TokenRefreshVO>> refreshToken(@RequestBody RefreshTokenDTO refreshTokenDTO) {
        TokenRefreshVO tokenRefreshVO = authService.refreshToken(refreshTokenDTO.getRefreshToken());
        return ResponseEntity.ok(Result.success(tokenRefreshVO));
    }

    /**
     * 用户登出
     */
    @PostMapping("/logout")
    public ResponseEntity<Result<Void>> logout(@RequestHeader("Authorization") String token) {
        authService.logout();
        return ResponseEntity.ok(Result.success());
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/currentUser")
    public ResponseEntity<Result<UserVO>> getCurrentUser() {
        UserVO userVO = authService.getCurrentUser();
        return ResponseEntity.ok(Result.success(userVO));
    }
}
