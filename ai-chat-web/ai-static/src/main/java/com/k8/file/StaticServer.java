package com.k8.file;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @Author: k8
 * @CreateTime: 2025-09-23
 * @Version: 1.0
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.k8"})
public class StaticServer {
    public static void main(String[] args) {
        SpringApplication.run(StaticServer.class);
    }
}
