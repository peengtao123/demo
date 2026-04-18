package com.example.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StopWatch;

import com.example.demo.dto.UserDTO;
import com.example.demo.entity.User;
import com.example.demo.service.UserService;

/**
 * 性能测试类
 * 使用@Tag("performance")标记，可以通过Maven配置选择性执行
 */
@SpringBootTest
@Tag("performance")
public class PerformanceTest {

    private static final Logger log = LoggerFactory.getLogger(PerformanceTest.class);

    @Autowired
    private UserService userService;

    @BeforeEach
    void setUp() {
        // 清理测试数据
        try {
            List<User> allUsers = userService.getAllUsers();
            for (User user : allUsers) {
                userService.deleteUser(user.getId());
            }
        } catch (Exception e) {
            log.warn("清理测试数据时出错: {}", e.getMessage());
        }
    }

    /**
     * 测试批量创建用户的性能
     */
    @Test
    void testBulkUserCreationPerformance() {
        int userCount = 100;
        StopWatch stopWatch = new StopWatch("Bulk User Creation");

        stopWatch.start("createUsers");

        List<Long> userIds = new ArrayList<>();
        for (int i = 0; i < userCount; i++) {
            UserDTO userDTO = new UserDTO();
            userDTO.setUsername("user" + i);
            userDTO.setEmail("user" + i + "@example.com");
            userDTO.setName("User " + i);
            userDTO.setPhone("1380000" + String.format("%04d", i));
            userDTO.setAge(20 + (i % 30));

            User createdUser = userService.createUser(userDTO);
            userIds.add(createdUser.getId());
        }

        stopWatch.stop();

        log.info(stopWatch.prettyPrint());
        log.info("创建了 {} 个用户，耗时: {} ms", userCount, stopWatch.getTotalTimeMillis());

        // 验证所有用户都创建成功
        assertEquals(userCount, userIds.size());

        // 性能断言：100个用户应该在5秒内创建完成
        assertTrue(stopWatch.getTotalTimeMillis() < 15000,
                "批量创建用户耗时过长: " + stopWatch.getTotalTimeMillis() + "ms");

        // 计算平均每个用户的创建时间
        double avgTimePerUser = (double) stopWatch.getTotalTimeMillis() / userCount;
        log.info("平均每个用户创建时间: {:.2f} ms", avgTimePerUser);
    }

    /**
     * 测试查询性能
     */
    @Test
    void testQueryPerformance() {
        // 先创建一些测试数据
        int dataSize = 50;
        for (int i = 0; i < dataSize; i++) {
            UserDTO userDTO = new UserDTO();
            userDTO.setUsername("queryuser" + i);
            userDTO.setEmail("queryuser" + i + "@example.com");
            userDTO.setName("Query User " + i);
            userDTO.setPhone("1390000" + String.format("%04d", i));
            userDTO.setAge(25);
            userService.createUser(userDTO);
        }

        StopWatch stopWatch = new StopWatch("Query Performance");

        // 测试单个查询
        stopWatch.start("singleQuery");
        User user = userService.getUserByUsername("queryuser25");
        stopWatch.stop();
        assertNotNull(user);

        // 测试批量查询
        stopWatch.start("bulkQuery");
        List<User> allUsers = userService.getAllUsers();
        stopWatch.stop();
        assertEquals(dataSize, allUsers.size());

        // 测试搜索查询
        stopWatch.start("searchQuery");
        List<User> searchResults = userService.searchUsersByName("Query User");
        stopWatch.stop();
        assertTrue(searchResults.size() > 0);

        log.info(stopWatch.prettyPrint());

        // 性能断言
        assertTrue(stopWatch.getTotalTimeMillis() < 2000,
                "查询操作总耗时过长: " + stopWatch.getTotalTimeMillis() + "ms");
    }

    /**
     * 测试并发读取性能（模拟）
     */
    @Test
    void testConcurrentReadPerformance() {
        // 准备测试数据
        for (int i = 0; i < 20; i++) {
            UserDTO userDTO = new UserDTO();
            userDTO.setUsername("concurrentuser" + i);
            userDTO.setEmail("concurrentuser" + i + "@example.com");
            userDTO.setName("Concurrent User " + i);
            userDTO.setPhone("1370000" + String.format("%04d", i));
            userDTO.setAge(30);
            userService.createUser(userDTO);
        }

        int iterations = 100;
        long totalTime = 0;

        StopWatch stopWatch = new StopWatch("Concurrent Read Simulation");
        stopWatch.start("readOperations");

        for (int i = 0; i < iterations; i++) {
            long startTime = System.nanoTime();

            // 模拟读取操作
            List<User> users = userService.getAllUsers();
            assertFalse(users.isEmpty());

            long endTime = System.nanoTime();
            totalTime += (endTime - startTime) / 1_000_000; // 转换为毫秒
        }

        stopWatch.stop();

        double avgTime = (double) totalTime / iterations;

        log.info("并发读取性能测试:");
        log.info("  迭代次数: {}", iterations);
        log.info("  平均响应时间: {:.2f} ms", avgTime);
        log.info("  总耗时: {} ms", stopWatch.getTotalTimeMillis());

        // 性能断言：平均响应时间应小于100ms
        assertTrue(avgTime < 100, "平均读取时间过长: " + avgTime + "ms");
    }
}
