package com.example.demo.controller;

import com.example.demo.entity.Permission;
import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.service.PermissionService;
import com.example.demo.service.RoleService;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

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

    /**
     * 管理首页/仪表盘
     */
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        long userCount = userService.getAllUsers().size();
        long roleCount = roleService.getAllRoles().size();
        long permissionCount = permissionService.getAllPermissions().size();
        
        model.addAttribute("userCount", userCount);
        model.addAttribute("roleCount", roleCount);
        model.addAttribute("permissionCount", permissionCount);
        addCurrentUserToModel(model);
        
        return "admin/dashboard";
    }

    /**
     * 用户管理列表页面
     */
    @GetMapping("/users")
    public String userList(Model model) {
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        addCurrentUserToModel(model);
        return "admin/users/list";
    }

    /**
     * 角色管理列表页面
     */
    @GetMapping("/roles")
    public String roleList(Model model) {
        List<Role> roles = roleService.getAllRoles();
        model.addAttribute("roles", roles);
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
    public String saveRole(@ModelAttribute Role role, RedirectAttributes redirectAttributes) {
        try {
            if (role.getId() == null) {
                roleService.createRole(role);
                redirectAttributes.addFlashAttribute("success", "角色创建成功");
            } else {
                roleService.updateRole(role.getId(), role);
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
     * 权限管理列表页面
     */
    @GetMapping("/permissions")
    public String permissionList(Model model) {
        List<Permission> permissions = permissionService.getAllPermissions();
        model.addAttribute("permissions", permissions);
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
     * 将当前登录用户信息添加到Model
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
}
