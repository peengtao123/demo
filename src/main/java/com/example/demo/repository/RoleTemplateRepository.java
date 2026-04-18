package com.example.demo.repository;

import com.example.demo.entity.RoleTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 角色模板数据访问接口
 * <p>提供角色模板的数据库操作方法，继承自JpaRepository获得基础CRUD功能。</p>
 * 
 * @author Demo Team
 * @version 1.0
 * @since 2024-01-01
 */
@Repository
public interface RoleTemplateRepository extends JpaRepository<RoleTemplate, Long> {
    /**
     * 根据模板编码查找角色模板
     * 
     * @param code 模板编码
     * @return 角色模板实体Optional
     */
    Optional<RoleTemplate> findByCode(String code);
}
