package com.example.demo.config;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(UserRepository userRepository) {
        return args -> {
            // 如果数据库为空，则添加一些测试数据
            if (userRepository.count() == 0) {
                User user1 = new User("zhangsan", "zhangsan@example.com", "张三");
                user1.setPhone("13800138001");
                user1.setAge(25);
                
                User user2 = new User("lisi", "lisi@example.com", "李四");
                user2.setPhone("13800138002");
                user2.setAge(30);
                
                User user3 = new User("wangwu", "wangwu@example.com", "王五");
                user3.setPhone("13800138003");
                user3.setAge(28);

                userRepository.save(user1);
                userRepository.save(user2);
                userRepository.save(user3);

                System.out.println("初始化数据完成，添加了3个测试用户");
            }
        };
    }
}