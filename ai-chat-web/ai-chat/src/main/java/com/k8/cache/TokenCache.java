package com.k8.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

import static com.k8.util.JwtUtil.ACCESS_TOKEN_EXPIRE_TIME;
import static com.k8.util.JwtUtil.REFRESH_TOKEN_EXPIRE_TIME;

/**
 * @Author: k8
 * @CreateTime: 2025-08-28
 * @Version: 1.0
 */
@Component
public class TokenCache {
    private static final Cache<String, String> ACCESS_TOKEN_CACHE = CacheBuilder.newBuilder().expireAfterWrite(ACCESS_TOKEN_EXPIRE_TIME, TimeUnit.MILLISECONDS).initialCapacity(200).build();
    private static final Cache<String, String> REFRESH_TOKEN_CACHE = CacheBuilder.newBuilder().expireAfterWrite(REFRESH_TOKEN_EXPIRE_TIME, TimeUnit.MILLISECONDS).initialCapacity(200).build();

    public void putAccessToken(String jti, String userId) {
        ACCESS_TOKEN_CACHE.put(jti, userId);
    }

    public void removeAccessToken(String jti) {
        ACCESS_TOKEN_CACHE.invalidate(jti);
    }

    public boolean containsAccessToken(String userId, String jti) {
        String cacheUserId = ACCESS_TOKEN_CACHE.getIfPresent(jti);
        return cacheUserId != null && cacheUserId.equals(userId);
    }

    public void putRefreshToken(String jti, String userId) {
        REFRESH_TOKEN_CACHE.put(jti, userId);
    }

    public void removeRefreshToken(String jti) {
        REFRESH_TOKEN_CACHE.invalidate(jti);
    }

    public boolean containsRefreshToken(String userId, String jti) {
        String cacheUserId = REFRESH_TOKEN_CACHE.getIfPresent(jti);
        return cacheUserId != null && cacheUserId.equals(userId);
    }

    /**
     * 根据userId移除所有相关的token
     */
    public void removeAllTokensByUserId(String userId) {
        // 这里可以实现更复杂的逻辑来移除用户的所有token
        // 当前实现比较简单，实际项目中可能需要维护userId到jti的映射
    }
}
