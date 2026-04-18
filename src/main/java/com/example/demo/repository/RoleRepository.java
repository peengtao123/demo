package com.example.demo.repository;

import com.example.demo.entity.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 角色数据访问接口
 * <p>提供角色的数据库操作方法，继承自JpaRepository获得基础CRUD功能。</p>
 * 
 * @author Demo Team
 * @version 1.0
 * @since 2024-01-01
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    /**
     * 根据角色名称查找角色
     * 
     * @param name 角色名称
     * @return 角色实体Optional
     */
    Optional<Role> findByName(String name);
    
    /**
     * 分页查询所有角色
     * 
     * @param pageable 分页参数
     * @return 分页的角色列表
     */
    Page<Role> findAll(Pageable pageable);
    
    /**
     * 根据名称或描述模糊搜索（分页）
     * 
     * @param keyword 搜索关键词
     * @param pageable 分页参数
     * @return 分页的角色列表
     */
    @Query("SELECT r FROM Role r WHERE r.name LIKE %:keyword% OR r.description LIKE %:keyword%")
    Page<Role> searchRoles(@Param("keyword") String keyword, Pageable pageable);
    
    /**
     * 根据状态查询角色
     * 
     * @param status 状态（true=启用，false=禁用）
     * @return 角色列表
     */
    List<Role> findByStatus(Boolean status);
    
    /**
     * 统计启用/禁用角色数量
     * 
     * @param status 状态
     * @return 该状态的角色总数
     */
    long countByStatus(Boolean status);
    
    /**
     * 检查角色是否被用户使用
     * 
     * @param roleId 角色ID
     * @return 使用该角色的用户数量
     */
    @Query("SELECT COUNT(u) FROM User u JOIN u.roles r WHERE r.id = :roleId")
    long countUsersByRoleId(@Param("roleId") Long roleId);
}
