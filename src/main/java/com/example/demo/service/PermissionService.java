package com.example.demo.service;

import com.example.demo.entity.Permission;
import com.example.demo.repository.PermissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class PermissionService {

    @Autowired
    private PermissionRepository permissionRepository;

    /**
     * 获取所有权限（不分页）
     */
    @Transactional(readOnly = true)
    public List<Permission> getAllPermissions() {
        return permissionRepository.findAll(Sort.by(Sort.Direction.ASC, "sortOrder", "createTime"));
    }

    /**
     * 分页查询权限列表
     */
    @Transactional(readOnly = true)
    public Page<Permission> getPermissionsWithPaging(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "sortOrder", "createTime"));
        return permissionRepository.findAll(pageable);
    }

    /**
     * 搜索权限
     */
    @Transactional(readOnly = true)
    public Page<Permission> searchPermissions(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "sortOrder", "createTime"));
        return permissionRepository.searchPermissions(keyword, pageable);
    }

    /**
     * 根据ID查询权限
     */
    @Transactional(readOnly = true)
    public Optional<Permission> getPermissionById(Long id) {
        return permissionRepository.findById(id);
    }

    /**
     * 创建权限
     */
    public Permission createPermission(Permission permission) {
        // 检查权限编码是否已存在
        if (permissionRepository.findByCode(permission.getCode()).isPresent()) {
            throw new RuntimeException("权限编码已存在: " + permission.getCode());
        }
        
        // 检查权限名称是否已存在
        if (permissionRepository.findByName(permission.getName()).isPresent()) {
            throw new RuntimeException("权限名称已存在: " + permission.getName());
        }
        
        if (permission.getStatus() == null) {
            permission.setStatus(true);
        }
        if (permission.getSortOrder() == null) {
            permission.setSortOrder(0);
        }
        if (permission.getType() == null || permission.getType().isEmpty()) {
            permission.setType("MENU");
        }
        
        return permissionRepository.save(permission);
    }

    /**
     * 更新权限
     */
    public Permission updatePermission(Long id, Permission permissionDetails) {
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("权限不存在: " + id));
        
        // 如果修改了编码，检查新编码是否已被其他权限使用
        if (!permission.getCode().equals(permissionDetails.getCode()) && 
            permissionRepository.findByCode(permissionDetails.getCode()).isPresent()) {
            throw new RuntimeException("权限编码已存在: " + permissionDetails.getCode());
        }
        
        // 如果修改了名称，检查新名称是否已被其他权限使用
        if (!permission.getName().equals(permissionDetails.getName()) && 
            permissionRepository.findByName(permissionDetails.getName()).isPresent()) {
            throw new RuntimeException("权限名称已存在: " + permissionDetails.getName());
        }
        
        permission.setName(permissionDetails.getName());
        permission.setCode(permissionDetails.getCode());
        permission.setDescription(permissionDetails.getDescription());
        permission.setIcon(permissionDetails.getIcon());
        permission.setStatus(permissionDetails.getStatus());
        permission.setParentId(permissionDetails.getParentId());
        permission.setSortOrder(permissionDetails.getSortOrder());
        permission.setType(permissionDetails.getType());
        
        return permissionRepository.save(permission);
    }

    /**
     * 删除权限
     */
    public void deletePermission(Long id) {
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("权限不存在: " + id));
        
        // 检查是否有角色使用该权限
        long roleCount = permissionRepository.countRolesByPermissionId(id);
        if (roleCount > 0) {
            throw new RuntimeException("该权限正在被 " + roleCount + " 个角色使用，无法删除");
        }
        
        permissionRepository.delete(permission);
    }

    /**
     * 批量删除权限
     */
    public void batchDeletePermissions(List<Long> ids) {
        for (Long id : ids) {
            long roleCount = permissionRepository.countRolesByPermissionId(id);
            if (roleCount > 0) {
                Permission permission = permissionRepository.findById(id).orElse(null);
                String permName = permission != null ? permission.getName() : "ID:" + id;
                throw new RuntimeException("权限 '" + permName + "' 正在被角色使用，无法删除");
            }
        }
        permissionRepository.deleteAllById(ids);
    }

    /**
     * 启用/禁用权限
     */
    public Permission togglePermissionStatus(Long id) {
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("权限不存在: " + id));
        permission.setStatus(!permission.getStatus());
        return permissionRepository.save(permission);
    }

    /**
     * 根据编码查询权限
     */
    @Transactional(readOnly = true)
    public Optional<Permission> findByCode(String code) {
        return permissionRepository.findByCode(code);
    }

    /**
     * 根据名称查询权限
     */
    @Transactional(readOnly = true)
    public Optional<Permission> findByName(String name) {
        return permissionRepository.findByName(name);
    }

    /**
     * 获取启用的权限列表
     */
    @Transactional(readOnly = true)
    public List<Permission> getEnabledPermissions() {
        return permissionRepository.findByStatus(true);
    }

    /**
     * 根据父级ID获取子权限
     */
    @Transactional(readOnly = true)
    public List<Permission> getChildPermissions(Long parentId) {
        return permissionRepository.findByParentId(parentId);
    }

    /**
     * 根据类型获取权限列表
     */
    @Transactional(readOnly = true)
    public List<Permission> getPermissionsByType(String type) {
        return permissionRepository.findByType(type);
    }

    /**
     * 构建权限树
     */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> buildPermissionTree() {
        List<Permission> allPermissions = permissionRepository.findAll(Sort.by(Sort.Direction.ASC, "sortOrder"));
        return buildTree(allPermissions, null);
    }

    /**
     * 递归构建树形结构
     */
    private List<Map<String, Object>> buildTree(List<Permission> permissions, Long parentId) {
        List<Map<String, Object>> tree = new ArrayList<>();
        
        for (Permission permission : permissions) {
            if ((parentId == null && permission.getParentId() == null) ||
                (parentId != null && parentId.equals(permission.getParentId()))) {
                
                Map<String, Object> node = new HashMap<>();
                node.put("id", permission.getId());
                node.put("name", permission.getName());
                node.put("code", permission.getCode());
                node.put("icon", permission.getIcon());
                node.put("type", permission.getType());
                node.put("status", permission.getStatus());
                
                // 递归查找子节点
                List<Map<String, Object>> children = buildTree(permissions, permission.getId());
                if (!children.isEmpty()) {
                    node.put("children", children);
                }
                
                tree.add(node);
            }
        }
        
        return tree;
    }

    /**
     * 统计启用权限数量
     */
    @Transactional(readOnly = true)
    public long countEnabledPermissions() {
        return permissionRepository.countByStatus(true);
    }

    /**
     * 统计禁用权限数量
     */
    @Transactional(readOnly = true)
    public long countDisabledPermissions() {
        return permissionRepository.countByStatus(false);
    }

    /**
     * 检查权限是否被角色使用
     */
    @Transactional(readOnly = true)
    public boolean isPermissionInUse(Long permissionId) {
        return permissionRepository.countRolesByPermissionId(permissionId) > 0;
    }

    /**
     * 获取使用该权限的角色数量
     */
    @Transactional(readOnly = true)
    public long getRolesCountByPermission(Long permissionId) {
        return permissionRepository.countRolesByPermissionId(permissionId);
    }
}
