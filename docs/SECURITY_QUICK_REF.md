# 🔐 Spring Security 快速参考卡

## 🚀 快速启动

```bash
mvn spring-boot:run
```

访问: http://localhost:8080/login

## 👤 测试账号

| 用户名 | 密码 | 角色 | 说明 |
|--------|------|------|------|
| admin | admin123 | ADMIN | 管理员 |
| zhangsan | user123 | USER | 普通用户 |
| lisi | user123 | USER | 普通用户 |
| wangwu | user123 | USER | 普通用户 |

## 🔗 常用URL

| 路径 | 说明 | 认证 |
|------|------|------|
| `/login` | 登录页面 | ❌ 公开 |
| `/pages/` | 首页 | ✅ 需要登录 |
| `/pages/users` | 用户列表 | ✅ 需要登录 |
| `/api/users` | API接口 | ✅ 需要登录 |
| `/logout` | 退出登录 | POST方法 |

## 📁 核心文件

### 配置类
- `SecurityConfig.java` - 安全配置
- `CustomUserDetailsService.java` - 用户服务

### 控制器
- `AuthController.java` - 登录处理
- `PageController.java` - 页面路由

### 模板
- `templates/login.html` - 登录页面

## 🔧 关键代码

### 密码加密
```java
@Autowired
private PasswordEncoder passwordEncoder;

// 加密
String encoded = passwordEncoder.encode("rawPassword");

// 验证
boolean valid = passwordEncoder.matches("raw", encoded);
```

### 获取当前用户
```java
Authentication auth = SecurityContextHolder.getContext().getAuthentication();
String username = auth.getName();
```

### Thymeleaf中显示用户
```html
<span th:text="${currentUsername}">用户名</span>
```

### 登出表单
```html
<form th:action="@{/logout}" method="post">
    <button type="submit">退出</button>
</form>
```

## 🛡️ 安全规则

### 公开访问
- `/login` - 登录页
- `/css/**` - 样式文件
- `/js/**` - JavaScript文件
- `/images/**` - 图片资源

### 需要认证
- `/api/**` - 所有API
- `/pages/**` - 所有页面
- 其他所有路径

## ⚙️ 配置要点

### SecurityConfig核心配置
```java
.authorizeHttpRequests(auth -> auth
    .requestMatchers("/login", "/css/**").permitAll()
    .requestMatchers("/api/**", "/pages/**").authenticated()
    .anyRequest().authenticated()
)
.formLogin(form -> form
    .loginPage("/login")
    .defaultSuccessUrl("/pages/", true)
    .permitAll()
)
.logout(logout -> logout
    .logoutUrl("/logout")
    .logoutSuccessUrl("/login?logout=true")
    .permitAll()
)
```

## 🐛 常见问题

### Q: 登录后仍跳转登录页？
A: 检查 `.defaultSuccessUrl("/pages/", true)` 第二个参数是否为true

### Q: 端口被占用？
A: 
```bash
netstat -ano | findstr :8080
taskkill /PID <进程ID> /F
```

### Q: 密码验证失败？
A: 确保使用 `passwordEncoder.encode()` 加密后存储

### Q: CSRF错误？
A: 当前已禁用CSRF。如需启用，移除 `.csrf(csrf -> csrf.disable())`

## 💡 提示

1. **密码永远不要明文存储** - 始终使用BCrypt加密
2. **生产环境启用HTTPS** - 保护传输安全
3. **生产环境启用CSRF** - 防止跨站请求伪造
4. **实施登录限制** - 防止暴力破解
5. **定期更新依赖** - 保持安全性

## 📚 更多信息

- 详细指南: `SPRING_SECURITY_GUIDE.md`
- 完整总结: `SPRING_SECURITY_SUMMARY.md`
- 官方文档: https://spring.io/projects/spring-security

---

**版本**: Spring Security 7.0.4  
**最后更新**: 2026-04-18
