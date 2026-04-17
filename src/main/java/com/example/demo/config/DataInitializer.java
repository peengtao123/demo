package com.example.demo.config;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            // 如果数据库为空，则添加一些测试数据
            if (userRepository.count() == 0) {
                // 管理员账号
                User admin = new User("admin", "admin@example.com", "管理员");
                admin.setPhone("13800138000");
                admin.setAge(30);
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setRole("ADMIN");
                
                // 普通用户1
                User user1 = new User("zhangsan", "zhangsan@example.com", "张三");
                user1.setPhone("13800138001");
                user1.setAge(25);
                user1.setPassword(passwordEncoder.encode("user123"));
                user1.setRole("USER");
                
                // 普通用户2
                User user2 = new User("lisi", "lisi@example.com", "李四");
                user2.setPhone("13800138002");
                user2.setAge(30);
                user2.setPassword(passwordEncoder.encode("user123"));
                user2.setRole("USER");
                
                // 普通用户3
                User user3 = new User("wangwu", "wangwu@example.com", "王五");
                user3.setPhone("13800138003");
                user3.setAge(28);
                user3.setPassword(passwordEncoder.encode("user123"));
                user3.setRole("USER");

                userRepository.save(admin);
                userRepository.save(user1);
                userRepository.save(user2);
                userRepository.save(user3);

                System.out.println("初始化数据完成，添加了4个测试用户（1个管理员 + 3个普通用户）");
                System.out.println("管理员账号: admin / admin123");
                System.out.println("普通用户账号: zhangsan / user123");
            }
        };
    }
}
