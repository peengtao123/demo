package com.example.demo.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Jackson JSON配置类
 * <p>根据Spring Boot 4.x最佳实践，仅定义自定义的ObjectMapper Bean。
 * Spring Boot会自动检测并配置所有JSON消息转换器。</p>
 * 
 * @author Demo Team
 * @version 1.0
 * @since 2024-01-01
 */
@Configuration
public class JacksonConfig {

    /**
     * 创建ObjectMapper Bean
     * <p>用于测试类中的依赖注入和JSON序列化配置。</p>
     * 
     * @return ObjectMapper实例
     */
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
