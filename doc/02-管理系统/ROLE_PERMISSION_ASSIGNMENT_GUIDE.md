# 角色权限分配功能使用说明

## 📋 功能概述

本系统支持为角色分配一个或多个权限，实现灵活的RBAC（基于角色的访问控制）权限管理。

## ✨ 主要特性

- ✅ **多权限支持**：一个角色可以同时拥有多个权限
- ✅ **可视化选择**：使用复选框界面，直观易用
- ✅ **全选功能**：一键全选/取消全选所有权限
- ✅ **实时反馈**：显示权限图标、名称、code和描述
- ✅ **审计日志**：记录所有权限分配操作
- ✅ **自动加载**：编辑角色时自动选中已有权限

## 🔧 使用方法

### 1. 新建角色时分配权限

1. 访问 **角色管理** → **新建角色**
2. 填写角色基本信息（名称、描述）
3. 在 **🔑 权限分配** 区域选择一个或多个权限
   - 可以逐个勾选
   - 也可以使用"全选/取消全选"快速操作
4. 点击 **💾 保存角色**

### 2. 编辑角色时修改权限

1. 访问 **角色管理** → 点击角色列表中的 **编辑** 按钮
2. 在 **🔑 权限分配** 区域修改权限选择
   - ✅ 勾选 = 添加该权限
   - ⬜ 取消勾选 = 移除该权限
3. 点击 **💾 保存角色**

### 3. 查看角色权限

1. 访问 **角色管理** → 点击角色列表中的 **详情** 按钮
2. 在 **🔑 已分配权限** 部分查看角色的所有权限
3. 每个权限显示：
   - 权限图标
   - 权限名称
   - 权限code（用于后端校验）
   - 权限描述

## 🎨 界面说明

### 权限选择界面

```
┌─────────────────────────────────────────────┐
│ 🔑 权限分配                                  │
├─────────────────────────────────────────────┤
│ ☑️ 全选 / 取消全选                           │
├─────────────────────────────────────────────┤
│ ☑️ 👥 user:view                             │
│    查看用户列表                               │
│                                             │
│ ☑️ 👥 user:create                           │
│    创建新用户                                 │
│                                             │
│ ⬜ 👥 user:delete                            │
│    删除用户                                   │
│                                             │
│ ☑️ 🛡️ role:menu                             │
│    角色管理菜单入口                           │
└─────────────────────────────────────────────┘
💡 提示：勾选的权限将分配给该角色，用户拥有该角色后将获得相应权限
```

### 交互效果

- **鼠标悬停**：边框变为紫色，背景变浅蓝
- **选中状态**：复选框打勾
- **全选按钮**：
  - ✅ 全部勾选 = 全选状态
  - ⬜ 全部未勾选 = 未选状态
  - ◑ 部分勾选 = 半选状态（indeterminate）

### 权限卡片样式

```
┌──────────────────────────┐
│ 👥 查看用户列表           │
│ user:view                │
│ 允许查看系统中的用户信息   │
└──────────────────────────┘
```

## 📊 权限类型说明

| 权限类型 | Code格式 | 说明 | 示例 |
|---------|----------|------|------|
| **菜单权限** | `module:menu` | 控制菜单项是否显示 | `user:menu`, `role:menu` |
| **查看权限** | `module:view` | 控制查看功能 | `user:view`, `role:view` |
| **创建权限** | `module:create` | 控制创建功能 | `user:create`, `role:create` |
| **编辑权限** | `module:edit` | 控制编辑功能 | `user:edit`, `role:edit` |
| **删除权限** | `module:delete` | 控制删除功能 | `user:delete`, `role:delete` |

## 🔍 技术实现

### 后端实现

**AdminController.java**

```java
@PostMapping("/roles/save")
public String saveRole(@ModelAttribute Role role, 
                      @RequestParam(required = false) List<Long> permissionIds,
                      RedirectAttributes redirectAttributes) {
    try {
        if (role.getId() == null) {
            // 创建新角色
            Role savedRole = roleService.createRole(role);
            
            // 分配权限
            if (permissionIds != null && !permissionIds.isEmpty()) {
                roleService.assignPermissions(savedRole.getId(), permissionIds);
            }
            
            redirectAttributes.addFlashAttribute("success", "角色创建成功");
        } else {
            // 更新角色
            roleService.updateRole(role.getId(), role);
            
            // 更新权限
            if (permissionIds != null) {
                roleService.assignPermissions(role.getId(), permissionIds);
            }
            
            redirectAttributes.addFlashAttribute("success", "角色更新成功");
        }
    } catch (Exception e) {
        redirectAttributes.addFlashAttribute("error", "操作失败: " + e.getMessage());
    }
    return "redirect:/admin/roles";
}
```

