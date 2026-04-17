package com.example.demo.service;

import com.example.demo.entity.Permission;
import com.example.demo.entity.Role;
import com.example.demo.entity.RoleTemplate;
import com.example.demo.repository.PermissionRepository;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.RoleTemplateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class RoleTemplateService {

    @Autowired
    private RoleTemplateRepository roleTemplateRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    /**
     * 获取所有角色模板
     */
    @Transactional(readOnly = true)
    public List<RoleTemplate> getAllTemplates() {
        return roleTemplateRepository.findAll();
    }

    /**
     * 根据编码获取模板
     */
    @Transactional(readOnly = true)
    public RoleTemplate getTemplateByCode(String code) {
        return roleTemplateRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("角色模板不存在: " + code));
    }

    /**
     * 从模板创建角色
     */
    public Role createRoleFromTemplate(String templateCode, String roleName, String description) {
        RoleTemplate template = getTemplateByCode(templateCode);
        
        // 检查角色名是否已存在
        if (roleRepository.findByName(roleName).isPresent()) {
            throw new RuntimeException("角色名称已存在: " + roleName);
        }
        
        // 创建新角色
        Role role = new Role();
        role.setName(roleName);
        role.setDescription(description != null ? description : template.getDescription());
        role.setIcon(template.getIcon());
        role.setStatus(true);
        role.setSortOrder(0);
        
        // 复制模板的权限
        Set<Permission> permissions = new HashSet<>(template.getPermissions());
        role.setPermissions(permissions);
        
        return roleRepository.save(role);
    }

    /**
     * 初始化默认角色模板
     */
    @Transactional
    public void initializeDefaultTemplates() {
        // 如果已有模板，不重复初始化
        if (!roleTemplateRepository.findAll().isEmpty()) {
            return;
        }

        // 1. 超级管理员模板
        RoleTemplate adminTemplate = new RoleTemplate("ADMIN_TEMPLATE", "超级管理员模板", "拥有系统所有权限");
        adminTemplate.setIcon("👑");
        List<Permission> allPermissions = permissionRepository.findAll();
        adminTemplate.getPermissions().addAll(allPermissions);
        roleTemplateRepository.save(adminTemplate);

        // 2. 内容编辑者模板
        RoleTemplate editorTemplate = new RoleTemplate("EDITOR_TEMPLATE", "内容编辑者模板", "可以管理内容和用户");
        editorTemplate.setIcon("✏️");
        List<Permission> editorPermissions = permissionRepository.findByType("MENU");
        editorTemplate.getPermissions().addAll(editorPermissions);
        roleTemplateRepository.save(editorTemplate);

        // 3. 只读用户模板
        RoleTemplate viewerTemplate = new RoleTemplate("VIEWER_TEMPLATE", "只读用户模板", "只能查看数据，不能修改");
        viewerTemplate.setIcon("👁️");
        List<Permission> viewPermissions = permissionRepository.findAll().stream()
                .filter(p -> p.getCode().contains(":view") || p.getCode().contains(":list"))
                .toList();
        viewerTemplate.getPermissions().addAll(viewPermissions);
        roleTemplateRepository.save(viewerTemplate);
    }
}
