package com.example.demo.repository;

import com.example.demo.entity.Permission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {
    Optional<Permission> findByCode(String code);
    Optional<Permission> findByName(String name);
    
    // 分页查询所有权限
    Page<Permission> findAll(Pageable pageable);
    
    // 根据名称、编码或描述模糊搜索（分页）
    @Query("SELECT p FROM Permission p WHERE p.name LIKE %:keyword% OR p.code LIKE %:keyword% OR p.description LIKE %:keyword%")
    Page<Permission> searchPermissions(@Param("keyword") String keyword, Pageable pageable);
    
    // 根据状态查询权限
    List<Permission> findByStatus(Boolean status);
    
    // 根据父级ID查询子权限
    List<Permission> findByParentId(Long parentId);
    
    // 根据类型查询权限
    List<Permission> findByType(String type);
    
    // 统计启用/禁用权限数量
    long countByStatus(Boolean status);
    
    // 检查权限是否被角色使用
    @Query("SELECT COUNT(r) FROM Role r JOIN r.permissions p WHERE p.id = :permissionId")
    long countRolesByPermissionId(@Param("permissionId") Long permissionId);
}
