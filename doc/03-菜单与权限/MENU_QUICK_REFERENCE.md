# 菜单权限配置 - 快速参考

## 🚀 一键初始化

```bash
# 启动应用，自动初始化所有菜单数据
mvn spring-boot:run
```

## 📋 核心概念

| 概念 | 说明 | 示例 |
|------|------|------|
| **一级菜单** | 侧边栏主导航项 | 用户管理、角色管理 |
| **二级菜单** | 子导航项 | 用户列表、角色列表 |
| **功能按钮** | 页面内的操作按钮 | 创建、编辑、删除 |
| **权限编码** | 唯一标识符 | `user:view`, `user:create` |
| **图标** | Bootstrap Icons类名 | `bi-people`, `bi-key` |
| **排序** | 显示顺序（数字越小越前） | 1, 2, 3... |

## 🎯 现有菜单结构（5个一级菜单）

```
1️⃣ 仪表盘 (dashboard:menu)
   └─ bi-speedometer2

2️⃣ 用户管理 (user:menu)
   ├─ 📋 用户列表 (user:view)
   ├─ ➕ 创建用户 (user:create)
   ├─ ✏️ 编辑用户 (user:edit)
   └─ 🗑️ 删除用户 (user:delete)

3️⃣ 角色管理 (role:menu)
   ├─ 📋 角色列表 (role:view)
   ├─ ➕ 创建角色 (role:create)
   ├─ ✏️ 编辑角色 (role:edit)
   └─ 🗑️ 删除角色 (role:delete)

4️⃣ 权限管理 (permission:menu)
   ├─ 📋 权限列表 (permission:view)
   ├─ ➕ 创建权限 (permission:create)
   ├─ ✏️ 编辑权限 (permission:edit)
   └─ 🗑️ 删除权限 (permission:delete)

5️⃣ 审计日志 (audit:menu)
   ├─ 📋 日志列表 (audit:view)
   └─ 🗑️ 删除日志 (audit:delete)
```

## 💻 常用SQL命令

### 查看完整菜单树
```sql
SELECT 
    p1.name AS menu,
    p2.name AS submenu,
    p2.type,
    p2.icon
FROM permissions p1
LEFT JOIN permissions p2 ON p2.parent_id = p1.id
WHERE p1.parent_id IS NULL
ORDER BY p1.sort_order, p2.sort_order;
```

### 添加新的一级菜单
```sql
INSERT INTO permissions (name, code, description, icon, type, parent_id, sort_order, status) 
VALUES ('新菜单名称', 'new:menu', '描述信息', 'bi-icon-name', 'MENU', NULL, 6, true);
```

### 添加子菜单/按钮
```sql
INSERT INTO permissions (name, code, description, icon, type, parent_id, sort_order, status) 
VALUES ('子项名称', 'new:item', '描述', 'bi-icon', 'MENU', 
        (SELECT id FROM permissions WHERE code = 'parent:code'), 1, true);
```

### 分配权限给角色
```sql
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id 
FROM roles r, permissions p
WHERE r.name = 'ADMIN' 
  AND p.code = 'new:permission';
```

## 🎨 常用图标速查

| 类别 | 图标类名 | 用途 |
|------|---------|------|
| 导航 | `bi-house` | 首页 |
| 用户 | `bi-people` | 用户管理 |
| 安全 | `bi-shield-lock` | 角色/权限 |
| 工具 | `bi-gear` | 设置 |
| 数据 | `bi-graph-up` | 统计图表 |
| 文件 | `bi-file-text` | 文档 |
| 通讯 | `bi-envelope` | 邮件 |
| 时间 | `bi-calendar` | 日程 |
| 通知 | `bi-bell` | 消息提醒 |
| 帮助 | `bi-question-circle` | 帮助中心 |
| 列表 | `bi-list-ul` | 列表视图 |
| 速度 | `bi-speedometer2` | 仪表盘 |
| 钥匙 | `bi-key` | 权限管理 |
| 日志 | `bi-journal-text` | 审计日志 |

