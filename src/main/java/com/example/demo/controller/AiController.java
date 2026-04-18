package com.example.demo.controller;

import com.example.demo.annotation.RequirePermission;
import com.example.demo.service.AiChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

/**
 * AI助手控制器
 * <p>提供AI智能对话功能的页面和API接口。</p>
 * 
 * <h2>功能模块</h2>
 * <ul>
 *   <li><strong>聊天页面：</strong>/admin/ai/chat - AI对话界面</li>
 *   <li><strong>发送消息：</strong>POST /admin/ai/api/chat - 同步对话API</li>
 *   <li><strong>流式对话：</strong>GET /admin/ai/api/stream - 流式响应API</li>
 * </ul>
 * 
 * <h2>权限要求</h2>
 * <ul>
 *   <li>访问聊天页面需要"ai:menu"权限</li>
 *   <li>发送消息需要"ai:chat"权限</li>
 * </ul>
 * 
 * @author Demo Team
 * @version 1.0
 * @since 2024-01-01
 */
@Controller
@RequestMapping("/admin/ai")
public class AiController {

    @Autowired
    private AiChatService aiChatService;

    /**
     * 显示AI聊天页面
     * 
     * @param model 模型对象
     * @return 聊天页面模板名称
     */
    @GetMapping("/chat")
    @RequirePermission("ai:menu")
    public String chatPage(Model model) {
        model.addAttribute("title", "AI助手");
        model.addAttribute("activeMenu", "ai");
        model.addAttribute("serviceAvailable", aiChatService.isAiServiceAvailable());
        return "admin/ai/chat";
    }

    /**
     * 发送消息并获取AI回复（同步）
     * 
     * @param message 用户消息
     * @return AI回复的JSON响应
     */
    @PostMapping("/api/chat")
    @ResponseBody
    @RequirePermission("ai:chat")
    public String sendMessage(@RequestParam String message) {
        if (message == null || message.trim().isEmpty()) {
            return "{\"error\": \"消息不能为空\"}";
        }
        
        String response = aiChatService.chat(message.trim());
        return "{\"response\": \"" + escapeJson(response) + "\"}";
    }

    /**
     * 流式对话接口
     * 
     * @param message 用户消息
     * @return 流式文本响应
     */
    @GetMapping(value = "/api/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @ResponseBody
    @RequirePermission("ai:chat")
    public Flux<String> streamChat(@RequestParam String message) {
        if (message == null || message.trim().isEmpty()) {
            return Flux.just("data: {\"error\": \"消息不能为空\"}\n\n");
        }
        
        return aiChatService.chatStream(message.trim())
                .map(content -> "data: " + content + "\n\n");
    }

    /**
     * 转义JSON字符串中的特殊字符
     * 
     * @param input 原始字符串
     * @return 转义后的字符串
     */
    private String escapeJson(String input) {
        if (input == null) {
            return "";
        }
        return input.replace("\\", "\\\\")
                   .replace("\"", "\\\"")
                   .replace("\n", "\\n")
                   .replace("\r", "\\r")
                   .replace("\t", "\\t");
    }
}
