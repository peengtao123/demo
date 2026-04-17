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

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);
    
    // 分页查询所有角色
    Page<Role> findAll(Pageable pageable);
    
    // 根据名称或描述模糊搜索（分页）
    @Query("SELECT r FROM Role r WHERE r.name LIKE %:keyword% OR r.description LIKE %:keyword%")
    Page<Role> searchRoles(@Param("keyword") String keyword, Pageable pageable);
    
    // 根据状态查询角色
    List<Role> findByStatus(Boolean status);
    
    // 统计启用/禁用角色数量
    long countByStatus(Boolean status);
    
    // 检查角色是否被用户使用
    @Query("SELECT COUNT(u) FROM User u JOIN u.roles r WHERE r.id = :roleId")
    long countUsersByRoleId(@Param("roleId") Long roleId);
}
