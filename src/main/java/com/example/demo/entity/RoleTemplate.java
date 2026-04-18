package com.example.demo.entity;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * 角色模板实体类
 * <p>对应数据库中的role_templates表，用于预设常用的角色配置。
 * 角色模板可以快速创建具有标准权限配置的角色，避免重复配置。</p>
 * 
 * @author Demo Team
 * @version 1.0
 * @since 2024-01-01
 */
@Entity
@Table(name = "role_templates")
@EntityListeners(AuditingEntityListener.class)
public class RoleTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String code;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 200)
    private String description;

    @Column(length = 50)
    private String icon;

    @ManyToMany
    @JoinTable(
        name = "template_permission",
        joinColumns = @JoinColumn(name = "template_id"),
        inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    private Set<Permission> permissions = new HashSet<>();

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createTime;

    /**
     * 默认构造函数
     */
    public RoleTemplate() {
    }

    /**
     * 带参数的构造函数
     * 
     * @param code 模板编码
     * @param name 模板名称
     * @param description 模板描述
     */
    public RoleTemplate(String code, String name, String description) {
        this.code = code;
        this.name = name;
        this.description = description;
    }

    // Getters and Setters
    
    /**
     * 获取模板ID
     * 
     * @return 模板ID
     */
    public Long getId() {
        return id;
    }

    /**
     * 设置模板ID
     * 
     * @param id 模板ID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 获取模板编码
     * 
     * @return 模板编码
     */
    public String getCode() {
        return code;
    }

    /**
     * 设置模板编码
     * 
     * @param code 模板编码
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * 获取模板名称
     * 
     * @return 模板名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置模板名称
     * 
     * @param name 模板名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取模板描述
     * 
     * @return 模板描述
     */
    public String getDescription() {
        return description;
    }

    /**
     * 设置模板描述
     * 
     * @param description 模板描述
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * 获取模板图标
     * 
     * @return 图标类名
     */
    public String getIcon() {
        return icon;
    }

    /**
     * 设置模板图标
     * 
     * @param icon 图标类名
     */
    public void setIcon(String icon) {
        this.icon = icon;
    }

    /**
     * 获取权限集合
     * 
     * @return 权限集合
     */
    public Set<Permission> getPermissions() {
        return permissions;
    }

    /**
     * 设置权限集合
     * 
     * @param permissions 权限集合
     */
    public void setPermissions(Set<Permission> permissions) {
        this.permissions = permissions;
    }

    /**
     * 获取创建时间
     * 
     * @return 创建时间
     */
    public LocalDateTime getCreateTime() {
        return createTime;
    }

    /**
     * 设置创建时间
     * 
     * @param createTime 创建时间
     */
    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "RoleTemplate{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
