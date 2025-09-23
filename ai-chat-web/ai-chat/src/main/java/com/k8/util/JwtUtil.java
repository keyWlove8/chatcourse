package com.k8.util;

import com.k8.exception.BusinessException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;

/**
 * JWT工具类
 * 用于生成和解析JWT Token
 * 支持RSA非对称加密和HMAC对称加密
 *
 * @Author: k8
 * @CreateTime: 2025-07-29
 * @Version: 1.0
 */
@Component
public class JwtUtil {

    // 默认使用对称加密的密钥（用于开发环境）
    private static final String DEFAULT_SECRET_KEY = "k8DefaultSecretKeyForJWTTokenGeneration2025";
    
    // 密钥文件路径（相对于classpath）
    private static final String PRIVATE_KEY_PATH = "path/to/private.key";
    private static final String PUBLIC_KEY_PATH = "path/to/public.key";
    
    // AccessToken过期时间2小时（单位：毫秒）
    public static final long ACCESS_TOKEN_EXPIRE_TIME = 2 * 60 * 60 * 1000;

    // RefreshToken过期时间7天（单位：毫秒）
    public static final long REFRESH_TOKEN_EXPIRE_TIME = 7 * 24 * 60 * 60 * 1000;

    // 私钥缓存
    private static PrivateKey privateKey;

    // 公钥缓存
    private static PublicKey publicKey;
    
    // 对称加密密钥
    private static SecretKey secretKey;
    
    // 是否使用RSA加密
    private static boolean useRSA = false;

    static {
        try {
            // 尝试加载RSA密钥
            loadRSAKeys();
            if (privateKey != null && publicKey != null) {
                useRSA = true;
            } else {
                // 如果RSA密钥加载失败，使用对称加密
                secretKey = Keys.hmacShaKeyFor(DEFAULT_SECRET_KEY.getBytes(StandardCharsets.UTF_8));
                useRSA = false;
            }
        } catch (Exception e) {
            // 如果RSA密钥加载失败，使用对称加密
            try {
                secretKey = Keys.hmacShaKeyFor(DEFAULT_SECRET_KEY.getBytes(StandardCharsets.UTF_8));
                useRSA = false;
            } catch (Exception ex) {
                throw new RuntimeException("初始化JWT密钥失败", ex);
            }
        }
    }

    /**
     * 生成Access Token
     *
     * @param userId   用户ID
     * @param username 用户名
     * @return Access Token字符串
     */
    public static String generateAccessToken(String userId, String username) {
        // 设置Claims
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);
        claims.put("type", "access");

