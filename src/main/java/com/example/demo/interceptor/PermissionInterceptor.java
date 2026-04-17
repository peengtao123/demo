package com.example.demo.interceptor;

import com.example.demo.annotation.RequirePermission;
import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * 权限验证拦截器
 */
@Component
public class PermissionInterceptor implements HandlerInterceptor {

    @Autowired
    private UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, 
                            Object handler) throws Exception {
        // 只处理方法级别的注解
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        RequirePermission requirePermission = handlerMethod.getMethodAnnotation(RequirePermission.class);
        
        // 如果方法上没有注解，检查类级别
        if (requirePermission == null) {
            requirePermission = handlerMethod.getBeanType().getAnnotation(RequirePermission.class);
        }
        
        // 没有权限要求，直接放行
        if (requirePermission == null) {
            return true;
        }

        // 获取当前用户
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "未登录");
            return false;
        }

        String username = authentication.getName();
        User user = userService.getUserByUsername(username);
        
        // 获取用户的所有权限编码
        Set<String> userPermissions = user.getRoles().stream()
                .flatMap(role -> role.getPermissions().stream())
                .filter(permission -> permission.getStatus()) // 只考虑启用的权限
                .map(permission -> permission.getCode())
                .collect(Collectors.toSet());

        // 验证权限
        String requiredPermission = requirePermission.value();
        boolean hasPermission = userPermissions.contains(requiredPermission);

        if (!hasPermission) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, 
                "没有权限访问: " + requiredPermission);
            return false;
        }

        return true;
    }
}