**RoleService.java**

```java
@Transactional
public void assignPermissions(Long roleId, List<Long> permissionIds) {
    Role role = roleRepository.findById(roleId)
            .orElseThrow(() -> new RuntimeException("角色不存在"));
    
    Set<Permission> permissions = new HashSet<>();
    for (Long permissionId : permissionIds) {
        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new RuntimeException("权限不存在: " + permissionId));
        permissions.add(permission);
    }
    
    role.setPermissions(permissions);
    roleRepository.save(role);
    
    // 记录审计日志
    auditLogService.log(...);
}
```

### 前端实现

**form.html**

```html
<!-- 全选按钮 -->
<input type="checkbox" id="selectAllPermissions" />

<!-- 权限列表 -->
<div th:each="perm : ${allPermissions}" class="permission-item">
    <label>
        <input type="checkbox" name="permissionIds" th:value="${perm.id}" 
               th:checked="${role.permissions != null and role.permissions.contains(perm)}"
               class="permission-checkbox" />
        <div>
            <div>
                <i th:class="${perm.icon}"></i>
                <span th:text="${perm.name}">权限名称</span>
            </div>
            <div>
                <code th:text="${perm.code}">permission:code</code>
            </div>
        </div>
    </label>
</div>

<script>
// 全选/取消全选功能
document.getElementById('selectAllPermissions').addEventListener('change', function() {
    const isChecked = this.checked;
    document.querySelectorAll('.permission-checkbox').forEach(function(checkbox) {
        checkbox.checked = isChecked;
    });
});
</script>
```

**detail.html**

```html
<div th:if="${role.permissions != null and !role.permissions.empty}">
    <div class="detail-label">🔑 已分配权限:</div>
    <div th:each="perm : ${role.permissions}">
        <i th:class="${perm.icon}"></i>
        <span th:text="${perm.name}">权限名称</span>
        <code th:text="${perm.code}">permission:code</code>
    </div>
</div>
```

## 🛡️ 安全考虑

1. **权限验证**：只有具有角色管理权限的管理员才能分配权限
2. **审计日志**：所有权限分配操作都会记录到审计日志
3. **数据校验**：后端验证权限ID的有效性，防止无效数据
4. **事务保护**：权限分配操作在事务中执行，确保数据一致性

## 📝 注意事项

1. **菜单权限的重要性**：如果角色没有某个模块的`:menu`权限，即使用户有该模块的其他权限，也无法在菜单中看到该模块
2. **权限叠加**：用户拥有多个角色时，权限是所有角色权限的并集
3. **即时生效**：权限分配后立即生效，但前端页面可能需要刷新才能看到变化
4. **默认权限**：新建角色默认没有任何权限，需要手动分配

## 🔗 相关功能

- [用户角色分配](./USER_ROLE_ASSIGNMENT_GUIDE.md) - 为用户分配角色
- [动态菜单配置](./DYNAMIC_MENU_AND_AUDIT_LOG_GUIDE.md) - 基于权限的动态菜单
- [高级权限控制](./ADVANCED_PERMISSION_GUIDE.md) - 按钮级权限控制

## 🐛 常见问题

### Q1: 为什么分配了权限但菜单还是不显示？

**A**: 检查以下几点：
1. 确认角色是否有对应的`:menu`权限（如`user:menu`）
2. 确认用户是否分配了该角色
3. 刷新浏览器页面（Ctrl+F5）
4. 检查浏览器Console是否有错误

### Q2: 如何批量修改多个角色的权限？

**A**: 当前版本不支持批量修改权限，需要逐个编辑角色。未来版本可能会添加此功能。

### Q3: 删除权限后，已分配该权限的角色会怎样？

**A**: 删除权限前系统会检查是否有角色使用该权限，如果有则不允许删除。需要先修改这些角色的权限分配。

### Q4: 全选功能如何使用？

**A**: 
1. 点击顶部的"全选/取消全选"复选框
2. 所有权限会自动勾选或取消
3. 如果手动修改部分权限，全选按钮会显示半选状态（◑）

### Q5: 权限code的命名规范是什么？

**A**: 推荐使用 `模块:操作` 的格式，例如：
- `user:view` - 查看用户
- `user:create` - 创建用户
- `user:menu` - 用户管理菜单
- `role:delete` - 删除角色

## 📞 技术支持

如有问题，请查看：
- 浏览器控制台（F12）的错误信息
- 后端应用日志
- 审计日志中的权限分配记录

---

**最后更新**: 2026-04-18
**版本**: v1.0
