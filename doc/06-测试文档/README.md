# 测试文档 🧪

本目录包含单元测试和Web MVC测试的相关文档。

## 📚 文档列表

- **[TEST_README.md](./TEST_README.md)** - 单元测试完整指南
- **[WEBMVC_TEST_README.md](./WEBMVC_TEST_README.md)** - Web MVC测试指南

## 🎯 测试类型

### 单元测试（Unit Test）
- ✅ Service层测试
- ✅ Repository层测试
- ✅ 工具类测试
- ✅ Mock对象使用

### Web MVC测试
- ✅ Controller层测试
- ✅ 请求/响应验证
- ✅ 会话管理测试
- ✅ 安全上下文测试

## 🛠️ 测试框架

- **JUnit 5** - 测试框架
- **Mockito** - Mock框架
- **Spring Boot Test** - Spring集成测试
- **TestRestTemplate** - REST API测试

## 💡 最佳实践

### 测试命名规范
```java
@Test
@DisplayName("创建用户 - 成功场景")
void createUser_Success() {
    // 测试代码
}

@Test
@DisplayName("创建用户 - 用户名已存在")
void createUser_UsernameAlreadyExists() {
    // 测试代码
}
```

### 测试结构（AAA模式）
```java
// Arrange - 准备测试数据
User user = new User("test", "test@example.com");

// Act - 执行被测试的方法
User savedUser = userService.createUser(user);

// Assert - 验证结果
assertNotNull(savedUser.getId());
assertEquals("test", savedUser.getUsername());
```

## 🔗 相关文档

- **管理系统**: [../02-管理系统/](../02-管理系统/) - 被测试的功能模块
- **Spring Security**: [../04-Spring-Security/](../04-Spring-Security/) - 安全测试

---

**最后更新**: 2026-04-18
