package com.k8.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.k8.entity.UserInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * @Author: k8
 * @CreateTime: 2025-09-23
 * @Version: 1.0
 */
@Mapper
public interface UserMapper extends BaseMapper<UserInfo> {
    
    /**
     * 根据用户ID查询用户信息
     */
    @Select("SELECT * FROM user_info WHERE user_id = #{userId}")
    UserInfo selectById(@Param("userId") String userId);

    /**
     * 根据用户名查询用户信息
     */
    @Select("SELECT * FROM user_info WHERE username = #{userName}")
    UserInfo selectByUserName(@Param("userName") String userName);
}
