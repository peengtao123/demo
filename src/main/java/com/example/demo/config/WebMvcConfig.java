package com.example.demo.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * WebMVC自定义配置类
 * 用于演示如何定制Spring WebMVC的Bean功能
 */
@Configuration
public class WebMvcConfig {

    /**
     * 自定义ObjectMapper Bean
     * 配置JSON序列化/反序列化的行为
     * Spring Boot会自动使用这个ObjectMapper来配置HTTP消息转换器
     */
    @Bean
    public ObjectMapper customObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        // 美化输出
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        // 忽略空值字段
        objectMapper.setDefaultPropertyInclusion(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL);
        return objectMapper;
    }

    /**
     * 添加拦截器（可选）
     * 可以在这里添加自定义的请求拦截器
     */
    /*
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new CustomInterceptor())
                .addPathPatterns("/api/**")
                .excludePathPatterns("/api/public/**");
    }
    */
}
