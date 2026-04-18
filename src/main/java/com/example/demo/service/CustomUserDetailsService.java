package com.example.demo.service;

import com.example.demo.entity.Permission;
import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 自定义用户详情服务 - 用于Spring Security认证
 * 
 * <p>实现 UserDetailsService 接口，提供从数据库加载用户信息的功能。
 * 在用户登录时，Spring Security 会调用此服务获取用户详情和权限信息。</p>
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    /**
     * 根据用户名加载用户详情
     *
     * <p>此方法由 Spring Security 在用户登录时自动调用。
     * 从数据库中查询用户信息，并构建包含角色权限的 UserDetails 对象。</p>
     *
     * @param username 用户名
     * @return 包含用户信息和权限的 UserDetails 对象
     * @throws UsernameNotFoundException 如果用户不存在则抛出异常
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("用户不存在: " + username));

        // 收集用户的所有权限（包括角色和具体权限）
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        
        // 添加角色权限
        Set<Role> roles = user.getRoles();
        if (roles != null && !roles.isEmpty()) {
            for (Role role : roles) {
                authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName()));
            }
        } else {
            // 如果没有配置角色，使用默认的role字段
            authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole()));
        }
        
        // 这里可以添加具体权限，如果需要的话
        // 例如：从角色的permissions中获取
        
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                authorities
        );
    }
}
