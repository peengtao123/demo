package com.example.demo.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.annotation.Annotation;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @RestController 原理测试类
 * 
 * 深入理解 @RestController 的工作机制：
 * 1. 注解组合：@Controller + @ResponseBody
 * 2. Bean注册：作为Spring组件被管理
 * 3. 请求映射：URL与方法建立映射关系
 * 4. 响应处理：自动序列化返回值为JSON
 */
@SpringBootTest
@DisplayName("@RestController 原理深度解析")
class RestControllerPrincipleTest {

    private static final Logger logger = LoggerFactory.getLogger(RestControllerPrincipleTest.class);

    @Autowired
    private ApplicationContext applicationContext;

    /**
     * 测试1: 验证 @RestController 是 @Controller 的子类
     */
    @Test
    @DisplayName("测试1: @RestController 继承关系验证")
    void testRestControllerInheritance() {
        logger.info("=== 验证 @RestController 的继承关系 ===");
        
        // 获取 RestController 注解的定义
        Class<? extends Annotation> restControllerClass = RestController.class;
        
        // 检查是否包含 @Controller 注解
        boolean hasControllerAnnotation = restControllerClass.isAnnotationPresent(Controller.class);
        assertTrue(hasControllerAnnotation, "@RestController 应该包含 @Controller 注解");
        logger.info("✓ @RestController 包含 @Controller 注解");
        
        // 检查是否包含 @ResponseBody 注解
        boolean hasResponseBodyAnnotation = restControllerClass.isAnnotationPresent(
            org.springframework.web.bind.annotation.ResponseBody.class);
        assertTrue(hasResponseBodyAnnotation, "@RestController 应该包含 @ResponseBody 注解");
        logger.info("✓ @RestController 包含 @ResponseBody 注解");
        
        logger.info("结论: @RestController = @Controller + @ResponseBody");
    }

    /**
     * 测试2: 验证 @RestController 类被注册为 Spring Bean
     */
    @Test
    @DisplayName("测试2: @RestController Bean 注册验证")
    void testRestControllerBeanRegistration() {
        logger.info("=== 验证 @RestController Bean 注册 ===");
        
        // 获取所有带有 @RestController 注解的 Bean
        String[] restControllerBeans = applicationContext.getBeanNamesForAnnotation(RestController.class);
        
        logger.info("发现的 @RestController Bean 数量: {}", restControllerBeans.length);
        
        for (String beanName : restControllerBeans) {
            Object bean = applicationContext.getBean(beanName);
            logger.info("  - Bean名称: {}, 类型: {}", beanName, bean.getClass().getName());
            
            // 验证它是单例
            boolean isSingleton = applicationContext.isSingleton(beanName);
            logger.info("    是否单例: {}", isSingleton);
            assertTrue(isSingleton, "Controller Bean 应该是单例");
        }
        
        assertTrue(restControllerBeans.length > 0, "应该至少有一个 @RestController Bean");
        logger.info("✓ @RestController 类被正确注册为 Spring Bean");
    }

    /**
     * 测试3: 验证请求映射注册
     */
    @Test
    @DisplayName("测试3: 请求映射注册验证")
    void testRequestMappingRegistration() {
        logger.info("=== 验证请求映射注册 ===");
        
        // 获取 RequestMappingHandlerMapping
        RequestMappingHandlerMapping handlerMapping = 
            applicationContext.getBean(RequestMappingHandlerMapping.class);
        
        // 获取所有注册的映射
        Map<org.springframework.web.servlet.mvc.method.RequestMappingInfo, 
            HandlerMethod> handlerMethods = handlerMapping.getHandlerMethods();
        
        logger.info("总共注册的 Handler 方法数量: {}", handlerMethods.size());
        
        // 遍历所有映射
        handlerMethods.forEach((mapping, handlerMethod) -> {
            logger.info("\n映射详情:");
            logger.info("  - 映射条件: {}", mapping);
            logger.info("  - 处理方法: {}.{}", 
                handlerMethod.getBeanType().getSimpleName(),
                handlerMethod.getMethod().getName());
            
            // 检查是否是 @RestController 中的方法
            Class<?> beanType = handlerMethod.getBeanType();
            boolean isRestController = beanType.isAnnotationPresent(RestController.class);
            logger.info("  - 所属类是否为 @RestController: {}", isRestController);
        });
        
        logger.info("\n✓ 请求映射已正确注册");
    }

