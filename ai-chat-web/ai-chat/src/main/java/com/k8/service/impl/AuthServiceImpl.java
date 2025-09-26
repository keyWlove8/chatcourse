package com.k8.service.impl;


import com.k8.bean.TokenContext;
import com.k8.cache.TokenCache;
import com.k8.dto.LoginDTO;
import com.k8.entity.UserInfo;
import com.k8.exception.BusinessException;
import com.k8.mapper.UserMapper;
import com.k8.service.AuthService;
import com.k8.util.AuthUtil;
import com.k8.util.JwtUtil;
import com.k8.util.PasswdUtil;
import com.k8.vo.LoginVO;
import com.k8.vo.TokenRefreshVO;
import com.k8.vo.UserVO;
import io.jsonwebtoken.Claims;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;


import static com.k8.constants.AuthConstants.ACCESS_TOKEN_EXPIRE_TIME;
import static com.k8.constants.AuthConstants.ADMIN_TYPE;
import static com.k8.constants.AuthConstants.REFRESH_TOKEN_EXPIRE;


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

        TokenContext tokenContext = JwtUtil.generateTokens(userInfo.getUserId(), userInfo.getUsername());

        cacheTokens(tokenContext);

        // 4. 组装返回结果
        LoginVO loginVO = new LoginVO();
        loginVO.setAccessToken(tokenContext.getAccessToken());
        loginVO.setRefreshToken(tokenContext.getRefreshToken());
        loginVO.setUserInfo(buildUserVo(userInfo));
        return loginVO;
    }

    @Override
    public TokenRefreshVO refreshToken(String refreshToken) {
        try {
            // 解析refreshToken
            Claims refreshClaims = JwtUtil.parseToken(refreshToken);

            String jti = refreshClaims.getId();

            // 验证refreshToken是否在缓存中存在
            if (!tokenCache.containsRefreshToken(jti)) {
                throw new BusinessException(REFRESH_TOKEN_EXPIRE, "refreshToken不存在或已过期");
            }

            // 验证refreshToken的有效性
            JwtUtil.validRefreshToken(refreshClaims);

            String userId = refreshClaims.get("userId", String.class);

            // 获取用户信息
            String username = refreshClaims.get("username", String.class);

            // 生成新的token
            TokenContext tokenContext = JwtUtil.generateTokens(userId, username);

            refresh(refreshClaims);
            // 缓存新的token
            cacheTokens(tokenContext);

            // 返回新的token信息
            return new TokenRefreshVO(tokenContext.getAccessToken(), tokenContext.getRefreshToken(), "Bearer", ACCESS_TOKEN_EXPIRE_TIME / 2);

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException(500, "Token刷新失败: " + e.getMessage());
        }
    }

    private void cacheTokens(TokenContext tokenContext) {
        tokenCache.putAccessToken(tokenContext.getAccessJti(), tokenContext.getUserid());
        tokenCache.putRefreshToken(tokenContext.getRefreshJti(), tokenContext.getUserid());
    }

    @Override
    public void logout() {
        Claims claims = AuthUtil.getClaims();
        String jti = claims.getId();
        String refreshJti = claims.get("refreshJti", String.class);
        tokenCache.removeAccessToken(jti);
        tokenCache.removeRefreshToken(refreshJti);
    }

    private void refresh(Claims refreshClaims){
        String refreshJti = refreshClaims.getId();
        String accessJti = refreshClaims.get("accessJti", String.class);
        tokenCache.removeRefreshToken(refreshJti);
        tokenCache.removeAccessToken(accessJti);
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

    private static UserVO buildUserVo(UserInfo userInfo) {
        UserVO userVO = new UserVO();
        userVO.setUserId(userInfo.getUserId());
        userVO.setUsername(userInfo.getUsername());
        userVO.setIsAdmin(ADMIN_TYPE.equals(userInfo.getType()));
        return userVO;
    }
}
    
