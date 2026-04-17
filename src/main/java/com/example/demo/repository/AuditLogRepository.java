package com.example.demo.repository;

import com.example.demo.entity.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    
    // 分页查询所有日志
    Page<AuditLog> findAll(Pageable pageable);
    
    // 根据操作人查询
    Page<AuditLog> findByOperator(String operator, Pageable pageable);
    
    // 根据操作类型查询
    Page<AuditLog> findByOperationType(String operationType, Pageable pageable);
    
    // 根据目标类型查询
    Page<AuditLog> findByTargetType(String targetType, Pageable pageable);
    
    // 根据时间范围查询
    Page<AuditLog> findByCreateTimeBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);
    
    // 根据操作人和时间范围查询
    Page<AuditLog> findByOperatorAndCreateTimeBetween(String operator, LocalDateTime start, 
                                                      LocalDateTime end, Pageable pageable);
    
    // 统计指定操作类型的数量
    long countByOperationType(String operationType);
    
    // 获取最近的操作日志
    List<AuditLog> findTop10ByOrderByCreateTimeDesc();
}
