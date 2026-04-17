package com.example.demo.service;

import com.example.demo.entity.AuditLog;
import com.example.demo.repository.AuditLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class AuditLogService {

    @Autowired
    private AuditLogRepository auditLogRepository;

    /**
     * 记录审计日志
     */
    public void log(String operator, String operationType, String targetType, 
                   String targetId, String description) {
        AuditLog log = new AuditLog(operator, operationType, targetType, targetId, description);
        auditLogRepository.save(log);
    }

    /**
     * 记录审计日志（带IP地址）
     */
    public void logWithIp(String operator, String operationType, String targetType, 
                         String targetId, String description, String ipAddress) {
        AuditLog log = new AuditLog(operator, operationType, targetType, targetId, description);
        log.setIpAddress(ipAddress);
        auditLogRepository.save(log);
    }

    /**
     * 记录审计日志（带变更前后值）
     */
    public void logWithChanges(String operator, String operationType, String targetType, 
                              String targetId, String description, 
                              String oldValue, String newValue) {
        AuditLog log = new AuditLog(operator, operationType, targetType, targetId, description);
        log.setOldValue(oldValue);
        log.setNewValue(newValue);
        auditLogRepository.save(log);
    }

    /**
     * 分页查询审计日志
     */
    @Transactional(readOnly = true)
    public Page<AuditLog> getAuditLogs(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createTime"));
        return auditLogRepository.findAll(pageable);
    }

    /**
     * 根据操作人查询
     */
    @Transactional(readOnly = true)
    public Page<AuditLog> findByOperator(String operator, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createTime"));
        return auditLogRepository.findByOperator(operator, pageable);
    }

    /**
     * 根据操作类型查询
     */
    @Transactional(readOnly = true)
    public Page<AuditLog> findByOperationType(String operationType, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createTime"));
        return auditLogRepository.findByOperationType(operationType, pageable);
    }

    /**
     * 根据目标类型查询
     */
    @Transactional(readOnly = true)
    public Page<AuditLog> findByTargetType(String targetType, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createTime"));
        return auditLogRepository.findByTargetType(targetType, pageable);
    }

    /**
     * 根据时间范围查询
     */
    @Transactional(readOnly = true)
    public Page<AuditLog> findByTimeRange(LocalDateTime start, LocalDateTime end, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createTime"));
        return auditLogRepository.findByCreateTimeBetween(start, end, pageable);
    }

    /**
     * 获取最近的操作日志
     */
    @Transactional(readOnly = true)
    public List<AuditLog> getRecentLogs() {
        return auditLogRepository.findTop10ByOrderByCreateTimeDesc();
    }

    /**
     * 统计指定操作类型的数量
     */
    @Transactional(readOnly = true)
    public long countByOperationType(String operationType) {
        return auditLogRepository.countByOperationType(operationType);
    }
}
