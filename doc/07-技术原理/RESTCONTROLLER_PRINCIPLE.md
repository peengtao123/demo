# @RestController 原理详解

## 📌 核心概念

`@RestController` 是 Spring MVC 中用于构建 RESTful API 的核心注解，它是一个**组合注解**。

## 🔍 注解定义

```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Controller      // ← 继承自 @Controller
@ResponseBody  // ← 添加 @ResponseBody
public @interface RestController {
    String value() default "";
}
```

**等价于：**
```java
@Controller
@ResponseBody
public class MyController { ... }
```

## ⚙️ 工作原理

### 1️⃣ Bean 注册阶段

```
应用启动
  ↓
@ComponentScan 扫描包路径
  ↓
发现 @RestController 注解的类
  ↓
因为 @RestController 包含 @Controller
  ↓
@Controller 包含 @Component
  ↓
将该类注册为 Spring Bean（单例）
  ↓
存入 ApplicationContext
```

**验证代码：**
```java
// 获取所有 @RestController Bean
String[] beans = applicationContext.getBeanNamesForAnnotation(RestController.class);
// 结果: ["userController"]
```

### 2️⃣ 请求映射注册阶段

```
RequestMappingHandlerMapping 初始化
  ↓
扫描所有 Controller Bean
  ↓
查找方法级别的映射注解：
  - @RequestMapping
  - @GetMapping
  - @PostMapping
  - @PutMapping
  - @DeleteMapping
  ↓
建立映射关系：URL → HandlerMethod
  ↓
存储到内部的 Map 结构中
```

**示例：**
```java
@RestController
@RequestMapping("/api/users")
public class UserController {
    
    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        return userService.findById(id);
    }
}

// 注册的映射：
// GET /api/users/{id} → UserController.getUserById(Long)
```

### 3️⃣ 请求处理阶段

```
客户端发送 HTTP 请求
  ↓
┌─────────────────────────────────────┐
│ DispatcherServlet (前端控制器)       │
│ - 接收所有 HTTP 请求                  │
│ - 协调各个组件                        │
└─────────────────────────────────────┘
  ↓
┌─────────────────────────────────────┐
│ HandlerMapping                       │
│ - 根据 URL 查找匹配的 HandlerMethod  │
│ - 返回 HandlerExecutionChain         │
└─────────────────────────────────────┘
  ↓
┌─────────────────────────────────────┐
│ HandlerAdapter                       │
│ - 执行目标方法                        │
│ - 处理方法参数解析                    │
│   (@PathVariable, @RequestParam等)   │
└─────────────────────────────────────┘
  ↓
┌─────────────────────────────────────┐
│ Controller 方法执行                   │
│ - 调用业务逻辑                        │
│ - 返回对象（User、ApiResponse等）     │
└─────────────────────────────────────┘
  ↓
┌─────────────────────────────────────┐
│ @ResponseBody 生效                    │
│ - 标记返回值需要写入响应体             │
│ - 不经过视图解析器                    │
└─────────────────────────────────────┘
  ↓
┌─────────────────────────────────────┐
│ HttpMessageConverter                 │
│ - 选择合适的转换器（JSON/XML）        │
│ - MappingJackson2HttpMessageConverter│
│ - 序列化对象为 JSON                   │
└─────────────────────────────────────┘
  ↓
┌─────────────────────────────────────┐
│ 写入 HTTP Response                   │
│ - 设置 Content-Type: application/json│
│ - 写入响应体                          │
└─────────────────────────────────────┘
  ↓
返回给客户端
```

## 🔄 @Controller vs @RestController

| 特性 | @Controller | @RestController |
|------|------------|-----------------|
| 组成 | 单独注解 | @Controller + @ResponseBody |
| 返回值 | 视图名称 | 数据对象 |
| 视图解析 | ✅ 需要 ViewResolver | ❌ 不需要 |
| 消息转换 | 需手动加 @ResponseBody | ✅ 自动转换 |
| 适用场景 | 服务端渲染页面 | RESTful API |
| 典型返回 | "user-view" (字符串) | User 对象 → JSON |

### 对比示例

#### 传统 @Controller（服务端渲染）
```java
@Controller
public class TraditionalController {
    
    @GetMapping("/user/{id}")
    public String getUser(@PathVariable Long id, Model model) {
        User user = userService.findById(id);
        model.addAttribute("user", user);
        return "user-detail";  // ← 返回视图名称
    }
}

// 流程：
// 返回 "user-detail" 
//   → ViewResolver 解析 
//   → 找到 user-detail.html (Thymeleaf)
//   → 渲染 HTML
//   → 返回 HTML 页面
```

