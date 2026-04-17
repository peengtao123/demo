package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class DemoApplicationTests {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private Environment environment;

    @Test
    void contextLoads() {
        // 基础测试：验证应用上下文能够正常加载
        assertThat(applicationContext).isNotNull();
    }

    @Test
    void testApplicationContextIsNotNull() {
        // 验证应用上下文不为空
        assertThat(applicationContext).isNotNull();
        assertThat(applicationContext.getId()).isNotNull();
    }

    @Test
    void testMainApplicationBeanExists() {
        // 验证主应用类 Bean 是否存在
        assertThat(applicationContext.containsBean("demoApplication")).isTrue();
        
        // 验证可以通过类型获取 Bean
        DemoApplication demoApplication = applicationContext.getBean(DemoApplication.class);
        assertThat(demoApplication).isNotNull();
    }

    @Test
    void testEnvironmentIsLoaded() {
        // 验证环境配置已正确加载
        assertThat(environment).isNotNull();
        
        // 验证可以获取属性（即使没有配置具体属性，也应该能获取默认值）
        String activeProfiles = environment.getProperty("spring.profiles.active");

        System.out.println("Active Profiles: " + activeProfiles);
        // profiles 可能为空，这是正常的
        assertThat(environment.getActiveProfiles()).isNotNull();
    }

    @Test
    void testApplicationContextContainsExpectedBeans() {
        // 验证 Spring Boot 自动配置的核心 Bean 是否存在
        assertThat(applicationContext.containsBean("environment")).isTrue();
        assertThat(applicationContext.containsBean("systemProperties")).isTrue();
        assertThat(applicationContext.containsBean("systemEnvironment")).isTrue();
    }

    @Test
    void testApplicationName() {
        // 验证应用名称配置
        String appName = environment.getProperty("spring.application.name");
        System.out.println("Application Name: " + appName);
        // 如果没有配置，可能为 null，这是正常的
        assertThat(applicationContext.getApplicationName()).isNotNull();
    }

    @Test
    void testBeanDefinitionCount() {
        // 验证应用上下文中有一定数量的 Bean 定义
        int beanDefinitionCount = applicationContext.getBeanDefinitionCount();
        assertThat(beanDefinitionCount).isGreaterThan(0);
        
        // 打印 Bean 数量用于调试（可选）
        System.out.println("Total beans in context: " + beanDefinitionCount);
    }

    @Test
    void testGetAllBeanNames() {
        // 获取所有 Bean 名称并验证不为空
        String[] beanNames = applicationContext.getBeanDefinitionNames();
        assertThat(beanNames).isNotNull();
        assertThat(beanNames.length).isGreaterThan(0);
    }
}
