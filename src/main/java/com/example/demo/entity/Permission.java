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
 * 权限实体类
 * <p>对应数据库中的permissions表，定义系统中的最小权限单元。
 * 权限是RBAC模型中的核心概念，用于细粒度控制用户对系统资源的访问。</p>
 * 
 * <h2>主要功能</h2>
 * <ul>
 *   <li>定义系统权限（如菜单访问、按钮操作、API调用等）</li>
 *   <li>支持树形结构（通过parentId实现父子关系）</li>
 *   <li>区分权限类型（菜单、按钮、API等）</li>
 *   <li>被多个角色拥有，实现权限复用</li>
 *   <li>支持权限状态管理（启用/禁用）</li>
 *   <li>支持权限排序和图标展示</li>
 * </ul>
 * 
 * <h2>数据约束</h2>
 * <ul>
 *   <li>权限名称：2-100个字符，全局唯一，必填</li>
 *   <li>权限编码：2-100个字符，全局唯一，必填（用于代码中验证）</li>
 *   <li>描述：可选，最多200个字符</li>
 *   <li>图标：可选，最多50个字符，通常为Bootstrap Icons类名</li>
 *   <li>父ID：可选，用于构建树形结构，null表示根节点</li>
 *   <li>排序号：可选，默认为0，数值越小越靠前</li>
 *   <li>类型：可选，默认为"MENU"，可选值：MENU/BUTTON/API</li>
 * </ul>
 * 
 * <h3>关联关系</h3>
 * <ul>
 *   <li>与Role：多对多关系，一个权限可以被多个角色拥有</li>
 *   <li>自引用：通过parentId实现树形层级结构</li>
 * </ul>
 * 
 * <h3>权限编码规范</h3>
 * <p>建议采用"模块:操作"的命名格式，例如：</p>
 * <ul>
 *   <li>user:view - 查看用户列表</li>
 *   <li>user:create - 创建用户</li>
 *   <li>user:edit - 编辑用户</li>
 *   <li>user:delete - 删除用户</li>
 *   <li>role:menu - 角色管理菜单入口</li>
 * </ul>
 * 
 * <h3>使用场景</h3>
 * <ul>
 *   <li>前端按钮级权限控制（perm:hasPermission）</li>
 *   <li>后端API接口权限拦截（@RequirePermission）</li>
 *   <li>动态菜单生成和过滤</li>
 *   <li>角色权限分配和管理</li>
 * </ul>
 * 
 * @author Demo Team
 * @version 1.0
 * @since 2024-01-01
 * @see Role
 * @see User
 * @see AuditingEntityListener
 */
@Entity
@Table(name = "permissions")
@EntityListeners(AuditingEntityListener.class)
public class Permission {

    /**
     * 权限ID，主键，自增
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 权限名称，用于显示
     * <p>约束：2-100个字符，全局唯一，不能为空</p>
     * <p>示例："查看用户"、"创建角色"、"删除权限"</p>
     */
    @NotBlank(message = "权限名称不能为空")
    @Size(min = 2, max = 100, message = "权限名称长度必须在2-100个字符之间")
    @Column(unique = true, nullable = false, length = 100)
    private String name;

    /**
     * 权限编码，用于代码中进行权限验证
     * <p>约束：2-100个字符，全局唯一，不能为空</p>
     * <p>命名规范：建议使用"模块:操作"格式，如"user:view"</p>
     */
    @NotBlank(message = "权限编码不能为空")
    @Size(min = 2, max = 100, message = "权限编码长度必须在2-100个字符之间")
    @Column(unique = true, nullable = false, length = 100)
    private String code;

    /**
     * 权限描述，说明权限的具体用途
     * <p>约束：可选，最多200个字符</p>
     */
    @Size(max = 200, message = "描述长度不能超过200个字符")
    @Column(length = 200)
    private String description;

    /**
     * 权限图标，用于前端菜单展示
     * <p>约束：可选，最多50个字符</p>
     * <p>建议使用Bootstrap Icons类名，如："bi-person"、"bi-gear"</p>
     */
    @Column(length = 50)
    private String icon;

    /**
     * 权限状态
     * <p>true: 启用（可分配给角色），false: 禁用（不可分配）</p>
     * <p>默认值：true（启用）</p>
     */
    @Column(nullable = false)
    private Boolean status = true;

