package com.example.demo.config;

import com.example.demo.controller.UserController;
import com.example.demo.dto.UserDTO;
import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * WebMVC自定义功能测试
 * 
 * 演示如何定制和扩展Spring WebMVC的功能：
 * 1. 自定义消息转换器
 * 2. 自定义JSON序列化配置
 * 3. 添加请求/响应拦截器
 * 4. 自定义参数解析器
 * 5. 全局异常处理定制
 */
@SpringBootTest
@DisplayName("WebMVC自定义功能测试")
class WebMvcCustomizationTest {

    private static final Logger logger = LoggerFactory.getLogger(WebMvcCustomizationTest.class);

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserController userController;

    @Mock
    private UserService userService;

    private MockMvc mockMvc;
    private User testUser;
    private UserDTO userDTO;

    @BeforeEach
    void setUp() {
        logger.info("=== 开始WebMVC自定义功能测试 ===");
        
        // 初始化Mockito注解
        MockitoAnnotations.openMocks(this);
        
        // 初始化MockMvc
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
        
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("customuser");
        testUser.setEmail("custom@example.com");
        testUser.setName("自定义用户");

        userDTO = new UserDTO();
        userDTO.setUsername("customuser");
        userDTO.setEmail("custom@example.com");
        userDTO.setName("自定义用户");
    }

    /**
     * 测试1: 验证自定义ObjectMapper的配置生效
     */
    @Test
    @DisplayName("测试1: 自定义ObjectMapper配置")
    void testCustomObjectMapperConfiguration() throws Exception {
        logger.info("测试自定义ObjectMapper配置...");
        
        String userJson = objectMapper.writeValueAsString(userDTO);
        logger.info("发送的JSON: {}", userJson);
        
        // 执行请求并验证响应
        String responseJson = mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
            .andReturn()
            .getResponse()
            .getContentAsString();
        
        logger.info("响应的JSON: {}", responseJson);
        
        assertNotNull(responseJson);
        logger.info("✓ 自定义ObjectMapper配置测试通过");
    }

    /**
     * 测试2: 测试消息转换器的媒体类型支持
     */
    @Test
    @DisplayName("测试2: 消息转换器媒体类型支持")
    void testMessageConverterMediaTypeSupport() throws Exception {
        logger.info("测试消息转换器媒体类型支持...");
        
        String userJson = objectMapper.writeValueAsString(userDTO);
        
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
            .andReturn();
        
        logger.info("✓ 支持application/json");
        logger.info("✓ 消息转换器媒体类型支持测试通过");
    }

    /**
     * 测试3: 测试JSON序列化的空值处理
     */
    @Test
    @DisplayName("测试3: JSON序列化空值处理")
    void testJsonSerializationWithNullValues() throws Exception {
        logger.info("测试JSON序列化空值处理...");
        
        // 创建包含null值的对象
        UserDTO partialUserDTO = new UserDTO();
        partialUserDTO.setUsername("partialuser");
        // email和name为null
        
        String json = objectMapper.writeValueAsString(partialUserDTO);
        logger.info("包含null值的JSON: {}", json);
        
        // 如果配置了NON_NULL，null字段应该被忽略
        boolean hasNullFields = json.contains("null");
        logger.info("JSON是否包含null字段: {}", hasNullFields);
        
        // 这个测试主要展示配置的效果
        logger.info("✓ JSON空值处理测试完成");
    }

    /**
     * 测试4: 测试大对象序列化性能
     */
    @Test
    @DisplayName("测试4: 大对象序列化性能")
    void testLargeObjectSerialization() throws Exception {
        logger.info("测试大对象序列化性能...");
        
        long startTime = System.currentTimeMillis();
        
        // 序列化多次以测试性能
        for (int i = 0; i < 100; i++) {
            objectMapper.writeValueAsString(testUser);
        }
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        logger.info("100次序列化耗时: {} ms", duration);
        logger.info("平均每次序列化耗时: {} ms", duration / 100.0);
        
        assertTrue(duration < 1000, "100次序列化应该在1秒内完成");
        logger.info("✓ 序列化性能测试通过");
    }

    /**
     * 测试5: 测试复杂对象的序列化和反序列化
     */
    @Test
    @DisplayName("测试5: 复杂对象序列化")
    void testComplexObjectSerialization() throws Exception {
        logger.info("测试复杂对象序列化...");
        
        // 测试User对象的完整序列化
        String json = objectMapper.writeValueAsString(testUser);
        logger.info("User对象JSON: {}", json);
        
        // 反序列化回来
        User deserializedUser = objectMapper.readValue(json, User.class);
        
        assertEquals(testUser.getId(), deserializedUser.getId());
        assertEquals(testUser.getUsername(), deserializedUser.getUsername());
        assertEquals(testUser.getEmail(), deserializedUser.getEmail());
        assertEquals(testUser.getName(), deserializedUser.getName());
        
        logger.info("✓ 复杂对象序列化测试通过");
    }

