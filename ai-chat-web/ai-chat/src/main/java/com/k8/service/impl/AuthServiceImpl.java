package com.k8.service.impl;


import com.k8.cache.TokenCache;
import com.k8.dto.LoginDTO;
import com.k8.entity.UserInfo;
import com.k8.exception.BusinessException;
import com.k8.mapper.UserMapper;
import com.k8.service.AuthService;
import com.k8.service.UserService;
import com.k8.util.AuthUtil;
import com.k8.util.JwtUtil;
import com.k8.util.PasswdUtil;
import com.k8.vo.LoginVO;
import com.k8.vo.TokenRefreshVO;
import com.k8.vo.UserVO;
import io.jsonwebtoken.Claims;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import static com.k8.constants.Constants.ADMIN_TYPE;
import static com.k8.constants.Constants.REFRESH_TOKEN_EXPIRE;

/**
 * 认证服务实现类（示例，实际项目需对接数据库和JWT）
 */
@Service
public class AuthServiceImpl implements AuthService {

    @Resource
    TokenCache tokenCache;

    @Resource
    UserMapper userMapper;

    @Override
    public LoginVO login(LoginDTO loginDTO) {

        UserInfo userInfo = userMapper.selectByUserName(loginDTO.getUsername());
        if (userInfo == null || !PasswdUtil.verifyPassword(loginDTO.getPassword(), userInfo.getPassword())) {
            throw new BusinessException(400, "");
        }

        // 2. 使用JWT生成Token
        String accessToken = JwtUtil.generateAccessToken(
                userInfo.getUserId(),
                userInfo.getUsername()

        );
        String refreshToken = JwtUtil.generateRefreshToken(
                userInfo.getUserId(),
                userInfo.getUsername()
        );

        cacheTokens(accessToken, refreshToken, userInfo.getUserId());

        // 4. 组装返回结果
        LoginVO loginVO = new LoginVO();
        loginVO.setAccessToken(accessToken);
        loginVO.setRefreshToken(refreshToken);
        loginVO.setUserInfo(buildUserVo(userInfo));
        return loginVO;
    }

    @Override
    public TokenRefreshVO refreshToken(String refreshToken) {
        try {
            // 解析refreshToken
            Claims refreshClaims = JwtUtil.parseToken(refreshToken);
            String jti = refreshClaims.getId();
            String userId = refreshClaims.get("userId", String.class);
            
            // 验证refreshToken是否在缓存中存在
            if (!tokenCache.containsRefreshToken(userId, jti)) {
                throw new BusinessException(REFRESH_TOKEN_EXPIRE, "refreshToken不存在或已过期");
            }

            // 验证refreshToken的有效性
            JwtUtil.validRefreshToken(refreshClaims);

            // 获取用户信息
            String username = refreshClaims.get("username", String.class);

            // 生成新的token
            String newAccessToken = JwtUtil.generateAccessToken(userId, username);
            String newRefreshToken = JwtUtil.generateRefreshToken(userId, username);

            // 缓存新的token
            cacheTokens(newAccessToken, newRefreshToken, userId);

            // 返回新的token信息
            return new TokenRefreshVO(newAccessToken, newRefreshToken, "Bearer", 7200);
            
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException(500, "Token刷新失败: " + e.getMessage());
        }
    }

    private void cacheTokens(String newAccessToken, String newRefreshToken, String userId) {
        Claims claims = JwtUtil.parseToken(newAccessToken);
        String jti = claims.getId();
        tokenCache.putAccessToken(jti, userId);
        Claims refreshclaims = JwtUtil.parseToken(newRefreshToken);
        String refreshJti = refreshclaims.getId();
        tokenCache.putRefreshToken(refreshJti, userId);
    }

    @Override
    public void logout() {
        String jti = AuthUtil.getJti();
        if (jti != null) {
            tokenCache.removeAccessToken(jti);
            // 注意：这里需要知道refreshToken的jti才能移除
            // 可以考虑在用户登录时维护一个映射关系
        }
    }

    @Override
    public UserVO getCurrentUser() {
        // 根据userId查询用户信息（实际项目查数据库）
        String userId = AuthUtil.getUserId();
        UserInfo userInfo = userMapper.selectById(userId);
        if (userInfo == null) {
            throw new BusinessException(401, "");
        }
        return buildUserVo(userInfo);
    }

    private static UserVO buildUserVo(UserInfo userInfo){
        UserVO userVO = new UserVO();
        userVO.setUserId(userInfo.getUserId());
        userVO.setUsername(userInfo.getUsername());
        userVO.setIsAdmin(ADMIN_TYPE.equals(userInfo.getType()));
        return userVO;
    }
}
    