👉 更多图标：https://icons.getbootstrap.com/

## 🔐 权限控制使用

### Thymeleaf模板（视图层）
```html
<!-- 按钮级权限控制 -->
<button perm:hasPermission="user:create">新建用户</button>

<!-- 菜单项权限控制 -->
<a perm:hasPermission="user:view" href="/admin/users">用户列表</a>
```

### Controller注解（API层）
```java
@PostMapping
@RequirePermission("user:create")
public ApiResponse create(@RequestBody UserDTO dto) {
    return userService.create(dto);
}
```

## 📝 添加新菜单步骤

### 第1步：插入权限记录
```sql
-- 一级菜单
INSERT INTO permissions (name, code, description, icon, type, parent_id, sort_order, status) 
VALUES ('系统设置', 'system:menu', '系统配置', 'bi-gear', 'MENU', NULL, 6, true);

-- 二级菜单
INSERT INTO permissions (name, code, description, icon, type, parent_id, sort_order, status) 
VALUES ('基础设置', 'system:basic', '参数配置', 'bi-sliders', 'MENU', 
        (SELECT id FROM permissions WHERE code = 'system:menu'), 1, true);
```

### 第2步：分配给角色
```sql
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id 
FROM roles r, permissions p
WHERE r.name = 'ADMIN' 
  AND p.code IN ('system:menu', 'system:basic');
```

### 第3步：更新DataInitializer（可选）
在 [`DataInitializer.java`](file://d:\桌面\padmin\demo\src\main\java\com\example\demo\config\DataInitializer.java) 中添加相同的初始化代码。

### 第4步：测试验证
- 重启应用
- 用不同角色账号登录
- 检查菜单显示是否正确

## ⚠️ 注意事项

1. **权限编码必须唯一** - `code`字段有UNIQUE约束
2. **父子关系要正确** - `parentId`必须指向存在的权限ID
3. **排序号决定顺序** - 数字越小越靠前
4. **类型要区分** - MENU用于导航，BUTTON用于功能按钮
5. **状态控制显示** - `status=false`的权限不显示
6. **记得分配角色** - 新权限需要分配给角色才能生效

## 🔍 常见问题

### Q1: 菜单不显示？
- 检查权限是否分配给了当前用户的角色
- 检查`status`是否为`true`
- 检查`type`是否为`MENU`

### Q2: 按钮看不到？
- 确认使用了`perm:hasPermission`属性
- 确认权限编码正确
- 确认当前用户拥有该权限

### Q3: 如何修改菜单顺序？
```sql
UPDATE permissions SET sort_order = 新数值 WHERE code = '权限编码';
```

### Q4: 如何禁用某个菜单？
```sql
UPDATE permissions SET status = false WHERE code = '权限编码';
```

## 📚 相关文档

- 📘 [详细配置指南](MENU_PERMISSION_CONFIG_GUIDE.md)
- 🎨 [可视化指南](MENU_VISUALIZATION_GUIDE.md)
- 🔐 [高级权限控制](ADVANCED_PERMISSION_GUIDE.md)
- 📊 [动态菜单和审计日志](DYNAMIC_MENU_AND_AUDIT_LOG_GUIDE.md)

## 🆘 获取帮助

遇到问题？检查以下位置：

1. **数据库** - H2控制台: http://localhost:8080/h2-console
2. **日志** - 查看应用启动日志
3. **代码** - [`DataInitializer.java`](file://d:\桌面\padmin\demo\src\main\java\com\example\demo\config\DataInitializer.java)
4. **SQL脚本** - [`init_menu_permissions.sql`](file://d:\桌面\padmin\demo\init_menu_permissions.sql)

---

💡 **提示**: 建议先阅读 [MENU_PERMISSION_CONFIG_GUIDE.md](MENU_PERMISSION_CONFIG_GUIDE.md) 了解完整概念，再使用本快速参考。
