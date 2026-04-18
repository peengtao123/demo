package com.example.demo.config;

import com.example.demo.interceptor.PermissionInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC配置类
 * <p>配置Spring MVC的核心功能，包括拦截器、视图解析器、消息转换器等。
 * 该类实现了WebMvcConfigurer接口，用于自定义MVC行为。</p>
 * 
 * <h2>主要功能</h2>
 * <ul>
 *   <li>注册自定义拦截器（PermissionInterceptor）</li>
 *   <li>配置拦截器的路径匹配规则</li>
 *   <li>排除不需要拦截的路径（静态资源、登录页面等）</li>
 * </ul>
 * 
 * <h2>拦截器配置</h2>
 * <ul>
 *   <li><strong>拦截路径：</strong>/admin/**（所有管理后台请求）</li>
 *   <li><strong>排除路径：</strong>
 *     <ul>
 *       <li>/admin/login - 登录页面</li>
 *       <li>/admin/logout - 登出接口</li>
 *       <li>/css/**、/js/**、/images/** - 静态资源</li>
 *       <li>/favicon.ico - 网站图标</li>
 *     </ul>
 *   </li>
 * </ul>
 * 
 * <h3>执行顺序</h3>
 * <ol>
 *   <li>请求到达DispatcherServlet</li>
 *   <li>执行PermissionInterceptor.preHandle()</li>
 *   <li>如果preHandle返回true，继续执行Controller方法</li>
 *   <li>如果preHandle返回false，中断请求并返回响应</li>
 *   <li>执行Controller方法</li>
 *   <li>执行PermissionInterceptor.postHandle()</li>
 *   <li>执行PermissionInterceptor.afterCompletion()</li>
 * </ol>
 * 
 * <h3>扩展建议</h3>
 * <p>如需添加更多MVC配置，可以在此类中实现其他WebMvcConfigurer方法：</p>
 * <ul>
 *   <li>addViewControllers() - 配置视图控制器</li>
 *   <li>addResourceHandlers() - 配置静态资源处理</li>
 *   <li>configureMessageConverters() - 配置消息转换器</li>
 *   <li>addCorsMappings() - 配置跨域支持</li>
 * </ul>
 * 
 * @author Demo Team
 * @version 1.0
 * @since 2024-01-01
 * @see PermissionInterceptor
 * @see WebMvcConfigurer
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    /**
     * 权限拦截器
     * <p>用于验证用户是否具有访问特定资源的权限</p>
     */
    @Autowired
    private PermissionInterceptor permissionInterceptor;

    /**
     * 注册拦截器
     * <p>将PermissionInterceptor注册到拦截器链中，并配置拦截规则。</p>
     * 
     * @param registry InterceptorRegistry对象，用于注册拦截器
     * 
     * <p><b>注意：</b>拦截器会按照注册的顺序执行。如果有多个拦截器，
     *           需要注意它们的执行顺序和优先级。</p>
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册权限拦截器，排除静态资源和登录相关路径
        registry.addInterceptor(permissionInterceptor)
                .addPathPatterns("/admin/**")
                .excludePathPatterns(
                    "/admin/login",
                    "/admin/logout",
                    "/css/**",
                    "/js/**",
                    "/images/**",
                    "/favicon.ico"
                );
    }
}