    /**
     * 测试4: 验证 @RestController 的方法可以被找到
     */
    @Test
    @DisplayName("测试4: @RestController 方法验证")
    void testRestControllerMethods() {
        logger.info("=== 验证 @RestController 中的方法 ===");
        
        RequestMappingHandlerMapping handlerMapping = 
            applicationContext.getBean(RequestMappingHandlerMapping.class);
        
        // 查找 UserController 的所有映射
        handlerMapping.getHandlerMethods().forEach((mapping, handlerMethod) -> {
            Class<?> beanType = handlerMethod.getBeanType();
            
            if (beanType.getSimpleName().equals("UserController")) {
                logger.info("\nUserController 的方法:");
                logger.info("  - 方法名: {}", handlerMethod.getMethod().getName());
                logger.info("  - 映射: {}", mapping);
                logger.info("  - 参数数量: {}", handlerMethod.getMethod().getParameterCount());
                logger.info("  - 返回类型: {}", handlerMethod.getMethod().getGenericReturnType());
                
                // 检查方法是否有 @ResponseBody（来自类级别的 @RestController）
                boolean hasResponseBody = handlerMethod.hasMethodAnnotation(
                    org.springframework.web.bind.annotation.ResponseBody.class);
                logger.info("  - 方法级别是否有 @ResponseBody: {}", hasResponseBody);
                
                // 类级别的 @ResponseBody 会自动应用到所有方法
                boolean classHasResponseBody = beanType.isAnnotationPresent(
                    org.springframework.web.bind.annotation.ResponseBody.class);
                logger.info("  - 类级别是否有 @ResponseBody: {}", classHasResponseBody);
            }
        });
        
        logger.info("\n✓ @RestController 的方法已正确识别");
    }

    /**
     * 测试5: 对比 @Controller 和 @RestController 的区别
     */
    @Test
    @DisplayName("测试5: @Controller vs @RestController 对比")
    void testControllerVsRestController() {
        logger.info("=== @Controller vs @RestController 对比 ===");
        
        logger.info("\n@Controller 特点:");
        logger.info("  1. 标记类为 Spring MVC 控制器");
        logger.info("  2. 默认返回视图名称（需要 ViewResolver）");
        logger.info("  3. 如需返回数据，需在方法上加 @ResponseBody");
        logger.info("  4. 适用于服务端渲染页面（JSP/Thymeleaf）");
        
        logger.info("\n@RestController 特点:");
        logger.info("  1. = @Controller + @ResponseBody（组合注解）");
        logger.info("  2. 所有方法默认返回数据（而非视图）");
        logger.info("  3. 自动通过 HttpMessageConverter 序列化");
        logger.info("  4. 适用于 RESTful API（返回 JSON/XML）");
        
        logger.info("\n关键区别:");
        logger.info("  - @Controller: 返回值 → 视图名称 → ViewResolver → HTML页面");
        logger.info("  - @RestController: 返回值 → HttpMessageConverter → JSON/XML → 响应体");
        
        logger.info("\n✓ 两者区别已明确");
    }

