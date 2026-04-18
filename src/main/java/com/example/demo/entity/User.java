package com.example.demo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * 用户实体类
 * <p>对应数据库中的users表，存储系统用户的基本信息、认证信息和关联关系。</p>
 * 
 * <h2>主要功能</h2>
 * <ul>
 *   <li>用户基本信息管理（用户名、邮箱、姓名、电话等）</li>
 *   <li>用户认证信息（密码加密存储）</li>
 *   <li>多角色关联（通过user_role中间表）</li>
 *   <li>用户状态管理（启用/禁用）</li>
 *   <li>登录信息追踪（最后登录时间、IP）</li>
 *   <li>审计字段自动维护（创建时间、更新时间）</li>
 * </ul>
 * 
 * <h2>数据约束</h2>
 * <ul>
 *   <li>用户名：3-50个字符，全局唯一，必填</li>
 *   <li>邮箱：标准邮箱格式，全局唯一，必填</li>
 *   <li>姓名：最多100个字符，必填</li>
 *   <li>密码：必填，建议加密存储</li>
 *   <li>电话：可选，最多20个字符</li>
 *   <li>头像URL：可选，最多500个字符</li>
 *   <li>备注：可选，最多500个字符</li>
 * </ul>
 * 
 * <h3>关联关系</h3>
 * <ul>
 *   <li>与Role：多对多关系，通过user_role中间表关联</li>
 *   <li>一个用户可以拥有多个角色</li>
 *   <li>使用EAGER加载策略，确保权限验证时能立即获取角色信息</li>
 * </ul>
 * 
 * @author Demo Team
 * @version 2.0
 * @since 2024-01-01
 * @see Role
 * @see Permission
 * @see AuditingEntityListener
 */
@Entity
@Table(name = "users")
@EntityListeners(AuditingEntityListener.class)
public class User {

