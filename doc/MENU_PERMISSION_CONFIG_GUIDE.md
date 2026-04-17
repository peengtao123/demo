# 菜单权限配置说明

## 📋 概述

本文档详细说明系统中菜单权限的层级结构、图标配置和排序规则。

## 🎯 菜单结构设计

### 一级菜单（5个）

| 序号 | 菜单名称 | 权限编码 | 图标 | 说明 |
|------|---------|---------|------|------|
| 1 | 仪表盘 | `dashboard:menu` | bi-speedometer2 | 系统主控制面板 |
| 2 | 用户管理 | `user:menu` | bi-people | 用户相关功能管理 |
| 3 | 角色管理 | `role:menu` | bi-shield-lock | 角色和权限管理 |
| 4 | 权限管理 | `permission:menu` | bi-key | 权限配置管理 |
| 5 | 审计日志 | `audit:menu` | bi-journal-text | 系统操作日志 |

### 二级菜单和功能按钮

#### 1. 用户管理子项

| 类型 | 名称 | 权限编码 | 图标 | 排序 |
|------|------|---------|------|------|
| MENU | 用户列表 | `user:view` | bi-list-ul | 1 |
| BUTTON | 创建用户 | `user:create` | - | 2 |
| BUTTON | 编辑用户 | `user:edit` | - | 3 |
| BUTTON | 删除用户 | `user:delete` | - | 4 |

#### 2. 角色管理子项

| 类型 | 名称 | 权限编码 | 图标 | 排序 |
|------|------|---------|------|------|
| MENU | 角色列表 | `role:view` | bi-list-ul | 1 |
| BUTTON | 创建角色 | `role:create` | - | 2 |
| BUTTON | 编辑角色 | `role:edit` | - | 3 |
| BUTTON | 删除角色 | `role:delete` | - | 4 |

#### 3. 权限管理子项

| 类型 | 名称 | 权限编码 | 图标 | 排序 |
|------|------|---------|------|------|
| MENU | 权限列表 | `permission:view` | bi-list-ul | 1 |
| BUTTON | 创建权限 | `permission:create` | - | 2 |
| BUTTON | 编辑权限 | `permission:edit` | - | 3 |
| BUTTON | 删除权限 | `permission:delete` | - | 4 |

#### 4. 审计日志子项

| 类型 | 名称 | 权限编码 | 图标 | 排序 |
|------|------|---------|------|------|
| MENU | 日志列表 | `audit:view` | bi-list-ul | 1 |
| BUTTON | 删除日志 | `audit:delete` | - | 2 |

## 🔧 配置字段说明

### Permission实体字段

```java
@Entity
@Table(name = "permissions")
public class Permission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;              // 权限ID（自增）
    
    private String name;          // 权限名称（显示名称）
    private String code;          // 权限编码（唯一标识）
    private String description;   // 权限描述
    private String icon;          // 图标类名（Bootstrap Icons）
    private Boolean status;       // 状态（true=启用，false=禁用）
    private Long parentId;        // 父级ID（NULL表示一级菜单）
    private Integer sortOrder;    // 排序号（数字越小越靠前）
    private String type;          // 类型（MENU=菜单，BUTTON=按钮）
}
```

### 字段详细说明

#### 1. type（类型）
- **MENU**: 菜单类型，会在侧边栏显示
- **BUTTON**: 按钮类型，用于页面内的功能按钮权限控制

