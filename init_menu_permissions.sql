-- ============================================
-- 菜单权限配置SQL脚本
-- 包含完整的菜单层级结构、图标和排序
-- ============================================

-- 清空现有权限数据（可选，谨慎使用）
-- DELETE FROM permissions;

-- ============ 一级菜单 ============

-- 1. 仪表盘菜单
INSERT INTO permissions (name, code, description, icon, type, parent_id, sort_order, status) 
VALUES ('仪表盘', 'dashboard:menu', '系统主控制面板', 'bi-speedometer2', 'MENU', NULL, 1, true);

-- 2. 用户管理菜单
INSERT INTO permissions (name, code, description, icon, type, parent_id, sort_order, status) 
VALUES ('用户管理', 'user:menu', '用户相关功能管理', 'bi-people', 'MENU', NULL, 2, true);

-- 3. 角色管理菜单
INSERT INTO permissions (name, code, description, icon, type, parent_id, sort_order, status) 
VALUES ('角色管理', 'role:menu', '角色和权限管理', 'bi-shield-lock', 'MENU', NULL, 3, true);

-- 4. 权限管理菜单
INSERT INTO permissions (name, code, description, icon, type, parent_id, sort_order, status) 
VALUES ('权限管理', 'permission:menu', '权限配置管理', 'bi-key', 'MENU', NULL, 4, true);

-- 5. 审计日志菜单
INSERT INTO permissions (name, code, description, icon, type, parent_id, sort_order, status) 
VALUES ('审计日志', 'audit:menu', '系统操作日志', 'bi-journal-text', 'MENU', NULL, 5, true);

-- ============ 二级菜单和功能按钮 ============

-- 用户管理子项
INSERT INTO permissions (name, code, description, icon, type, parent_id, sort_order, status) 
VALUES ('用户列表', 'user:view', '查看用户列表', 'bi-list-ul', 'MENU', 
        (SELECT id FROM permissions WHERE code = 'user:menu'), 1, true);

INSERT INTO permissions (name, code, description, icon, type, parent_id, sort_order, status) 
VALUES ('创建用户', 'user:create', '创建新用户', NULL, 'BUTTON', 
        (SELECT id FROM permissions WHERE code = 'user:menu'), 2, true);

INSERT INTO permissions (name, code, description, icon, type, parent_id, sort_order, status) 
VALUES ('编辑用户', 'user:edit', '编辑用户信息', NULL, 'BUTTON', 
        (SELECT id FROM permissions WHERE code = 'user:menu'), 3, true);

INSERT INTO permissions (name, code, description, icon, type, parent_id, sort_order, status) 
VALUES ('删除用户', 'user:delete', '删除用户', NULL, 'BUTTON', 
        (SELECT id FROM permissions WHERE code = 'user:menu'), 4, true);

-- 角色管理子项
INSERT INTO permissions (name, code, description, icon, type, parent_id, sort_order, status) 
VALUES ('角色列表', 'role:view', '查看角色列表', 'bi-list-ul', 'MENU', 
        (SELECT id FROM permissions WHERE code = 'role:menu'), 1, true);

INSERT INTO permissions (name, code, description, icon, type, parent_id, sort_order, status) 
VALUES ('创建角色', 'role:create', '创建新角色', NULL, 'BUTTON', 
        (SELECT id FROM permissions WHERE code = 'role:menu'), 2, true);

INSERT INTO permissions (name, code, description, icon, type, parent_id, sort_order, status) 
VALUES ('编辑角色', 'role:edit', '编辑角色信息', NULL, 'BUTTON', 
        (SELECT id FROM permissions WHERE code = 'role:menu'), 3, true);

INSERT INTO permissions (name, code, description, icon, type, parent_id, sort_order, status) 
VALUES ('删除角色', 'role:delete', '删除角色', NULL, 'BUTTON', 
        (SELECT id FROM permissions WHERE code = 'role:menu'), 4, true);

-- 权限管理子项
INSERT INTO permissions (name, code, description, icon, type, parent_id, sort_order, status) 
VALUES ('权限列表', 'permission:view', '查看权限列表', 'bi-list-ul', 'MENU', 
        (SELECT id FROM permissions WHERE code = 'permission:menu'), 1, true);

INSERT INTO permissions (name, code, description, icon, type, parent_id, sort_order, status) 
VALUES ('创建权限', 'permission:create', '创建新权限', NULL, 'BUTTON', 
        (SELECT id FROM permissions WHERE code = 'permission:menu'), 2, true);

INSERT INTO permissions (name, code, description, icon, type, parent_id, sort_order, status) 
VALUES ('编辑权限', 'permission:edit', '编辑权限信息', NULL, 'BUTTON', 
        (SELECT id FROM permissions WHERE code = 'permission:menu'), 3, true);

INSERT INTO permissions (name, code, description, icon, type, parent_id, sort_order, status) 
VALUES ('删除权限', 'permission:delete', '删除权限', NULL, 'BUTTON', 
        (SELECT id FROM permissions WHERE code = 'permission:menu'), 4, true);

-- 审计日志子项
INSERT INTO permissions (name, code, description, icon, type, parent_id, sort_order, status) 
VALUES ('日志列表', 'audit:view', '查看审计日志', 'bi-list-ul', 'MENU', 
        (SELECT id FROM permissions WHERE code = 'audit:menu'), 1, true);

INSERT INTO permissions (name, code, description, icon, type, parent_id, sort_order, status) 
VALUES ('删除日志', 'audit:delete', '删除审计日志', NULL, 'BUTTON', 
        (SELECT id FROM permissions WHERE code = 'audit:menu'), 2, true);

-- ============ 系统管理菜单 ============

-- 6. 系统管理菜单
INSERT INTO permissions (name, code, description, icon, type, parent_id, sort_order, status) 
VALUES ('系统管理', 'profile:menu', '个人信息和密码管理', 'bi-gear', 'MENU', NULL, 6, true);

INSERT INTO permissions (name, code, description, icon, type, parent_id, sort_order, status) 
VALUES ('个人信息', 'profile:view', '查看和编辑个人信息', 'bi-person', 'MENU', 
        (SELECT id FROM permissions WHERE code = 'profile:menu'), 1, true);

INSERT INTO permissions (name, code, description, icon, type, parent_id, sort_order, status) 
VALUES ('修改密码', 'profile:password', '修改登录密码', 'bi-key', 'BUTTON', 
        (SELECT id FROM permissions WHERE code = 'profile:menu'), 2, true);

-- ============ 查询验证 ============
-- 查看所有菜单层级结构
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
