package com.k8.auth.simple;

import com.k8.entity.UserInfo;

/**
 * @Author: k8
 * @CreateTime: 2025-08-28
 * @Version: 1.0
 */
public interface AuthStore {
    UserInfo getUserInfo(String userId);
}
