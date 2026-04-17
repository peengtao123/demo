package com.example.demo.controller;

import com.example.demo.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

/**
 * PageController的单元测试 - 使用Mockito进行单元测试
 */
public class PageControllerTest {

    private PageController pageController;

    @Mock
    private UserService userService;

    @Mock
    private Model model;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        pageController = new PageController();
        // 通过反射设置userService
        try {
            var field = PageController.class.getDeclaredField("userService");
            field.setAccessible(true);
            field.set(pageController, userService);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testHomePage() {
        when(userService.getAllUsers()).thenReturn(Collections.emptyList());

        String viewName = pageController.home(model);

        assertEquals("index", viewName);
        verify(model).addAttribute(eq("userCount"), eq(0L));
    }

    @Test
    public void testUserListPage() {
        when(userService.getAllUsers()).thenReturn(Collections.emptyList());

        String viewName = pageController.userList(model);

        assertEquals("users/list", viewName);
        verify(model).addAttribute(eq("users"), any());
    }
}
