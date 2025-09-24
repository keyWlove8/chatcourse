package com.k8.result;

import com.k8.vo.TokenRefreshVO;
import lombok.AllArgsConstructor;
import lombok.Data;


/**
 * @Author: k8
 * @CreateTime: 2025-09-23
 * @Version: 1.0
 */

@Data
@AllArgsConstructor
public class Result<T> {
    private Integer code;           // 业务状态码
    private String message;         // 响应消息
    private T data;                // 响应数据
    private Long timestamp;         // 时间戳
    private Boolean success;        // 是否成功

    // 成功响应
    public static <T> Result<T> success(T data) {
        return new Result<>(200, "成功", data, System.currentTimeMillis(), true);
    }
    public static <T> Result<T> success() {
        return new Result<>(200, "成功", null, System.currentTimeMillis(), true);
    }

    public static <T> Result<T> success(String message, T data) {
        return new Result<>(200, message, data, System.currentTimeMillis(), true);
    }

    // 失败响应
    public static <T> Result<T> fail(Integer code, String message) {
        return new Result<>(code, message, null, System.currentTimeMillis(), false);
    }
    public static <T> Result<T> fail(String message) {
        return new Result<>(500, message, null, System.currentTimeMillis(), false);
    }

    // Token刷新成功响应
    public static Result<TokenRefreshVO> tokenRefreshSuccess(String accessToken, String refreshToken) {
        TokenRefreshVO data = new TokenRefreshVO(accessToken, refreshToken, "Bearer", 7200);
        return new Result<>(200, "Token刷新成功", data, System.currentTimeMillis(), true);
    }
}
