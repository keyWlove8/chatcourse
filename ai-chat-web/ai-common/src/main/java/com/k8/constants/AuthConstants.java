package com.k8.constants;

/**
 * @Author: k8
 * @CreateTime: 2025-09-23
 * @Version: 1.0
 */
public interface AuthConstants {
    int UN_ACCESS_TOKEN = 401;
    int REFRESH_TOKEN_EXPIRE = 402;
    int NO_ACCESS = 403;
    String ADMIN_TYPE = "admin";

    // AccessToken过期时间2小时（单位：毫秒）
    long ACCESS_TOKEN_EXPIRE_TIME = 4 * 60 * 60 * 1000;

    // RefreshToken过期时间7天（单位：毫秒）
    long REFRESH_TOKEN_EXPIRE_TIME = 7 * 24 * 60 * 60 * 1000;
}
