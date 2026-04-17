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

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(UserRepository userRepository, 
                                   RoleRepository roleRepository,
                                   PermissionRepository permissionRepository,
                                   PasswordEncoder passwordEncoder) {
        return args -> {
            // 如果数据库为空，则添加一些测试数据
            if (userRepository.count() == 0) {
                
                // ============ 初始化权限 ============
                Permission userView = new Permission("查看用户", "user:view", "查看用户列表和详情");
                Permission userCreate = new Permission("创建用户", "user:create", "创建新用户");
                Permission userEdit = new Permission("编辑用户", "user:edit", "编辑用户信息");
                Permission userDelete = new Permission("删除用户", "user:delete", "删除用户");
                
                Permission roleView = new Permission("查看角色", "role:view", "查看角色列表和详情");
                Permission roleCreate = new Permission("创建角色", "role:create", "创建新角色");
                Permission roleEdit = new Permission("编辑角色", "role:edit", "编辑角色信息");
                Permission roleDelete = new Permission("删除角色", "role:delete", "删除角色");
                
                Permission permView = new Permission("查看权限", "permission:view", "查看权限列表和详情");
                Permission permCreate = new Permission("创建权限", "permission:create", "创建新权限");
                Permission permEdit = new Permission("编辑权限", "permission:edit", "编辑权限信息");
                Permission permDelete = new Permission("删除权限", "permission:delete", "删除权限");
                
                permissionRepository.save(userView);
                permissionRepository.save(userCreate);
                permissionRepository.save(userEdit);
                permissionRepository.save(userDelete);
                permissionRepository.save(roleView);
                permissionRepository.save(roleCreate);
                permissionRepository.save(roleEdit);
                permissionRepository.save(roleDelete);
                permissionRepository.save(permView);
                permissionRepository.save(permCreate);
                permissionRepository.save(permEdit);
                permissionRepository.save(permDelete);
                
                System.out.println("初始化权限数据完成");
                
                // ============ 初始化角色 ============
                Role adminRole = new Role("ADMIN", "系统管理员 - 拥有所有权限");
                Role userRole = new Role("USER", "普通用户 - 基本访问权限");
                Role editorRole = new Role("EDITOR", "编辑者 - 可以编辑内容");
                
                roleRepository.save(adminRole);
                roleRepository.save(userRole);
                roleRepository.save(editorRole);
                
                System.out.println("初始化角色数据完成");
                
                // ============ 初始化用户 ============
                // 管理员账号
                User admin = new User("admin", "admin@example.com", "管理员");
                admin.setPhone("13800138000");
                admin.setAge(30);
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setRole("ADMIN");
                Set<Role> adminRoles = new HashSet<>();
                adminRoles.add(adminRole);
                admin.setRoles(adminRoles);
                
                // 普通用户1
                User user1 = new User("zhangsan", "zhangsan@example.com", "张三");
                user1.setPhone("13800138001");
                user1.setAge(25);
                user1.setPassword(passwordEncoder.encode("user123"));
                user1.setRole("USER");
                Set<Role> user1Roles = new HashSet<>();
                user1Roles.add(userRole);
                user1.setRoles(user1Roles);
                
                // 普通用户2
                User user2 = new User("lisi", "lisi@example.com", "李四");
                user2.setPhone("13800138002");
                user2.setAge(30);
                user2.setPassword(passwordEncoder.encode("user123"));
                user2.setRole("USER");
                Set<Role> user2Roles = new HashSet<>();
                user2Roles.add(userRole);
                user2.setRoles(user2Roles);
                
                // 编辑者用户
                User user3 = new User("wangwu", "wangwu@example.com", "王五");
                user3.setPhone("13800138003");
                user3.setAge(28);
                user3.setPassword(passwordEncoder.encode("user123"));
                user3.setRole("EDITOR");
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
