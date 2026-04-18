package com.example.demo.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

/**
 * AI聊天服务
 * <p>提供基于Spring AI的智能对话功能，支持文本生成和流式响应。</p>
 * 
 * @author Demo Team
 * @version 1.0
 * @since 2024-01-01
 */
@Service
public class AiChatService {

    @Autowired(required = false)
    private ChatModel chatModel;

    @Autowired(required = false)
    private ChatClient chatClient;

    /**
     * 发送消息并获取AI回复
     * 
     * @param message 用户输入的消息
     * @return AI的回复内容
     */
    public String chat(String message) {
        if (chatModel == null) {
            return "AI服务未配置，请在application.properties中配置spring.ai.openai.api-key";
        }

        try {
            Prompt prompt = new Prompt(message);
            String response = chatModel.call(prompt).getResult().getOutput().getText();
            return response != null ? response : "抱歉，我没有理解您的问题。";
        } catch (Exception e) {
            return "AI服务调用失败: " + e.getMessage();
        }
    }

    /**
     * 发送消息并获取流式AI回复
     * 
     * @param message 用户输入的消息
     * @return 流式响应
     */
    public Flux<String> chatStream(String message) {
        if (chatClient == null) {
            return Flux.just("AI服务未配置，请在application.properties中配置spring.ai.openai.api-key");
        }

        try {
            return chatClient.prompt()
                    .user(message)
                    .stream()
                    .content();
        } catch (Exception e) {
            return Flux.just("AI服务调用失败: " + e.getMessage());
        }
    }

    /**
     * 检查AI服务是否可用
     * 
     * @return true表示AI服务已配置且可用
     */
    public boolean isAiServiceAvailable() {
        return chatModel != null || chatClient != null;
    }
}
