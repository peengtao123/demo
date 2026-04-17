# 管理系统快速参考

## 🚀 快速启动

```bash
# 1. 启动项目
mvn spring-boot:run

# 2. 访问系统
浏览器打开: http://localhost:8080/login
```

## 👤 默认账号

| 角色 | 用户名 | 密码 | 权限 |
|------|--------|------|------|
| 👑 管理员 | admin | admin123 | 所有管理功能 |
| 👤 普通用户 | zhangsan | user123 | 基本访问 |
| 👤 普通用户 | lisi | user123 | 基本访问 |
| ✏️ 编辑者 | wangwu | user123 | 内容编辑 |

## 📍 主要URL

| 路径 | 说明 | 权限要求 |
|------|------|----------|
| `/login` | 登录页面 | 公开 |
| `/admin/dashboard` | 管理仪表盘 | ADMIN角色 |
| `/admin/users` | 用户管理 | ADMIN角色 |
| `/admin/roles` | 角色管理 | ADMIN角色 |
| `/admin/permissions` | 权限管理 | ADMIN角色 |
| `/h2-console` | H2数据库控制台 | 公开 |

## 🎭 预定义角色

### ADMIN (管理员)
- 访问所有管理功能
- 管理用户、角色、权限
- 系统最高权限

### USER (普通用户)
- 基本访问权限
- 查看个人信息
- 无法访问管理后台

### EDITOR (编辑者)
- 内容编辑权限
- 介于USER和ADMIN之间

## 🔐 预定义权限

### 用户相关
- `user:view` - 查看用户
- `user:create` - 创建用户
- `user:edit` - 编辑用户
- `user:delete` - 删除用户

### 角色相关
- `role:view` - 查看角色
- `role:create` - 创建角色
- `role:edit` - 编辑角色
- `role:delete` - 删除角色

### 权限相关
- `permission:view` - 查看权限
- `permission:create` - 创建权限
- `permission:edit` - 编辑权限
- `permission:delete` - 删除权限

## 💻 常用操作

### 创建新角色
1. 访问 `/admin/roles`
2. 点击 "+ 新建角色"
3. 填写角色名称和描述
4. 点击 "保存"

### 创建新权限
1. 访问 `/admin/permissions`
2. 点击 "+ 新建权限"
3. 填写权限名称、编码和描述
4. 点击 "保存"

### 查看用户列表
1. 访问 `/admin/users`
2. 查看所有用户信息
3. 点击 "查看详情" 查看更多信息

## 🔧 技术栈

- **框架**: Spring Boot 4.0.5
- **安全**: Spring Security 7.0
- **数据**: Spring Data JPA + H2
- **视图**: Thymeleaf
- **Java**: 17

## 📊 数据库表

```
users           - 用户表
roles           - 角色表
permissions     - 权限表
user_role       - 用户-角色关联
role_permission - 角色-权限关联
```

## ⚠️ 注意事项

1. **生产环境**需要启用CSRF保护
2. 使用外部数据库替代H2
3. 配置HTTPS
4. 加强密码策略
5. 添加日志记录

## 📖 详细文档

- [管理系统完整指南](./ADMIN_SYSTEM_GUIDE.md)
- [更新总结](./UPDATE_SUMMARY.md)
- [功能验证清单](./VERIFICATION_CHECKLIST.md)

## 🆘 常见问题

### Q: 登录后无法访问管理页面？
A: 确保使用ADMIN角色的账号（admin/admin123）

### Q: 如何重置数据？
A: 重启应用，H2内存数据库会自动重建

### Q: 如何查看数据库？
A: 访问 http://localhost:8080/h2-console
   - JDBC URL: jdbc:h2:mem:userdb
   - 用户名: sa
   - 密码: (留空)

### Q: 忘记管理员密码怎么办？
A: 重启应用，数据会重新初始化

## 🎯 下一步

1. 实现角色与权限的关联管理
2. 添加用户分配角色功能
3. 实现分页和搜索
4. 添加操作日志
5. 完善表单验证

---

**提示**: 更多详细信息请查看完整文档 📚
