# Demo 项目

基于 Spring Boot 4.0.5 构建的 Java Web 应用程序演示项目。

## 📚 文档

所有详细文档已整理到 `doc/` 目录中：

### 快速开始
- [README](doc/README.md) - 项目概述和快速开始指南
- [快速开始 - 高级权限](doc/QUICK_START_ADVANCED_PERMISSION.md) - 权限系统快速上手

### 功能模块文档
- [布局说明](doc/README_LAYOUT.md) - 页面布局和UI设计
- [角色权限管理](doc/README_ROLE_PERMISSION.md) - 角色和权限管理系统
- [用户管理](doc/README_USER_MANAGEMENT.md) - 用户管理功能

### 菜单权限配置
- [菜单权限配置指南](doc/MENU_PERMISSION_CONFIG_GUIDE.md) - 详细的菜单权限配置说明
- [菜单可视化指南](doc/MENU_VISUALIZATION_GUIDE.md) - 菜单结构的可视化展示
- [菜单快速参考](doc/MENU_QUICK_REFERENCE.md) - 常用命令和配置速查

### 高级功能
- [高级权限控制指南](doc/ADVANCED_PERMISSION_GUIDE.md) - 细粒度权限控制实现
- [动态菜单和审计日志](doc/DYNAMIC_MENU_AND_AUDIT_LOG_GUIDE.md) - 动态菜单和审计日志功能

### 测试文档
- [角色权限测试](doc/TEST_ROLE_PERMISSION.md) - 角色权限功能测试指南
- [用户管理测试](doc/TEST_USER_MANAGEMENT.md) - 用户管理功能测试指南

## 🚀 快速启动

```bash
# 编译并运行
mvn spring-boot:run

# 访问应用
# http://localhost:8080
```

## 📋 默认账号

- **管理员**: admin / admin123
- **普通用户**: zhangsan / user123
- **编辑者**: wangwu / user123

## 🛠️ 技术栈

- **后端**: Spring Boot 4.0.5, Spring Security, Spring Data JPA
- **数据库**: H2 (内存数据库)
- **前端**: Thymeleaf, Bootstrap 5, Bootstrap Icons
- **构建工具**: Maven

## 📁 项目结构

```
demo/
├── doc/                    # 文档目录
│   ├── README.md
│   ├── MENU_*.md          # 菜单相关文档
│   ├── ADVANCED_*.md      # 高级功能文档
│   └── TEST_*.md          # 测试文档
├── src/
│   ├── main/
│   │   ├── java/          # Java源代码
│   │   └── resources/     # 配置文件和模板
│   └── test/              # 测试代码
├── pom.xml                # Maven配置
└── init_menu_permissions.sql  # 菜单权限初始化SQL
```

## 🔗 相关链接

- [H2 控制台](http://localhost:8080/h2-console) - 数据库管理
- [登录页面](http://localhost:8080/login) - 系统登录

## 📝 许可证

本项目仅用于学习和演示目的。