#### 2. icon（图标）
使用 [Bootstrap Icons](https://icons.getbootstrap.com/) 图标库，常用图标：
- `bi-speedometer2` - 仪表盘
- `bi-people` - 用户/人员
- `bi-shield-lock` - 安全/角色
- `bi-key` - 钥匙/权限
- `bi-journal-text` - 日志/文档
- `bi-list-ul` - 列表

#### 3. parentId（父级ID）
- `NULL`: 表示一级菜单
- 具体ID值: 表示该权限是某个菜单的子项

#### 4. sortOrder（排序）
- 数字越小，显示越靠前
- 一级菜单之间独立排序
- 二级菜单在各自父菜单下独立排序

## 📊 菜单层级关系图

```
系统菜单结构
├── 1. 仪表盘 (dashboard:menu)
│   └── [无子项]
│
├── 2. 用户管理 (user:menu)
│   ├── 用户列表 (user:view) - MENU
│   ├── 创建用户 (user:create) - BUTTON
│   ├── 编辑用户 (user:edit) - BUTTON
│   └── 删除用户 (user:delete) - BUTTON
│
├── 3. 角色管理 (role:menu)
│   ├── 角色列表 (role:view) - MENU
│   ├── 创建角色 (role:create) - BUTTON
│   ├── 编辑角色 (role:edit) - BUTTON
│   └── 删除角色 (role:delete) - BUTTON
│
├── 4. 权限管理 (permission:menu)
│   ├── 权限列表 (permission:view) - MENU
│   ├── 创建权限 (permission:create) - BUTTON
│   ├── 编辑权限 (permission:edit) - BUTTON
│   └── 删除权限 (permission:delete) - BUTTON
│
└── 5. 审计日志 (audit:menu)
    ├── 日志列表 (audit:view) - MENU
    └── 删除日志 (audit:delete) - BUTTON
```

## 💾 数据初始化方式

### 方式一：通过DataInitializer自动初始化（推荐）

系统启动时会自动执行 [`DataInitializer.java`](file://d:\桌面\padmin\demo\src\main\java\com\example\demo\config\DataInitializer.java) 中的初始化逻辑：

```bash
# 启动应用
mvn spring-boot:run
```

**优点**：
- 自动化，无需手动操作
- 包含完整的测试数据（用户、角色、权限）
- 适合开发和测试环境

### 方式二：执行SQL脚本

直接执行 [`init_menu_permissions.sql`](file://d:\桌面\padmin\demo\init_menu_permissions.sql) 文件：

```bash
# H2数据库控制台
# 访问 http://localhost:8080/h2-console
# JDBC URL: jdbc:h2:mem:testdb
# 用户名: sa
# 密码: （空）
# 然后执行SQL脚本
```

**优点**：
- 可以单独执行，不影响其他数据
- 适合生产环境或已有数据的场景

## 🎨 前端动态菜单渲染

### Thymeleaf模板示例

```html
<!-- 侧边栏菜单 -->
<div class="sidebar-menu">
    <div th:each="menu : ${menus}" class="menu-item">
        <!-- 一级菜单 -->
        <a th:href="@{${menu.url}}" 
           th:text="${menu.name}"
           perm:hasPermission="${menu.code}">
            <i th:class="${menu.icon}"></i>
            <span th:text="${menu.name}"></span>
        </a>
        
        <!-- 二级菜单 -->
        <div th:if="${menu.children != null and !menu.children.isEmpty()}" 
             class="submenu">
            <div th:each="child : ${menu.children}">
                <a th:href="@{${child.url}}" 
                   th:text="${child.name}"
                   perm:hasPermission="${child.code}">
                    <i th:class="${child.icon}"></i>
                    <span th:text="${child.name}"></span>
                </a>
            </div>
        </div>
    </div>
</div>
```

### Controller中构建菜单树

```java
@GetMapping("/admin/dashboard")
public String dashboard(Model model, Authentication authentication) {
    User currentUser = getCurrentUser(authentication);
    
    // 获取当前用户的所有权限
    List<Permission> permissions = permissionService.getUserPermissions(currentUser);
    
    // 构建菜单树
    List<MenuVO> menuTree = buildMenuTree(permissions);
    
    model.addAttribute("menus", menuTree);
    return "admin/dashboard";
}

private List<MenuVO> buildMenuTree(List<Permission> permissions) {
    // 过滤出所有菜单类型的权限
    List<Permission> menus = permissions.stream()
        .filter(p -> "MENU".equals(p.getType()))
        .sorted(Comparator.comparing(Permission::getSortOrder))
        .collect(Collectors.toList());
    
    // 构建树形结构
    List<MenuVO> rootMenus = new ArrayList<>();
    for (Permission menu : menus) {
        if (menu.getParentId() == null) {
            MenuVO menuVO = convertToVO(menu);
            // 查找子菜单
            List<MenuVO> children = menus.stream()
                .filter(m -> menu.getId().equals(m.getParentId()))
                .map(this::convertToVO)
                .collect(Collectors.toList());
            menuVO.setChildren(children);
            rootMenus.add(menuVO);
        }
    }
    return rootMenus;
}
```

## 🔐 权限控制最佳实践

### 1. 视图层权限控制（Thymeleaf自定义方言）

```html
<!-- 只有拥有 user:create 权限的用户才能看到"新建用户"按钮 -->
<button perm:hasPermission="user:create" 
        onclick="createUser()">
    新建用户
</button>

<!-- 没有权限时，该元素会被从DOM中移除 -->
```

### 2. API层权限控制（自定义注解 + 拦截器）

```java
@RestController
@RequestMapping("/api/users")
public class UserController {
    
    @PostMapping
    @RequirePermission("user:create")
    public ApiResponse createUser(@RequestBody UserDTO userDTO) {
        // 只有拥有 user:create 权限才能访问此接口
        return userService.create(userDTO);
    }
    
    @PutMapping("/{id}")
    @RequirePermission("user:edit")
    public ApiResponse updateUser(@PathVariable Long id, @RequestBody UserDTO userDTO) {
        // 只有拥有 user:edit 权限才能访问此接口
        return userService.update(id, userDTO);
    }
}
```

## 📝 扩展新菜单

### 步骤1：在数据库中插入新权限

```sql
-- 添加新的一级菜单
INSERT INTO permissions (name, code, description, icon, type, parent_id, sort_order, status) 
VALUES ('系统设置', 'system:menu', '系统配置管理', 'bi-gear', 'MENU', NULL, 6, true);

-- 添加子菜单
INSERT INTO permissions (name, code, description, icon, type, parent_id, sort_order, status) 
VALUES ('基础设置', 'system:basic', '基础参数配置', 'bi-sliders', 'MENU', 
        (SELECT id FROM permissions WHERE code = 'system:menu'), 1, true);
```

### 步骤2：更新DataInitializer（可选）

如果希望在应用重启后仍然保持新菜单，需要更新 [`DataInitializer.java`](file://d:\桌面\padmin\demo\src\main\java\com\example\demo\config\DataInitializer.java)。

### 步骤3：分配权限给角色

```sql
-- 将新权限分配给管理员角色
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id 
FROM roles r, permissions p
WHERE r.name = 'ADMIN' 
  AND p.code IN ('system:menu', 'system:basic');
```

## 🚀 快速开始

1. **启动应用**
   ```bash
   mvn spring-boot:run
   ```

2. **访问系统**
   - 登录地址: http://localhost:8080/login
   - 管理员账号: admin / admin123

3. **查看菜单**
   - 登录后即可看到完整的菜单结构
   - 不同角色的用户会看到不同的菜单项

4. **管理权限**
   - 访问: http://localhost:8080/admin/permissions
   - 可以查看、编辑、新增权限

## 📌 注意事项

1. **权限编码唯一性**: 每个权限的 `code` 字段必须唯一
2. **父子关系**: 确保 `parentId` 指向存在的权限ID
3. **排序规则**: `sortOrder` 数值越小越靠前
4. **图标选择**: 建议使用 Bootstrap Icons，保持一致性
5. **类型区分**: MENU类型用于导航，BUTTON类型用于功能按钮
6. **状态管理**: `status=false` 的权限不会显示在菜单中

## 🔗 相关文档

- [高级权限控制指南](ADVANCED_PERMISSION_GUIDE.md)
- [动态菜单和审计日志指南](DYNAMIC_MENU_AND_AUDIT_LOG_GUIDE.md)
- [角色权限测试指南](TEST_ROLE_PERMISSION.md)