    /**
     * 父权限ID，用于构建树形结构
     * <p>约束：可选，null表示根节点</p>
     * <p>用途：实现菜单的层级关系（一级菜单、二级菜单等）</p>
     */
    @Column(name = "parent_id")
    private Long parentId;

    /**
     * 排序号，用于控制权限在前端的显示顺序
     * <p>约束：可选，默认为0，数值越小越靠前</p>
     * <p>同一父节点下的子节点按此字段排序</p>
     */
    @Column(name = "sort_order")
    private Integer sortOrder = 0;

    /**
     * 权限类型，区分不同种类的权限
     * <p>约束：可选，默认为"MENU"</p>
     * <p>可选值：</p>
     * <ul>
     *   <li>MENU - 菜单权限（控制菜单项显示）</li>
     *   <li>BUTTON - 按钮权限（控制按钮显示）</li>
     *   <li>API - API权限（控制接口访问）</li>
     * </ul>
     */
    @Column(length = 50)
    private String type = "MENU";

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
     * 拥有该权限的角色集合
     * <p>关联关系：多对多，Permission是反方（Inverse Side）</p>
     * <p>映射：通过Role.permissions字段中的mappedBy指定</p>
     */
    @ManyToMany(mappedBy = "permissions")
    private Set<Role> roles = new HashSet<>();

    /**
     * 无参构造函数
     * <p>JPA规范要求必须提供无参构造函数</p>
     */
    public Permission() {
    }

    /**
     * 便捷构造函数
     * 
     * @param name 权限名称
     * @param code 权限编码
     * @param description 权限描述
     */
    public Permission(String name, String code, String description) {
        this.name = name;
        this.code = code;
        this.description = description;
    }

    // Getters and Setters
    
    /**
     * 获取权限ID
     * 
     * @return 权限ID
     */
    public Long getId() {
        return id;
    }

    /**
     * 设置权限ID
     * 
     * @param id 权限ID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 获取权限名称
     * 
     * @return 权限名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置权限名称
     * 
     * @param name 权限名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取权限编码
     * 
     * @return 权限编码
     */
    public String getCode() {
        return code;
    }

    /**
     * 设置权限编码
     * 
     * @param code 权限编码
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * 获取权限描述
     * 
     * @return 权限描述
     */
    public String getDescription() {
        return description;
    }

    /**
     * 设置权限描述
     * 
     * @param description 权限描述
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * 获取权限图标
     * 
     * @return 图标类名
     */
    public String getIcon() {
        return icon;
    }

    /**
     * 设置权限图标
     * 
     * @param icon 图标类名（如Bootstrap Icons）
     */
    public void setIcon(String icon) {
        this.icon = icon;
    }

    /**
     * 获取权限状态
     * 
     * @return true表示启用，false表示禁用
     */
    public Boolean getStatus() {
        return status;
    }

    /**
     * 设置权限状态
     * 
     * @param status true表示启用，false表示禁用
     */
    public void setStatus(Boolean status) {
        this.status = status;
    }

    /**
     * 获取父权限ID
     * 
     * @return 父权限ID，null表示根节点
     */
    public Long getParentId() {
        return parentId;
    }

    /**
     * 设置父权限ID
     * 
     * @param parentId 父权限ID
     */
    public void setParentId(Long parentId) {
        this.parentId = parentId;
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
     * 获取权限类型
     * 
     * @return 权限类型（MENU/BUTTON/API）
     */
    public String getType() {
        return type;
    }

    /**
     * 设置权限类型
     * 
     * @param type 权限类型
     */
    public void setType(String type) {
        this.type = type;
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
     * 获取拥有该权限的角色集合
     * 
     * @return 角色集合
     */
    public Set<Role> getRoles() {
        return roles;
    }

    /**
     * 设置拥有该权限的角色集合
     * 
     * @param roles 角色集合
     */
    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    /**
     * 生成权限的字符串表示
     * <p>包含主要字段信息，用于调试和日志记录</p>
     * 
     * @return 权限信息的字符串形式
     */
    @Override
    public String toString() {
        return "Permission{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", code='" + code + '\'' +
                ", description='" + description + '\'' +
                ", icon='" + icon + '\'' +
                ", status=" + status +
                ", parentId=" + parentId +
                ", sortOrder=" + sortOrder +
                ", type='" + type + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}
