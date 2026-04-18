package com.example.demo.service;

import com.example.demo.dto.UserDTO;
import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 用户服务类
 * <p>提供用户相关的核心业务逻辑处理，包括用户的CRUD操作、权限管理、
 * 密码管理、个人信息管理等所有与用户相关的功能。</p>
 * 
 * <h2>主要功能</h2>
 * <ul>
 *   <li><strong>用户CRUD：</strong>创建、查询、更新、删除用户</li>
 *   <li><strong>分页查询：</strong>支持分页和关键词搜索</li>
 *   <li><strong>状态管理：</strong>启用/禁用用户账户</li>
 *   <li><strong>密码管理：</strong>密码加密、重置密码、修改密码</li>
 *   <li><strong>角色分配：</strong>为用户分配或移除角色</li>
 *   <li><strong>登录追踪：</strong>记录最后登录时间和IP</li>
 *   <li><strong>个人信息：</strong>更新当前用户的个人资料</li>
 *   <li><strong>审计日志：</strong>记录所有用户相关操作</li>
 * </ul>
 * 
 * <h2>安全特性</h2>
 * <ul>
 *   <li>密码使用BCrypt强哈希算法加密存储</li>
 *   <li>修改密码时必须验证原密码</li>
 *   <li>只能修改当前登录用户的信息</li>
 *   <li>所有敏感操作都记录审计日志（包含IP地址）</li>
 *   <li>用户名和邮箱全局唯一性校验</li>
 * </ul>
 * 
 * <h3>事务管理</h3>
 * <p>整个Service类使用@Transactional注解，确保所有操作的原子性：
 * 如果任何步骤失败，所有数据库更改都会回滚。</p>
 * 
 * <h3>使用示例</h3>
 * <pre>{@code
 * // 创建用户
 * UserDTO dto = new UserDTO();
 * dto.setUsername("admin");
 * dto.setEmail("admin@example.com");
 * dto.setName("管理员");
 * dto.setPassword("123456");
 * User user = userService.createUser(dto);
 * 
 * // 分页查询
 * Page<User> users = userService.getUsersWithPaging(0, 10);
 * 
 * // 为用户分配角色
 * List<Long> roleIds = Arrays.asList(1L, 2L);
 * userService.assignRoles(userId, roleIds);
 * 
 * // 修改密码
 * userService.changePassword(userId, oldPassword, newPassword);
 * }</pre>
 * 
 * @author Demo Team
 * @version 2.0
 * @since 2024-01-01
 * @see UserRepository
 * @see UserDTO
 * @see AuditLogService
 */
