package com.example.demo.service;

import com.example.demo.dto.UserDTO;
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
import java.util.List;
import java.util.stream.Collectors;

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
     */
    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在，ID: " + id));
    }

    /**
     * 查询所有用户（不分页）
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
     * 更新最后登录信息
     */
    public void updateLastLoginInfo(Long id, String ip) {
        User user = getUserById(id);
        user.setLastLoginTime(LocalDateTime.now());
        user.setLastLoginIp(ip);
        userRepository.save(user);
    }

    /**
     * 为用户分配角色
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
     */
    @Transactional(readOnly = true)
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户不存在，用户名: " + username));
    }

    /**
     * 根据姓名模糊查询用户
     */
    @Transactional(readOnly = true)
    public List<User> searchUsersByName(String name) {
        return userRepository.findByNameContainingIgnoreCase(name);
    }

    /**
     * 统计启用用户数量
     */
    @Transactional(readOnly = true)
    public long countEnabledUsers() {
        return userRepository.countByStatus(true);
    }

    /**
     * 统计禁用用户数量
     */
    @Transactional(readOnly = true)
    public long countDisabledUsers() {
        return userRepository.countByStatus(false);
    }

    /**
     * 将 DTO 转换为实体
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
     */
    public List<UserDTO> convertToDTOList(List<User> users) {
        return users.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
}
