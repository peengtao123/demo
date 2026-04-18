package com.example.demo.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 权限验证注解
 * <p>用于Controller方法或类上，声明访问该方法所需的权限编码。
 * 配合PermissionInterceptor拦截器使用，实现API级别的细粒度权限控制。</p>
 * 
 * <h2>功能特性</h2>
 * <ul>
 *   <li>支持方法级别和类级别的权限声明</li>
 *   <li>支持单个权限或多个权限验证</li>
 *   <li>支持"所有权限"或"任一权限"的验证模式</li>
 *   <li>无权限时自动返回403 Forbidden响应</li>
 * </ul>
 * 
 * <h2>使用示例</h2>
 * <pre>{@code
 * // 单个权限验证
 * @RequirePermission("user:create")
 * @PostMapping("/users")
 * public ApiResponse createUser(@RequestBody User user) {
 *     // ...
 * }
 * 
 * // 多个权限验证（需要所有权限）
 * @RequirePermission(value = {"user:edit", "user:delete"}, requireAll = true)
 * @DeleteMapping("/users/{id}")
 * public ApiResponse deleteUser(@PathVariable Long id) {
 *     // ...
 * }
 * 
 * // 多个权限验证（只需任一权限）
 * @RequirePermission(value = {"admin:manage", "user:manage"}, requireAll = false)
 * @GetMapping("/dashboard")
 * public ApiResponse getDashboard() {
 *     // ...
 * }
 * 
 * // 类级别声明（应用于所有方法）
 * @RequirePermission("system:config")
 * @RestController
 * @RequestMapping("/api/system")
 * public class SystemConfigController {
 *     // 所有方法都需要system:config权限
 * }
 * }</pre>
 * 
 * <h2>权限验证流程</h2>
 * <ol>
 *   <li>请求到达Controller前，PermissionInterceptor拦截</li>
 *   <li>检查方法或类上是否有@RequirePermission注解</li>
 *   <li>获取当前登录用户的权限集合</li>
 *   <li>根据requireAll参数判断验证逻辑：</li>
 *   <li>requireAll=true：用户必须拥有所有指定权限</li>
 *   <li>requireAll=false：用户只需拥有任一指定权限</li>
 *   <li>验证通过则放行，否则返回403错误</li>
 * </ol>
 * 
 * <h2>注意事项</h2>
 * <ul>
 *   <li>权限编码必须与数据库中Permission.code字段完全匹配</li>
 *   <li>未登录用户会被Security框架拦截，不会到达此注解验证</li>
 *   <li>建议与方法级别的@RequestMapping等注解配合使用</li>
 *   <li>类级别注解会作用于所有方法，方法级别注解会覆盖类级别</li>
 * </ul>
 * 
 * @author Demo Team
 * @version 1.0
 * @since 2024-01-01
 * @see com.example.demo.interceptor.PermissionInterceptor
 * @see com.example.demo.entity.Permission
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequirePermission {
    /**
     * 需要的权限编码
     * <p>可以指定单个权限或多个权限</p>
     * <p>示例："user:create"、{"user:view", "user:edit"}</p>
     * 
     * @return 权限编码数组
     */
    String value();
    
    /**
     * 是否需要所有权限（多个权限时）
     * <p>true：用户必须拥有value中指定的所有权限</p>
     * <p>false：用户只需拥有value中指定的任一权限即可</p>
     * <p>默认值：false（任一权限）</p>
     * 
     * @return 是否需要所有权限
     */
    boolean requireAll() default false;
}
