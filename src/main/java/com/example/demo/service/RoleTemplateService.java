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

/**
 * 角色模板服务类 - 提供角色模板管理和基于模板创建角色的功能
 */
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
     *
     * @return 所有角色模板列表
     */
    @Transactional(readOnly = true)
    public List<RoleTemplate> getAllTemplates() {
        return roleTemplateRepository.findAll();
    }

    /**
     * 根据编码获取模板
     *
     * @param code 角色模板编码
     * @return 对应的角色模板对象
     * @throws RuntimeException 如果模板不存在则抛出异常
     */
    @Transactional(readOnly = true)
    public RoleTemplate getTemplateByCode(String code) {
        return roleTemplateRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("角色模板不存在: " + code));
    }

    /**
     * 从模板创建角色
     *
     * @param templateCode 角色模板编码
     * @param roleName 新角色的名称
     * @param description 新角色的描述（可选，为null时使用模板的描述）
     * @return 创建成功的角色对象，包含模板中的所有权限
     * @throws RuntimeException 如果模板不存在或角色名称已存在则抛出异常
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
     *
     * <p>系统启动时自动调用，创建三个默认的角色模板：
     * <ul>
     *   <li>超级管理员模板 - 拥有所有权限</li>
     *   <li>内容编辑者模板 - 可以管理内容和用户</li>
     *   <li>只读用户模板 - 只能查看数据，不能修改</li>
     * </ul>
     * 如果数据库中已有模板，则不会重复初始化。
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
