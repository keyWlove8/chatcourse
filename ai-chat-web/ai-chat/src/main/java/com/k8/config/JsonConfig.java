package com.k8.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * @Author: k8
 * @CreateTime: 2025-09-23
 * @Version: 1.0
 */
@Configuration
public class JsonConfig {
    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();

        // 基本配置
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL); // 忽略 null 字段
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // 禁用时间戳
        mapper.enable(SerializationFeature.INDENT_OUTPUT); // 美化输出

        // 注册 Java 8 时间模块（LocalDate/LocalDateTime）
        mapper.registerModule(new JavaTimeModule());

        // 注册自定义模块（可选）
        // mapper.registerModule(new CustomModule());

        return mapper;
    }
}
