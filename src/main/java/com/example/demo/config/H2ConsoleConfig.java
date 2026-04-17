package com.example.demo.config;

import org.springframework.context.annotation.Configuration;

/**
 * H2控制台配置类
 * H2控制台主要通过application.properties/yml配置启用
 */
@Configuration
public class H2ConsoleConfig {
    // H2控制台配置建议在application.properties中设置:
    // spring.h2.console.enabled=true
    // spring.h2.console.path=/h2-console
    // spring.h2.console.settings.web-allow-others=true
}