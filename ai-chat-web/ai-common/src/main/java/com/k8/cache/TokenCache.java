package com.k8.cache;

import jakarta.annotation.Resource;
import lombok.Data;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

import static com.k8.constants.AuthConstants.ACCESS_TOKEN_EXPIRE_TIME;
import static com.k8.constants.AuthConstants.REFRESH_TOKEN_EXPIRE_TIME;

/**
 * @Author: k8
 * @CreateTime: 2025-09-23
 * @Version: 1.0
 */
@Component
@Data
public class TokenCache {
    @Resource
    private RedisTemplate<String,Object> redisTemplate;

    private static String REFRESH_PREFIX = "refresh_token:";
    private static String ACCESS_PREFIX = "access_token:";

    public void putAccessToken(String jti, String userId) {
        redisTemplate.opsForValue().set(ACCESS_PREFIX + jti, userId, ACCESS_TOKEN_EXPIRE_TIME, TimeUnit.MILLISECONDS);
    }

    public void removeAccessToken(String jti) {
        redisTemplate.delete(ACCESS_PREFIX + jti);
    }

    public boolean containsAccessToken(String jti) {
        return redisTemplate.hasKey(ACCESS_PREFIX + jti);
    }

    public void putRefreshToken(String jti, String userId) {
        redisTemplate.opsForValue().set(REFRESH_PREFIX + jti, userId, REFRESH_TOKEN_EXPIRE_TIME, TimeUnit.MILLISECONDS);
    }



    public void removeRefreshToken(String refreshJti) {
        redisTemplate.delete(REFRESH_PREFIX + refreshJti);
    }

    public boolean containsRefreshToken(String jti) {
        return redisTemplate.hasKey(REFRESH_PREFIX + jti);
    }

    /**
     * 根据userId移除所有相关的token
     */
    public void removeAllTokensByUserId(String userId) {
        // 这里可以实现更复杂的逻辑来移除用户的所有token
        // 当前实现比较简单，实际项目中可能需要维护userId到jti的映射
    }
}
