package com.k8.service;

import com.k8.entity.UserInfo;
import com.k8.vo.UserVO;

/**
 * @Author: k8
 * @CreateTime: 2025-09-23
 * @Version: 1.0
 */
public interface UserService {
    UserVO selectUserById(String userId);
    UserVO selectUserByName(String userName);

    boolean isAdmin(String userId);
}
