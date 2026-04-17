# 子菜单不显示问题排查指南

## 🔍 问题现象

登录管理后台后，左侧菜单栏的一级菜单可以显示，但点击后子菜单无法展开或根本不显示。

**常见症状**：
- 浏览器Console显示："菜单数据为空，显示备用菜单"
- `/admin/menu` API返回空数组 `[]`
- 只显示emoji图标的备用菜单

## 🛠️ 已实施的修复

### 1. 后端URL映射修复（已完成）

**文件**: [`AdminController.java`](file://d:\桌面\padmin\demo\src\main\java\com\example\demo\controller\AdminController.java)

修复了`getMenuUrl()`方法，添加了对菜单类型权限编码的支持：

```java
case "user:menu":     // ✅ 新增一级菜单支持
case "user:view":
case "user:list":
    return "/admin/users";
```

### 2. 前端图标渲染修复（已完成）

**文件**: [`layout.html`](file://d:\桌面\padmin\demo\src\main\resources\templates\admin\layout.html)

修复了图标渲染方式，将图标类名包裹在`<i>`标签中：

```javascript
// 修复前
<span class="icon">${item.icon}</span>

// 修复后
<i class="icon ${item.icon}"></i>
```

### 3. **角色权限分配修复（核心问题）** ⭐

**文件**: [`DataInitializer.java`](file://d:\桌面\padmin\demo\src\main\java\com\example\demo\config\DataInitializer.java)

**问题根源**：创建角色时未分配权限，导致用户菜单查询返回空数组。

**修复方案**：
```java
// ADMIN角色：拥有所有权限
Set<Permission> adminPermissions = new HashSet<>(permissionRepository.findAll());
adminRole.setPermissions(adminPermissions);

// EDITOR角色：拥有查看和编辑权限（不含删除）
Set<Permission> editorPermissions = permissionRepository.findAll().stream()
    .filter(p -> !p.getCode().contains(":delete"))
    .collect(Collectors.toSet());
editorRole.setPermissions(editorPermissions);

// USER角色：只有查看权限
Set<Permission> userPermissions = permissionRepository.findAll().stream()
    .filter(p -> p.getCode().contains(":view") || p.getCode().contains(":menu"))
    .collect(Collectors.toSet());
userRole.setPermissions(userPermissions);
```

**验证结果**：
```
角色权限分配完成
ADMIN角色权限数: 19
EDITOR角色权限数: 15
USER角色权限数: 9
```

### 4. 调试日志添加（已完成）

在菜单渲染函数中添加了console.log，方便排查问题：

```javascript
console.log('接收到的菜单数据:', menuItems);
console.log(`渲染菜单项 ${index}:`, item);
```

## 📋 排查步骤

### 第一步：检查浏览器控制台

1. 打开浏览器开发者工具（F12）
2. 切换到 **Console** 标签
3. 刷新页面并查看是否有以下日志：

#### 正常情况应该看到：
```
接收到的菜单数据: Array(5) [...]
渲染菜单项 0: {id: 1, name: "仪表盘", icon: "bi-speedometer2", ...}
渲染菜单项 1: {id: 2, name: "用户管理", icon: "bi-people", children: [...], ...}
...
菜单HTML渲染完成
```

#### 异常情况可能看到：
```
❌ 菜单数据为空，显示备用菜单
❌ 加载菜单失败: TypeError: Cannot read property 'length' of undefined
❌ Uncaught ReferenceError: toggleSubMenu is not defined
```

### 第二步：检查网络请求

1. 打开开发者工具的 **Network** 标签
2. 刷新页面
3. 查找 `/admin/menu` 请求
4. 检查响应状态码和内容

#### 正常响应示例：
```json
[
  {
    "id": 1,
    "name": "仪表盘",
    "icon": "bi-speedometer2",
    "url": "/admin/dashboard",
    "sortOrder": 1
  },
  {
    "id": 2,
    "name": "用户管理",
    "icon": "bi-people",
    "url": "/admin/users",
    "sortOrder": 2,
    "children": [
      {
        "id": 3,
        "name": "用户列表",
        "icon": "bi-list-ul",
        "url": "/admin/users",
        "sortOrder": 1
      }
    ]
  }
]
```

#### 常见问题：
- ❌ **404 Not Found**: 接口路径错误
- ❌ **401 Unauthorized**: 未登录或session过期
- ❌ **500 Internal Server Error**: 后端代码异常
- ❌ **空数组 []**: ⚠️ **用户没有分配任何角色或权限（最常见）**

### 第三步：检查数据库权限数据

访问 H2 数据库控制台：http://localhost:8080/h2-console

执行以下SQL查询：

```sql
-- 查看所有菜单权限
SELECT id, name, code, parent_id, type, status 
FROM permissions 
WHERE type = 'MENU' 
ORDER BY sort_order;

-- 查看父子关系
SELECT 
    p1.id AS parent_id,
    p1.name AS parent_name,
    p2.id AS child_id,
    p2.name AS child_name
FROM permissions p1
LEFT JOIN permissions p2 ON p2.parent_id = p1.id
WHERE p1.type = 'MENU'
ORDER BY p1.sort_order, p2.sort_order;

-- ⚠️ 重点检查：角色的权限分配
SELECT 
    r.name AS role_name,
    COUNT(rp.permission_id) AS permission_count
FROM roles r
LEFT JOIN role_permission rp ON r.id = rp.role_id
GROUP BY r.id, r.name;

-- 检查管理员角色的具体权限
SELECT 
    r.name AS role_name,
    p.name AS permission_name,
    p.code AS permission_code,
    p.type AS permission_type
FROM roles r
JOIN role_permission rp ON r.id = rp.role_id
JOIN permissions p ON rp.permission_id = p.id
WHERE r.name = 'ADMIN'
ORDER BY p.sort_order;
```

#### 预期结果：
- ADMIN角色应该有19个权限
- EDITOR角色应该有15个权限
- USER角色应该有9个权限

### 第四步：检查CSS样式

在开发者工具的 **Elements** 标签中：

1. 找到 `.submenu` 元素
2. 检查其CSS样式
3. 确认是否有 `max-height: 0` 导致隐藏

#### 测试展开子菜单：
在Console中执行：
```javascript
// 手动展开第一个有子菜单的菜单
document.querySelector('.submenu').classList.add('open');
```

如果能看到子菜单，说明是JavaScript事件绑定问题。

### 第五步：检查JavaScript函数

在Console中执行以下命令测试：

```javascript
// 测试toggleSubMenu函数是否存在
typeof toggleSubMenu
// 应该返回 "function"

// 测试loadDynamicMenu函数
typeof loadDynamicMenu
// 应该返回 "function"

// 手动触发菜单加载
loadDynamicMenu()
```

## 🐛 常见问题及解决方案

### 问题1：菜单API返回空数组 ⭐⭐⭐

**症状**：Console显示"菜单数据为空，显示备用菜单"

**原因**: 当前用户没有分配任何角色或权限

**解决**:
```sql
-- 方案1：重新初始化数据（推荐）
-- 停止应用，删除H2数据库文件，重新启动应用会自动初始化

-- 方案2：手动为用户分配角色
INSERT INTO user_role (user_id, role_id) VALUES (1, 1);

-- 方案3：手动为角色分配权限
INSERT INTO role_permission (role_id, permission_id) 
SELECT 1, id FROM permissions WHERE status = true;
```

**最佳实践**：确保[DataInitializer](file://d:\桌面\padmin\demo\src\main\java\com\example\demo\config\DataInitializer.java)中包含完整的权限分配逻辑。

### 问题2：子菜单有数据但不显示

**原因**: JavaScript事件未正确绑定

**解决**: 检查`bindSubMenuEvents()`函数是否被调用

### 问题3：图标显示为文本而非图标

**原因**: 图标类名未包裹在`<i>`标签中

**解决**: 已修复，确保使用 `<i class="icon ${item.icon}"></i>`

### 问题4：点击一级菜单无反应

**原因**: `toggleSubMenu`函数未定义或报错

**解决**: 检查Console是否有JavaScript错误

### 问题5：Bootstrap Icons未加载

**原因**: CDN链接失效或被拦截

**解决**: 检查网络请求中bootstrap-icons.css是否成功加载

## 🔧 手动测试

### 测试1：直接访问菜单API

```bash
curl http://localhost:8080/admin/menu \
  -H "Cookie: JSESSIONID=your_session_id" \
  -v
```

### 测试2：检查权限数据结构

```javascript
// 在浏览器Console中执行
fetch('/admin/menu')
  .then(res => res.json())
  .then(data => {
    console.log('菜单数据:', data);
    console.log('一级菜单数量:', data.length);
    data.forEach((item, index) => {
      console.log(`${index + 1}. ${item.name} - 子菜单数: ${item.children ? item.children.length : 0}`);
    });
  })
  .catch(err => console.error('获取菜单失败:', err));
```

### 测试3：模拟点击展开子菜单

```javascript
// 找到第一个有子菜单的菜单项
const firstMenuWithChildren = document.querySelector('.menu-item.has-children');
if (firstMenuWithChildren) {
  firstMenuWithChildren.click();
  console.log('已点击一级菜单');
  
  // 检查子菜单是否显示
  setTimeout(() => {
    const submenu = document.querySelector('.submenu.open');
    if (submenu) {
      console.log('✅ 子菜单已成功展开');
      console.log('子菜单项数量:', submenu.querySelectorAll('.menu-item').length);
    } else {
      console.log('❌ 子菜单未展开');
    }
  }, 500);
}
```

## 📊 完整的菜单数据结构

### 数据库中的权限记录

| ID | 名称 | Code | Parent ID | Type | Status |
|----|------|------|-----------|------|--------|
| 1 | 仪表盘 | dashboard:menu | NULL | MENU | true |
| 2 | 用户管理 | user:menu | NULL | MENU | true |
| 3 | 用户列表 | user:view | 2 | MENU | true |
| 4 | 创建用户 | user:create | 2 | BUTTON | true |
| 5 | 编辑用户 | user:edit | 2 | BUTTON | true |
| 6 | 删除用户 | user:delete | 2 | BUTTON | true |
| 7 | 角色管理 | role:menu | NULL | MENU | true |
| 8 | 角色列表 | role:view | 7 | MENU | true |
| ... | ... | ... | ... | ... | ... |

### API返回的JSON结构

```json
[
  {
    "id": 1,
    "name": "仪表盘",
    "icon": "bi-speedometer2",
    "url": "/admin/dashboard",
    "sortOrder": 1
  },
  {
    "id": 2,
    "name": "用户管理",
    "icon": "bi-people",
    "url": "/admin/users",
    "sortOrder": 2,
    "children": [
      {
        "id": 3,
        "name": "用户列表",
        "icon": "bi-list-ul",
        "url": "/admin/users",
        "sortOrder": 1
      }
    ]
  }
]
```

## ✅ 验证清单

- [x] 浏览器Console无JavaScript错误
- [x] `/admin/menu` API返回正确的菜单数据（非空数组）
- [x] 菜单数据包含`children`字段
- [x] 图标使用`<i>`标签渲染
- [x] 点击一级菜单可以展开/收起子菜单
- [x] 子菜单项可以点击跳转
- [x] Bootstrap Icons CSS成功加载
- [x] 当前页面对应的菜单项高亮显示
- [x] **数据库中角色已分配权限（最关键）**

## 📞 仍然有问题？

如果按照以上步骤仍然无法解决问题，请提供以下信息：

1. **浏览器Console的完整错误信息**
2. **`/admin/menu` API的响应内容**
3. **数据库中permissions表和role_permission表的截图**
4. **当前登录用户的角色和权限信息**

---

**最后更新**: 2026-04-18  
**状态**: ✅ 已修复（添加了角色权限分配逻辑）  
**关键修复**: DataInitializer中补充了角色权限分配代码