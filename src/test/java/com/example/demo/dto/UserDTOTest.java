package com.example.demo.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * UserDTO单元测试
 */
class UserDTOTest {

    @Test
    void testDefaultConstructor() {
        UserDTO userDTO = new UserDTO();
        assertNotNull(userDTO);
        assertNull(userDTO.getId());
        assertNull(userDTO.getUsername());
        assertNull(userDTO.getEmail());
        assertNull(userDTO.getName());
        assertNull(userDTO.getPhone());
        assertNull(userDTO.getAge());
    }

    @Test
    void testParameterizedConstructor() {
        UserDTO userDTO = new UserDTO("testuser", "test@example.com", "Test User");
        assertNotNull(userDTO);
        assertNull(userDTO.getId());
        assertEquals("testuser", userDTO.getUsername());
        assertEquals("test@example.com", userDTO.getEmail());
        assertEquals("Test User", userDTO.getName());
        assertNull(userDTO.getPhone());
        assertNull(userDTO.getAge());
    }

    @Test
    void testSettersAndGetters() {
        UserDTO userDTO = new UserDTO();
        
        Long id = 1L;
        String username = "testuser";
        String email = "test@example.com";
        String name = "Test User";
        String phone = "1234567890";
        Integer age = 25;

        userDTO.setId(id);
        userDTO.setUsername(username);
        userDTO.setEmail(email);
        userDTO.setName(name);
        userDTO.setPhone(phone);
        userDTO.setAge(age);

        assertEquals(id, userDTO.getId());
        assertEquals(username, userDTO.getUsername());
        assertEquals(email, userDTO.getEmail());
        assertEquals(name, userDTO.getName());
        assertEquals(phone, userDTO.getPhone());
        assertEquals(age, userDTO.getAge());
    }

    @Test
    void testFullUserDTO() {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setUsername("john_doe");
        userDTO.setEmail("john@example.com");
        userDTO.setName("John Doe");
        userDTO.setPhone("13800138000");
        userDTO.setAge(30);

        assertEquals(1L, userDTO.getId());
        assertEquals("john_doe", userDTO.getUsername());
        assertEquals("john@example.com", userDTO.getEmail());
        assertEquals("John Doe", userDTO.getName());
        assertEquals("13800138000", userDTO.getPhone());
        assertEquals(30, userDTO.getAge());
    }

    @Test
    void testValidationConstraints() {
        // 测试DTO可以接受各种值，实际验证由Jakarta Validation处理
        UserDTO userDTO = new UserDTO();
        
        // 测试用户名边界值
        userDTO.setUsername("abc"); // 最小长度3
        assertEquals("abc", userDTO.getUsername());
        
        userDTO.setUsername("a".repeat(50)); // 最大长度50
        assertEquals(50, userDTO.getUsername().length());
        
        // 测试姓名边界值
        userDTO.setName("a".repeat(100)); // 最大长度100
        assertEquals(100, userDTO.getName().length());
        
        // 测试电话边界值
        userDTO.setPhone("12345678901234567890"); // 最大长度20
        assertEquals(20, userDTO.getPhone().length());
    }

    @Test
    void testUpdateFields() {
        UserDTO userDTO = new UserDTO("old_user", "old@example.com", "Old Name");
        
        // 更新字段
        userDTO.setUsername("new_user");
        userDTO.setEmail("new@example.com");
        userDTO.setName("New Name");
        userDTO.setPhone("13900139000");
        userDTO.setAge(35);

        assertEquals("new_user", userDTO.getUsername());
        assertEquals("new@example.com", userDTO.getEmail());
        assertEquals("New Name", userDTO.getName());
        assertEquals("13900139000", userDTO.getPhone());
        assertEquals(35, userDTO.getAge());
    }

    @Test
    void testNullValues() {
        UserDTO userDTO = new UserDTO();
        
        // 设置为null
        userDTO.setUsername(null);
        userDTO.setEmail(null);
        userDTO.setName(null);
        userDTO.setPhone(null);
        userDTO.setAge(null);

        assertNull(userDTO.getUsername());
        assertNull(userDTO.getEmail());
        assertNull(userDTO.getName());
        assertNull(userDTO.getPhone());
        assertNull(userDTO.getAge());
    }

    @Test
    void testEmptyStrings() {
        UserDTO userDTO = new UserDTO();
        
        userDTO.setUsername("");
        userDTO.setEmail("");
        userDTO.setName("");
        userDTO.setPhone("");

        assertEquals("", userDTO.getUsername());
        assertEquals("", userDTO.getEmail());
        assertEquals("", userDTO.getName());
        assertEquals("", userDTO.getPhone());
    }
}
