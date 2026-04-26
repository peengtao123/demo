package com.example.demo.service;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.example.demo.controller.UserController;
import com.example.demo.entity.User;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
class WebMvcBeanTest {

    @Autowired
    private UserController userController;

    @Mock // 使用 Mockito 的 @Mock 注解替代 @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        // 初始化 Mock 对象
        MockitoAnnotations.openMocks(this);
        
        // 手动构建 MockMvc
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
        
        // 配置 Mock 行为
        User mockUser = new User("testuser", "test@example.com", "Test User");
        mockUser.setId(1L);
        when(userService.getUserById(1L)).thenReturn(mockUser);
    }

    @Test
    void testGetUserEndpoint() throws Exception {
        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.username").value("testuser"));
    }
}
