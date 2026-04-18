package com.example.demo.config;

import com.example.demo.dialect.PermissionDialect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Thymeleaf配置类
 * <p>配置Thymeleaf模板引擎的自定义功能，包括权限控制方言。</p>
 * 
 * @author Demo Team
 * @version 1.0
 * @since 2024-01-01
 */
@Configuration
public class ThymeleafConfig {

    /**
     * 注册权限控制方言
     * <p>将PermissionDialect注册为Bean，使Thymeleaf模板支持perm:hasPermission语法。</p>
     * 
     * @return PermissionDialect实例
     */
    @Bean
    public PermissionDialect permissionDialect() {
        return new PermissionDialect();
    }
}
