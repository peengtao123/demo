# 菜单权限配置可视化指南

## 🌳 完整菜单树结构

```
📱 系统菜单层级结构
│
├── 🏠 1. 仪表盘 (dashboard:menu) [sort:1]
│   └── 📊 bi-speedometer2
│
├── 👥 2. 用户管理 (user:menu) [sort:2]
│   └── 🔑 bi-people
│   │
│   ├── 📋 用户列表 (user:view) [sort:1] - MENU
│   │   └── 📝 bi-list-ul
│   │
│   ├── ➕ 创建用户 (user:create) [sort:2] - BUTTON
│   │
│   ├── ✏️ 编辑用户 (user:edit) [sort:3] - BUTTON
│   │
│   └── 🗑️ 删除用户 (user:delete) [sort:4] - BUTTON
│
├── 🛡️ 3. 角色管理 (role:menu) [sort:3]
│   └── 🔐 bi-shield-lock
│   │
│   ├── 📋 角色列表 (role:view) [sort:1] - MENU
│   │   └── 📝 bi-list-ul
│   │
│   ├── ➕ 创建角色 (role:create) [sort:2] - BUTTON
│   │
│   ├── ✏️ 编辑角色 (role:edit) [sort:3] - BUTTON
│   │
│   └── 🗑️ 删除角色 (role:delete) [sort:4] - BUTTON
│
├── 🔑 4. 权限管理 (permission:menu) [sort:4]
│   └── 🗝️ bi-key
│   │
│   ├── 📋 权限列表 (permission:view) [sort:1] - MENU
│   │   └── 📝 bi-list-ul
│   │
│   ├── ➕ 创建权限 (permission:create) [sort:2] - BUTTON
│   │
│   ├── ✏️ 编辑权限 (permission:edit) [sort:3] - BUTTON
│   │
│   └── 🗑️ 删除权限 (permission:delete) [sort:4] - BUTTON
│
└── 📜 5. 审计日志 (audit:menu) [sort:5]
    └── 📖 bi-journal-text
    │
    ├── 📋 日志列表 (audit:view) [sort:1] - MENU
    │   └── 📝 bi-list-ul
    │
    └── 🗑️ 删除日志 (audit:delete) [sort:2] - BUTTON
```

## 📊 数据库表关系图

```
┌─────────────────┐         ┌──────────────────────┐
│   permissions   │         │   role_permissions   │
├─────────────────┤         ├──────────────────────┤
│ id (PK)         │◄────────│ permission_id (FK)   │
│ name            │         │ role_id (FK)         │
│ code (UNIQUE)   │         └──────────┬───────────┘
│ description     │                    │
│ icon            │                    │
│ status          │         ┌──────────▼───────────┐
│ parent_id (FK)  │         │       roles          │
│ sort_order      │         ├──────────────────────┤
│ type            │         │ id (PK)              │
│ create_time     │         │ name                 │
│ update_time     │         │ description          │
└─────────────────┘         └──────────────────────┘
```

## 🎨 图标预览

### 一级菜单图标

| 图标类名 | 预览 | 用途 |
|---------|------|------|
| `bi-speedometer2` | 🏎️ | 仪表盘/控制面板 |
| `bi-people` | 👥 | 用户管理 |
| `bi-shield-lock` | 🛡️ | 角色/安全管理 |
| `bi-key` | 🔑 | 权限管理 |
| `bi-journal-text` | 📜 | 日志/文档 |

### 二级菜单图标

| 图标类名 | 预览 | 用途 |
|---------|------|------|
| `bi-list-ul` | 📋 | 列表视图 |

### 更多可用图标

