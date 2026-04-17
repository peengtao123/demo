# 用户角色分配功能使用说明

## 📋 功能概述

本系统支持为用户分配一个或多个角色，实现灵活的权限管理。

## ✨ 主要特性

- ✅ **多角色支持**：一个用户可以同时拥有多个角色
- ✅ **可视化选择**：使用复选框界面，直观易用
- ✅ **实时反馈**：显示角色图标、名称和描述
- ✅ **审计日志**：记录所有角色分配操作
- ✅ **自动加载**：编辑用户时自动选中已有角色

## 🔧 使用方法

### 1. 新建用户时分配角色

1. 访问 **用户管理** → **新建用户**
2. 填写用户基本信息（用户名、邮箱、姓名等）
3. 在 **🎭 角色分配** 区域选择一个或多个角色
4. 点击 **💾 保存用户**

### 2. 编辑用户时修改角色

1. 访问 **用户管理** → 点击用户列表中的 **编辑** 按钮
2. 在 **🎭 角色分配** 区域修改角色选择
   - ✅ 勾选 = 添加该角色
   - ⬜ 取消勾选 = 移除该角色
3. 点击 **💾 保存用户**

### 3. 查看用户角色

1. 访问 **用户管理** → 点击用户列表中的 **详情** 按钮
2. 在 **🎭 角色信息** 部分查看用户的所有角色
3. 每个角色显示：
   - 角色名称
   - 角色描述

## 🎨 界面说明

### 角色选择界面

```
┌─────────────────────────────────────┐
│ 🎭 角色分配                          │
├─────────────────────────────────────┤
│ ☑️ 👑 ADMIN                         │
│    系统管理员，拥有所有权限           │
│                                     │
│ ☑️ ✏️ EDITOR                        │
│    内容编辑者，可以创建和编辑内容     │
│                                     │
│ ⬜ 👤 USER                           │
│    普通用户，只有查看权限             │
└─────────────────────────────────────┘
至少选择一个角色，可多选
```

### 交互效果

- **鼠标悬停**：边框变为紫色，背景变浅蓝
- **选中状态**：复选框打勾
- **提示信息**：底部显示"至少选择一个角色，可多选"

## 📊 默认角色说明

| 角色 | 图标 | 权限数量 | 说明 |
|------|------|---------|------|
| **ADMIN** | 👑 | 19个 | 系统管理员，拥有所有权限（增删改查） |
| **EDITOR** | ✏️ | 15个 | 内容编辑者，可以创建和编辑，但不能删除 |
| **USER** | 👤 | 9个 | 普通用户，只有查看权限 |

## 🔍 技术实现

### 后端实现

**AdminController.java**

```java
@PostMapping("/users/save")
public String saveUser(@ModelAttribute User user, 
                      @RequestParam(required = false) List<Long> roleIds,
                      RedirectAttributes redirectAttributes) {
    // 1. 创建或更新用户基本信息
    if (user.getId() == null) {
        User savedUser = userService.createUser(userDTO);
        // 2. 分配角色
        if (roleIds != null && !roleIds.isEmpty()) {
            assignRolesToUser(savedUser.getId(), roleIds);
        }
    } else {
        userService.updateUserInfo(user.getId(), user);
        // 3. 更新角色分配
        if (roleIds != null) {
            assignRolesToUser(user.getId(), roleIds);
        }
    }
}

private void assignRolesToUser(Long userId, List<Long> roleIds) {
    User user = userService.getUserById(userId);
    Set<Role> roles = new HashSet<>();
    
    for (Long roleId : roleIds) {
        Role role = roleService.getRoleById(roleId)
                .orElseThrow(() -> new RuntimeException("角色不存在: " + roleId));
        roles.add(role);
    }
    
    user.setRoles(roles);
    userRepository.save(user);
    
    // 记录审计日志
    auditLogService.log(...);
}
```

### 前端实现

**form.html**

```html
<div class="form-group">
    <label>选择角色 <span class="required">*</span></label>
    <div style="display: grid; grid-template-columns: repeat(auto-fill, minmax(200px, 1fr)); gap: 10px;">
        <div th:each="role : ${roles}" class="role-checkbox-item">
            <label>
                <input type="checkbox" name="roleIds" th:value="${role.id}" 
                       th:checked="${user.roles != null and user.roles.contains(role)}" />
                <div>
                    <div>
                        <i th:class="${role.icon}"></i>
                        <span th:text="${role.name}">角色名称</span>
                    </div>
                    <div th:text="${role.description}">角色描述</div>
                </div>
            </label>
        </div>
    </div>
</div>
```

## 🛡️ 安全考虑

1. **权限验证**：只有具有用户管理权限的管理员才能分配角色
2. **审计日志**：所有角色分配操作都会记录到审计日志
3. **数据校验**：后端验证角色ID的有效性，防止无效数据

## 📝 注意事项

1. **至少选择一个角色**：建议为每个用户至少分配一个角色，否则用户可能无法登录
2. **多角色权限叠加**：用户拥有多个角色时，权限是所有角色权限的并集
3. **角色变更生效**：角色分配后立即生效，无需重新登录
4. **默认角色**：如果未分配角色，新用户默认没有权限

## 🔗 相关功能

- [角色管理](./ROLE_MANAGEMENT.md) - 创建和管理角色
- [权限管理](./PERMISSION_MANAGEMENT.md) - 定义系统权限
- [菜单权限配置](./MENU_PERMISSION_CONFIG_GUIDE.md) - 配置菜单显示规则

## 🐛 常见问题

### Q1: 为什么用户登录后看不到菜单？

**A**: 检查以下几点：
1. 确认用户已分配角色
2. 确认角色已分配权限（特别是 `*:menu` 类型的菜单权限）
3. 刷新浏览器页面（Ctrl+F5）

### Q2: 如何批量修改多个用户的角色？

**A**: 当前版本不支持批量修改角色，需要逐个编辑用户。未来版本可能会添加此功能。

### Q3: 删除角色后，已分配该角色的用户会怎样？

**A**: 删除角色前系统会检查是否有用户使用该角色，如果有则不允许删除。需要先修改这些用户的角色分配。

## 📞 技术支持

如有问题，请查看：
- 浏览器控制台（F12）的错误信息
- 后端应用日志
- 审计日志中的角色分配记录

---

**最后更新**: 2026-04-18
**版本**: v1.0
