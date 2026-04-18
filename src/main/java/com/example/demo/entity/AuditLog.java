package com.example.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 权限审计日志实体
 * <p>对应数据库中的audit_logs表，用于记录系统中所有重要的权限相关操作。
 * 审计日志是系统安全和合规性的重要组成部分，提供操作追溯能力。</p>
 * 
 * <h2>主要功能</h2>
 * <ul>
 *   <li>记录用户的关键操作（创建、修改、删除等）</li>
 *   <li>保存操作前后的数据变化（oldValue/newValue）</li>
 *   <li>记录操作人的IP地址和时间戳</li>
 *   <li>支持按操作类型、目标类型进行筛选和查询</li>
 *   <li>为安全审计和问题排查提供数据支持</li>
 * </ul>
 * 
 * <h2>记录场景</h2>
 * <ul>
 *   <li>用户管理：创建用户、修改用户信息、分配角色</li>
 *   <li>角色管理：创建角色、修改角色、分配权限</li>
 *   <li>权限管理：创建权限、修改权限配置</li>
 *   <li>个人信息：修改密码、更新个人资料</li>
 *   <li>系统配置：修改系统参数、调整权限策略</li>
 * </ul>
 * 
 * <h3>操作类型规范</h3>
 * <p>建议使用统一的枚举值，例如：</p>
 * <ul>
 *   <li>CREATE - 创建操作</li>
 *   <li>UPDATE - 更新操作</li>
 *   <li>DELETE - 删除操作</li>
 *   <li>ROLE_ASSIGN - 角色分配</li>
 *   <li>PERMISSION_ASSIGN - 权限分配</li>
 *   <li>PASSWORD_CHANGE - 密码修改</li>
 *   <li>LOGIN - 登录</li>
 *   <li>LOGOUT - 登出</li>
 * </ul>
 * 
 * <h3>数据约束</h3>
 * <ul>
 *   <li>操作人：必填，最多50个字符</li>
 *   <li>操作类型：必填，最多20个字符</li>
 *   <li>目标类型：可选，最多100个字符（如"User"、"Role"）</li>
 *   <li>目标ID：可选，最多100个字符</li>
 *   <li>描述：可选，TEXT类型，详细说明操作内容</li>
 *   <li>旧值：可选，TEXT类型，JSON格式存储修改前的数据</li>
 *   <li>新值：可选，TEXT类型，JSON格式存储修改后的数据</li>
 *   <li>IP地址：可选，最多50个字符</li>
 * </ul>
 * 
 * <h3>使用建议</h3>
 * <ul>
 *   <li>在Service层的关键业务方法中记录审计日志</li>
 *   <li>异步记录日志以避免影响主业务流程性能</li>
 *   <li>定期归档或删除过期日志以控制数据量</li>
 *   <li>敏感信息（如密码）不应记录到日志中</li>
 * </ul>
 * 
 * @author Demo Team
 * @version 1.0
 * @since 2024-01-01
 * @see AuditingEntityListener
 */
@Entity
@Table(name = "audit_logs")
@EntityListeners(AuditingEntityListener.class)
public class AuditLog {

    /**
     * 日志ID，主键，自增
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 操作人用户名
     * <p>约束：必填，最多50个字符</p>
     * <p>来源：当前登录用户的username</p>
     */
    @Column(nullable = false, length = 50)
    private String operator;

    /**
     * 操作类型
     * <p>约束：必填，最多20个字符</p>
     * <p>示例："CREATE"、"UPDATE"、"DELETE"、"ROLE_ASSIGN"</p>
     */
    @Column(nullable = false, length = 20)
    private String operationType;

    /**
     * 操作目标类型
     * <p>约束：可选，最多100个字符</p>
     * <p>示例："User"、"Role"、"Permission"</p>
     */
    @Column(length = 100)
    private String targetType;

    /**
     * 操作目标ID
     * <p>约束：可选，最多100个字符</p>
     * <p>被操作对象的ID，便于追溯具体记录</p>
     */
    @Column(length = 100)
    private String targetId;

    /**
     * 操作描述
     * <p>约束：可选，TEXT类型</p>
     * <p>详细说明本次操作的内容，如"为用户admin分配了ADMIN角色"</p>
     */
    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * 修改前的值
     * <p>约束：可选，TEXT类型</p>
     * <p>通常以JSON格式存储，记录数据变更前的状态</p>
     */
    @Column(columnDefinition = "TEXT")
    private String oldValue;

