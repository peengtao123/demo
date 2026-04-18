package com.example.demo.repository;

import com.example.demo.entity.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 审计日志数据访问接口
 * <p>提供审计日志的数据库操作方法，继承自JpaRepository获得基础CRUD功能。</p>
 * 
 * @author Demo Team
 * @version 1.0
 * @since 2024-01-01
 */
@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    
    /**
     * 分页查询所有日志
     * 
     * @param pageable 分页参数
     * @return 分页的审计日志列表
     */
    Page<AuditLog> findAll(Pageable pageable);
    
    /**
     * 根据操作人查询日志
     * 
     * @param operator 操作人用户名
     * @param pageable 分页参数
     * @return 分页的审计日志列表
     */
    Page<AuditLog> findByOperator(String operator, Pageable pageable);
    
    /**
     * 根据操作类型查询日志
     * 
     * @param operationType 操作类型（如CREATE、UPDATE、DELETE等）
     * @param pageable 分页参数
     * @return 分页的审计日志列表
     */
    Page<AuditLog> findByOperationType(String operationType, Pageable pageable);
    
    /**
     * 根据目标类型查询日志
     * 
     * @param targetType 目标类型（如USER、ROLE、PERMISSION等）
     * @param pageable 分页参数
     * @return 分页的审计日志列表
     */
    Page<AuditLog> findByTargetType(String targetType, Pageable pageable);
    
    /**
     * 根据时间范围查询日志
     * 
     * @param start 开始时间
     * @param end 结束时间
     * @param pageable 分页参数
     * @return 分页的审计日志列表
     */
    Page<AuditLog> findByCreateTimeBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);
    
    /**
     * 根据操作人和时间范围查询日志
     * 
     * @param operator 操作人用户名
     * @param start 开始时间
     * @param end 结束时间
     * @param pageable 分页参数
     * @return 分页的审计日志列表
     */
    Page<AuditLog> findByOperatorAndCreateTimeBetween(String operator, LocalDateTime start, 
                                                      LocalDateTime end, Pageable pageable);
    
    /**
     * 统计指定操作类型的数量
     * 
     * @param operationType 操作类型
     * @return 该操作类型的日志总数
     */
    long countByOperationType(String operationType);
    
    /**
     * 获取最近的操作日志
     * 
     * @return 最近的10条审计日志列表
     */
    List<AuditLog> findTop10ByOrderByCreateTimeDesc();
}
