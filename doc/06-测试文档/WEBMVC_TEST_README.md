# Spring WebMVC Bean 单元测试指南

## 概述

本项目包含了Spring WebMVC核心Bean的全面单元测试，展示了如何测试和定制WebMVC的各种功能。

## 测试文件说明

### 1. WebMvcBeanTest.java
**位置**: `src/test/java/com/example/demo/config/WebMvcBeanTest.java`

这个测试类包含15个测试用例，覆盖了Spring WebMVC的核心Bean：

#### 测试内容：

1. **DispatcherServlet Bean验证** - 测试前端控制器
2. **HandlerMapping Bean验证** - 测试请求映射处理器
3. **HandlerAdapter Bean验证** - 测试处理器适配器
4. **MessageConverter Bean验证** - 测试HTTP消息转换器
5. **自定义ObjectMapper配置验证** - 测试JSON序列化配置
6. **Validator Bean验证** - 测试数据验证器
7. **ViewResolver Bean验证** - 测试视图解析器
8. **完整WebMVC请求处理流程** - 测试POST请求
9. **GET请求处理流程** - 测试GET请求
10. **异常处理流程** - 测试异常处理机制
11. **列出所有WebMVC相关Bean** - 展示所有注册的Bean
12. **PUT请求处理流程** - 测试PUT请求
13. **DELETE请求处理流程** - 测试DELETE请求
14. **查询参数处理** - 测试@RequestParam参数
15. **自定义WebMvcConfigurer配置验证** - 测试自定义配置

### 2. WebMvcCustomizationTest.java
**位置**: `src/test/java/com/example/demo/config/WebMvcCustomizationTest.java`

这个测试类包含10个测试用例，展示了如何定制WebMVC功能：

#### 测试内容：

1. **自定义ObjectMapper配置** - JSON序列化定制
2. **消息转换器媒体类型支持** - 测试不同Content-Type
3. **JSON序列化空值处理** - 测试null值处理策略
4. **大对象序列化性能** - 性能测试
5. **复杂对象序列化** - 完整对象序列化/反序列化
6. **日期时间格式化** - 日期格式配置说明
7. **自定义错误响应格式** - 错误处理定制
8. **请求体大小限制** - 大请求体处理
9. **并发请求处理** - 并发性能测试
10. **自定义拦截器配置说明** - 拦截器使用指南

### 3. WebMvcConfig.java
**位置**: `src/main/java/com/example/demo/config/WebMvcConfig.java`

这是一个配置类，展示了如何定制WebMVC的Bean：

- 自定义ObjectMapper Bean配置
- 扩展消息转换器列表
- 提供了添加拦截器的示例代码

## Spring WebMVC核心Bean说明

### 1. DispatcherServlet（前端控制器）
- **作用**: 所有HTTP请求的入口点
- **功能**: 协调各个组件处理请求
- **测试方法**: 通过ApplicationContext获取并验证

### 2. HandlerMapping（请求映射处理器）
- **作用**: 将URL映射到对应的Controller方法
- **实现**: RequestMappingHandlerMapping
- **功能**: 解析@RequestMapping注解
- **测试方法**: 检查已注册的handler方法数量

### 3. HandlerAdapter（处理器适配器）
- **作用**: 适配不同类型的处理器
- **实现**: RequestMappingHandlerAdapter
- **功能**: 
  - 参数解析（HandlerMethodArgumentResolver）
  - 返回值处理（HandlerMethodReturnValueHandler）
- **测试方法**: 验证参数解析器和返回值处理器配置

### 4. MessageConverter（消息转换器）
- **作用**: HTTP消息的序列化和反序列化
- **常用实现**: 
  - MappingJackson2HttpMessageConverter (JSON)
  - StringHttpMessageConverter (文本)
  - FormHttpMessageConverter (表单)
- **测试方法**: 通过实际请求验证JSON转换

### 5. Validator（验证器）
- **作用**: 验证请求参数的合法性
- **实现**: LocalValidatorFactoryBean
- **功能**: 基于JSR-303/JSR-380注解验证
- **测试方法**: 发送无效数据验证错误响应

