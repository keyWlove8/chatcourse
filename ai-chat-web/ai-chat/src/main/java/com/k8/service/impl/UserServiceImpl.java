package com.k8.service.impl;

import com.k8.entity.UserInfo;
import com.k8.mapper.UserMapper;
import com.k8.service.UserService;
import com.k8.util.PasswdUtil;
import com.k8.vo.UserVO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import static com.k8.constants.Constants.ADMIN_TYPE;
import static com.k8.constants.Constants.USER_TYPE;

/**
 * @Author: k8
 * @CreateTime: 2025-08-28
 * @Version: 1.0
 */
@Service
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;
    @Override
    public UserVO selectUserById(String userId) {
        UserInfo userInfo = userMapper.selectById(userId);
        if (userInfo == null) return null;
        return buildUserVo(userInfo);
    }

    private UserVO buildUserVo(UserInfo userInfo) {
        UserVO userVO = new UserVO();
        userVO.setUserId(userInfo.getUserId());
        userVO.setUsername(userInfo.getUsername());
        userVO.setIsAdmin(ADMIN_TYPE.equals(userInfo.getType()));
        return userVO;
    }

    @Override
    public UserVO selectUserByName(String userName) {
        UserInfo userInfo = userMapper.selectByUserName(userName);
        if (userInfo == null) return null;
        return buildUserVo(userInfo);
    }

    @Override
    public boolean isAdmin(String userId) {
        UserInfo userInfo = userMapper.selectById(userId);
        return userInfo == null ? false : ADMIN_TYPE.equals(userInfo.getType());
    }
}
