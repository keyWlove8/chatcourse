package com.k8.util;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * @Author: k8
 * @CreateTime: 2025-08-28
 * @Version: 1.0
 */
public class AuthUtil {
    public static String getToken(){
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null) {
            return null;
        }

        HttpServletRequest request = requestAttributes.getRequest();
        if (request == null) {
            return null;
        }

        String authorization = request.getHeader("Authorization");
        if (authorization != null && authorization.startsWith("Bearer ")) {
            return authorization.substring(7);
        }

        return authorization;
    }

    public static String getUserId(){
        String token = getToken();
        Claims claims = JwtUtil.parseToken(token);
        return claims.get("userId", String.class);
    }

    public static String getJti(){
        String token = getToken();
        Claims claims = JwtUtil.parseToken(token);
        return claims.getId();
    }
}
