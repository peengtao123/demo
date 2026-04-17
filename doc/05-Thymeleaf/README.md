# Thymeleaf 🎨

本目录包含Thymeleaf模板引擎的使用指南和最佳实践。

## 📚 文档列表

- **[THYMELEAF_README.md](./THYMELEAF_README.md)** - Thymeleaf入门指南
- **[THYMELEAF_QUICKSTART.md](./THYMELEAF_QUICKSTART.md)** - ⚡ Thymeleaf快速开始
- **[THYMELEAF_SUMMARY.md](./THYMELEAF_SUMMARY.md)** - Thymeleaf总结

## 🎯 核心特性

### 模板语法
- ✅ 变量表达式 `${...}`
- ✅ 选择表达式 `*{...}`
- ✅ 消息表达式 `#{...}`
- ✅ URL表达式 `@{...}`
- ✅ 片段表达式 `~{...}`

### 布局复用
- ✅ 模板继承（th:replace）
- ✅ 片段包含（th:insert）
- ✅ 布局参数传递
- ✅ 条件渲染

### 与Spring集成
- ✅ Spring Security方言
- ✅ 自定义属性处理器
- ✅ 表单绑定
- ✅ 国际化支持

## 💡 最佳实践

### 图标渲染规范
```html
<!-- ✅ 正确：将图标类名放在class属性中 -->
<i class="${item.icon}"></i>

<!-- ❌ 错误：直接输出类名文本 -->
<span th:text="${item.icon}"></span>
```

### 权限控制
```html
<!-- 使用自定义权限标签 -->
<button perm:hasPermission="user:create">创建用户</button>
```

## 🔗 相关文档

- **菜单与权限**: [../03-菜单与权限/](../03-菜单与权限/) - 权限控制实现
- **Spring Security**: [../04-Spring-Security/](../04-Spring-Security/) - 安全集成

## 📖 推荐阅读顺序

1. **快速开始**: [THYMELEAF_QUICKSTART.md](./THYMELEAF_QUICKSTART.md) - 5分钟上手
2. **入门指南**: [THYMELEAF_README.md](./THYMELEAF_README.md) - 系统学习
3. **总结文档**: [THYMELEAF_SUMMARY.md](./THYMELEAF_SUMMARY.md) - 知识回顾

---

**最后更新**: 2026-04-18
