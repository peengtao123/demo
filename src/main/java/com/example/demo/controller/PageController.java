package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * 页面控制器 - 用于返回Thymeleaf视图
 * 
 * <p>提供系统各个页面的路由和视图渲染功能，包括用户列表、用户详情和首页等。</p>
 */
@Controller
@RequestMapping("/pages")
public class PageController {

    @Autowired
    private UserService userService;

    /**
     * 用户列表页面
     *
     * @param model Spring MVC 模型对象，用于传递用户列表和当前用户信息到视图
     * @return 用户列表页面模板名称 "users/list"
     */
    @GetMapping("/users")
    public String userList(Model model) {
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        addCurrentUserToModel(model);
        return "users/list";
    }

    /**
     * 用户详情页面
     * 
     * @param id 用户ID
     * @param model 模型对象，用于传递数据到视图
     * @return 视图名称
     */
    @GetMapping("/users/{id}")
    public String userDetail(@PathVariable Long id, Model model) {
        try {
            User user = userService.getUserById(id);
            model.addAttribute("user", user);
            addCurrentUserToModel(model);
            return "users/detail";
        } catch (RuntimeException e) {
            model.addAttribute("error", "用户不存在");
            return "error";
        }
    }

    /**
     * 首页
     * 
     * @param model 模型对象，用于传递数据到视图
     * @return 视图名称
     */
    @GetMapping("/")
    public String home(Model model) {
        long userCount = userService.getAllUsers().size();
        model.addAttribute("userCount", userCount);
        addCurrentUserToModel(model);
        return "index";
    }

    /**
     * 将当前登录用户信息添加到Model
     */
    private void addCurrentUserToModel(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            model.addAttribute("currentUsername", username);
        }
    }
}
