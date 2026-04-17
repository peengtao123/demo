package com.example.demo.config;

import com.example.demo.dialect.PermissionDialect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Thymeleaf配置
 */
@Configuration
public class ThymeleafConfig {

    @Bean
    public PermissionDialect permissionDialect() {
        return new PermissionDialect();
    }
}