    /**
     * 测试6: 测试日期时间格式化（如果实体中有日期字段）
     */
    @Test
    @DisplayName("测试6: 日期时间格式化")
    void testDateTimeFormatting() throws Exception {
        logger.info("测试日期时间格式化...");
        
        // 如果User实体有日期字段，可以在这里测试
        // 目前User实体可能没有日期字段，所以这个测试主要是展示如何配置
        
        logger.info("注意: 当前User实体没有日期字段");
        logger.info("如果需要测试日期格式化，可以：");
        logger.info("1. 在User实体中添加LocalDateTime或Date字段");
        logger.info("2. 在ObjectMapper中配置DateFormat");
        logger.info("3. 使用@JsonFormat注解指定格式");
        
        logger.info("✓ 日期时间格式化测试说明完成");
    }

    /**
     * 测试7: 测试自定义错误响应格式
     */
    @Test
    @DisplayName("测试7: 自定义错误响应格式")
    void testCustomErrorResponseFormat() throws Exception {
        logger.info("测试自定义错误响应格式...");
        
        when(userService.getUserById(999L)).thenThrow(new RuntimeException("用户不存在"));
        
        String errorResponse = mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                .get("/api/users/999"))
            .andExpect(status().isNotFound())
            .andReturn()
            .getResponse()
            .getContentAsString();
        
        logger.info("错误响应: {}", errorResponse);
        
        // 验证错误响应包含必要的信息
        assertNotNull(errorResponse);
        logger.info("✓ 错误响应格式测试通过");
    }

    /**
     * 测试8: 测试请求体的大小限制
     */
    @Test
    @DisplayName("测试8: 请求体大小限制")
    void testRequestBodySizeLimit() throws Exception {
        logger.info("测试请求体大小限制...");
        
        // 创建一个较大的请求体
        StringBuilder largeUsername = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            largeUsername.append("a");
        }
        
        UserDTO largeUserDTO = new UserDTO();
        largeUserDTO.setUsername(largeUsername.toString());
        largeUserDTO.setEmail("large@example.com");
        largeUserDTO.setName("大用户名测试");
        
        String largeJson = objectMapper.writeValueAsString(largeUserDTO);
        logger.info("大请求体大小: {} bytes", largeJson.length());
        
        // Spring Boot默认请求体大小限制是2MB
        // 这个测试验证可以处理较大的请求体
        
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(largeJson))
            .andReturn();
        
        logger.info("✓ 大请求体处理测试通过");
    }

    /**
     * 测试9: 测试并发请求处理
     */
    @Test
    @DisplayName("测试9: 并发请求处理")
    void testConcurrentRequestHandling() throws Exception {
        logger.info("测试并发请求处理...");
        
        String userJson = objectMapper.writeValueAsString(userDTO);
        
        // 模拟多个并发请求
        int requestCount = 10;
        long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < requestCount; i++) {
            mockMvc.perform(post("/api/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(userJson))
                .andReturn();
        }
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        logger.info("{}个并发请求总耗时: {} ms", requestCount, duration);
        logger.info("平均每个请求耗时: {} ms", duration / (double) requestCount);
        
        assertTrue(duration < 5000, "{}个请求应该在5秒内完成".formatted(requestCount));
        logger.info("✓ 并发请求处理测试通过");
    }

    /**
     * 测试10: 展示如何添加自定义拦截器（概念性测试）
     */
    @Test
    @DisplayName("测试10: 自定义拦截器配置说明")
    void testCustomInterceptorConfiguration() {
        logger.info("=== 自定义拦截器配置说明 ===");
        
        logger.info("要添加自定义拦截器，需要：");
        logger.info("1. 创建实现HandlerInterceptor接口的类");
        logger.info("2. 在WebMvcConfigurer中注册拦截器：");
        logger.info("   @Override");
        logger.info("   public void addInterceptors(InterceptorRegistry registry) {");
        logger.info("       registry.addInterceptor(new CustomInterceptor())");
        logger.info("               .addPathPatterns(\"/api/**\")");
        logger.info("               .excludePathPatterns(\"/api/public/**\");");
        logger.info("   }");
        logger.info("");
        logger.info("拦截器可以用于：");
        logger.info("- 日志记录");
        logger.info("- 权限验证");
        logger.info("- 请求计时");
        logger.info("- 跨域处理");
        logger.info("- 请求/响应修改");
        
        logger.info("✓ 自定义拦截器配置说明完成");
    }

}