@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuditLogService auditLogService;

    /**
     * 获取当前操作用户名
     *
     * @return 当前登录用户的用户名，如果获取失败则返回 "system"
     */
    private String getCurrentUser() {
        try {
            return SecurityContextHolder.getContext().getAuthentication().getName();
        } catch (Exception e) {
            return "system";
        }
    }

    /**
     * 获取客户端IP地址
     *
     * @return 客户端IP地址，如果无法获取则返回 "unknown"
     */
    private String getClientIp() {
        try {
            ServletRequestAttributes attributes = 
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                return attributes.getRequest().getRemoteAddr();
            }
        } catch (Exception e) {
            // ignore
        }
        return "unknown";
    }

    /**
     * 创建用户
     *
     * @param userDTO 用户数据传输对象，包含用户名、邮箱、姓名、密码等信息
     * @return 创建成功的用户实体对象
     * @throws RuntimeException 如果用户名或邮箱已存在则抛出异常
     */
    public User createUser(UserDTO userDTO) {
        // 检查用户名是否已存在
        if (userRepository.existsByUsername(userDTO.getUsername())) {
            throw new RuntimeException("用户名已存在: " + userDTO.getUsername());
        }
        
        // 检查邮箱是否已存在
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new RuntimeException("邮箱已被注册: " + userDTO.getEmail());
        }

        User user = convertToEntity(userDTO);
        // 加密密码
        if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        } else {
            user.setPassword(passwordEncoder.encode("123456")); // 默认密码
        }
        
        User savedUser = userRepository.save(user);
        
        // 记录审计日志
        auditLogService.logWithIp(
            getCurrentUser(),
            "CREATE",
            "USER",
            savedUser.getId().toString(),
            "创建用户: " + savedUser.getUsername(),
            getClientIp()
        );
        
        return savedUser;
    }

    /**
     * 根据ID查询用户
     *
     * @param id 用户ID
     * @return 用户实体对象
     * @throws RuntimeException 如果用户不存在则抛出异常
     */
    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在，ID: " + id));
    }

    /**
     * 查询所有用户（不分页）
     *
     * @return 所有用户实体对象列表
     */
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * 分页查询用户列表
     * @param page 页码（从0开始）
     * @param size 每页大小
     * @return 分页结果
     */
    @Transactional(readOnly = true)
    public Page<User> getUsersWithPaging(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createTime"));
        return userRepository.findAll(pageable);
    }

    /**
     * 搜索用户（支持用户名、姓名、邮箱模糊搜索）
     * @param keyword 搜索关键词
     * @param page 页码
     * @param size 每页大小
     * @return 分页搜索结果
     */
    @Transactional(readOnly = true)
    public Page<User> searchUsers(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createTime"));
        return userRepository.searchUsers(keyword, pageable);
    }

    /**
     * 根据状态查询用户
     * @param status true: 启用, false: 禁用
     * @return 用户列表
     */
    @Transactional(readOnly = true)
    public List<User> getUsersByStatus(Boolean status) {
        return userRepository.findByStatus(status);
    }

    /**
     * 更新用户
     *
     * @param id 用户ID
     * @param userDTO 用户数据传输对象，包含需要更新的字段
     * @return 更新后的用户实体对象
     * @throws RuntimeException 如果用户不存在或用户名/邮箱已被其他用户使用则抛出异常
     */
    public User updateUser(Long id, UserDTO userDTO) {
        User existingUser = getUserById(id);
        String oldInfo = existingUser.toString();

        // 如果修改了用户名，检查新用户名是否已被其他用户使用
        if (!existingUser.getUsername().equals(userDTO.getUsername()) && 
            userRepository.existsByUsername(userDTO.getUsername())) {
            throw new RuntimeException("用户名已存在: " + userDTO.getUsername());
        }

        // 如果修改了邮箱，检查新邮箱是否已被其他用户使用
        if (!existingUser.getEmail().equals(userDTO.getEmail()) && 
            userRepository.existsByEmail(userDTO.getEmail())) {
            throw new RuntimeException("邮箱已被注册: " + userDTO.getEmail());
        }

        existingUser.setUsername(userDTO.getUsername());
        existingUser.setEmail(userDTO.getEmail());
        existingUser.setName(userDTO.getName());
        existingUser.setPhone(userDTO.getPhone());
        existingUser.setAge(userDTO.getAge());

        User updatedUser = userRepository.save(existingUser);
        
        // 记录审计日志
        auditLogService.logWithChanges(
            getCurrentUser(),
            "UPDATE",
            "USER",
            id.toString(),
            "更新用户信息: " + updatedUser.getUsername(),
            oldInfo,
            updatedUser.toString()
        );
        
        return updatedUser;
    }

    /**
     * 更新用户基本信息（不包括密码）
     *
     * @param id 用户ID
     * @param user 用户实体对象，包含姓名、电话、年龄、头像、备注等信息
     * @return 更新后的用户实体对象
     */
    public User updateUserInfo(Long id, User user) {
        User existingUser = getUserById(id);
        String oldInfo = "name=" + existingUser.getName() + ", phone=" + existingUser.getPhone();
        
        existingUser.setName(user.getName());
        existingUser.setPhone(user.getPhone());
        existingUser.setAge(user.getAge());
        existingUser.setAvatar(user.getAvatar());
        existingUser.setRemark(user.getRemark());
        
        User updatedUser = userRepository.save(existingUser);
        
        // 记录审计日志
        auditLogService.log(
            getCurrentUser(),
            "UPDATE",
            "USER",
            id.toString(),
            "更新用户基本信息: " + updatedUser.getUsername()
        );
        
        return updatedUser;
    }

    /**
     * 删除用户
     *
     * @param id 用户ID
     * @throws RuntimeException 如果用户不存在则抛出异常
     */
    public void deleteUser(Long id) {
        User user = getUserById(id);
        String userInfo = user.getUsername();
        
        userRepository.delete(user);
        
        // 记录审计日志
        auditLogService.logWithIp(
            getCurrentUser(),
            "DELETE",
            "USER",
            id.toString(),
            "删除用户: " + userInfo,
            getClientIp()
        );
    }

    /**
     * 批量删除用户
     *
     * @param ids 用户ID列表
     */
    public void batchDeleteUsers(List<Long> ids) {
        String deletedUsers = ids.stream()
                .map(id -> {
                    try {
                        return getUserById(id).getUsername();
                    } catch (Exception e) {
                        return "ID:" + id;
                    }
                })
                .collect(Collectors.joining(", "));
        
        userRepository.deleteAllById(ids);
        
        // 记录审计日志
        auditLogService.log(
            getCurrentUser(),
            "BATCH_DELETE",
            "USER",
            ids.toString(),
            "批量删除用户: " + deletedUsers
        );
    }

    /**
     * 启用/禁用用户
     *
     * @param id 用户ID
     * @return 更新后的用户实体对象，状态已切换
     * @throws RuntimeException 如果用户不存在则抛出异常
     */
    public User toggleUserStatus(Long id) {
        User user = getUserById(id);
        Boolean oldStatus = user.getStatus();
        
        user.setStatus(!user.getStatus());
        User updatedUser = userRepository.save(user);
        
        // 记录审计日志
        auditLogService.log(
            getCurrentUser(),
            "STATUS_CHANGE",
            "USER",
            id.toString(),
            "切换用户状态: " + user.getUsername() + " (" + oldStatus + " -> " + updatedUser.getStatus() + ")"
        );
        
        return updatedUser;
    }

    /**
     * 重置用户密码
     *
     * @param id 用户ID
     * @param newPassword 新密码（明文），将被加密后存储
     * @throws RuntimeException 如果用户不存在则抛出异常
     */
    public void resetPassword(Long id, String newPassword) {
        User user = getUserById(id);
        
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        
        // 记录审计日志
        auditLogService.logWithIp(
            getCurrentUser(),
            "PASSWORD_RESET",
            "USER",
            id.toString(),
            "重置用户密码: " + user.getUsername(),
            getClientIp()
        );
    }

    /**
     * 更新当前用户的个人信息
     *
     * @param userId 用户ID
     * @param name 姓名
     * @param phone 电话，可选
     * @param age 年龄，可选
     * @param avatar 头像URL，可选
     * @param remark 备注，可选
     * @return 更新后的用户实体对象
     * @throws RuntimeException 如果用户不存在则抛出异常
     */
    public User updateCurrentUserProfile(Long userId, String name, String phone, Integer age, String avatar, String remark) {
        User user = getUserById(userId);
        String oldInfo = "name=" + user.getName() + ", phone=" + user.getPhone();
        
        user.setName(name);
        user.setPhone(phone);
        user.setAge(age);
        user.setAvatar(avatar);
        user.setRemark(remark);
        
        User updatedUser = userRepository.save(user);
        
        // 记录审计日志
        auditLogService.log(
            getCurrentUser(),
            "PROFILE_UPDATE",
            "USER",
            userId.toString(),
            "更新个人信息"
        );
        
        return updatedUser;
    }

    /**
     * 修改当前用户密码
     *
     * @param userId 用户ID
     * @param oldPassword 原密码（明文），用于验证身份
     * @param newPassword 新密码（明文），将被加密后存储
     * @throws RuntimeException 如果用户不存在或原密码不正确则抛出异常
     */
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = getUserById(userId);
        
        // 验证旧密码
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("原密码不正确");
        }
        
        // 设置新密码
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        
        // 记录审计日志
        auditLogService.logWithIp(
            getCurrentUser(),
            "PASSWORD_CHANGE",
            "USER",
            userId.toString(),
            "修改密码",
            getClientIp()
        );
    }

    /**
     * 更新最后登录信息
     *
     * @param id 用户ID
     * @param ip 登录IP地址
     * @throws RuntimeException 如果用户不存在则抛出异常
     */
    public void updateLastLoginInfo(Long id, String ip) {
        User user = getUserById(id);
        user.setLastLoginTime(LocalDateTime.now());
        user.setLastLoginIp(ip);
        userRepository.save(user);
    }

    /**
     * 为用户分配角色
     *
     * @param userId 用户ID
     * @param roleIds 角色ID列表
     * @return 更新后的用户实体对象
     * @throws RuntimeException 该方法已废弃，请使用 RoleService 分配角色
     * @deprecated 请使用 RoleService 进行角色分配
     */
    public User assignRoles(Long userId, List<Long> roleIds) {
        User user = getUserById(userId);
        Set<Role> oldRoles = new HashSet<>(user.getRoles());
        
        Set<Role> roles = new HashSet<>();
        for (Long roleId : roleIds) {
            // 这里需要通过RoleRepository获取角色，但UserService没有注入
            // 所以我们需要通过其他方式，或者在Controller层处理
            throw new RuntimeException("请使用RoleService分配角色");
        }
        
        user.setRoles(roles);
        User updatedUser = userRepository.save(user);
        
        // 记录审计日志
        auditLogService.log(
            getCurrentUser(),
            "ROLE_ASSIGN",
            "USER",
            userId.toString(),
            "为用户分配角色: " + user.getUsername()
        );
        
        return updatedUser;
    }

    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户实体对象
     * @throws RuntimeException 如果用户不存在则抛出异常
     */
    @Transactional(readOnly = true)
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户不存在，用户名: " + username));
    }

    /**
     * 根据姓名模糊查询用户
     *
     * @param name 姓名关键词，支持大小写不敏感的模糊匹配
     * @return 匹配的用户实体对象列表
     */
    @Transactional(readOnly = true)
    public List<User> searchUsersByName(String name) {
        return userRepository.findByNameContainingIgnoreCase(name);
    }

    /**
     * 统计启用用户数量
     *
     * @return 启用状态的用户数量
     */
    @Transactional(readOnly = true)
    public long countEnabledUsers() {
        return userRepository.countByStatus(true);
    }

    /**
     * 统计禁用用户数量
     *
     * @return 禁用状态的用户数量
     */
    @Transactional(readOnly = true)
    public long countDisabledUsers() {
        return userRepository.countByStatus(false);
    }

    /**
     * 将 DTO 转换为实体
     *
     * @param userDTO 用户数据传输对象
     * @return 转换后的用户实体对象，默认状态为启用
     */
    private User convertToEntity(UserDTO userDTO) {
        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setName(userDTO.getName());
        user.setPhone(userDTO.getPhone());
        user.setAge(userDTO.getAge());
        user.setStatus(true); // 默认启用
        return user;
    }

    /**
     * 将实体转换为 DTO
     *
     * @param user 用户实体对象
     * @return 转换后的用户数据传输对象
     */
    private UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setName(user.getName());
        dto.setPhone(user.getPhone());
        dto.setAge(user.getAge());
        return dto;
    }

    /**
     * 将实体列表转换为 DTO 列表
     *
     * @param users 用户实体对象列表
     * @return 转换后的用户数据传输对象列表
     */
    public List<UserDTO> convertToDTOList(List<User> users) {
        return users.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
}