        return generateToken(claims, ACCESS_TOKEN_EXPIRE_TIME);
    }

    public static String refreshAccessToken(String refreshToken) {
        Claims refreshClaims = parseToken(refreshToken);
        return refreshAccessToken(refreshClaims);
    }

    // 当前代码有问题：expiration.after(new Date()) 应该是before
    public static void validRefreshToken(Claims refreshClaims) {
        String type = refreshClaims.get("type", String.class);
        Date expiration = refreshClaims.getExpiration();
        if (!"refresh".equals(type)) {
            throw new RuntimeException("token类型不是refreshToken");
        }
        // 修复：应该是 before，表示过期时间在当前时间之前
        if (expiration.before(new Date())) {
            throw new RuntimeException("refreshToken超时");
        }
    }

    public static void validAccessToken(Claims accessClaims) {
        String type = accessClaims.get("type", String.class);
        Date expiration = accessClaims.getExpiration();
        if (!"access".equals(type)) {
            throw new RuntimeException("token类型不是accessToken");
        }
        // 修复：应该是 before
        if (expiration.before(new Date())) {
            throw new RuntimeException("accessToken超时");
        }
    }

    public static String refreshAccessToken(Claims refreshClaims) {
        validRefreshToken(refreshClaims);
        String userId = refreshClaims.get("userId", String.class);
        String username = refreshClaims.get("username", String.class);
        return generateAccessToken(userId, username);
    }

    /**
     * 生成Refresh Token
     *
     * @param userId   用户ID
     * @param username 用户名
     * @return Refresh Token字符串
     */
    public static String generateRefreshToken(String userId, String username) {
        // 设置Claims
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);
        claims.put("type", "refresh");

        return generateToken(claims, REFRESH_TOKEN_EXPIRE_TIME);
    }

    /**
     * 生成Token
     *
     * @param claims    载荷信息
     * @param expireTime 过期时间
     * @return Token字符串
     */
    public static String generateToken(Map<String, Object> claims, long expireTime) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + expireTime);
        claims.put("jti", UUID.randomUUID().toString());
        if (useRSA && privateKey != null) {
            // 使用RSA非对称加密
            return Jwts.builder()
                    .setClaims(claims)
                    .setIssuedAt(now)
                    .setExpiration(expiration)
                    .signWith(privateKey, SignatureAlgorithm.RS256)
                    .compact();
        } else if (secretKey != null) {
            // 使用HMAC对称加密
            return Jwts.builder()
                    .setClaims(claims)
                    .setIssuedAt(now)
                    .setExpiration(expiration)
                    .signWith(secretKey, SignatureAlgorithm.HS256)
                    .compact();
        } else {
            throw new RuntimeException("JWT密钥未初始化");
        }
    }

    /**
     * 解析Token
     *
     * @param token Token字符串
     * @return Claims对象
     */
    public static Claims parseToken(String token) {
        try {
            if (useRSA && publicKey != null) {
                // 使用RSA公钥解析
                return Jwts.parserBuilder()
                        .setSigningKey(publicKey)
                        .build()
                        .parseClaimsJws(token)
                        .getBody();
            } else if (secretKey != null) {
                // 使用对称密钥解析
                return Jwts.parserBuilder()
                        .setSigningKey(secretKey)
                        .build()
                        .parseClaimsJws(token)
                        .getBody();
            } else {
                throw new RuntimeException("JWT密钥未初始化");
            }
        } catch (Exception e) {
            throw new BusinessException(401, "Token解析失败");
        }
    }

    /**
     * 验证Token是否有效
     *
     * @param token Token字符串
     * @return 是否有效
     */
    public static boolean validateToken(String token) {
        try {
            Claims claims = parseToken(token);
            return !claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 从Token中获取用户ID
     *
     * @param token Token字符串
     * @return 用户ID
     */
    public static String getUserIdFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.get("userId", String.class);
    }

    /**
     * 从Token中获取用户名
     *
     * @param token Token字符串
     * @return 用户名
     */
    public static String getUsernameFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.get("username", String.class);
    }

    /**
     * 从Token中获取管理员状态
     *
     * @param token Token字符串
     * @return 是否管理员
     */
    public static Boolean getIsAdminFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.get("isAdmin", Boolean.class);
    }

    /**
     * 加载RSA密钥
     */
    private static void loadRSAKeys() {
        try {
            // 使用ClassLoader从classpath加载密钥文件
            privateKey = loadPrivateKeyFromResource(PRIVATE_KEY_PATH);
            publicKey = loadPublicKeyFromResource(PUBLIC_KEY_PATH);
        } catch (Exception e) {
            // 如果加载失败，密钥保持为null，将使用对称加密
        }
    }

    /**
     * 从classpath资源加载私钥
     */
    private static PrivateKey loadPrivateKeyFromResource(String resourcePath) throws Exception {
        try (InputStream inputStream = JwtUtil.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                throw new IOException("无法找到私钥文件: " + resourcePath);
            }
            
            String privateKeyStr = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            return loadPrivateKeyFromString(privateKeyStr);
        }
    }

    /**
     * 从classpath资源加载公钥
     */
    private static PublicKey loadPublicKeyFromResource(String resourcePath) throws Exception {
        try (InputStream inputStream = JwtUtil.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                throw new IOException("无法找到公钥文件: " + resourcePath);
            }
            
            String publicKeyStr = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            return loadPublicKeyFromString(publicKeyStr);
        }
    }

    /**
     * 从字符串加载私钥
     */
    private static PrivateKey loadPrivateKeyFromString(String privateKeyStr) throws Exception {
        // 移除PEM格式的头部和尾部
        String privateKeyPEM = privateKeyStr
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");
        
        byte[] decoded = Base64.getDecoder().decode(privateKeyPEM);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decoded);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(spec);
    }

    /**
     * 从字符串加载公钥
     */
    private static PublicKey loadPublicKeyFromString(String publicKeyStr) throws Exception {
        // 移除PEM格式的头部和尾部
        String publicKeyPEM = publicKeyStr
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");
        
        byte[] decoded = Base64.getDecoder().decode(publicKeyPEM);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(decoded);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePublic(spec);
    }

    /**
     * 设置RSA密钥（用于测试或动态配置）
     */
    public static void setRSAKeys(String privateKeyStr, String publicKeyStr) {
        try {
            privateKey = loadPrivateKeyFromString(privateKeyStr);
            publicKey = loadPublicKeyFromString(publicKeyStr);
            useRSA = true;
        } catch (Exception e) {
            throw new RuntimeException("设置RSA密钥失败", e);
        }
    }

    /**
     * 重置为对称加密模式
     */
    public static void resetToSymmetricEncryption() {
        try {
            secretKey = Keys.hmacShaKeyFor(DEFAULT_SECRET_KEY.getBytes(StandardCharsets.UTF_8));
            useRSA = false;
            privateKey = null;
            publicKey = null;
        } catch (Exception e) {
            throw new RuntimeException("重置为对称加密失败", e);
        }
    }
}

