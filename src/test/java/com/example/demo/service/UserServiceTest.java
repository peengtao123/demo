package com.example.demo.service;

import com.example.demo.dto.UserDTO;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * UserService单元测试
 * 使用Mockito模拟Repository层
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private UserDTO testUserDTO;

    @BeforeEach
    void setUp() {
        // 创建测试用户
        testUser = new User("testuser", "test@example.com", "Test User");
        testUser.setId(1L);
        testUser.setPhone("13800138000");
        testUser.setAge(25);

        // 创建测试DTO
        testUserDTO = new UserDTO("testuser", "test@example.com", "Test User");
        testUserDTO.setPhone("13800138000");
        testUserDTO.setAge(25);
    }

    @Test
    void testCreateUserSuccess() {
        // 模拟Repository行为
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // 执行测试
        User result = userService.createUser(testUserDTO);

        // 验证结果
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("test@example.com", result.getEmail());
        assertEquals("Test User", result.getName());

        // 验证方法调用
        verify(userRepository).existsByUsername("testuser");
        verify(userRepository).existsByEmail("test@example.com");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testCreateUserUsernameExists() {
        // 模拟用户名已存在
        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        // 执行测试并验证异常
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.createUser(testUserDTO);
        });

        assertEquals("用户名已存在: testuser", exception.getMessage());
        
        // 验证不会调用save方法
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testCreateUserEmailExists() {
        // 模拟邮箱已存在
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        // 执行测试并验证异常
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.createUser(testUserDTO);
        });

        assertEquals("邮箱已被注册: test@example.com", exception.getMessage());
        
        // 验证不会调用save方法
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testGetUserByIdSuccess() {
        // 模拟Repository行为
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // 执行测试
        User result = userService.getUserById(1L);

        // 验证结果
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("testuser", result.getUsername());

        // 验证方法调用
        verify(userRepository).findById(1L);
    }

    @Test
    void testGetUserByIdNotFound() {
        // 模拟用户不存在
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // 执行测试并验证异常
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.getUserById(999L);
        });

        assertEquals("用户不存在，ID: 999", exception.getMessage());
    }

    @Test
    void testGetAllUsers() {
        // 模拟返回多个用户
        User user2 = new User("user2", "user2@example.com", "User Two");
        user2.setId(2L);
        
        List<User> users = Arrays.asList(testUser, user2);
        when(userRepository.findAll()).thenReturn(users);

        // 执行测试
        List<User> result = userService.getAllUsers();

        // 验证结果
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("testuser", result.get(0).getUsername());
        assertEquals("user2", result.get(1).getUsername());

        // 验证方法调用
        verify(userRepository).findAll();
    }

    @Test
    void testUpdateUserSuccess() {
        // 模拟更新场景
        UserDTO updateDTO = new UserDTO("updateduser", "updated@example.com", "Updated User");
        updateDTO.setPhone("13900139000");
        updateDTO.setAge(30);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.existsByUsername("updateduser")).thenReturn(false);
        when(userRepository.existsByEmail("updated@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // 执行测试
        User result = userService.updateUser(1L, updateDTO);

        // 验证结果
        assertNotNull(result);
        assertEquals("updateduser", result.getUsername());
        assertEquals("updated@example.com", result.getEmail());
        assertEquals("Updated User", result.getName());
        assertEquals("13900139000", result.getPhone());
        assertEquals(30, result.getAge());

        // 验证方法调用
        verify(userRepository).findById(1L);
        verify(userRepository).existsByUsername("updateduser");
        verify(userRepository).existsByEmail("updated@example.com");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testUpdateUserNotFound() {
        // 模拟用户不存在
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // 执行测试并验证异常
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.updateUser(999L, testUserDTO);
        });

        assertEquals("用户不存在，ID: 999", exception.getMessage());
    }

    @Test
    void testUpdateUserUsernameExists() {
        // 模拟用户名被其他用户使用
        UserDTO updateDTO = new UserDTO("otheruser", "test@example.com", "Test User");
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.existsByUsername("otheruser")).thenReturn(true);

        // 执行测试并验证异常
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.updateUser(1L, updateDTO);
        });

        assertEquals("用户名已存在: otheruser", exception.getMessage());
        
        // 验证不会调用save方法
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testUpdateUserEmailExists() {
        // 模拟邮箱被其他用户使用
        UserDTO updateDTO = new UserDTO("testuser", "other@example.com", "Test User");
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.existsByEmail("other@example.com")).thenReturn(true);

        // 执行测试并验证异常
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.updateUser(1L, updateDTO);
        });

        assertEquals("邮箱已被注册: other@example.com", exception.getMessage());
        
        // 验证不会调用save方法
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testDeleteUserSuccess() {
        // 模拟删除场景
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        doNothing().when(userRepository).delete(testUser);

        // 执行测试
        userService.deleteUser(1L);

        // 验证方法调用
        verify(userRepository).findById(1L);
        verify(userRepository).delete(testUser);
    }

    @Test
    void testDeleteUserNotFound() {
        // 模拟用户不存在
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // 执行测试并验证异常
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.deleteUser(999L);
        });

        assertEquals("用户不存在，ID: 999", exception.getMessage());
        
        // 验证不会调用delete方法
        verify(userRepository, never()).delete(any(User.class));
    }

    @Test
    void testGetUserByUsernameSuccess() {
        // 模拟Repository行为
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        // 执行测试
        User result = userService.getUserByUsername("testuser");

        // 验证结果
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());

        // 验证方法调用
        verify(userRepository).findByUsername("testuser");
    }

    @Test
    void testGetUserByUsernameNotFound() {
        // 模拟用户不存在
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // 执行测试并验证异常
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.getUserByUsername("nonexistent");
        });

        assertEquals("用户不存在，用户名: nonexistent", exception.getMessage());
    }

    @Test
    void testSearchUsersByName() {
        // 模拟搜索结果
        User user2 = new User("user2", "user2@example.com", "John Smith");
        user2.setId(2L);
        
        List<User> users = Arrays.asList(testUser, user2);
        when(userRepository.findByNameContainingIgnoreCase("john")).thenReturn(users);

        // 执行测试
        List<User> result = userService.searchUsersByName("john");

        // 验证结果
        assertNotNull(result);
        assertEquals(2, result.size());

        // 验证方法调用
        verify(userRepository).findByNameContainingIgnoreCase("john");
    }

    @Test
    void testConvertToDTOList() {
        // 准备测试数据
        User user2 = new User("user2", "user2@example.com", "User Two");
        user2.setId(2L);
        
        List<User> users = Arrays.asList(testUser, user2);

        // 执行测试
        List<com.example.demo.dto.UserDTO> result = userService.convertToDTOList(users);

        // 验证结果
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("testuser", result.get(0).getUsername());
        assertEquals("user2", result.get(1).getUsername());
    }
}
