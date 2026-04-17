package com.example.demo.entity;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * User实体类单元测试
 */
@SpringBootTest
class UserTest {

    @Test
    void testDefaultConstructor() {
        User user = new User();
        assertNotNull(user);
        assertNull(user.getId());
        assertNull(user.getUsername());
        assertNull(user.getEmail());
        assertNull(user.getName());
        assertNull(user.getPhone());
        assertNull(user.getAge());
        assertNull(user.getCreateTime());
        assertNull(user.getUpdateTime());
    }

    @Test
    void testParameterizedConstructor() {
        User user = new User("testuser", "test@example.com", "Test User");
        assertNotNull(user);
        assertNull(user.getId());
        assertEquals("testuser", user.getUsername());
        assertEquals("test@example.com", user.getEmail());
        assertEquals("Test User", user.getName());
        assertNull(user.getPhone());
        assertNull(user.getAge());
    }

    @Test
    void testSettersAndGetters() {
        User user = new User();
        
        Long id = 1L;
        String username = "testuser";
        String email = "test@example.com";
        String name = "Test User";
        String phone = "1234567890";
        Integer age = 25;
        LocalDateTime createTime = LocalDateTime.now();
        LocalDateTime updateTime = LocalDateTime.now().plusHours(1);

        user.setId(id);
        user.setUsername(username);
        user.setEmail(email);
        user.setName(name);
        user.setPhone(phone);
        user.setAge(age);
        user.setCreateTime(createTime);
        user.setUpdateTime(updateTime);

        assertEquals(id, user.getId());
        assertEquals(username, user.getUsername());
        assertEquals(email, user.getEmail());
        assertEquals(name, user.getName());
        assertEquals(phone, user.getPhone());
        assertEquals(age, user.getAge());
        assertEquals(createTime, user.getCreateTime());
        assertEquals(updateTime, user.getUpdateTime());
    }

    @Test
    void testToString() {
        User user = new User("testuser", "test@example.com", "Test User");
        user.setId(1L);
        user.setPhone("1234567890");
        user.setAge(25);

        String toString = user.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("testuser"));
        assertTrue(toString.contains("test@example.com"));
        assertTrue(toString.contains("Test User"));
        assertTrue(toString.contains("1234567890"));
        assertTrue(toString.contains("25"));
    }

    @Test
    void testUserWithAllFields() {
        User user = new User();
        user.setId(1L);
        user.setUsername("john_doe");
        user.setEmail("john@example.com");
        user.setName("John Doe");
        user.setPhone("13800138000");
        user.setAge(30);

        assertEquals(1L, user.getId());
        assertEquals("john_doe", user.getUsername());
        assertEquals("john@example.com", user.getEmail());
        assertEquals("John Doe", user.getName());
        assertEquals("13800138000", user.getPhone());
        assertEquals(30, user.getAge());
    }

    @Test
    void testUserValidationConstraints() {
        // 这个测试验证User实体的字段可以接受各种值
        // 实际的验证逻辑由Jakarta Validation在Controller层处理
        User user = new User();
        
        // 测试边界值
        user.setUsername("abc"); // 最小长度3
        assertEquals("abc", user.getUsername());
        
        user.setUsername("a".repeat(50)); // 最大长度50
        assertEquals(50, user.getUsername().length());
        
        user.setName("a".repeat(100)); // 姓名最大长度100
        assertEquals(100, user.getName().length());
        
        user.setPhone("12345678901234567890"); // 电话最大长度20
        assertEquals(20, user.getPhone().length());
    }
}
