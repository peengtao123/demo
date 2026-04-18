package com.example.demo.repository;

import com.example.demo.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * UserRepository集成测试
 * 使用H2内存数据库进行测试
 */
@SpringBootTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User testUser1;
    private User testUser2;
    private User testUser3;

    @BeforeEach
    void setUp() {
        // 清空数据库
        userRepository.deleteAll();

        // 创建测试用户
        testUser1 = new User("user1", "user1@example.com", "User One");
        testUser1.setPassword("password123");
        testUser1.setPhone("13800138001");
        testUser1.setAge(25);

        testUser2 = new User("user2", "user2@example.com", "User Two");
        testUser2.setPassword("password123");
        testUser2.setPhone("13800138002");
        testUser2.setAge(30);

        testUser3 = new User("user3", "user3@example.com", "User Three");
        testUser3.setPassword("password123");
        testUser3.setPhone("13800138003");
        testUser3.setAge(35);

        // 保存测试用户
        testUser1 = userRepository.save(testUser1);
        testUser2 = userRepository.save(testUser2);
        testUser3 = userRepository.save(testUser3);
    }

    @Test
    void testFindByUsername() {
        Optional<User> found = userRepository.findByUsername("user1");
        
        assertTrue(found.isPresent());
        assertEquals("user1", found.get().getUsername());
        assertEquals("user1@example.com", found.get().getEmail());
        assertEquals("User One", found.get().getName());
    }

    @Test
    void testFindByUsernameNotFound() {
        Optional<User> found = userRepository.findByUsername("nonexistent");
        
        assertFalse(found.isPresent());
    }

    @Test
    void testFindByEmail() {
        Optional<User> found = userRepository.findByEmail("user2@example.com");
        
        assertTrue(found.isPresent());
        assertEquals("user2", found.get().getUsername());
        assertEquals("user2@example.com", found.get().getEmail());
    }

    @Test
    void testFindByEmailNotFound() {
        Optional<User> found = userRepository.findByEmail("nonexistent@example.com");
        
        assertFalse(found.isPresent());
    }

    @Test
    void testFindByNameContainingIgnoreCase() {
        List<User> users = userRepository.findByNameContainingIgnoreCase("user");
        
        assertEquals(3, users.size());
    }

    @Test
    void testFindByNameContainingIgnoreCasePartialMatch() {
        List<User> users = userRepository.findByNameContainingIgnoreCase("One");
        
        assertEquals(1, users.size());
        assertEquals("User One", users.get(0).getName());
    }

    @Test
    void testFindByNameContainingIgnoreCaseNoMatch() {
        List<User> users = userRepository.findByNameContainingIgnoreCase("NonExistent");
        
        assertTrue(users.isEmpty());
    }

    @Test
    void testExistsByUsername() {
        assertTrue(userRepository.existsByUsername("user1"));
        assertTrue(userRepository.existsByUsername("user2"));
        assertFalse(userRepository.existsByUsername("nonexistent"));
    }

    @Test
    void testExistsByEmail() {
        assertTrue(userRepository.existsByEmail("user1@example.com"));
        assertTrue(userRepository.existsByEmail("user2@example.com"));
        assertFalse(userRepository.existsByEmail("nonexistent@example.com"));
    }

    @Test
    void testSaveAndFindById() {
        User newUser = new User("newuser", "newuser@example.com", "New User");
        User savedUser = userRepository.save(newUser);

        assertNotNull(savedUser.getId());
        
        Optional<User> found = userRepository.findById(savedUser.getId());
        assertTrue(found.isPresent());
        assertEquals("newuser", found.get().getUsername());
        assertEquals("newuser@example.com", found.get().getEmail());
    }

    @Test
    void testUpdateUser() {
        Optional<User> found = userRepository.findByUsername("user1");
        assertTrue(found.isPresent());
        
        User user = found.get();
        user.setName("Updated Name");
        user.setAge(26);
        user.setPhone("13900139000");
        
        userRepository.save(user);

        Optional<User> updated = userRepository.findById(user.getId());
        assertTrue(updated.isPresent());
        assertEquals("Updated Name", updated.get().getName());
        assertEquals(26, updated.get().getAge());
        assertEquals("13900139000", updated.get().getPhone());
    }

    @Test
    void testDeleteUser() {
        Optional<User> found = userRepository.findByUsername("user1");
        assertTrue(found.isPresent());
        
        Long userId = found.get().getId();
        userRepository.deleteById(userId);

        Optional<User> deleted = userRepository.findById(userId);
        assertFalse(deleted.isPresent());
        
        assertEquals(2, userRepository.count());
    }

    @Test
    void testFindAll() {
        List<User> users = userRepository.findAll();
        
        assertEquals(3, users.size());
    }

    @Test
    void testCount() {
        assertEquals(3, userRepository.count());
    }

}
