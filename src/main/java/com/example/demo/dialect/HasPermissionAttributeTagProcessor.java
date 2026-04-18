package com.example.demo.dialect;

import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.AbstractAttributeTagProcessor;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.spring6.context.SpringContextUtils;
import org.thymeleaf.templatemode.TemplateMode;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * hasPermission属性处理器
 * <p>用于在Thymeleaf模板中实现基于权限的视图元素控制。当用户没有指定权限时，
 * 对应的HTML元素将被从DOM中移除，从而实现细粒度的前端权限控制。</p>
 * 
 * <h2>功能说明</h2>
 * <ul>
 *   <li>获取当前登录用户的认证信息</li>
 *   <li>查询用户拥有的所有角色及权限</li>
 *   <li>验证用户是否具有指定的权限编码</li>
 *   <li>无权限时自动移除HTML元素</li>
 * </ul>
 * 
 * <h2>用法示例</h2>
 * <pre>{@code
 * <!-- 按钮级权限控制 -->
 * <button perm:hasPermission="user:create">创建用户</button>
 * 
 * <!-- 菜单项权限控制 -->
 * <a href="/admin/users" perm:hasPermission="user:view">用户管理</a>
 * 
 * <!-- 区域内容权限控制 -->
 * <div perm:hasPermission="system:config">
 *     <p>系统配置信息</p>
 * </div>
 * }</pre>
 * 
 * <h2>处理流程</h2>
 * <ol>
 *   <li>从Spring上下文中获取UserService</li>
 *   <li>通过SecurityContextHolder获取当前认证信息</li>
 *   <li>根据用户名查询用户实体及其关联的角色和权限</li>
 *   <li>过滤出状态为启用的权限编码集合</li>
 *   <li>检查是否包含属性值指定的权限</li>
 *   <li>若无权限则调用structureHandler.removeElement()移除元素</li>
 * </ol>
 * 
 * <h2>注意事项</h2>
 * <ul>
 *   <li>该处理器仅在HTML模板模式下工作</li>
 *   <li>权限编码需与数据库中Permission.code字段完全匹配</li>
 *   <li>未认证或认证失败的用户将无法看到任何受保护的元素</li>
 *   <li>异常情况下会安全地隐藏元素而非抛出错误</li>
 * </ul>
 * 
 * @author Demo Team
 * @version 1.0
 * @since 2024-01-01
 * @see AbstractAttributeTagProcessor
 * @see UserService
 * @see com.example.demo.entity.Permission
 */
public class HasPermissionAttributeTagProcessor extends AbstractAttributeTagProcessor {

    /**
     * 自定义属性名称（不含前缀）
     * <p>在模板中使用格式：perm:hasPermission="permissionCode"</p>
     */
    private static final String ATTR_NAME = "hasPermission";
    
    /**
     * 处理器优先级
     * <p>数值越小优先级越高，1000表示中等优先级，确保在其他基础处理器之后执行</p>
     */
    private static final int PRECEDENCE = 1000;

    /**
     * 构造函数
     * 
     * @param dialectPrefix Thymeleaf方言前缀，通常为"perm"
     */
    public HasPermissionAttributeTagProcessor(String dialectPrefix) {
        super(
            TemplateMode.HTML,
            dialectPrefix,
            null,
            false,
            ATTR_NAME,
            true,
            PRECEDENCE,
            true
        );
    }

    /**
     * 处理hasPermission属性的核心方法
     * <p>该方法在Thymeleaf渲染模板时被调用，负责验证用户权限并决定是否显示对应元素。</p>
     * 
     * @param context Thymeleaf模板上下文，包含模板渲染所需的所有信息
     * @param tag 当前处理的HTML标签元素
     * @param attributeName 属性名称对象，包含属性的命名空间和本地名称
     * @param attributeValue 属性值，即需要验证的权限编码（如"user:create"）
     * @param structureHandler 结构处理器，用于修改模板结构（如移除元素、设置属性等）
     * 
     * <p><b>实现逻辑：</b>
     * <ol>
     *   <li>从Spring应用上下文中获取UserService Bean</li>
     *   <li>通过SecurityContextHolder获取当前用户的Authentication对象</li>
     *   <li>若用户未认证或未登录，直接移除元素</li>
     *   <li>根据用户名查询用户实体，获取其所有角色和权限</li>
     *   <li>过滤出启用状态的权限，提取权限编码集合</li>
     *   <li>检查权限集合是否包含attributeValue指定的权限</li>
     *   <li>若不包含则移除元素，否则保留元素正常渲染</li>
     *   <li>任何异常发生时都会安全地移除元素</li>
     * </ol>
     */
    @Override
    protected void doProcess(ITemplateContext context, IProcessableElementTag tag,
                            AttributeName attributeName, String attributeValue,
                            IElementTagStructureHandler structureHandler) {
        
        // 获取Spring上下文
        org.springframework.context.ApplicationContext appContext = 
            SpringContextUtils.getApplicationContext(context);
        UserService userService = appContext.getBean(UserService.class);

        // 获取当前用户
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            structureHandler.removeElement();
            return;
        }

        String username = authentication.getName();
        try {
            User user = userService.getUserByUsername(username);
            
            // 获取用户的所有权限编码
            Set<String> userPermissions = user.getRoles().stream()
                    .flatMap(role -> role.getPermissions().stream())
                    .filter(permission -> permission.getStatus())
                    .map(permission -> permission.getCode())
                    .collect(Collectors.toSet());

            // 检查是否有指定权限
            if (!userPermissions.contains(attributeValue)) {
                structureHandler.removeElement();
            }
        } catch (Exception e) {
            // 如果获取用户失败，隐藏元素
            structureHandler.removeElement();
        }
    }
}