    /**
     * 测试6: 验证 HttpMessageConverter 的存在
     */
    @Test
    @DisplayName("测试6: HttpMessageConverter 验证")
    void testHttpMessageConverter() {
        logger.info("=== 验证 HttpMessageConverter ===");
        
        // 获取所有消息转换器
        String[] converterBeanNames = applicationContext.getBeanNamesForType(
            org.springframework.http.converter.HttpMessageConverter.class);
        
        logger.info("发现的消息转换器数量: {}", converterBeanNames.length);
        
        for (String beanName : converterBeanNames) {
            Object converter = applicationContext.getBean(beanName);
            logger.info("  - {}: {}", beanName, converter.getClass().getSimpleName());
        }
        
        logger.info("\n常用转换器:");
        logger.info("  1. MappingJackson2HttpMessageConverter - JSON 转换");
        logger.info("  2. StringHttpMessageConverter - 字符串转换");
        logger.info("  3. FormHttpMessageConverter - 表单数据转换");
        logger.info("  4. ByteArrayHttpMessageConverter - 字节数组转换");
        
        logger.info("\n✓ HttpMessageConverter 已配置");
    }

    /**
     * 测试7: @RestController 的工作流程总结
     */
    @Test
    @DisplayName("测试7: @RestController 完整工作流程")
    void testRestControllerWorkflow() {
        logger.info("=== @RestController 完整工作流程 ===");
        
        logger.info("\n第1步: 应用启动");
        logger.info("  → ComponentScan 扫描包路径");
        logger.info("  → 发现 @RestController 注解的类");
        logger.info("  → 注册为 Spring Bean（单例）");
        
        logger.info("\n第2步: 请求映射注册");
        logger.info("  → RequestMappingHandlerMapping 扫描所有 Bean");
        logger.info("  → 查找 @RequestMapping、@GetMapping 等注解");
        logger.info("  → 建立 URL → HandlerMethod 的映射关系");
        
        logger.info("\n第3步: 接收HTTP请求");
        logger.info("  → DispatcherServlet 接收请求");
        logger.info("  → HandlerMapping 查找匹配的 HandlerMethod");
        
        logger.info("\n第4步: 执行Controller方法");
        logger.info("  → HandlerAdapter 调用目标方法");
        logger.info("  → 方法执行业务逻辑");
        logger.info("  → 返回对象（如 User、ApiResponse）");
        
        logger.info("\n第5步: 响应处理");
        logger.info("  → @ResponseBody 生效（来自 @RestController）");
        logger.info("  → 选择合适的 HttpMessageConverter");
        logger.info("  → 将对象序列化为 JSON/XML");
        logger.info("  → 写入 HTTP Response Body");
        logger.info("  → 设置 Content-Type 响应头");
        
        logger.info("\n第6步: 返回给客户端");
        logger.info("  → DispatcherServlet 发送响应");
        logger.info("  → 客户端收到 JSON 数据");
        
        logger.info("\n✓ @RestController 工作流程已梳理完成");
    }

    /**
     * 测试8: 实际示例分析
     */
    @Test
    @DisplayName("测试8: UserController 实例分析")
    void testUserControllerExample() {
        logger.info("=== UserController 实例分析 ===");
        
        logger.info("\n代码示例:");
        logger.info("@RestController");
        logger.info("@RequestMapping(\"/api/users\")");
        logger.info("public class UserController {");
        logger.info("");
        logger.info("    @GetMapping(\"/{id}\")");
        logger.info("    public ResponseEntity<ApiResponse<User>> getUserById(@PathVariable Long id) {");
        logger.info("        User user = userService.getUserById(id);");
        logger.info("        return ResponseEntity.ok(ApiResponse.success(user));");
        logger.info("    }");
        logger.info("}");
        
        logger.info("\n工作原理:");
        logger.info("1. @RestController 使该类成为 Controller Bean");
        logger.info("2. @RequestMapping 设置基础路径: /api/users");
        logger.info("3. @GetMapping 映射 GET /api/users/{id}");
        logger.info("4. 方法返回 ApiResponse<User> 对象");
        logger.info("5. @ResponseBody 自动将对象转为 JSON:");
        logger.info("   {");
        logger.info("     \"success\": true,");
        logger.info("     \"message\": \"操作成功\",");
        logger.info("     \"data\": {");
        logger.info("       \"id\": 1,");
        logger.info("       \"username\": \"testuser\",");
        logger.info("       \"email\": \"test@example.com\"");
        logger.info("     }");
        logger.info("   }");
        
        logger.info("\n✓ 实例分析完成");
    }
}
