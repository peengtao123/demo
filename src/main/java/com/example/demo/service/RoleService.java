package com.example.demo.service;

import com.example.demo.entity.Permission;
import com.example.demo.entity.Role;
import com.example.demo.repository.PermissionRepository;
import com.example.demo.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 角色服务类 - 提供角色相关的业务逻辑处理
 */
@Service
@Transactional
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private AuditLogService auditLogService;

    /**
     * 获取当前操作用户名
     *
     * @return 当前登录用户的用户名，如果获取失败则返回 "system"
     */
    private String getCurrentUser() {
        try {
            return SecurityContextHolder.getContext().getAuthentication().getName();
        } catch (Exception e) {
            return "system";
        }
    }

    /**
     * 获取所有角色（不分页）
     *
     * @return 按排序号和创建时间升序排列的所有角色列表
     */
    @Transactional(readOnly = true)
    public List<Role> getAllRoles() {
        return roleRepository.findAll(Sort.by(Sort.Direction.ASC, "sortOrder", "createTime"));
    }

    /**
     * 分页查询角色列表
     *
     * @param page 页码（从0开始）
     * @param size 每页大小
     * @return 按排序号和创建时间升序排列的分页角色结果
     */
    @Transactional(readOnly = true)
    public Page<Role> getRolesWithPaging(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "sortOrder", "createTime"));
        return roleRepository.findAll(pageable);
    }

    /**
     * 搜索角色
     *
     * @param keyword 搜索关键词，支持名称模糊匹配
     * @param page 页码（从0开始）
     * @param size 每页大小
     * @return 按排序号和创建时间升序排列的分页搜索结果
     */
    @Transactional(readOnly = true)
    public Page<Role> searchRoles(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "sortOrder", "createTime"));
        return roleRepository.searchRoles(keyword, pageable);
    }

    /**
     * 根据ID查询角色
     *
     * @param id 角色ID
     * @return 包含角色的Optional对象，如果不存在则返回空Optional
     */
    @Transactional(readOnly = true)
    public Optional<Role> getRoleById(Long id) {
        return roleRepository.findById(id);
    }

    /**
     * 创建角色
     *
     * @param role 角色实体对象，包含名称、描述、图标等信息
     * @return 创建成功的角色实体对象
     * @throws RuntimeException 如果角色名称已存在则抛出异常
     */
    public Role createRole(Role role) {
        // 检查角色名是否已存在
        if (roleRepository.findByName(role.getName()).isPresent()) {
            throw new RuntimeException("角色名称已存在: " + role.getName());
        }
        
        if (role.getStatus() == null) {
            role.setStatus(true);
        }
        if (role.getSortOrder() == null) {
            role.setSortOrder(0);
        }
        
        Role savedRole = roleRepository.save(role);
        
        // 记录审计日志
        auditLogService.log(
            getCurrentUser(),
            "CREATE",
            "ROLE",
            savedRole.getId().toString(),
            "创建角色: " + savedRole.getName()
        );
        
        return savedRole;
    }

    /**
     * 更新角色
     *
     * @param id 角色ID
     * @param roleDetails 包含更新信息的角色对象
     * @return 更新后的角色实体对象
     * @throws RuntimeException 如果角色不存在或新名称已被其他角色使用则抛出异常
     */
    public Role updateRole(Long id, Role roleDetails) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("角色不存在: " + id));
        String oldInfo = "name=" + role.getName() + ", status=" + role.getStatus();
        
        // 如果修改了名称，检查新名称是否已被其他角色使用
        if (!role.getName().equals(roleDetails.getName()) && 
            roleRepository.findByName(roleDetails.getName()).isPresent()) {
            throw new RuntimeException("角色名称已存在: " + roleDetails.getName());
        }
        
        role.setName(roleDetails.getName());
        role.setDescription(roleDetails.getDescription());
        role.setIcon(roleDetails.getIcon());
        role.setStatus(roleDetails.getStatus());
        role.setSortOrder(roleDetails.getSortOrder());
        
        Role updatedRole = roleRepository.save(role);
        
        // 记录审计日志
        auditLogService.logWithChanges(
            getCurrentUser(),
            "UPDATE",
            "ROLE",
            id.toString(),
            "更新角色: " + updatedRole.getName(),
            oldInfo,
            "name=" + updatedRole.getName() + ", status=" + updatedRole.getStatus()
        );
        
        return updatedRole;
    }

    /**
     * 删除角色
     *
     * @param id 角色ID
     * @throws RuntimeException 如果角色不存在或正在被用户使用则抛出异常
     */
    public void deleteRole(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("角色不存在: " + id));
        String roleName = role.getName();
        
        // 检查是否有用户使用该角色
        long userCount = roleRepository.countUsersByRoleId(id);
        if (userCount > 0) {
            throw new RuntimeException("该角色正在被 " + userCount + " 个用户使用，无法删除");
        }
        
        roleRepository.delete(role);
        
        // 记录审计日志
        auditLogService.log(
            getCurrentUser(),
            "DELETE",
            "ROLE",
            id.toString(),
            "删除角色: " + roleName
        );
    }

    /**
     * 批量删除角色
     *
     * @param ids 角色ID列表
     * @throws RuntimeException 如果任何角色正在被用户使用则抛出异常
     */
    public void batchDeleteRoles(List<Long> ids) {
        String deletedRoles = ids.stream()
                .map(id -> {
                    try {
                        return roleRepository.findById(id).map(Role::getName).orElse("ID:" + id);
                    } catch (Exception e) {
                        return "ID:" + id;
                    }
                })
                .collect(Collectors.joining(", "));
        
        for (Long id : ids) {
            long userCount = roleRepository.countUsersByRoleId(id);
            if (userCount > 0) {
                Role role = roleRepository.findById(id).orElse(null);
                String roleName = role != null ? role.getName() : "ID:" + id;
                throw new RuntimeException("角色 '" + roleName + "' 正在被用户使用，无法删除");
            }
        }
        
        roleRepository.deleteAllById(ids);
        
        // 记录审计日志
        auditLogService.log(
            getCurrentUser(),
            "BATCH_DELETE",
            "ROLE",
            ids.toString(),
            "批量删除角色: " + deletedRoles
        );
    }

    /**
     * 启用/禁用角色
     *
     * @param id 角色ID
     * @return 更新后的角色实体对象，状态已切换
     * @throws RuntimeException 如果角色不存在则抛出异常
     */
    public Role toggleRoleStatus(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("角色不存在: " + id));
        Boolean oldStatus = role.getStatus();
        
        role.setStatus(!role.getStatus());
        Role updatedRole = roleRepository.save(role);
        
        // 记录审计日志
        auditLogService.log(
            getCurrentUser(),
            "STATUS_CHANGE",
            "ROLE",
            id.toString(),
            "切换角色状态: " + updatedRole.getName() + " (" + oldStatus + " -> " + updatedRole.getStatus() + ")"
        );
        
        return updatedRole;
    }

    /**
     * 为角色分配权限
     *
     * @param roleId 角色ID
     * @param permissionIds 权限ID列表
     * @return 更新后的角色实体对象，包含新分配的权限
     * @throws RuntimeException 如果角色或权限不存在则抛出异常
     */
    public Role assignPermissions(Long roleId, List<Long> permissionIds) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("角色不存在: " + roleId));
        
        Set<Permission> oldPermissions = new HashSet<>(role.getPermissions());
        
        Set<Permission> permissions = new HashSet<>();
        for (Long permissionId : permissionIds) {
            Permission permission = permissionRepository.findById(permissionId)
                    .orElseThrow(() -> new RuntimeException("权限不存在: " + permissionId));
            permissions.add(permission);
        }
        
        role.setPermissions(permissions);
        Role updatedRole = roleRepository.save(role);
        
        // 记录审计日志
        auditLogService.log(
            getCurrentUser(),
            "PERMISSION_ASSIGN",
            "ROLE",
            roleId.toString(),
            "为角色分配权限: " + role.getName() + " (权限数量: " + permissionIds.size() + ")"
        );
        
        return updatedRole;
    }

    /**
     * 获取角色的权限列表
     *
     * @param roleId 角色ID
     * @return 角色关联的权限集合
     * @throws RuntimeException 如果角色不存在则抛出异常
     */
    @Transactional(readOnly = true)
    public Set<Permission> getRolePermissions(Long roleId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("角色不存在: " + roleId));
        return role.getPermissions();
    }

    /**
     * 根据名称查询角色
     *
     * @param name 角色名称
     * @return 包含角色的Optional对象，如果不存在则返回空Optional
     */
    @Transactional(readOnly = true)
    public Optional<Role> findByName(String name) {
        return roleRepository.findByName(name);
    }

    /**
     * 获取启用的角色列表
     *
     * @return 所有启用状态的角色列表
     */
    @Transactional(readOnly = true)
    public List<Role> getEnabledRoles() {
        return roleRepository.findByStatus(true);
    }

    /**
     * 统计启用角色数量
     *
     * @return 启用状态的角色数量
     */
    @Transactional(readOnly = true)
    public long countEnabledRoles() {
        return roleRepository.countByStatus(true);
    }

    /**
     * 统计禁用角色数量
     *
     * @return 禁用状态的角色数量
     */
    @Transactional(readOnly = true)
    public long countDisabledRoles() {
        return roleRepository.countByStatus(false);
    }

    /**
     * 检查角色是否被用户使用
     *
     * @param roleId 角色ID
     * @return 如果角色正在被至少一个用户使用则返回 true，否则返回 false
     */
    @Transactional(readOnly = true)
    public boolean isRoleInUse(Long roleId) {
        return roleRepository.countUsersByRoleId(roleId) > 0;
    }

    /**
     * 获取使用该角色的用户数量
     *
     * @param roleId 角色ID
     * @return 使用该角色的用户数量
     */
    @Transactional(readOnly = true)
    public long getUsersCountByRole(Long roleId) {
        return roleRepository.countUsersByRoleId(roleId);
    }
}
