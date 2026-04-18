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

/**
 * 权限数据访问接口
 * <p>提供权限的数据库操作方法，继承自JpaRepository获得基础CRUD功能。</p>
 * 
 * @author Demo Team
 * @version 1.0
 * @since 2024-01-01
 */
@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {
    /**
     * 根据权限编码查找权限
     * 
     * @param code 权限编码
     * @return 权限实体Optional
     */
    Optional<Permission> findByCode(String code);
    
    /**
     * 根据权限名称查找权限
     * 
     * @param name 权限名称
     * @return 权限实体Optional
     */
    Optional<Permission> findByName(String name);
    
    /**
     * 分页查询所有权限
     * 
     * @param pageable 分页参数
     * @return 分页的权限列表
     */
    Page<Permission> findAll(Pageable pageable);
    
    /**
     * 根据名称、编码或描述模糊搜索（分页）
     * 
     * @param keyword 搜索关键词
     * @param pageable 分页参数
     * @return 分页的权限列表
     */
    @Query("SELECT p FROM Permission p WHERE p.name LIKE %:keyword% OR p.code LIKE %:keyword% OR p.description LIKE %:keyword%")
    Page<Permission> searchPermissions(@Param("keyword") String keyword, Pageable pageable);
    
    /**
     * 根据状态查询权限
     * 
     * @param status 状态（true=启用，false=禁用）
     * @return 权限列表
     */
    List<Permission> findByStatus(Boolean status);
    
    /**
     * 根据父级ID查询子权限
     * 
     * @param parentId 父级权限ID
     * @return 子权限列表
     */
    List<Permission> findByParentId(Long parentId);
    
    /**
     * 根据类型查询权限
     * 
     * @param type 权限类型（MENU/BUTTON/API等）
     * @return 权限列表
     */
    List<Permission> findByType(String type);
    
    /**
     * 统计启用/禁用权限数量
     * 
     * @param status 状态
     * @return 该状态的权限总数
     */
    long countByStatus(Boolean status);
    
    /**
     * 检查权限是否被角色使用
     * 
     * @param permissionId 权限ID
     * @return 使用该权限的角色数量
     */
    @Query("SELECT COUNT(r) FROM Role r JOIN r.permissions p WHERE p.id = :permissionId")
    long countRolesByPermissionId(@Param("permissionId") Long permissionId);
}
