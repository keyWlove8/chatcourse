package com.k8.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @Author: k8
 * @CreateTime: 2025-08-28
 * @Version: 1.0
 */
public class PasswdUtil {
    public static void main(String[] args) {
        String s = encryptPasswordMd5("123456");
        String s1 = encryptPassword(s);
        System.out.println(s1);
    }
    private static final BCryptPasswordEncoder bcryptEncoder = new BCryptPasswordEncoder(12); // 强度为12
    private static final int SALT_LENGTH = 16;

    /**
     * 对MD5加密后的密码进行二次加密
     * @param md5Password 前端传来的MD5加密后的密码
     * @return 二次加密后的密码
     */
    public static String encryptPassword(String md5Password) {
        return bcryptEncoder.encode(md5Password);
    }

    /**
     * 验证密码
     * @param inputMd5Password 用户输入的MD5密码
     * @param storedPassword 数据库中存储的加密密码
     * @return 是否匹配
     */
    public static boolean verifyPassword(String inputMd5Password, String storedPassword) {
        return bcryptEncoder.matches(inputMd5Password, storedPassword);
    }

    private static String md5(String text) {
        if (text == null || text.isEmpty()) {
            throw new IllegalArgumentException("输入文本不能为空");
        }

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hash = md.digest(text.getBytes("UTF-8"));

            // 将字节数组转换为十六进制字符串
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b & 0xff));
            }

            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5算法不可用", e);
        } catch (Exception e) {
            throw new RuntimeException("MD5加密失败", e);
        }
    }

    /**
     * 密码加密（用于测试）
     * @param password 原始密码
     * @return MD5加密后的密码
     */
    public static String encryptPasswordMd5(String password) {
        return md5(password);
    }
}