#### @RestController（REST API）
```java
@RestController
@RequestMapping("/api/users")
public class RestUserController {
    
    @GetMapping("/{id}")
    public User getUser(@PathVariable Long id) {
        return userService.findById(id);  // ← 直接返回对象
    }
}

// 流程：
// 返回 User 对象
//   → @ResponseBody 生效
//   → HttpMessageConverter 转换
//   → 序列化为 JSON
//   → 返回 JSON 数据
```

## 🛠️ HttpMessageConverter 工作机制

### 转换器选择逻辑

```java
// Spring 内部伪代码
public void writeWithMessageConverters(Object returnValue) {
    
    // 1. 获取请求的 Accept 头
    MediaType requestedMediaType = request.getAcceptHeader();
    
    // 2. 遍历所有注册的 HttpMessageConverter
    for (HttpMessageConverter<?> converter : messageConverters) {
        
        // 3. 检查是否能处理该类型
        if (converter.canWrite(returnValue.getClass(), requestedMediaType)) {
            
            // 4. 执行转换
            converter.write(returnValue, requestedMediaType, response);
            return;
        }
    }
}
```

### 常用转换器

| 转换器 | 处理的媒体类型 | 用途 |
|--------|--------------|------|
| MappingJackson2HttpMessageConverter | application/json | Java对象 ↔ JSON |
| StringHttpMessageConverter | text/plain | String ↔ 文本 |
| FormHttpMessageConverter | application/x-www-form-urlencoded | 表单数据 |
| ByteArrayHttpMessageConverter | */* | byte[] ↔ 二进制 |
| MappingJackson2XmlHttpMessageConverter | application/xml | Java对象 ↔ XML |

### JSON 序列化示例

```java
@RestController
public class ExampleController {
    
    @GetMapping("/user")
    public User getUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        return user;
    }
}

// Jackson 序列化过程：
User 对象
  ↓
ObjectMapper.writeValueAsString(user)
  ↓
{
  "id": 1,
  "username": "testuser",
  "email": "test@example.com"
}
  ↓
写入 HTTP 响应体
```

## 🎯 关键组件说明

### 1. DispatcherServlet（前端控制器）
- **作用**：所有请求的统一入口
- **职责**：协调 HandlerMapping、HandlerAdapter、ViewResolver 等组件
- **配置**：Spring Boot 自动配置，无需手动配置

### 2. HandlerMapping（处理器映射）
- **实现**：RequestMappingHandlerMapping
- **作用**：将 URL 映射到具体的 Controller 方法
- **存储**：内部维护 `Map<RequestMappingInfo, HandlerMethod>`

### 3. HandlerAdapter（处理器适配器）
- **实现**：RequestMappingHandlerAdapter
- **作用**：
  - 执行目标方法
  - 参数解析（ArgumentResolver）
  - 返回值处理（ReturnValueHandler）

### 4. ArgumentResolver（参数解析器）
```java
// 常见解析器
@PathVariable      → PathVariableMethodArgumentResolver
@RequestParam      → RequestParamMethodArgumentResolver
@RequestBody       → RequestResponseBodyMethodProcessor
@RequestHeader     → RequestHeaderMethodArgumentResolver
HttpServletRequest → ServletRequestMethodArgumentResolver
```

### 5. ReturnValueHandler（返回值处理器）
```java
// 常见处理器
@ResponseBody           → RequestResponseBodyMethodProcessor
ResponseEntity          → ResponseEntityReturnValueHandler
ModelAndView            → ModelAndViewReturnValueHandler
String (视图名)         → ViewNameMethodReturnValueHandler
```

## 💡 实际应用示例

### 完整的 UserController

```java
@RestController
@RequestMapping("/api/users")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    /**
     * 创建用户
     * POST /api/users
     * Request Body: {"username":"test","email":"test@example.com"}
     * Response: {"success":true,"data":{"id":1,"username":"test",...}}
     */
    @PostMapping
    public ResponseEntity<ApiResponse<User>> createUser(
            @Valid @RequestBody UserDTO userDTO) {
        User user = userService.createUser(userDTO);
        return ResponseEntity.ok(ApiResponse.success(user));
    }
    
    /**
     * 查询用户
     * GET /api/users/1
     * Response: {"success":true,"data":{"id":1,"username":"test",...}}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<User>> getUserById(
            @PathVariable Long id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(ApiResponse.success(user));
    }
    
    /**
     * 更新用户
     * PUT /api/users/1
     * Request Body: {"username":"updated","email":"updated@example.com"}
     * Response: {"success":true,"data":{"id":1,"username":"updated",...}}
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<User>> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserDTO userDTO) {
        User user = userService.updateUser(id, userDTO);
        return ResponseEntity.ok(ApiResponse.success(user));
    }
    
    /**
     * 删除用户
     * DELETE /api/users/1
     * Response: {"success":true,"message":"删除成功"}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(
            @PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(ApiResponse.success("删除成功", null));
    }
}
```

## 🔧 自定义配置

### 1. 自定义 ObjectMapper

```java
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    
    @Bean
    public ObjectMapper customObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        // 美化输出
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        // 忽略空值
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        // 日期格式
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        return mapper;
    }
}
```

### 2. 全局异常处理

```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<?>> handleException(RuntimeException e) {
        return ResponseEntity.badRequest()
            .body(ApiResponse.error(e.getMessage()));
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleValidation(
            MethodArgumentNotValidException e) {
        String message = e.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .collect(Collectors.joining(", "));
        return ResponseEntity.badRequest()
            .body(ApiResponse.error(message));
    }
}
```

### 3. 添加拦截器

```java
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoggingInterceptor())
                .addPathPatterns("/api/**")
                .excludePathPatterns("/api/public/**");
    }
}

public class LoggingInterceptor implements HandlerInterceptor {
    
    @Override
    public boolean preHandle(HttpServletRequest request, 
                            HttpServletResponse response, 
                            Object handler) {
        log.info("请求: {} {}", request.getMethod(), request.getRequestURI());
        return true;
    }
    
    @Override
    public void afterCompletion(HttpServletRequest request, 
                               HttpServletResponse response, 
                               Object handler, 
                               Exception ex) {
        log.info("响应状态: {}", response.getStatus());
    }
}
```

## 📊 性能优化建议

### 1. 使用 ResponseEntity 控制响应
```java
// ✅ 推荐：精确控制状态码和响应头
@GetMapping("/{id}")
public ResponseEntity<User> getUser(@PathVariable Long id) {
    User user = userService.findById(id);
    return ResponseEntity.ok()
        .cacheControl(CacheControl.maxAge(1, TimeUnit.HOURS))
        .eTag("\"" + user.getVersion() + "\"")
        .body(user);
}

// ❌ 不推荐：无法控制响应细节
@GetMapping("/{id}")
public User getUser(@PathVariable Long id) {
    return userService.findById(id);
}
```

### 2. 合理使用 @ResponseBody
```java
// @RestController 已经包含 @ResponseBody
// 无需在方法上重复添加

@RestController
public class UserController {
    
    // ✅ 简洁
    @GetMapping("/{id}")
    public User getUser(@PathVariable Long id) {
        return userService.findById(id);
    }
    
    // ❌ 冗余（虽然也能工作）
    @GetMapping("/{id}")
    @ResponseBody
    public User getUser(@PathVariable Long id) {
        return userService.findById(id);
    }
}
```

### 3. 异步支持
```java
@RestController
public class AsyncController {
    
    @GetMapping("/async")
    public CompletableFuture<User> getUserAsync(@PathVariable Long id) {
        return CompletableFuture.supplyAsync(() -> {
            return userService.findById(id);
        });
    }
}
```

## 🧪 测试要点

参考项目中的测试类：
- [WebMvcBeanTest.java](file://d:\桌面\padmin\demo\src\test\java\com\example\demo\config\WebMvcBeanTest.java) - WebMVC Bean 功能测试
- [WebMvcCustomizationTest.java](file://d:\桌面\padmin\demo\src\test\java\com\example\demo\config\WebMvcCustomizationTest.java) - 自定义功能测试
- [RestControllerPrincipleTest.java](file://d:\桌面\padmin\demo\src\test\java\com\example\demo\config\RestControllerPrincipleTest.java) - @RestController 原理测试

## 📚 总结

### @RestController 的核心价值

1. **简化开发**：一个注解替代两个注解
2. **自动序列化**：无需手动转换 JSON
3. **RESTful 友好**：专为 API 设计
4. **灵活配置**：可自定义序列化行为

### 工作流程记忆口诀

```
一扫：ComponentScan 扫描注解
二注：注册为 Spring Bean
三映：RequestMapping 建立映射
四执：HandlerAdapter 执行方法
五转：HttpMessageConverter 转换
六返：写入响应体返回客户端
```

### 关键区别

```
@Controller + 视图名称 → ViewResolver → HTML 页面
@RestController + 对象 → MessageConverter → JSON 数据
```

---

**相关测试文件：**
- 运行原理测试：`mvn test -Dtest=RestControllerPrincipleTest`
- 查看日志输出了解详细工作流程
