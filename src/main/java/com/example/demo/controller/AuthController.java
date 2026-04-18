package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 认证控制器 - 处理登录相关页面
 * 
 * <p>提供用户登录页面的展示功能，包括登录失败和登出成功的提示信息。</p>
 */
@Controller
public class AuthController {

    /**
     * 登录页面
     *
     * @param error 错误标识，如果存在则显示登录失败提示
     * @param logout 登出标识，如果存在则显示登出成功提示
     * @param model Spring MVC 模型对象，用于传递数据到视图
     * @return 登录页面模板名称 "login"
     */
    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error,
                            @RequestParam(value = "logout", required = false) String logout,
                            Model model) {
        if (error != null) {
            model.addAttribute("error", "用户名或密码错误");
        }
        if (logout != null) {
            model.addAttribute("message", "已成功登出");
        }
        return "login";
    }
}
