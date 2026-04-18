# 系统管理 - 个人信息功能

## 功能概述

为登录用户提供了个人信息管理和密码修改功能，用户可以：
- 查看和编辑个人基本信息（姓名、电话、年龄、头像、备注）
- 修改登录密码（需验证原密码）

## 功能特性

### 1. 个人信息管理
- **访问路径**: `/admin/profile`
- **模板位置**: `src/main/resources/templates/admin/profile/index.html`
- **可编辑字段**:
  - 姓名（必填）
  - 电话（可选）
  - 年龄（可选）
  - 头像URL（可选）
  - 备注（可选）
- **只读字段**:
  - 用户名（不可修改）
  - 邮箱（不可修改，需联系管理员）
  - 注册时间（显示用）

### 2. 密码修改
- **访问路径**: `/admin/profile/password`
- **模板位置**: `src/main/resources/templates/admin/profile/password.html`
- **安全要求**:
  - 必须提供正确的原密码
  - 新密码长度至少6位
  - 需要二次确认新密码
  - 实时密码强度检测（弱/中/强）
  - 实时密码一致性验证
- **修改成功后**: 自动退出登录，需要重新登录

## 技术实现

### 后端实现

#### UserService 新增方法

1. **updateCurrentUserProfile()** - 更新个人信息
   ```java
   public User updateCurrentUserProfile(Long userId, String name, String phone, 
                                        Integer age, String avatar, String remark)
   ```
   - 更新用户的基本信息
   - 记录审计日志（PROFILE_UPDATE操作）

2. **changePassword()** - 修改密码
   ```java
   public void changePassword(Long userId, String oldPassword, String newPassword)
   ```
   - 验证原密码是否正确
   - 加密并保存新密码
   - 记录审计日志（PASSWORD_CHANGE操作）
   - 包含IP地址记录

#### AdminController 新增方法

1. **profile()** - GET `/admin/profile`
   - 显示个人信息页面
   - 加载当前用户的详细信息

2. **updateProfile()** - POST `/admin/profile/update`
   - 处理个人信息更新请求
   - 重定向回个人信息页面并显示成功/失败消息

3. **changePasswordPage()** - GET `/admin/profile/password`
   - 显示修改密码页面

4. **changePassword()** - POST `/admin/profile/password/change`
   - 处理密码修改请求
   - 验证新密码一致性
   - 验证新密码长度
   - 调用UserService修改密码
   - 成功后跳转到登出页面

### 前端实现

#### 个人信息页面 (index.html)
- 响应式布局设计
- 头像显示（支持URL或首字母占位）
- 表单分组展示（两列布局）
- 禁用字段视觉提示（灰色背景）
- 操作按钮：保存修改、修改密码

#### 修改密码页面 (password.html)
- 密码要求提示框
- 实时密码强度检测
  - 弱：红色（长度<6或缺乏复杂度）
  - 中：橙色（有一定复杂度）
  - 强：绿色（高复杂度）
- 实时密码一致性验证
- 友好的错误提示

### 菜单集成

#### 动态菜单配置
在 `init_menu_permissions.sql` 中添加了系统管理菜单权限：

```sql
-- 一级菜单
INSERT INTO permissions (name, code, description, icon, type, parent_id, sort_order, status) 
VALUES ('系统管理', 'profile:menu', '个人信息和密码管理', 'bi-gear', 'MENU', NULL, 6, true);

-- 二级菜单
INSERT INTO permissions (name, code, description, icon, type, parent_id, sort_order, status) 
VALUES ('个人信息', 'profile:view', '查看和编辑个人信息', 'bi-person', 'MENU', 
        (SELECT id FROM permissions WHERE code = 'profile:menu'), 1, true);

INSERT INTO permissions (name, code, description, icon, type, parent_id, sort_order, status) 
VALUES ('修改密码', 'profile:password', '修改登录密码', 'bi-key', 'BUTTON', 
        (SELECT id FROM permissions WHERE code = 'profile:menu'), 2, true);
```

#### URL映射
在 `AdminController.getMenuUrl()` 方法中添加：
```java
case "profile:menu":
case "profile:view":
    return "/admin/profile";
```

#### 备用菜单
在 `layout.html` 的 `showFallbackMenu()` 函数中添加：
```javascript
<a href="/admin/profile" class="menu-item ${currentActiveMenu === 'profile' ? 'active' : ''}">
    <span class="icon">⚙️</span>
    <span class="label">系统管理</span>
</a>
```

## 安全特性

1. **身份验证**: 所有操作都需要用户登录
2. **权限控制**: 只能修改当前登录用户的信息
3. **密码验证**: 修改密码必须提供正确的原密码
4. **密码强度**: 前端实时检测密码强度，提示用户
5. **审计日志**: 所有修改操作都记录审计日志
6. **IP记录**: 密码修改操作记录客户端IP地址

## 用户体验

1. **友好提示**: 成功/失败消息清晰可见
2. **实时反馈**: 密码强度和一致性实时验证
3. **视觉设计**: 与系统整体风格保持一致（紫色渐变主题）
4. **响应式**: 适配不同屏幕尺寸
5. **操作引导**: 密码要求明确列出

## 相关文件

### 后端文件
- `src/main/java/com/example/demo/service/UserService.java` - 业务逻辑
- `src/main/java/com/example/demo/controller/AdminController.java` - 控制器

### 前端文件
- `src/main/resources/templates/admin/profile/index.html` - 个人信息页面
- `src/main/resources/templates/admin/profile/password.html` - 修改密码页面
- `src/main/resources/templates/admin/layout.html` - 布局文件（菜单）

### 配置文件
- `init_menu_permissions.sql` - 菜单权限初始化脚本

## 使用说明

### 访问个人信息
1. 登录系统
2. 点击左侧菜单"系统管理"
3. 进入个人信息页面
4. 修改需要更新的字段
5. 点击"保存修改"按钮

### 修改密码
1. 在个人信息页面点击"修改密码"按钮
2. 或直接访问 `/admin/profile/password`
3. 输入原密码
4. 输入新密码（注意强度提示）
5. 再次输入新密码确认
6. 点击"确认修改"
7. 修改成功后自动退出，需用新密码重新登录

## 注意事项

1. 邮箱和用户名不可自行修改，如需修改请联系管理员
2. 密码修改后需要重新登录
3. 建议使用强密码（包含大小写字母、数字和特殊字符）
4. 所有操作都会记录在审计日志中
5. 头像URL需要是有效的图片链接

## 后续优化建议

1. 添加头像上传功能（目前仅支持URL）
2. 添加密码历史记录，防止重复使用旧密码
3. 添加双因素认证（2FA）
4. 添加登录设备管理
5. 添加账户活动日志查看
6. 支持更多个人资料字段（如生日、地址等）
