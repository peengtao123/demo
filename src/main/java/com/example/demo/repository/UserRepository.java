package com.example.demo.repository;

import com.example.demo.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 用户数据访问接口
 * <p>提供用户的数据库操作方法，继承自JpaRepository获得基础CRUD功能。</p>
 * 
 * @author Demo Team
 * @version 1.0
 * @since 2024-01-01
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * 根据用户名查找用户
     * 
     * @param username 用户名
     * @return 用户实体Optional
     */
    Optional<User> findByUsername(String username);
    
    /**
     * 根据邮箱查找用户
     * 
     * @param email 邮箱地址
     * @return 用户实体Optional
     */
    Optional<User> findByEmail(String email);
    
    /**
     * 根据姓名模糊搜索（忽略大小写）
     * 
     * @param name 姓名关键词
     * @return 用户列表
     */
    List<User> findByNameContainingIgnoreCase(String name);
    
    /**
     * 检查用户名是否已存在
     * 
     * @param username 用户名
     * @return true表示已存在
     */
    boolean existsByUsername(String username);
    
    /**
     * 检查邮箱是否已存在
     * 
     * @param email 邮箱地址
     * @return true表示已存在
     */
    boolean existsByEmail(String email);
    
    /**
     * 根据状态查询用户
     * 
     * @param status 状态（true=启用，false=禁用）
     * @return 用户列表
     */
    List<User> findByStatus(Boolean status);
    
    /**
     * 分页查询所有用户
     * 
     * @param pageable 分页参数
     * @return 分页的用户列表
     */
    Page<User> findAll(Pageable pageable);
    
    /**
     * 根据用户名或姓名模糊搜索（分页）
     * 
     * @param keyword 搜索关键词
     * @param pageable 分页参数
     * @return 分页的用户列表
     */
    @Query("SELECT u FROM User u WHERE u.username LIKE %:keyword% OR u.name LIKE %:keyword% OR u.email LIKE %:keyword%")
    Page<User> searchUsers(@Param("keyword") String keyword, Pageable pageable);
    
    /**
     * 根据角色查询用户
     * 
     * @param roleId 角色ID
     * @return 该角色下的用户列表
     */
    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.id = :roleId")
    List<User> findByRoleId(@Param("roleId") Long roleId);
    
    /**
     * 统计启用/禁用用户数量
     * 
     * @param status 状态
     * @return 该状态的用户总数
     */
    long countByStatus(Boolean status);
}
