package com.example.demo.controller;

import com.example.demo.entity.AuditLog;
import com.example.demo.entity.Permission;
import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.repository.AuditLogRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.AuditLogService;
import com.example.demo.service.PermissionService;
import com.example.demo.service.RoleService;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;
import java.util.stream.Collectors;
import java.util.Comparator;

/**
 * 管理页面控制器 - 用于后台管理系统
 */
@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private AuditLogService auditLogService;

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * 管理首页/仪表盘
     */
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        long userCount = userService.getAllUsers().size();
        long enabledUserCount = userService.countEnabledUsers();
        long disabledUserCount = userService.countDisabledUsers();
        long roleCount = roleService.getAllRoles().size();
        long enabledRoleCount = roleService.countEnabledRoles();
        long disabledRoleCount = roleService.countDisabledRoles();
        long permissionCount = permissionService.getAllPermissions().size();
        long enabledPermissionCount = permissionService.countEnabledPermissions();
        long disabledPermissionCount = permissionService.countDisabledPermissions();
        
        model.addAttribute("userCount", userCount);
        model.addAttribute("enabledUserCount", enabledUserCount);
        model.addAttribute("disabledUserCount", disabledUserCount);
        model.addAttribute("roleCount", roleCount);
        model.addAttribute("enabledRoleCount", enabledRoleCount);
        model.addAttribute("disabledRoleCount", disabledRoleCount);
        model.addAttribute("permissionCount", permissionCount);
        model.addAttribute("enabledPermissionCount", enabledPermissionCount);
        model.addAttribute("disabledPermissionCount", disabledPermissionCount);
        model.addAttribute("activeMenu", "dashboard");
        model.addAttribute("pageTitle", "📊 管理仪表盘");
        addCurrentUserToModel(model);
        
        return "admin/dashboard";
    }

    /**
     * 用户管理列表页面（支持分页和搜索）
     */
    @GetMapping("/users")
    public String userList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            Model model) {
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            // 搜索模式
            model.addAttribute("users", userService.searchUsers(keyword.trim(), page, size));
            model.addAttribute("keyword", keyword);
        } else {
            // 普通分页模式
            model.addAttribute("users", userService.getUsersWithPaging(page, size));
        }
        
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("activeMenu", "users");
        model.addAttribute("pageTitle", "👥 用户管理");
        addCurrentUserToModel(model);
        return "admin/users/list";
    }

    /**
     * 用户详情页面
     */
    @GetMapping("/users/{id}")
    public String userDetail(@PathVariable Long id, Model model) {
        try {
            User user = userService.getUserById(id);
            model.addAttribute("user", user);
            model.addAttribute("activeMenu", "users");
            model.addAttribute("pageTitle", "👤 用户详情");
            addCurrentUserToModel(model);
            return "admin/users/detail";
        } catch (RuntimeException e) {
            model.addAttribute("error", "用户不存在");
            return "error";
        }
    }

    /**
     * 新建用户页面
     */
    @GetMapping("/users/new")
    public String newUser(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("roles", roleService.getAllRoles());
        model.addAttribute("activeMenu", "users");
        model.addAttribute("pageTitle", "➕ 新建用户");
        addCurrentUserToModel(model);
        return "admin/users/form";
    }

    /**
     * 编辑用户页面
     */
    @GetMapping("/users/edit/{id}")
    public String editUser(@PathVariable Long id, Model model) {
        try {
            User user = userService.getUserById(id);
            model.addAttribute("user", user);
            model.addAttribute("roles", roleService.getAllRoles());
            model.addAttribute("activeMenu", "users");
            model.addAttribute("pageTitle", "✏️ 编辑用户");
            addCurrentUserToModel(model);
            return "admin/users/form";
        } catch (RuntimeException e) {
            model.addAttribute("error", "用户不存在");
            return "error";
        }
    }

    /**
     * 保存用户
     */
    @PostMapping("/users/save")
    public String saveUser(@ModelAttribute User user, 
                          @RequestParam(required = false) List<Long> roleIds,
                          RedirectAttributes redirectAttributes) {
        try {
            if (user.getId() == null) {
                // 新建用户
                com.example.demo.dto.UserDTO userDTO = new com.example.demo.dto.UserDTO();
                userDTO.setUsername(user.getUsername());
                userDTO.setEmail(user.getEmail());
                userDTO.setName(user.getName());
                userDTO.setPhone(user.getPhone());
                userDTO.setAge(user.getAge());
                userDTO.setPassword(user.getPassword());
                User savedUser = userService.createUser(userDTO);
                
                // 分配角色
                if (roleIds != null && !roleIds.isEmpty()) {
                    assignRolesToUser(savedUser.getId(), roleIds);
                }
                
                redirectAttributes.addFlashAttribute("success", "用户创建成功");
            } else {
                // 更新用户基本信息
                userService.updateUserInfo(user.getId(), user);
                
                // 更新角色分配
                if (roleIds != null) {
                    assignRolesToUser(user.getId(), roleIds);
                }
                
                redirectAttributes.addFlashAttribute("success", "用户更新成功");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "操作失败: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }

    /**
     * 为用户分配角色（辅助方法）
     */
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
        auditLogService.log(
            getCurrentUser(),
            "ROLE_ASSIGN",
            "USER",
            userId.toString(),
            "为用户分配角色，角色数量: " + roleIds.size()
        );
    }

    /**
     * 删除用户
     */
    @PostMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            userService.deleteUser(id);
            redirectAttributes.addFlashAttribute("success", "用户删除成功");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "删除失败: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }

    /**
     * 批量删除用户
     */
    @PostMapping("/users/batch-delete")
    public String batchDeleteUsers(@RequestParam List<Long> ids, RedirectAttributes redirectAttributes) {
        try {
            userService.batchDeleteUsers(ids);
            redirectAttributes.addFlashAttribute("success", "成功删除 " + ids.size() + " 个用户");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "批量删除失败: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }

    /**
     * 启用/禁用用户
     */
    @PostMapping("/users/toggle-status/{id}")
    public String toggleUserStatus(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            User user = userService.toggleUserStatus(id);
            String statusText = user.getStatus() ? "启用" : "禁用";
            redirectAttributes.addFlashAttribute("success", "用户已" + statusText);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "操作失败: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }

    /**
     * 重置用户密码
     */
    @PostMapping("/users/reset-password/{id}")
    public String resetPassword(@PathVariable Long id, 
                                @RequestParam String newPassword,
                                RedirectAttributes redirectAttributes) {
        try {
            userService.resetPassword(id, newPassword);
            redirectAttributes.addFlashAttribute("success", "密码重置成功");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "密码重置失败: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }

    /**
     * 角色管理列表页面（支持分页和搜索）
     */
    @GetMapping("/roles")
    public String roleList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            Model model) {
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            model.addAttribute("roles", roleService.searchRoles(keyword.trim(), page, size));
            model.addAttribute("keyword", keyword);
        } else {
            model.addAttribute("roles", roleService.getRolesWithPaging(page, size));
        }
        
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("activeMenu", "roles");
        model.addAttribute("pageTitle", "🎭 角色管理");
        addCurrentUserToModel(model);
        return "admin/roles/list";
    }

    /**
     * 角色详情页面
     */
    @GetMapping("/roles/{id}")
    public String roleDetail(@PathVariable Long id, Model model) {
        try {
            Role role = roleService.getRoleById(id)
                    .orElseThrow(() -> new RuntimeException("角色不存在"));
            model.addAttribute("role", role);
            model.addAttribute("allPermissions", permissionService.getAllPermissions());
            model.addAttribute("activeMenu", "roles");
            model.addAttribute("pageTitle", "🎭 角色详情");
            addCurrentUserToModel(model);
            return "admin/roles/detail";
        } catch (RuntimeException e) {
            model.addAttribute("error", "角色不存在");
            return "error";
        }
    }

    /**
     * 新建角色页面
     */
    @GetMapping("/roles/new")
    public String newRole(Model model) {
        model.addAttribute("role", new Role());
        model.addAttribute("allPermissions", permissionService.getAllPermissions());
        model.addAttribute("activeMenu", "roles");
        model.addAttribute("pageTitle", "➕ 新建角色");
        addCurrentUserToModel(model);
        return "admin/roles/form";
    }

    /**
     * 编辑角色页面
     */
    @GetMapping("/roles/edit/{id}")
    public String editRole(@PathVariable Long id, Model model) {
        try {
            Role role = roleService.getRoleById(id)
                    .orElseThrow(() -> new RuntimeException("角色不存在"));
            model.addAttribute("role", role);
            model.addAttribute("allPermissions", permissionService.getAllPermissions());
            model.addAttribute("activeMenu", "roles");
            model.addAttribute("pageTitle", "✏️ 编辑角色");
            addCurrentUserToModel(model);
            return "admin/roles/form";
        } catch (RuntimeException e) {
            model.addAttribute("error", "角色不存在");
            return "error";
        }
    }

    /**
     * 保存角色
     */
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

    /**
     * 删除角色
     */
    @PostMapping("/roles/delete/{id}")
    public String deleteRole(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            roleService.deleteRole(id);
            redirectAttributes.addFlashAttribute("success", "角色删除成功");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "删除失败: " + e.getMessage());
        }
        return "redirect:/admin/roles";
    }

    /**
     * 批量删除角色
     */
    @PostMapping("/roles/batch-delete")
    public String batchDeleteRoles(@RequestParam List<Long> ids, RedirectAttributes redirectAttributes) {
        try {
            roleService.batchDeleteRoles(ids);
            redirectAttributes.addFlashAttribute("success", "成功删除 " + ids.size() + " 个角色");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "批量删除失败: " + e.getMessage());
        }
        return "redirect:/admin/roles";
    }

    /**
     * 启用/禁用角色
     */
    @PostMapping("/roles/toggle-status/{id}")
    public String toggleRoleStatus(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Role role = roleService.toggleRoleStatus(id);
            String statusText = role.getStatus() ? "启用" : "禁用";
            redirectAttributes.addFlashAttribute("success", "角色已" + statusText);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "操作失败: " + e.getMessage());
        }
        return "redirect:/admin/roles";
    }

    /**
     * 权限管理列表页面（支持分页和搜索）
     */
    @GetMapping("/permissions")
    public String permissionList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            Model model) {
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            model.addAttribute("permissions", permissionService.searchPermissions(keyword.trim(), page, size));
            model.addAttribute("keyword", keyword);
        } else {
            model.addAttribute("permissions", permissionService.getPermissionsWithPaging(page, size));
        }
        
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("activeMenu", "permissions");
        model.addAttribute("pageTitle", "🔐 权限管理");
        addCurrentUserToModel(model);
        return "admin/permissions/list";
    }

    /**
     * 权限详情页面
     */
    @GetMapping("/permissions/{id}")
    public String permissionDetail(@PathVariable Long id, Model model) {
        try {
            Permission permission = permissionService.getPermissionById(id)
                    .orElseThrow(() -> new RuntimeException("权限不存在"));
            model.addAttribute("permission", permission);
            model.addAttribute("activeMenu", "permissions");
            model.addAttribute("pageTitle", "🔐 权限详情");
            addCurrentUserToModel(model);
            return "admin/permissions/detail";
        } catch (RuntimeException e) {
            model.addAttribute("error", "权限不存在");
            return "error";
        }
    }

    /**
     * 新建权限页面
     */
    @GetMapping("/permissions/new")
    public String newPermission(Model model) {
        model.addAttribute("permission", new Permission());
        model.addAttribute("allPermissions", permissionService.getAllPermissions());
        model.addAttribute("activeMenu", "permissions");
        model.addAttribute("pageTitle", "➕ 新建权限");
        addCurrentUserToModel(model);
        return "admin/permissions/form";
    }

    /**
     * 编辑权限页面
     */
    @GetMapping("/permissions/edit/{id}")
    public String editPermission(@PathVariable Long id, Model model) {
        try {
            Permission permission = permissionService.getPermissionById(id)
                    .orElseThrow(() -> new RuntimeException("权限不存在"));
            model.addAttribute("permission", permission);
            model.addAttribute("allPermissions", permissionService.getAllPermissions());
            model.addAttribute("activeMenu", "permissions");
            model.addAttribute("pageTitle", "✏️ 编辑权限");
            addCurrentUserToModel(model);
            return "admin/permissions/form";
        } catch (RuntimeException e) {
            model.addAttribute("error", "权限不存在");
            return "error";
        }
    }

    /**
     * 保存权限
     */
    @PostMapping("/permissions/save")
    public String savePermission(@ModelAttribute Permission permission, RedirectAttributes redirectAttributes) {
        try {
            if (permission.getId() == null) {
                permissionService.createPermission(permission);
                redirectAttributes.addFlashAttribute("success", "权限创建成功");
            } else {
                permissionService.updatePermission(permission.getId(), permission);
                redirectAttributes.addFlashAttribute("success", "权限更新成功");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "操作失败: " + e.getMessage());
        }
        return "redirect:/admin/permissions";
    }

    /**
     * 删除权限
     */
    @PostMapping("/permissions/delete/{id}")
    public String deletePermission(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            permissionService.deletePermission(id);
            redirectAttributes.addFlashAttribute("success", "权限删除成功");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "删除失败: " + e.getMessage());
        }
        return "redirect:/admin/permissions";
    }

    /**
     * 批量删除权限
     */
    @PostMapping("/permissions/batch-delete")
    public String batchDeletePermissions(@RequestParam List<Long> ids, RedirectAttributes redirectAttributes) {
        try {
            permissionService.batchDeletePermissions(ids);
            redirectAttributes.addFlashAttribute("success", "成功删除 " + ids.size() + " 个权限");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "批量删除失败: " + e.getMessage());
        }
        return "redirect:/admin/permissions";
    }

    /**
     * 启用/禁用权限
     */
    @PostMapping("/permissions/toggle-status/{id}")
    public String togglePermissionStatus(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Permission permission = permissionService.togglePermissionStatus(id);
            String statusText = permission.getStatus() ? "启用" : "禁用";
            redirectAttributes.addFlashAttribute("success", "权限已" + statusText);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "操作失败: " + e.getMessage());
        }
        return "redirect:/admin/permissions";
    }

    /**
     * 审计日志列表页面
     */
    @GetMapping("/audit-logs")
    public String auditLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String operator,
            @RequestParam(required = false) String operationType,
            @RequestParam(required = false) String targetType,
            Model model) {
        
        Page<AuditLog> logs;
        
        if (operator != null && !operator.isEmpty()) {
            logs = auditLogService.findByOperator(operator, page, size);
        } else if (operationType != null && !operationType.isEmpty()) {
            logs = auditLogService.findByOperationType(operationType, page, size);
        } else if (targetType != null && !targetType.isEmpty()) {
            logs = auditLogService.findByTargetType(targetType, page, size);
        } else {
            logs = auditLogService.getAuditLogs(page, size);
        }
        
        model.addAttribute("logs", logs);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("operator", operator);
        model.addAttribute("operationType", operationType);
        model.addAttribute("targetType", targetType);
        model.addAttribute("activeMenu", "audit");
        model.addAttribute("pageTitle", "审计日志");
        model.addAttribute("currentUsername", getCurrentUser());
        
        return "admin/audit/logs";
    }
    
    /**
     * 删除审计日志
     */
    @PostMapping("/audit-logs/delete/{id}")
    public String deleteAuditLog(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            auditLogRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "日志删除成功");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "删除失败: " + e.getMessage());
        }
        return "redirect:/admin/audit-logs";
    }
    
    /**
     * 批量删除审计日志
     */
    @PostMapping("/audit-logs/batch-delete")
    public String batchDeleteAuditLogs(@RequestParam List<Long> ids, RedirectAttributes redirectAttributes) {
        try {
            auditLogRepository.deleteAllById(ids);
            redirectAttributes.addFlashAttribute("success", "成功删除 " + ids.size() + " 条日志");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "批量删除失败: " + e.getMessage());
        }
        return "redirect:/admin/audit-logs";
    }

    /**
     * 获取当前用户的动态菜单（JSON API）
     */
    @GetMapping("/menu")
    @ResponseBody
    public List<Map<String, Object>> getUserMenu() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated()) {
                return Collections.emptyList();
            }
            
            String username = auth.getName();
            User user = userService.getUserByUsername(username);
            
            // 获取用户的所有启用的权限（只包含MENU类型）
            List<Permission> menuPermissions = user.getRoles().stream()
                    .flatMap(role -> role.getPermissions().stream())
                    .filter(Permission::getStatus)
                    .filter(p -> "MENU".equals(p.getType()))
                    .sorted(Comparator.comparingInt(Permission::getSortOrder))
                    .collect(Collectors.toList());
            
            // 构建菜单树
            return buildMenuTree(menuPermissions, null);
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
    
    /**
     * 递归构建菜单树
     */
    private List<Map<String, Object>> buildMenuTree(List<Permission> allMenus, Long parentId) {
        List<Map<String, Object>> menuTree = new ArrayList<>();
        
        for (Permission permission : allMenus) {
            if ((parentId == null && permission.getParentId() == null) ||
                (parentId != null && parentId.equals(permission.getParentId()))) {
                
                Map<String, Object> menuItem = new HashMap<>();
                menuItem.put("id", permission.getId());
                menuItem.put("name", permission.getName());
                menuItem.put("icon", permission.getIcon() != null ? permission.getIcon() : "📄");
                menuItem.put("url", getMenuUrl(permission.getCode()));
                menuItem.put("sortOrder", permission.getSortOrder());
                
                // 递归查找子菜单
                List<Map<String, Object>> children = buildMenuTree(allMenus, permission.getId());
                if (!children.isEmpty()) {
                    menuItem.put("children", children);
                }
                
                menuTree.add(menuItem);
            }
        }
        
        return menuTree;
    }
    
    /**
     * 根据权限编码获取菜单URL
     */
    private String getMenuUrl(String code) {
        if (code == null) return "#";
        
        switch (code.toLowerCase()) {
            // 仪表盘
            case "dashboard:menu":
            case "dashboard:view":
                return "/admin/dashboard";
            
            // 用户管理
            case "user:menu":
            case "user:view":
            case "user:list":
                return "/admin/users";
            
            // 角色管理
            case "role:menu":
            case "role:view":
            case "role:list":
                return "/admin/roles";
            
            // 权限管理
            case "permission:menu":
            case "permission:view":
            case "permission:list":
                return "/admin/permissions";
            
            // 审计日志
            case "audit:menu":
            case "audit:view":
            case "audit:log":
                return "/admin/audit-logs";
            
            default:
                return "#";
        }
    }

    /**
     * 添加当前用户信息到Model
     */
    private void addCurrentUserToModel(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            model.addAttribute("currentUsername", username);
            
            // 添加用户角色信息
            var authorities = authentication.getAuthorities();
            model.addAttribute("currentUserRoles", authorities);
        }
    }
    
    /**
     * 获取当前用户名
     */
    private String getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }
        return "anonymous";
    }
}