    /**
     * 修改后的值
     * <p>约束：可选，TEXT类型</p>
     * <p>通常以JSON格式存储，记录数据变更后的状态</p>
     */
    @Column(columnDefinition = "TEXT")
    private String newValue;

    /**
     * 操作人IP地址
     * <p>约束：可选，最多50个字符</p>
     * <p>用于安全审计和异常行为检测</p>
     */
    @Column(length = 50)
    private String ipAddress;

    /**
     * 创建时间，由JPA审计功能自动填充
     * <p>特性：不可更新，仅在创建时设置</p>
     * <p>用途：作为操作发生的时间戳</p>
     */
    @CreatedDate
    @Column(updatable = false, nullable = false)
    private LocalDateTime createTime;

    /**
     * 无参构造函数
     * <p>JPA规范要求必须提供无参构造函数</p>
     */
    public AuditLog() {
    }

    /**
     * 便捷构造函数
     * 
     * @param operator 操作人用户名
     * @param operationType 操作类型
     * @param targetType 目标类型
     * @param targetId 目标ID
     * @param description 操作描述
     */
    public AuditLog(String operator, String operationType, String targetType, 
                   String targetId, String description) {
        this.operator = operator;
        this.operationType = operationType;
        this.targetType = targetType;
        this.targetId = targetId;
        this.description = description;
    }

    // Getters and Setters
    
    /**
     * 获取日志ID
     * 
     * @return 日志ID
     */
    public Long getId() {
        return id;
    }

    /**
     * 设置日志ID
     * 
     * @param id 日志ID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 获取操作人
     * 
     * @return 操作人用户名
     */
    public String getOperator() {
        return operator;
    }

    /**
     * 设置操作人
     * 
     * @param operator 操作人用户名
     */
    public void setOperator(String operator) {
        this.operator = operator;
    }

    /**
     * 获取操作类型
     * 
     * @return 操作类型
     */
    public String getOperationType() {
        return operationType;
    }

    /**
     * 设置操作类型
     * 
     * @param operationType 操作类型
     */
    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    /**
     * 获取目标类型
     * 
     * @return 目标类型
     */
    public String getTargetType() {
        return targetType;
    }

    /**
     * 设置目标类型
     * 
     * @param targetType 目标类型
     */
    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }

    /**
     * 获取目标ID
     * 
     * @return 目标ID
     */
    public String getTargetId() {
        return targetId;
    }

    /**
     * 设置目标ID
     * 
     * @param targetId 目标ID
     */
    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    /**
     * 获取操作描述
     * 
     * @return 操作描述
     */
    public String getDescription() {
        return description;
    }

    /**
     * 设置操作描述
     * 
     * @param description 操作描述
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * 获取旧值
     * 
     * @return 修改前的值（JSON格式）
     */
    public String getOldValue() {
        return oldValue;
    }

    /**
     * 设置旧值
     * 
     * @param oldValue 修改前的值
     */
    public void setOldValue(String oldValue) {
        this.oldValue = oldValue;
    }

    /**
     * 获取新值
     * 
     * @return 修改后的值（JSON格式）
     */
    public String getNewValue() {
        return newValue;
    }

    /**
     * 设置新值
     * 
     * @param newValue 修改后的值
     */
    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }

    /**
     * 获取IP地址
     * 
     * @return 操作人IP地址
     */
    public String getIpAddress() {
        return ipAddress;
    }

    /**
     * 设置IP地址
     * 
     * @param ipAddress IP地址
     */
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    /**
     * 获取创建时间
     * 
     * @return 操作发生的时间戳
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
     * 生成审计日志的字符串表示
     * <p>包含主要字段信息，用于调试和日志记录</p>
     * 
     * @return 审计日志信息的字符串形式
     */
    @Override
    public String toString() {
        return "AuditLog{" 
                + "id=" + id 
                + ", operator='" + operator + '\'' 
                + ", operationType='" + operationType + '\'' 
                + ", targetType='" + targetType + '\'' 
                + ", targetId='" + targetId + '\'' 
                + ", createTime=" + createTime +
                '}';
    }
}
