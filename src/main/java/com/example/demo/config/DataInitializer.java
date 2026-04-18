package com.example.demo.config;

import com.example.demo.entity.Permission;
import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.repository.PermissionRepository;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 数据初始化配置类
 * <p>在应用启动时自动初始化测试数据，包括用户、角色和权限。</p>
 * 
 * @author Demo Team
 * @version 1.0
 * @since 2024-01-01
 */
@Configuration
public class DataInitializer {

    /**
     * 初始化数据库测试数据
     * <p>当数据库为空时，创建默认的用户、角色和权限数据。</p>
     * 
     * @param userRepository 用户仓库
     * @param roleRepository 角色仓库
     * @param permissionRepository 权限仓库
     * @param passwordEncoder 密码编码器
     * @return CommandLineRunner执行器
     */
    @Bean
    CommandLineRunner initDatabase(UserRepository userRepository, 
                                   RoleRepository roleRepository,
                                   PermissionRepository permissionRepository,
                                   PasswordEncoder passwordEncoder) {
        return args -> {
            // 如果数据库为空，则添加一些测试数据
            if (userRepository.count() == 0) {
                
                // ============ 初始化权限（包含菜单和功能权限）============
                
                // --- 一级菜单：仪表盘 ---
                Permission dashboardMenu = new Permission("仪表盘", "dashboard:menu", "系统主控制面板");
                dashboardMenu.setIcon("bi-speedometer2");
                dashboardMenu.setType("MENU");
                dashboardMenu.setSortOrder(1);
                dashboardMenu.setParentId(null);
                permissionRepository.save(dashboardMenu);
                
                // --- 一级菜单：用户管理 ---
                Permission userMenu = new Permission("用户管理", "user:menu", "用户相关功能管理");
                userMenu.setIcon("bi-people");
                userMenu.setType("MENU");
                userMenu.setSortOrder(2);
                userMenu.setParentId(null);
                permissionRepository.save(userMenu);
                
                // 二级菜单/功能：用户列表
                Permission userList = new Permission("用户列表", "user:view", "查看用户列表");
                userList.setIcon("bi-list-ul");
                userList.setType("MENU");
                userList.setSortOrder(1);
                userList.setParentId(userMenu.getId());
                permissionRepository.save(userList);
                
                // 功能按钮：创建用户
                Permission userCreate = new Permission("创建用户", "user:create", "创建新用户");
                userCreate.setType("BUTTON");
                userCreate.setSortOrder(2);
                userCreate.setParentId(userMenu.getId());
                permissionRepository.save(userCreate);
                
                // 功能按钮：编辑用户
                Permission userEdit = new Permission("编辑用户", "user:edit", "编辑用户信息");
                userEdit.setType("BUTTON");
                userEdit.setSortOrder(3);
                userEdit.setParentId(userMenu.getId());
                permissionRepository.save(userEdit);
                
                // 功能按钮：删除用户
                Permission userDelete = new Permission("删除用户", "user:delete", "删除用户");
                userDelete.setType("BUTTON");
                userDelete.setSortOrder(4);
                userDelete.setParentId(userMenu.getId());
                permissionRepository.save(userDelete);
                
                // --- 一级菜单：角色管理 ---
                Permission roleMenu = new Permission("角色管理", "role:menu", "角色和权限管理");
                roleMenu.setIcon("bi-shield-lock");
                roleMenu.setType("MENU");
                roleMenu.setSortOrder(3);
                roleMenu.setParentId(null);
                permissionRepository.save(roleMenu);
                
                // 二级菜单/功能：角色列表
                Permission roleList = new Permission("角色列表", "role:view", "查看角色列表");
                roleList.setIcon("bi-list-ul");
                roleList.setType("MENU");
                roleList.setSortOrder(1);
                roleList.setParentId(roleMenu.getId());
                permissionRepository.save(roleList);
                
                // 功能按钮：创建角色
                Permission roleCreate = new Permission("创建角色", "role:create", "创建新角色");
                roleCreate.setType("BUTTON");
                roleCreate.setSortOrder(2);
                roleCreate.setParentId(roleMenu.getId());
                permissionRepository.save(roleCreate);
                
                // 功能按钮：编辑角色
                Permission roleEdit = new Permission("编辑角色", "role:edit", "编辑角色信息");
                roleEdit.setType("BUTTON");
                roleEdit.setSortOrder(3);
                roleEdit.setParentId(roleMenu.getId());
                permissionRepository.save(roleEdit);
                
                // 功能按钮：删除角色
                Permission roleDelete = new Permission("删除角色", "role:delete", "删除角色");
                roleDelete.setType("BUTTON");
                roleDelete.setSortOrder(4);
                roleDelete.setParentId(roleMenu.getId());
                permissionRepository.save(roleDelete);
                
                // --- 一级菜单：权限管理 ---
                Permission permMenu = new Permission("权限管理", "permission:menu", "权限配置管理");
                permMenu.setIcon("bi-key");
                permMenu.setType("MENU");
                permMenu.setSortOrder(4);
                permMenu.setParentId(null);
                permissionRepository.save(permMenu);
                
                // 二级菜单/功能：权限列表
                Permission permList = new Permission("权限列表", "permission:view", "查看权限列表");
                permList.setIcon("bi-list-ul");
                permList.setType("MENU");
                permList.setSortOrder(1);
                permList.setParentId(permMenu.getId());
                permissionRepository.save(permList);
                
                // 功能按钮：创建权限
                Permission permCreate = new Permission("创建权限", "permission:create", "创建新权限");
                permCreate.setType("BUTTON");
                permCreate.setSortOrder(2);
                permCreate.setParentId(permMenu.getId());
                permissionRepository.save(permCreate);
                
                // 功能按钮：编辑权限
                Permission permEdit = new Permission("编辑权限", "permission:edit", "编辑权限信息");
                permEdit.setType("BUTTON");
                permEdit.setSortOrder(3);
                permEdit.setParentId(permMenu.getId());
                permissionRepository.save(permEdit);
                
                // 功能按钮：删除权限
                Permission permDelete = new Permission("删除权限", "permission:delete", "删除权限");
                permDelete.setType("BUTTON");
                permDelete.setSortOrder(4);
                permDelete.setParentId(permMenu.getId());
                permissionRepository.save(permDelete);
                
                // --- 一级菜单：审计日志 ---
                Permission auditMenu = new Permission("审计日志", "audit:menu", "系统操作日志");
                auditMenu.setIcon("bi-journal-text");
                auditMenu.setType("MENU");
                auditMenu.setSortOrder(5);
                auditMenu.setParentId(null);
                permissionRepository.save(auditMenu);
                
                // 二级菜单/功能：日志列表
                Permission auditList = new Permission("日志列表", "audit:view", "查看审计日志");
                auditList.setIcon("bi-list-ul");
                auditList.setType("MENU");
                auditList.setSortOrder(1);
                auditList.setParentId(auditMenu.getId());
                permissionRepository.save(auditList);
                
                // 功能按钮：删除日志
                Permission auditDelete = new Permission("删除日志", "audit:delete", "删除审计日志");
                auditDelete.setType("BUTTON");
                auditDelete.setSortOrder(2);
                auditDelete.setParentId(auditMenu.getId());
                permissionRepository.save(auditDelete);                
                // --- 一级菜单：AI助手 ---
                Permission aiMenu = new Permission("AI助手", "ai:menu", "智能对话助手");
                aiMenu.setIcon("bi-robot");
                aiMenu.setType("MENU");
                aiMenu.setSortOrder(6);
                aiMenu.setParentId(null);
                permissionRepository.save(aiMenu);
                
                // 二级菜单/功能：AI聊天
                Permission aiChat = new Permission("AI聊天", "ai:chat", "与AI进行对话");
                aiChat.setIcon("bi-chat-dots");
                aiChat.setType("MENU");
                aiChat.setSortOrder(1);
                aiChat.setParentId(aiMenu.getId());
                permissionRepository.save(aiChat);
                
                System.out.println("初始化权限数据完成（包含菜单层级结构）");
                
                // ============ 初始化角色 ============
                Role adminRole = new Role("ADMIN", "系统管理员 - 拥有所有权限");
                Role userRole = new Role("USER", "普通用户 - 基本访问权限");
                Role editorRole = new Role("EDITOR", "编辑者 - 可以编辑内容");
                
                roleRepository.save(adminRole);
                roleRepository.save(userRole);
                roleRepository.save(editorRole);
                
                System.out.println("初始化角色数据完成");
                
                // ============ 为角色分配权限 ============
                // ADMIN角色：拥有所有权限
                Set<Permission> adminPermissions = new HashSet<>(permissionRepository.findAll());
                adminRole.setPermissions(adminPermissions);
                roleRepository.save(adminRole);
                
                // EDITOR角色：拥有查看和编辑权限（不含删除）
                Set<Permission> editorPermissions = permissionRepository.findAll().stream()
                    .filter(p -> !p.getCode().contains(":delete"))  // 排除删除权限
                    .collect(Collectors.toSet());
                editorRole.setPermissions(editorPermissions);
                roleRepository.save(editorRole);
                
                // USER角色：只有查看权限
                Set<Permission> userPermissions = permissionRepository.findAll().stream()
                    .filter(p -> p.getCode().contains(":view") || p.getCode().contains(":menu"))  // 只包含查看和菜单权限
                    .collect(Collectors.toSet());
                userRole.setPermissions(userPermissions);
                roleRepository.save(userRole);
                
                System.out.println("角色权限分配完成");
                System.out.println("ADMIN角色权限数: " + adminRole.getPermissions().size());
                System.out.println("EDITOR角色权限数: " + editorRole.getPermissions().size());
                System.out.println("USER角色权限数: " + userRole.getPermissions().size());
                
                // ============ 初始化用户 ============
                // 管理员账号
                User admin = new User("admin", "admin@example.com", "管理员");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setPhone("13800138000");
                admin.setAge(30);
                Set<Role> adminRoles = new HashSet<>();
                adminRoles.add(adminRole);
                admin.setRoles(adminRoles);
                
                // 普通用户1
                User user1 = new User("zhangsan", "zhangsan@example.com", "张三");
                user1.setPassword(passwordEncoder.encode("user123"));
                user1.setPhone("13800138001");
                user1.setAge(25);
                Set<Role> user1Roles = new HashSet<>();
                user1Roles.add(userRole);
                user1.setRoles(user1Roles);
                
                // 普通用户2
                User user2 = new User("lisi", "lisi@example.com", "李四");
                user2.setPassword(passwordEncoder.encode("user123"));
                user2.setPhone("13800138002");
                user2.setAge(30);
                Set<Role> user2Roles = new HashSet<>();
                user2Roles.add(userRole);
                user2.setRoles(user2Roles);
                
                // 编辑者用户
                User user3 = new User("wangwu", "wangwu@example.com", "王五");
                user3.setPassword(passwordEncoder.encode("user123"));
                user3.setPhone("13800138003");
                user3.setAge(28);
                Set<Role> user3Roles = new HashSet<>();
                user3Roles.add(editorRole);
                user3.setRoles(user3Roles);

                userRepository.save(admin);
                userRepository.save(user1);
                userRepository.save(user2);
                userRepository.save(user3);

                System.out.println("初始化数据完成，添加了4个测试用户（1个管理员 + 2个普通用户 + 1个编辑者）");
                System.out.println("管理员账号: admin / admin123");
                System.out.println("普通用户账号: zhangsan / user123");
                System.out.println("编辑者账号: wangwu / user123");
            }
        };
    }
}
