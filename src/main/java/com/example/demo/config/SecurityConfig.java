package com.example.demo.config;

import com.example.demo.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security安全配置类
 * <p>配置应用程序的安全策略，包括认证、授权、密码加密等核心安全功能。
 * 这是Spring Security的核心配置类，控制整个应用的安全行为。</p>
 * 
 * <h2>主要功能</h2>
 * <ul>
 *   <li>配置URL访问权限规则（哪些路径需要认证、哪些可以公开访问）</li>
 *   <li>配置表单登录（登录页面、成功/失败跳转）</li>
 *   <li>配置登出逻辑（会话失效、Cookie清除）</li>
 *   <li>配置密码编码器（BCrypt强哈希算法）</li>
 *   <li>配置认证管理器（AuthenticationManager）</li>
 * </ul>
 * 
 * <h2>访问权限规则</h2>
 * <ul>
 *   <li><strong>公开访问：</strong>/login、/css/**、/js/**、/images/**（静态资源）</li>
 *   <li><strong>管理员访问：</strong>/admin/**（需要ADMIN角色）</li>
 *   <li><strong>认证用户访问：</strong>/api/**、/pages/**（需要登录）</li>
 *   <li><strong>其他请求：</strong>需要认证</li>
 * </ul>
 * 
 * <h3>登录配置</h3>
 * <ul>
 *   <li>登录页面：/login</li>
 *   <li>登录处理URL：/login（POST）</li>
 *   <li>登录成功跳转：/admin/dashboard</li>
 *   <li>登录失败跳转：/login?error=true</li>
 * </ul>
 * 
 * <h3>登出配置</h3>
 * <ul>
 *   <li>登出URL：/logout（POST）</li>
 *   <li>登出成功跳转：/login?logout=true</li>
 *   <li>会话失效：是</li>
 *   <li>删除Cookie：JSESSIONID</li>
 * </ul>
 * 
 * <h3>安全建议</h3>
 * <ul>
 *   <li>生产环境应启用CSRF保护（当前已禁用，仅用于开发简化）</li>
 *   <li>建议使用HTTPS协议传输敏感数据</li>
 *   <li>定期更新密码策略和加密算法</li>
 *   <li>配置适当的会话超时时间</li>
 *   <li>添加登录失败次数限制以防止暴力破解</li>
 * </ul>
 * 
 * @author Demo Team
 * @version 1.0
 * @since 2024-01-01
 * @see CustomUserDetailsService
 * @see BCryptPasswordEncoder
 * @see SecurityFilterChain
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * 自定义用户详情服务
     * <p>用于从数据库加载用户信息和权限</p>
     */
    private final CustomUserDetailsService userDetailsService;

    /**
     * 构造函数注入CustomUserDetailsService
     * 
     * @param userDetailsService 自定义用户详情服务
     */
    public SecurityConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    /**
     * 配置密码编码器
     * <p>使用BCrypt强哈希算法对密码进行加密。BCrypt具有以下优势：</p>
     * <ul>
     *   <li>自动加盐，防止彩虹表攻击</li>
     *   <li>可调节的计算强度，适应硬件发展</li>
     *   <li>单向加密，无法解密</li>
     * </ul>
     * 
     * @return BCryptPasswordEncoder实例
     * 
     * <p><b>注意：</b>BCrypt默认strength为10，可根据安全需求调整（范围4-31）</p>
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 配置认证管理器
     * <p>创建AuthenticationManager Bean，负责用户认证流程。</p>
     * <p>使用DaoAuthenticationProvider从数据库验证用户凭据。</p>
     * 
     * @return AuthenticationManager实例
     * 
     * <p><b>注意：</b>Spring Boot 4.x要求通过构造函数传入UserDetailsService，
     *           不再支持无参构造函数配合setUserDetailsService的方式</p>
     */
    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return new ProviderManager(authProvider);
    }

    /**
     * 配置安全过滤链
     * <p>定义HTTP安全策略，包括URL访问控制、表单登录、登出等配置。</p>
     * 
     * @param http HttpSecurity对象，用于配置安全规则
     * @return SecurityFilterChain实例
     * @throws Exception 配置异常
     * 
     * <p><b>配置顺序要求：</b>
     * <ol>
     *   <li>首先配置authorizeHttpRequests定义访问规则</li>
     *   <li>然后配置formLogin定义登录行为</li>
     *   <li>接着配置logout定义登出行为</li>
     *   <li>最后配置csrf等其他安全选项</li>
     * </ol>
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorize -> authorize
                // 公开访问的页面
                .requestMatchers("/login", "/css/**", "/js/**", "/images/**").permitAll()
                // 管理页面 - 需要ADMIN角色
                .requestMatchers("/admin/**").hasRole("ADMIN")
                // API端点需要认证
                .requestMatchers("/api/**").authenticated()
                // 普通页面访问需要认证
                .requestMatchers("/pages/**").authenticated()
                // 其他请求需要认证
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/admin/dashboard", true) // 登录成功后跳转到管理仪表盘
                .failureUrl("/login?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            .csrf(csrf -> csrf.disable()); // 简化配置，生产环境应启用CSRF

        return http.build();
    }
}
