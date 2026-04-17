package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * 页面控制器 - 用于返回Thymeleaf视图
 */
@Controller
@RequestMapping("/pages")
public class PageController {

    @Autowired
    private UserService userService;

    /**
     * 用户列表页面
     */
    @GetMapping("/users")
    public String userList(Model model) {
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "users/list";
    }

    /**
     * 用户详情页面
     */
    @GetMapping("/users/{id}")
    public String userDetail(@PathVariable Long id, Model model) {
        try {
            User user = userService.getUserById(id);
            model.addAttribute("user", user);
            return "users/detail";
        } catch (RuntimeException e) {
            model.addAttribute("error", "用户不存在");
            return "error";
        }
    }

    /**
     * 首页
     */
    @GetMapping("/")
    public String home(Model model) {
        long userCount = userService.getAllUsers().size();
        model.addAttribute("userCount", userCount);
        return "index";
    }
}