### 6. ViewResolver（视图解析器）
- **作用**: 将逻辑视图名解析为实际视图
- **注意**: REST API通常不需要ViewResolver
- **测试方法**: 检查是否存在ViewResolver Bean

### 7. ObjectMapper（JSON处理器）
- **作用**: Java对象与JSON之间的转换
- **可定制项**:
  - 日期格式
  - 空值处理
  - 字段命名策略
  - 序列化特性
- **测试方法**: 序列化/反序列化测试

## 如何运行测试

### 运行所有WebMVC测试
```bash
mvn test -Dtest=WebMvcBeanTest,WebMvcCustomizationTest
```

### 运行单个测试类
```bash
# 运行Bean功能测试
mvn test -Dtest=WebMvcBeanTest

# 运行自定义功能测试
mvn test -Dtest=WebMvcCustomizationTest
```

### 运行单个测试方法
```bash
mvn test -Dtest=WebMvcBeanTest#testDispatcherServletBean
```

## 如何定制WebMVC功能

### 1. 自定义ObjectMapper

```java
@Bean
public ObjectMapper customObjectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    // 美化输出
    objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    // 忽略空值字段
    objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    // 配置日期格式
    objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
    return objectMapper;
}
```

### 2. 添加自定义拦截器

```java
@Override
public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(new CustomInterceptor())
            .addPathPatterns("/api/**")
            .excludePathPatterns("/api/public/**");
}
```

拦截器可以实现：
- 日志记录
- 权限验证
- 请求计时
- 跨域处理
- 请求/响应修改

### 3. 自定义消息转换器

```java
@Override
public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
    // 添加自定义转换器
    converters.add(0, new CustomMessageConverter());
}
```

### 4. 配置全局异常处理

创建@ControllerAdvice类：

```java
@ControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<?>> handleRuntimeException(RuntimeException e) {
        return ResponseEntity.badRequest()
            .body(ApiResponse.error(e.getMessage()));
    }
}
```

### 5. 自定义参数解析器

```java
@Override
public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
    resolvers.add(new CustomArgumentResolver());
}
```

## 测试最佳实践

### 1. 使用@Slf4j或LoggerFactory进行日志输出
```java
private static final Logger logger = LoggerFactory.getLogger(WebMvcBeanTest.class);
```

### 2. 使用MockMvc进行HTTP请求测试
```java
@Autowired
private MockMvc mockMvc;

mockMvc.perform(get("/api/users/1"))
    .andExpect(status().isOk())
    .andExpect(content().contentType(MediaType.APPLICATION_JSON));
```

### 3. 使用@Mock模拟依赖
```java
@Mock
private UserService userService;

@BeforeEach
void setUp() {
    MockitoAnnotations.openMocks(this);
}
```

### 4. 验证JSON响应
```java
.andExpect(jsonPath("$.success").value(true))
.andExpect(jsonPath("$.data.username").value("testuser"))
```

## 常见问题

### Q1: 为什么使用@SpringBootTest而不是@WebMvcTest？
A: Spring Boot 4.x中`@WebMvcTest`的支持可能还不完善，使用`@SpringBootTest`可以更稳定地测试WebMVC Bean。

### Q2: 为什么Mock Service没有被注入到Controller？
A: 使用`MockMvcBuilders.standaloneSetup()`时，需要手动设置Controller的依赖。可以使用`ReflectionTestUtils`注入Mock对象。

### Q3: 如何测试真实的业务逻辑？
A: 对于完整的集成测试，建议使用`@SpringBootTest`配合真实的Service层，或者使用`MockMvcBuilders.webAppContextSetup()`。

## 参考资源

- [Spring WebMVC官方文档](https://docs.spring.io/spring-framework/reference/web/webmvc.html)
- [Spring Testing官方文档](https://docs.spring.io/spring-framework/reference/testing.html)
- [MockMvc使用指南](https://spring.io/guides/gs/testing-web/)

## 总结

通过这些测试，你可以：
1. 理解Spring WebMVC的核心Bean及其作用
2. 学习如何测试WebMVC的各个组件
3. 掌握定制WebMVC功能的方法
4. 了解最佳实践和常见问题的解决方案

这些测试不仅验证了WebMVC的功能，还作为文档展示了如何使用和定制Spring WebMVC。
