package com.example.demo.dialect;

import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
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
 * 用法: <button perm:hasPermission="user:create">创建用户</button>
 */
public class HasPermissionAttributeTagProcessor extends AbstractAttributeTagProcessor {

    private static final String ATTR_NAME = "hasPermission";
    private static final int PRECEDENCE = 1000;

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
