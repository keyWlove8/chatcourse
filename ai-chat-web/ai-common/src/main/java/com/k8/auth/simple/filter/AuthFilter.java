package com.k8.simple.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.k8.simple.annotation.Auth;
import com.k8.cache.TokenCache;
import com.k8.exception.BusinessException;
import com.k8.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.annotation.Resource;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import static com.k8.constants.Constants.UN_ACCESS_TOKEN;

/**
 * @Author: k8
 * @CreateTime: 2025-09-23
 * @Version: 1.0
 */

@WebFilter(urlPatterns = "/*", filterName = "authFilter")
public class AuthFilter implements Filter {
    @Resource
    TokenCache tokenCache;

    private RequestMappingHandlerMapping requestMappingHandlerMapping;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        WebApplicationContext wac = WebApplicationContextUtils.getWebApplicationContext(filterConfig.getServletContext());
        if (wac != null) {
            requestMappingHandlerMapping = wac.getBean(RequestMappingHandlerMapping.class);
        }
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String authorization = request.getHeader("Authorization");

        try {
            if (checkAuth(request)) {
                if (authorization == null) {
                    sendErrorResponse(response, 401, "Token为空");
                    return;
                }
                String accessToken = authorization.replace("Bearer ", "");
                Claims accessClaims = JwtUtil.parseToken(accessToken);
                String jti = accessClaims.getId();
                if (!tokenCache.containsAccessToken(accessClaims.get("userId", String.class), jti)) {
                    sendErrorResponse(response, UN_ACCESS_TOKEN, "accessToken不存在或已过期");
                    return;
                }
                JwtUtil.validAccessToken(accessClaims);
            }
        } catch (BusinessException e) {
            // 业务异常，返回对应的错误码
            sendErrorResponse(response, e.getCode(), e.getMessage());
            return;
        } catch (Exception e) {
            // 其他异常，返回500
            sendErrorResponse(response, 500, "服务器内部错误: " + e.getMessage());
            return;
        }
        filterChain.doFilter(request, response);
    }

    /**
     * 发送错误响应
     */
    private void sendErrorResponse(HttpServletResponse response, int code, String message) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(200); // 返回200状态码，错误信息在响应体中

        // 构建统一的错误响应
        Map<String, Object> result = new HashMap<>();
        result.put("code", code);
        result.put("message", message);
        result.put("data", null);
        result.put("timestamp", System.currentTimeMillis());
        result.put("success", false);

        PrintWriter writer = response.getWriter();
        writer.write(new ObjectMapper().writeValueAsString(result));
        writer.flush();
    }

    private boolean checkAuth(HttpServletRequest httpRequest) throws Exception {
        HandlerMethod handlerMethod = getHandlerMethod(httpRequest);
        if (handlerMethod != null) {
            return !handlerMethod.hasMethodAnnotation(Auth.class) || handlerMethod.getMethodAnnotation(Auth.class).required();
        }
        return false;
    }

    private HandlerMethod getHandlerMethod(HttpServletRequest request) throws Exception {
        if (requestMappingHandlerMapping == null) {
            return null;
        }
        HandlerExecutionChain executionChain = requestMappingHandlerMapping.getHandler(request);
        if (executionChain == null) return null;
        Object handler = executionChain.getHandler();
        if (handler == null) return null;
        if (handler instanceof HandlerMethod) {
            return (HandlerMethod) handler;
        }
        return null;
    }

    @Override
    public void destroy() {
        // 清理逻辑，释放资源
        Filter.super.destroy();
    }
}
