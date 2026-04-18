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
 * <p>实现API级别的细粒度权限控制，通过检查@RequirePermission注解来验证用户权限。
 * 该拦截器在Controller方法执行前进行权限校验，无权限则返回403错误。</p>
 * 
 * <h2>工作原理</h2>
 * <ol>
 *   <li>拦截所有/admin/**路径的请求（由WebMvcConfig配置）</li>
 *   <li>检查目标方法或类上是否有@RequirePermission注解</li>
 *   <li>从SecurityContext获取当前登录用户</li>
 *   <li>查询用户的所有角色和权限</li>
 *   <li>验证用户是否具有所需的权限编码</li>
 *   <li>有权限则放行，无权限则返回403 Forbidden</li>
 * </ol>
 * 
 * <h2>注解优先级</h2>
 * <ul>
 *   <li><strong>方法级别优先：</strong>如果方法上有@RequirePermission，使用方法上的注解</li>
 *   <li><strong>类级别降级：</strong>如果方法上没有，则使用类上的注解</li>
 *   <li><strong>无注解放行：</strong>如果都没有，直接放行（不限制权限）</li>
 * </ul>
 * 
 * <h2>权限验证逻辑</h2>
 * <ul>
 *   <li>获取用户所有角色的权限集合</li>
 *   <li>过滤出状态为启用的权限（status=true）</li>
 *   <li>提取权限编码（code字段）</li>
 *   <li>检查是否包含注解中指定的权限编码</li>
 * </ul>
 * 
 * <h2>响应处理</h2>
 * <ul>
 *   <li><strong>未登录：</strong>返回401 Unauthorized</li>
 *   <li><strong>无权限：</strong>返回403 Forbidden，附带权限编码信息</li>
 *   <li><strong>有权限：</strong>返回true，继续执行Controller方法</li>
 * </ul>
 * 
 * <h2>使用场景</h2>
 * <ul>
 *   <li>管理后台的敏感操作（删除用户、修改配置等）</li>
 *   <li>API接口的访问控制</li>
 *   <li>需要细粒度权限控制的业务功能</li>
 * </ul>
 * 
 * <h2>注意事项</h2>
 * <ul>
 *   <li>仅对标注了@RequirePermission的方法生效</li>
 *   <li>权限编码必须与数据库中Permission.code完全匹配</li>
 *   <li>用户必须有至少一个角色，否则无法获取权限</li>
 *   <li>禁用的权限不会被计入验证范围</li>
 * </ul>
 * 
 * @author Demo Team
 * @version 1.0
 * @since 2024-01-01
 * @see RequirePermission
 * @see HandlerInterceptor
 * @see UserService
 */
@Component
public class PermissionInterceptor implements HandlerInterceptor {

    /**
     * 用户服务，用于查询用户信息和权限
     */
    @Autowired
    private UserService userService;

    /**
     * 预处理请求，在执行Controller方法前进行权限验证
     * <p>这是拦截器的核心方法，负责检查用户是否有权限访问目标资源。</p>
     * 
     * @param request HTTP请求对象
     * @param response HTTP响应对象
     * @param handler 被调用的处理器（通常是HandlerMethod）
     * @return true表示放行，false表示中断请求
     * @throws Exception 处理异常
     * 
     * <p><b>验证流程：</b>
     * <ol>
     *   <li>检查handler是否为HandlerMethod类型，非方法调用直接放行</li>
     *   <li>获取方法上的@RequirePermission注解</li>
     *   <li>如果方法上没有，尝试获取类上的注解</li>
     *   <li>没有注解则放行（无需权限验证）</li>
     *   <li>从SecurityContext获取当前认证信息</li>
     *   <li>未认证返回401错误</li>
     *   <li>查询用户实体及其权限集合</li>
     *   <li>验证是否有所需权限</li>
     *   <li>无权限返回403错误</li>
     *   <li>有权限返回true，继续执行</li>
     * </ol>
     */
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
