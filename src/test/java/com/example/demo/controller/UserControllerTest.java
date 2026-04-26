package com.example.demo.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.demo.dto.UserDTO;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserService;

/**
 * UserController集成测试
 * 通过Service层间接测试Controller功能
 */
@SpringBootTest
class UserControllerTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        // 清空数据库
        userRepository.deleteAll();
    }

    @Test
    void testCreateUserSuccess() {
        UserDTO newUser = new UserDTO("newuser", "newuser@example.com", "New User");
        newUser.setPhone("13900139000");
        newUser.setAge(30);

        assertNull(newUser.getId());
        User created = userService.createUser(newUser);

        assertNotNull(created);
        assertNotNull(created.getId());
        assertEquals("newuser", created.getUsername());
        assertEquals("newuser@example.com", created.getEmail());
        assertEquals("New User", created.getName());
    }

    @Test
    void testGetUserByIdSuccess() {
        // 先创建用户
        UserDTO dto = new UserDTO("testuser", "test@example.com", "Test User");
        User created = userService.createUser(dto);

        // 查询用户
        User found = userService.getUserById(created.getId());

        assertNotNull(found);
        assertEquals(created.getId(), found.getId());
        assertEquals("testuser", found.getUsername());
    }

    @Test
    void testGetAllUsers() {
        // 创建多个用户
        userService.createUser(new UserDTO("user1", "user1@example.com", "User One"));
        userService.createUser(new UserDTO("user2", "user2@example.com", "User Two"));

        List<User> users = userService.getAllUsers();

        assertNotNull(users);
        assertEquals(2, users.size());
    }

    @Test
    void testUpdateUserSuccess() {
        // 先创建用户
        UserDTO dto = new UserDTO("olduser", "old@example.com", "Old User");
        User created = userService.createUser(dto);

        // 更新用户
        UserDTO updateDto = new UserDTO("newuser", "new@example.com", "New User");
        updateDto.setPhone("13900139000");
        updateDto.setAge(30);

        User updated = userService.updateUser(created.getId(), updateDto);

        assertNotNull(updated);
        assertEquals("newuser", updated.getUsername());
        assertEquals("new@example.com", updated.getEmail());
        assertEquals("New User", updated.getName());
        assertEquals("13900139000", updated.getPhone());
        assertEquals(30, updated.getAge());
    }

    @Test
    void testDeleteUserSuccess() {
        // 先创建用户
        UserDTO dto = new UserDTO("deleteuser", "delete@example.com", "Delete User");
        User created = userService.createUser(dto);

        // 删除用户
        userService.deleteUser(created.getId());

        // 验证删除
        assertThrows(RuntimeException.class, () -> {
            userService.getUserById(created.getId());
        });
    }

    @Test
    void testGetUserByUsernameSuccess() {
        // 先创建用户
        UserDTO dto = new UserDTO("searchuser", "search@example.com", "Search User");
        userService.createUser(dto);

        // 根据用户名查询
        User found = userService.getUserByUsername("searchuser");

        assertNotNull(found);
        assertEquals("searchuser", found.getUsername());
    }

    @Test
    void testSearchUsersByName() {
        // 创建测试数据
        userService.createUser(new UserDTO("john1", "john1@example.com", "John Smith"));
        userService.createUser(new UserDTO("john2", "john2@example.com", "John Doe"));
        userService.createUser(new UserDTO("jane", "jane@example.com", "Jane Smith"));

        // 搜索
        List<User> results = userService.searchUsersByName("john");

        assertNotNull(results);
        assertEquals(2, results.size());
    }
}
