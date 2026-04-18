package com.example.demo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * 角色实体类
 * <p>对应数据库中的roles表，用于定义系统中的角色及其权限集合。
 * 角色是权限管理的核心概念，通过角色可以实现RBAC（基于角色的访问控制）。</p>
 * 
 * <h2>主要功能</h2>
 * <ul>
 *   <li>定义系统角色（如管理员、编辑、普通用户等）</li>
 *   <li>关联多个权限，形成权限集合</li>
 *   <li>被多个用户拥有，实现批量权限分配</li>
 *   <li>支持角色状态管理（启用/禁用）</li>
 *   <li>支持角色排序，便于前端展示</li>
 * </ul>
 * 
 * <h2>数据约束</h2>
 * <ul>
 *   <li>角色名称：2-50个字符，全局唯一，必填</li>
 *   <li>描述：可选，最多200个字符</li>
 *   <li>图标：可选，最多50个字符，通常为Bootstrap Icons类名</li>
 *   <li>排序号：可选，默认为0，数值越小越靠前</li>
 * </ul>
 * 
 * <h2>关联关系</h2>
 * <ul>
 *   <li>与User：多对多关系，一个角色可以被多个用户拥有</li>
 *   <li>与Permission：多对多关系，通过role_permission中间表关联</li>
 *   <li>一个角色可以包含多个权限</li>
 * </ul>
 * 
 * <h3>使用场景</h3>
 * <ul>
 *   <li>用户注册时分配默认角色</li>
 *   <li>管理员为用户分配或移除角色</li>
 *   <li>权限验证时检查用户是否拥有特定角色</li>
 *   <li>动态菜单生成时根据角色过滤菜单项</li>
 * </ul>
 * 
 * @author Demo Team
 * @version 1.0
 * @since 2024-01-01
 * @see User
 * @see Permission
 * @see AuditingEntityListener
 */
@Entity
@Table(name = "roles")
@EntityListeners(AuditingEntityListener.class)
public class Role {

    /**
     * 角色ID，主键，自增
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 角色名称，用于标识角色
     * <p>约束：2-50个字符，全局唯一，不能为空</p>
     * <p>示例："超级管理员"、"内容编辑"、"普通用户"</p>
     */
    @NotBlank(message = "角色名称不能为空")
    @Size(min = 2, max = 50, message = "角色名称长度必须在2-50个字符之间")
    @Column(unique = true, nullable = false, length = 50)
    private String name;

    /**
     * 角色描述，说明角色的职责和权限范围
     * <p>约束：可选，最多200个字符</p>
     */
    @Size(max = 200, message = "描述长度不能超过200个字符")
    @Column(length = 200)
    private String description;

    /**
     * 角色图标，用于前端展示
     * <p>约束：可选，最多50个字符</p>
     * <p>建议使用Bootstrap Icons类名，如："bi-shield-lock"、"bi-person"</p>
     */
    @Column(length = 50)
    private String icon;

    /**
     * 角色状态
     * <p>true: 启用（可分配给用户），false: 禁用（不可分配）</p>
     * <p>默认值：true（启用）</p>
     */
    @Column(nullable = false)
    private Boolean status = true;

    /**
     * 排序号，用于控制角色在前端的显示顺序
     * <p>约束：可选，默认为0，数值越小越靠前</p>
     */
    @Column(name = "sort_order")
    private Integer sortOrder = 0;

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
     * 拥有该角色的用户集合
     * <p>关联关系：多对多，Role是反方（Inverse Side）</p>
     * <p>映射：通过User.roles字段中的mappedBy指定</p>
     */
    @ManyToMany(mappedBy = "roles")
    private Set<User> users = new HashSet<>();

    /**
     * 角色拥有的权限集合
     * <p>关联关系：多对多，通过role_permission中间表关联</p>
     * <p>加载策略：默认LAZY（延迟加载），需要时显式fetch</p>
     */
    @ManyToMany
    @JoinTable(
        name = "role_permission",
        joinColumns = @JoinColumn(name = "role_id"),
        inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    private Set<Permission> permissions = new HashSet<>();

    /**
     * 无参构造函数
     * <p>JPA规范要求必须提供无参构造函数</p>
     */
    public Role() {
    }

    /**
     * 便捷构造函数
     * 
     * @param name 角色名称
     * @param description 角色描述
     */
    public Role(String name, String description) {
        this.name = name;
        this.description = description;
    }

    // Getters and Setters
    
    /**
     * 获取角色ID
     * 
     * @return 角色ID
     */
    public Long getId() {
        return id;
    }

    /**
     * 设置角色ID
     * 
     * @param id 角色ID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 获取角色名称
     * 
     * @return 角色名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置角色名称
     * 
     * @param name 角色名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取角色描述
     * 
     * @return 角色描述
     */
    public String getDescription() {
        return description;
    }

    /**
     * 设置角色描述
     * 
     * @param description 角色描述
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * 获取角色图标
     * 
     * @return 图标类名
     */
    public String getIcon() {
        return icon;
    }

    /**
     * 设置角色图标
     * 
     * @param icon 图标类名（如Bootstrap Icons）
     */
    public void setIcon(String icon) {
        this.icon = icon;
    }

    /**
     * 获取角色状态
     * 
     * @return true表示启用，false表示禁用
     */
    public Boolean getStatus() {
        return status;
    }

    /**
     * 设置角色状态
     * 
     * @param status true表示启用，false表示禁用
     */
    public void setStatus(Boolean status) {
        this.status = status;
    }

    /**
     * 获取排序号
     * 
     * @return 排序号
     */
    public Integer getSortOrder() {
        return sortOrder;
    }

    /**
     * 设置排序号
     * 
     * @param sortOrder 排序号
     */
    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
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
     * 获取拥有该角色的用户集合
     * 
     * @return 用户集合
     */
    public Set<User> getUsers() {
        return users;
    }

    /**
     * 设置拥有该角色的用户集合
     * 
     * @param users 用户集合
     */
    public void setUsers(Set<User> users) {
        this.users = users;
    }

    /**
     * 获取角色拥有的权限集合
     * 
     * @return 权限集合
     */
    public Set<Permission> getPermissions() {
        return permissions;
    }

    /**
     * 设置角色的权限集合
     * 
     * @param permissions 权限集合
     */
    public void setPermissions(Set<Permission> permissions) {
        this.permissions = permissions;
    }

    /**
     * 生成角色的字符串表示
     * <p>包含主要字段信息，用于调试和日志记录</p>
     * 
     * @return 角色信息的字符串形式
     */
    @Override
    public String toString() {
        return "Role{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", icon='" + icon + '\'' +
                ", status=" + status +
                ", sortOrder=" + sortOrder +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}