访问 [Bootstrap Icons](https://icons.getbootstrap.com/) 查看更多图标：

常用推荐：
- `bi-house` - 首页
- `bi-gear` - 设置
- `bi-sliders` - 配置
- `bi-graph-up` - 统计
- `bi-calendar` - 日程
- `bi-bell` - 通知
- `bi-envelope` - 邮件
- `bi-folder` - 文件夹
- `bi-file-text` - 文档
- `bi-image` - 图片
- `bi-video` - 视频
- `bi-music-note` - 音频
- `bi-chat` - 聊天
- `bi-question-circle` - 帮助
- `bi-info-circle` - 信息

## 💡 使用示例

### 示例1：添加新的"系统设置"菜单

```sql
-- 1. 添加一级菜单
INSERT INTO permissions (name, code, description, icon, type, parent_id, sort_order, status) 
VALUES ('系统设置', 'system:menu', '系统配置管理', 'bi-gear', 'MENU', NULL, 6, true);

-- 2. 添加子菜单
INSERT INTO permissions (name, code, description, icon, type, parent_id, sort_order, status) 
VALUES ('基础设置', 'system:basic', '基础参数配置', 'bi-sliders', 'MENU', 
        (SELECT id FROM permissions WHERE code = 'system:menu'), 1, true);

INSERT INTO permissions (name, code, description, icon, type, parent_id, sort_order, status) 
VALUES ('邮件配置', 'system:email', '邮件服务配置', 'bi-envelope', 'MENU', 
        (SELECT id FROM permissions WHERE code = 'system:menu'), 2, true);

-- 3. 分配给管理员角色
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id 
FROM roles r, permissions p
WHERE r.name = 'ADMIN' 
  AND p.code IN ('system:menu', 'system:basic', 'system:email');
```

### 示例2：前端动态菜单渲染

```html
<!-- sidebar.html -->
<nav class="sidebar">
    <div th:each="menu : ${menus}" class="menu-group">
        <!-- 一级菜单 -->
        <a th:href="@{${menu.url}}" 
           class="menu-link"
           perm:hasPermission="${menu.code}">
            <i th:class="${menu.icon}"></i>
            <span th:text="${menu.name}"></span>
        </a>
        
        <!-- 二级菜单（如果有） -->
        <ul th:if="${menu.children != null and !menu.children.isEmpty()}" 
            class="submenu">
            <li th:each="child : ${menu.children}">
                <a th:href="@{${child.url}}" 
                   perm:hasPermission="${child.code}">
                    <i th:if="${child.icon}" th:class="${child.icon}"></i>
                    <span th:text="${child.name}"></span>
                </a>
            </li>
        </ul>
    </div>
</nav>
```

### 示例3：按钮级权限控制

```html
<!-- user-list.html -->
<div class="toolbar">
    <!-- 只有拥有 user:create 权限的用户才能看到此按钮 -->
    <button perm:hasPermission="user:create" 
            onclick="showCreateModal()"
            class="btn btn-primary">
        <i class="bi bi-plus-lg"></i>
        新建用户
    </button>
    
    <!-- 批量删除按钮 -->
    <button perm:hasPermission="user:delete" 
            onclick="batchDelete()"
            class="btn btn-danger">
        <i class="bi bi-trash"></i>
        批量删除
    </button>
</div>

<table>
    <tr th:each="user : ${users}">
        <td th:text="${user.username}"></td>
        <td>
            <!-- 编辑按钮 -->
            <button perm:hasPermission="user:edit" 
                    th:onclick="'editUser(' + ${user.id} + ')'"
                    class="btn btn-sm btn-warning">
                <i class="bi bi-pencil"></i>
            </button>
            
            <!-- 删除按钮 -->
            <button perm:hasPermission="user:delete" 
                    th:onclick="'deleteUser(' + ${user.id} + ')'"
                    class="btn btn-sm btn-danger">
                <i class="bi bi-trash"></i>
            </button>
        </td>
    </tr>
</table>
```

## 🔍 查询示例

### 查看所有菜单层级

```sql
SELECT 
    p1.id AS menu_id,
    p1.name AS menu_name,
    p1.icon AS menu_icon,
    p1.sort_order AS menu_sort,
    p2.id AS sub_id,
    p2.name AS sub_name,
    p2.type AS sub_type,
    p2.icon AS sub_icon,
    p2.sort_order AS sub_sort
FROM permissions p1
LEFT JOIN permissions p2 ON p2.parent_id = p1.id
WHERE p1.parent_id IS NULL
ORDER BY p1.sort_order, p2.sort_order;
```

### 查看某个角色的所有权限

```sql
SELECT 
    r.name AS role_name,
    p.name AS permission_name,
    p.code AS permission_code,
    p.type AS permission_type,
    p.icon AS permission_icon
FROM roles r
JOIN role_permissions rp ON r.id = rp.role_id
JOIN permissions p ON rp.permission_id = p.id
WHERE r.name = 'ADMIN'
ORDER BY p.sort_order;
```

### 查看用户的完整菜单树

```sql
SELECT DISTINCT
    p1.id AS menu_id,
    p1.name AS menu_name,
    p1.icon AS menu_icon,
    p1.sort_order AS menu_sort,
    p2.id AS sub_id,
    p2.name AS sub_name,
    p2.type AS sub_type,
    p2.icon AS sub_icon,
    p2.sort_order AS sub_sort
FROM users u
JOIN user_roles ur ON u.id = ur.user_id
JOIN roles r ON ur.role_id = r.id
JOIN role_permissions rp ON r.id = rp.role_id
JOIN permissions p1 ON rp.permission_id = p1.id
LEFT JOIN permissions p2 ON p2.parent_id = p1.id
WHERE u.username = 'admin'
  AND p1.status = true
  AND p1.type = 'MENU'
  AND p1.parent_id IS NULL
ORDER BY p1.sort_order, p2.sort_order;
```

## 📈 性能优化建议

### 1. 菜单缓存

```java
@Service
public class MenuService {
    
    @Cacheable(value = "userMenus", key = "#userId")
    public List<MenuVO> getUserMenus(Long userId) {
        // 查询并构建菜单树
        return buildMenuTree(userId);
    }
    
    @CacheEvict(value = "userMenus", allEntries = true)
    public void clearMenuCache() {
        // 权限变更时清除缓存
    }
}
```

### 2. 数据库索引

```sql
-- 为常用查询字段添加索引
CREATE INDEX idx_permissions_parent_id ON permissions(parent_id);
CREATE INDEX idx_permissions_type ON permissions(type);
CREATE INDEX idx_permissions_status ON permissions(status);
CREATE INDEX idx_permissions_sort_order ON permissions(sort_order);
CREATE INDEX idx_role_permissions_role_id ON role_permissions(role_id);
CREATE INDEX idx_role_permissions_permission_id ON role_permissions(permission_id);
```

## ✅ 检查清单

在添加新菜单前，请确认：

- [ ] 权限编码（code）唯一且不重复
- [ ] 设置了正确的父级ID（parentId）
- [ ] 配置了合适的排序号（sortOrder）
- [ ] 选择了恰当的图标（icon）
- [ ] 正确设置了类型（MENU或BUTTON）
- [ ] 将权限分配给了相应的角色
- [ ] 测试不同角色用户的菜单显示
- [ ] 验证按钮级权限控制是否生效

## 🔗 相关链接

- [Bootstrap Icons 官网](https://icons.getbootstrap.com/)
- [Thymeleaf 官方文档](https://www.thymeleaf.org/)
- [Spring Security 权限控制](https://spring.io/projects/spring-security)
- [菜单配置说明文档](MENU_PERMISSION_CONFIG_GUIDE.md)
