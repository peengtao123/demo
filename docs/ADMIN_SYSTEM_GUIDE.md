# 管理系统功能说明

## 📋 功能概述

本项目新增了完整的管理系统，包括用户管理、角色管理和权限管理功能。

## ✨ 新增功能

### 1. 管理仪表盘
- **访问路径**: `/admin/dashboard`
- **功能**: 
  - 显示系统统计信息（用户数、角色数、权限数）
  - 快速导航到各个管理模块
  - 仅管理员角色可访问

### 2. 用户管理
- **访问路径**: `/admin/users`
- **功能**:
  - 查看所有用户列表
  - 查看用户详细信息
  - 显示用户角色信息

### 3. 角色管理
- **访问路径**: `/admin/roles`
- **功能**:
  - 查看角色列表
  - 创建新角色
  - 编辑角色信息
  - 删除角色
  - 查看角色详情

### 4. 权限管理
- **访问路径**: `/admin/permissions`
- **功能**:
  - 查看权限列表
  - 创建新权限
  - 编辑权限信息
  - 删除权限
  - 查看权限详情

## 🔐 权限控制

### 角色说明

系统预定义了三种角色：

1. **ADMIN (管理员)**
   - 拥有所有权限
   - 可访问管理系统所有功能
   - 默认账号: `admin / admin123`

2. **USER (普通用户)**
   - 基本访问权限
   - 可查看个人信息
   - 默认账号: `zhangsan / user123`

3. **EDITOR (编辑者)**
   - 内容编辑权限
   - 默认账号: `wangwu / user123`

### 访问控制规则

- `/admin/**` - 需要 ADMIN 角色
- `/api/**` - 需要认证
- `/pages/**` - 需要认证
- `/login` - 公开访问

## 🚀 快速开始

### 1. 启动项目

```bash
mvn spring-boot:run
```

### 2. 登录系统

访问: http://localhost:8080/login

使用管理员账号登录:
- 用户名: `admin`
- 密码: `admin123`

### 3. 自动跳转

登录成功后会自动跳转到管理仪表盘: http://localhost:8080/admin/dashboard

## 📊 数据模型

### 实体关系

```
User (用户) ←→ Role (角色) ←→ Permission (权限)
  ↓              ↓
多对多         多对多
```

### 主要实体

#### User (用户)
- id: 用户ID
- username: 用户名
- email: 邮箱
- name: 姓名
- phone: 电话
- age: 年龄
- password: 密码（加密存储）
- role: 角色（兼容字段）
- roles: 角色集合（多对多关系）
- createTime: 创建时间
- updateTime: 更新时间

#### Role (角色)
- id: 角色ID
- name: 角色名称（唯一）
- description: 角色描述
- users: 用户集合
- createTime: 创建时间
- updateTime: 更新时间

#### Permission (权限)
- id: 权限ID
- name: 权限名称（唯一）
- code: 权限编码（唯一，如 user:view）
- description: 权限描述
- roles: 角色集合
- createTime: 创建时间
- updateTime: 更新时间

## 🎨 页面展示

### 管理仪表盘
- 渐变色背景设计
- 统计卡片展示
- 快捷导航入口

### 列表页面
- 清晰的表格展示
- 操作按钮（查看、编辑、删除）
- 成功/错误提示

### 表单页面
- 简洁的表单设计
- 实时验证
- 友好的提示信息

## 🔧 技术实现

### 后端技术
- Spring Boot 4.0.5
- Spring Security 7.0
- Spring Data JPA
- Thymeleaf 模板引擎
- H2 内存数据库

### 安全配置
- BCrypt 密码加密
- 基于角色的访问控制（RBAC）
- 会话管理
- 登录/登出功能

### 前端技术
- HTML5 + CSS3
- Thymeleaf 模板
- 响应式设计
- 现代化UI风格

## 📝 初始化数据

系统启动时会自动初始化以下数据：

### 权限（12个）
- user:view, user:create, user:edit, user:delete
- role:view, role:create, role:edit, role:delete
- permission:view, permission:create, permission:edit, permission:delete

### 角色（3个）
- ADMIN - 系统管理员
- USER - 普通用户
- EDITOR - 编辑者

### 用户（4个）
- admin (ADMIN角色)
- zhangsan (USER角色)
- lisi (USER角色)
- wangwu (EDITOR角色)

## 🔗 相关文档

- [Spring Security 指南](./SPRING_SECURITY_GUIDE.md)
- [Thymeleaf 使用指南](./THYMELEAF_README.md)
- [测试说明](./TEST_README.md)

## ⚠️ 注意事项

1. **生产环境配置**
   - 启用 CSRF 保护（当前已禁用以简化开发）
   - 使用外部数据库替代 H2
   - 配置 HTTPS
   - 加强密码策略

2. **权限扩展**
   - 可以在 Role 和 Permission 之间建立多对多关系
   - 可以实现更细粒度的权限控制
   - 可以添加权限注解进行方法级保护

3. **性能优化**
   - 添加分页功能
   - 实现缓存机制
   - 优化数据库查询

## 🎯 后续优化建议

1. 实现角色与权限的关联管理
2. 添加用户分配角色功能
3. 实现权限的动态配置
4. 添加操作日志记录
5. 实现数据权限控制
6. 添加导出功能
7. 实现批量操作
8. 添加搜索和过滤功能