    /**
     * 用户ID，主键，自增
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 用户名，用于登录和身份标识
     * <p>约束：3-50个字符，全局唯一，不能为空</p>
     */
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 50, message = "用户名长度必须在3-50个字符之间")
    @Column(unique = true, nullable = false, length = 50)
    private String username;

    /**
     * 邮箱地址，用于联系和密码找回
     * <p>约束：标准邮箱格式，全局唯一，不能为空</p>
     */
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    @Column(unique = true, nullable = false, length = 100)
    private String email;

    /**
     * 真实姓名，用于显示
     * <p>约束：最多100个字符，不能为空</p>
     */
    @NotBlank(message = "姓名不能为空")
    @Size(max = 100, message = "姓名长度不能超过100个字符")
    @Column(nullable = false, length = 100)
    private String name;

    /**
     * 联系电话
     * <p>约束：可选，最多20个字符</p>
     */
    @Column(length = 20)
    private String phone;

    /**
     * 年龄
     * <p>约束：可选，整数类型</p>
     */
    private Integer age;

    /**
     * 登录密码（应加密存储）
     * <p>约束：不能为空，建议使用BCrypt等强哈希算法加密</p>
     */
    @NotBlank(message = "密码不能为空")
    @Column(nullable = false)
    private String password;

    /**
     * 角色标识（保留字段，用于向后兼容）
     * <p>默认值为"USER"，新的权限系统使用roles集合</p>
     * 
     * @deprecated 请使用 {@link #roles} 字段进行权限管理
     */
    @Column(length = 50)
    private String role = "USER"; // 默认角色为USER（保留字段用于兼容）

    /**
     * 用户拥有的角色集合
     * <p>关联关系：多对多，通过user_role中间表关联</p>
     * <p>加载策略：EAGER（立即加载），确保权限验证时可用</p>
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_role",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    /**
     * 用户状态
     * <p>true: 启用（可登录），false: 禁用（不可登录）</p>
     * <p>默认值：true（启用）</p>
     */
    @Column(nullable = false)
    private Boolean status = true; // true: 启用, false: 禁用

    /**
     * 头像URL地址
     * <p>约束：可选，最多500个字符，支持HTTP/HTTPS链接或Base64</p>
     */
    @Column(length = 500)
    private String avatar;

    /**
     * 最后登录时间
     * <p>每次成功登录后自动更新</p>
     */
    private LocalDateTime lastLoginTime;

    /**
     * 最后登录IP地址
     * <p>约束：可选，最多50个字符，用于安全审计</p>
     */
    @Column(length = 50)
    private String lastLoginIp;

    /**
     * 备注信息
     * <p>约束：可选，最多500个字符，用于管理员记录特殊说明</p>
     */
    @Column(length = 500)
    private String remark;

    /**
     * 创建时间，由JPA审计功能自动填充
     * <p>特性：不可更新，仅在创建时设置</p>
     */
    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createTime;

    /**
     * 更新时间，由JPA审计功能自动维护
     * <p>特性：每次更新实体时自动刷新</p>
     */
    @LastModifiedDate
    private LocalDateTime updateTime;

    /**
     * 无参构造函数
     * <p>JPA规范要求必须提供无参构造函数</p>
     */
    public User() {
    }

    /**
     * 便捷构造函数
     * 
     * @param username 用户名
     * @param email 邮箱
     * @param name 姓名
     */
    public User(String username, String email, String name) {
        this.username = username;
        this.email = email;
        this.name = name;
    }

    // Getters and Setters
    
    /**
     * 获取用户ID
     * 
     * @return 用户ID
     */
    public Long getId() {
        return id;
    }

    /**
     * 设置用户ID
     * 
     * @param id 用户ID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 获取用户名
     * 
     * @return 用户名
     */
    public String getUsername() {
        return username;
    }

    /**
     * 设置用户名
     * 
     * @param username 用户名
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * 获取邮箱
     * 
     * @return 邮箱地址
     */
    public String getEmail() {
        return email;
    }

    /**
     * 设置邮箱
     * 
     * @param email 邮箱地址
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * 获取姓名
     * 
     * @return 真实姓名
     */
    public String getName() {
        return name;
    }

    /**
     * 设置姓名
     * 
     * @param name 真实姓名
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取电话
     * 
     * @return 联系电话
     */
    public String getPhone() {
        return phone;
    }

    /**
     * 设置电话
     * 
     * @param phone 联系电话
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * 获取年龄
     * 
     * @return 年龄
     */
    public Integer getAge() {
        return age;
    }

    /**
     * 设置年龄
     * 
     * @param age 年龄
     */
    public void setAge(Integer age) {
        this.age = age;
    }

    /**
     * 获取密码
     * 
     * @return 加密后的密码
     */
    public String getPassword() {
        return password;
    }

    /**
     * 设置密码
     * 
     * @param password 明文或加密后的密码
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * 获取角色标识（兼容字段）
     * 
     * @return 角色字符串
     * @deprecated 请使用 {@link #getRoles()} 获取角色集合
     */
    public String getRole() {
        return role;
    }

    /**
     * 设置角色标识（兼容字段）
     * 
     * @param role 角色字符串
     * @deprecated 请使用 {@link #setRoles(Set)} 设置角色集合
     */
    public void setRole(String role) {
        this.role = role;
    }

    /**
     * 获取用户拥有的角色集合
     * 
     * @return 角色集合
     */
    public Set<Role> getRoles() {
        return roles;
    }

    /**
     * 设置用户的角色集合
     * 
     * @param roles 角色集合
     */
    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    /**
     * 获取用户状态
     * 
     * @return true表示启用，false表示禁用
     */
    public Boolean getStatus() {
        return status;
    }

    /**
     * 设置用户状态
     * 
     * @param status true表示启用，false表示禁用
     */
    public void setStatus(Boolean status) {
        this.status = status;
    }

    /**
     * 获取头像URL
     * 
     * @return 头像地址
     */
    public String getAvatar() {
        return avatar;
    }

    /**
     * 设置头像URL
     * 
     * @param avatar 头像地址
     */
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    /**
     * 获取最后登录时间
     * 
     * @return 最后登录时间戳
     */
    public LocalDateTime getLastLoginTime() {
        return lastLoginTime;
    }

    /**
     * 设置最后登录时间
     * 
     * @param lastLoginTime 最后登录时间戳
     */
    public void setLastLoginTime(LocalDateTime lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    /**
     * 获取最后登录IP
     * 
     * @return IP地址
     */
    public String getLastLoginIp() {
        return lastLoginIp;
    }

    /**
     * 设置最后登录IP
     * 
     * @param lastLoginIp IP地址
     */
    public void setLastLoginIp(String lastLoginIp) {
        this.lastLoginIp = lastLoginIp;
    }

    /**
     * 获取备注信息
     * 
     * @return 备注内容
     */
    public String getRemark() {
        return remark;
    }

    /**
     * 设置备注信息
     * 
     * @param remark 备注内容
     */
    public void setRemark(String remark) {
        this.remark = remark;
    }

    /**
     * 获取创建时间
     * 
     * @return 创建时间戳
     */
    public LocalDateTime getCreateTime() {
        return createTime;
    }

    /**
     * 设置创建时间（通常由JPA自动管理）
     * 
     * @param createTime 创建时间戳
     */
    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    /**
     * 获取更新时间
     * 
     * @return 最后更新时间戳
     */
    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    /**
     * 设置更新时间（通常由JPA自动管理）
     * 
     * @param updateTime 更新时间戳
     */
    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    /**
     * 生成用户的字符串表示
     * <p>包含主要字段信息，用于调试和日志记录</p>
     * 
     * @return 用户信息的字符串形式
     */
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", age=" + age +
                ", role='" + role + '\'' +
                ", status=" + status +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}
