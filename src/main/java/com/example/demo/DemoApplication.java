package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Spring Boot应用程序主类
 * <p>这是整个应用的入口点，负责启动Spring Boot应用并初始化所有组件。</p>
 * 
 * <h2>主要功能</h2>
 * <ul>
 *   <li>启动Spring Boot应用上下文</li>
 *   <li>启用自动配置（@SpringBootApplication）</li>
 *   <li>启用JPA审计功能（@EnableJpaAuditing）</li>
 *   <li>扫描并注册所有Bean组件</li>
 * </ul>
 * 
 * <h2>注解说明</h2>
 * <ul>
 *   <li>@SpringBootApplication - 组合注解，包含@Configuration、@EnableAutoConfiguration和@ComponentScan</li>
 *   <li>@EnableJpaAuditing - 启用JPA审计功能，支持@CreatedDate、@LastModifiedDate等注解</li>
 * </ul>
 * 
 * <h2>启动流程</h2>
 * <ol>
 *   <li>加载application.properties/yml配置文件</li>
 *   <li>初始化Spring应用上下文</li>
 *   <li>扫描并注册所有Bean（Controller、Service、Repository等）</li>
 *   <li>配置数据源和JPA</li>
 *   <li>启动内嵌Web服务器（Tomcat）</li>
 *   <li>应用准备就绪，开始接收请求</li>
 * </ol>
 * 
 * <h3>运行方式</h3>
 * <pre>{@code
 * // 方式1：IDE中直接运行
 * // 右键 -> Run 'DemoApplication'
 * 
 * // 方式2：Maven命令
 * mvn spring-boot:run
 * 
 * // 方式3：打包后运行
 * mvn clean package
 * java -jar target/demo-0.0.1-SNAPSHOT.jar
 * }</pre>
 * 
 * @author Demo Team
 * @version 1.0
 * @since 2024-01-01
 * @see SpringApplication
 */
@SpringBootApplication
@EnableJpaAuditing
public class DemoApplication {

    /**
     * 应用程序入口方法
     * <p>Spring Boot应用的启动点，调用SpringApplication.run()方法启动应用。</p>
     * 
     * @param args 命令行参数，可传递配置选项
     */
    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

}
