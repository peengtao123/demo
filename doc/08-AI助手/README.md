# Spring AI 集成与 AI 助手功能

## 概述

本项目已成功集成 Spring AI 框架，提供智能对话助手功能。用户可以通过系统管理菜单访问 AI 助手，进行智能问答和文本生成。

## 功能特性

- ✅ **智能对话**：基于 OpenAI GPT 模型的智能问答
- ✅ **流式响应**：支持实时流式输出，提升用户体验
- ✅ **权限控制**：集成系统权限管理体系
- ✅ **美观界面**：现代化的聊天界面设计
- ✅ **审计日志**：所有操作自动记录审计日志

## 技术栈

- **Spring AI**: 1.0.0-M5
- **OpenAI API**: GPT-3.5-turbo / GPT-4
- **前端技术**: Thymeleaf + Bootstrap Icons
- **权限框架**: Spring Security + 自定义权限注解

## 配置说明

### 1. 添加 OpenAI API Key

在 `src/main/resources/application.properties` 中添加以下配置：

```properties
# Spring AI OpenAI Configuration
spring.ai.openai.api-key=your-openai-api-key-here
spring.ai.openai.chat.options.model=gpt-3.5-turbo
```

**获取 API Key 步骤：**
1. 访问 [OpenAI Platform](https://platform.openai.com/)
2. 注册/登录账户
3. 进入 API Keys 页面创建新的密钥
4. 复制密钥并替换配置文件中的 `your-openai-api-key-here`

### 2. 可选配置

```properties
# 模型选择（默认：gpt-3.5-turbo）
spring.ai.openai.chat.options.model=gpt-4

# 温度参数（0-2，越高越有创造性）
spring.ai.openai.chat.options.temperature=0.7

# 最大token数
spring.ai.openai.chat.options.max-tokens=1000
```

## 菜单与权限

### 菜单结构

```
系统管理
├── ...
└── AI助手 (bi-robot)
    └── AI聊天 (bi-chat-dots)
```

### 权限配置

系统初始化时会自动创建以下权限：

| 权限名称 | 权限编码 | 类型 | 说明 |
|---------|---------|------|------|
| AI助手 | `ai:menu` | MENU | 一级菜单入口 |
| AI聊天 | `ai:chat` | MENU | 聊天功能权限 |

### 角色权限分配

- **ADMIN 角色**：自动拥有所有 AI 权限
- **EDITOR 角色**：拥有 AI 聊天权限
- **USER 角色**：需要手动分配权限

如需为其他角色分配 AI 权限：
1. 进入"系统管理" -> "角色管理"
2. 编辑目标角色
3. 在权限列表中勾选"AI助手"相关权限
4. 保存

## 使用指南

### 访问 AI 助手

1. 使用管理员账号登录系统（admin/admin123）
2. 在左侧菜单找到"AI助手" -> "AI聊天"
3. 在聊天界面输入问题并发送

### 功能说明

- **文本输入**：在底部输入框输入您的问题
- **发送消息**：点击"发送"按钮或按 Enter 键
- **实时响应**：AI 回复会逐字显示（流式输出）
- **历史记录**：当前会话的消息会保留在页面上

### 示例对话

```
用户：请介绍一下 Spring Boot 的主要特点

AI：Spring Boot 是一个用于简化 Spring 应用开发的框架，主要特点包括：
1. 自动配置：根据类路径中的依赖自动配置 Spring 应用
2. 内嵌服务器：内置 Tomcat、Jetty 等 Web 服务器
3. 起步依赖：提供简化的依赖描述
4. 生产就绪：提供监控、指标等生产级功能
...
```

## 开发指南

### 核心文件

#### 后端文件

- **Service**: `src/main/java/com/example/demo/service/AiChatService.java`
  - 提供同步和流式聊天功能
  - 检查 AI 服务可用性

- **Controller**: `src/main/java/com/example/demo/controller/AiController.java`
  - `/admin/ai/chat` - 聊天页面
  - `/admin/ai/api/chat` - 同步聊天 API
  - `/admin/ai/api/stream` - 流式聊天 API

- **数据初始化**: `src/main/java/com/example/demo/config/DataInitializer.java`
  - 自动创建 AI 菜单和权限

#### 前端文件

- **模板**: `src/main/resources/templates/admin/ai/chat.html`
  - 聊天界面布局
  - JavaScript 交互逻辑
  - 消息渲染和滚动

### 扩展功能

#### 1. 添加更多 AI 模型

在 `pom.xml` 中添加其他模型支持：

```xml
<!-- Anthropic Claude -->
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-anthropic-spring-boot-starter</artifactId>
</dependency>

<!-- Azure OpenAI -->
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-azure-openai-spring-boot-starter</artifactId>
</dependency>
```

#### 2. 实现上下文记忆

修改 `AiChatService` 以支持多轮对话：

```java
private Map<String, List<Message>> conversationHistory = new ConcurrentHashMap<>();

public String chatWithHistory(String userId, String message) {
    List<Message> history = conversationHistory.computeIfAbsent(userId, k -> new ArrayList<>());
    history.add(new UserMessage(message));
    
    Prompt prompt = new Prompt(history);
    String response = chatModel.call(prompt).getResult().getOutput().getText();
    
    history.add(new AssistantMessage(response));
    return response;
}
```

#### 3. 添加函数调用

```java
@Bean
public FunctionCallback weatherFunction() {
    return FunctionCallback.builder()
        .function("getWeather", location -> {
            // 调用天气 API
            return weatherService.getWeather(location);
        })
        .description("获取指定城市的天气信息")
        .inputType(LocationRequest.class)
        .build();
}
```

### API 接口文档

#### 1. 同步聊天接口

**请求**
```http
POST /admin/ai/api/chat
Content-Type: application/x-www-form-urlencoded

message=你好
```

**响应**
```json
{
  "response": "你好！有什么可以帮助你的吗？"
}
```

#### 2. 流式聊天接口

**请求**
```http
GET /admin/ai/api/stream?message=介绍一下Spring%20AI
```

**响应** (Server-Sent Events)
```
data: Spring
 
data: AI
 
data: 是
...
```

## 故障排查

### 问题 1：AI 服务未配置提示

**现象**：页面顶部显示黄色警告"AI服务未配置"

**解决方案**：
1. 检查 `application.properties` 中是否配置了 `spring.ai.openai.api-key`
2. 确认 API Key 有效且未过期
3. 重启应用使配置生效

### 问题 2：API 调用失败

**现象**：发送消息后返回错误信息

**可能原因**：
- API Key 无效或配额用尽
- 网络连接问题
- OpenAI 服务不可用

**解决方案**：
1. 验证 API Key 是否正确
2. 检查 OpenAI 账户余额
3. 查看控制台日志获取详细错误信息

### 问题 3：菜单不显示

**现象**：左侧菜单中没有"AI助手"选项

**解决方案**：
1. 确认当前用户角色拥有 `ai:menu` 权限
2. 检查数据库中是否存在 AI 相关权限记录
3. 清除浏览器缓存并重新登录

### 问题 4：编译错误

**现象**：Maven 编译失败，提示找不到 Spring AI 类

**解决方案**：
```bash
# 清理并重新下载依赖
mvn clean install -U

# 刷新 IDE 项目
# Eclipse: Right-click project -> Maven -> Update Project
# IntelliJ: Right-click pom.xml -> Maven -> Reload Project
```

## 安全建议

1. **保护 API Key**
   - 不要将 API Key 提交到版本控制系统
   - 使用环境变量或密钥管理服务存储敏感信息
   - 定期轮换 API Key

2. **速率限制**
   - 在生产环境中实施请求频率限制
   - 监控 API 使用情况，防止滥用

3. **内容过滤**
   - 对用户输入进行验证和过滤
   - 设置合适的 temperature 参数控制输出质量

4. **成本控制**
   - 设置每月预算上限
   - 监控 token 使用量
   - 考虑使用更经济的模型（如 gpt-3.5-turbo）

## 性能优化

1. **启用响应缓存**
   ```java
   @Cacheable(value = "ai-responses", key = "#message")
   public String chat(String message) {
       // ...
   }
   ```

2. **异步处理**
   ```java
   @Async
   public CompletableFuture<String> chatAsync(String message) {
       return CompletableFuture.completedFuture(chat(message));
   }
   ```

3. **连接池配置**
   ```properties
   spring.ai.openai.connection-pool.max-connections=10
   spring.ai.openai.connection-pool.connection-timeout=5000
   ```

## 参考资料

- [Spring AI 官方文档](https://docs.spring.io/spring-ai/reference/)
- [OpenAI API 文档](https://platform.openai.com/docs/introduction)
- [Spring AI GitHub](https://github.com/spring-projects/spring-ai)

## 更新日志

### v1.0.0 (2024-01-01)
- ✨ 初始版本发布
- ✨ 集成 Spring AI 1.0.0-M5
- ✨ 实现基础聊天功能
- ✨ 添加流式响应支持
- ✨ 创建美观的聊天界面
- ✨ 集成权限控制系统

## 许可证

本项目采用 MIT 许可证。详见 LICENSE 文件。

## 联系方式

如有问题或建议，请联系开发团队。

---

**最后更新**: 2024-01-01  
**维护者**: Demo Team
