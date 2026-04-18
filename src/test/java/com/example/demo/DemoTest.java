package com.example.demo;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StopWatch;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DemoTest {

    private static final Logger log = LoggerFactory.getLogger(DemoTest.class);

    @Test
    void testHelloWorld() {
        String message = "Hello, World!";
        log.info(message);
        assertEquals("Hello, World!", message);
    }

    /**
     * 简单的性能测试示例
     * 使用StopWatch测量代码执行时间
     */
    @Test
    void testPerformanceWithStopWatch() {
        StopWatch stopWatch = new StopWatch("Performance Test");
        
        // 开始计时
        stopWatch.start("stringOperation");
        
        // 模拟一些操作
        String result = "";
        for (int i = 0; i < 1000; i++) {
            result += "test";
        }
        
        // 停止计时
        stopWatch.stop();
        
        // 输出性能统计
        log.info(stopWatch.prettyPrint());
        
        // 验证执行时间在合理范围内（例如小于1秒）
        assertTrue(stopWatch.getTotalTimeMillis() < 1000, 
                "操作执行时间过长: " + stopWatch.getTotalTimeMillis() + "ms");
        
        // 使用result变量以防止编译器优化
        assertTrue(result.length() > 0, "结果不应为空");
    }

    /**
     * 多次迭代性能测试
     * 计算平均执行时间
     */
    @Test
    void testPerformanceWithIterations() {
        int iterations = 100;
        long totalTime = 0;
        long minTime = Long.MAX_VALUE;
        long maxTime = Long.MIN_VALUE;
        
        for (int i = 0; i < iterations; i++) {
            long startTime = System.nanoTime();
            
            // 执行被测操作
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < 100; j++) {
                sb.append("data");
            }
            sb.toString();
            
            long endTime = System.nanoTime();
            long executionTime = (endTime - startTime) / 1_000_000; // 转换为毫秒
            
            totalTime += executionTime;
            minTime = Math.min(minTime, executionTime);
            maxTime = Math.max(maxTime, executionTime);
        }
        
        double avgTime = (double) totalTime / iterations;
        
        log.info("性能测试结果:");
        log.info("  迭代次数: {}", iterations);
        log.info("  平均执行时间: {:.2f} ms", avgTime);
        log.info("  最小执行时间: {} ms", minTime);
        log.info("  最大执行时间: {} ms", maxTime);
        log.info("  总执行时间: {} ms", totalTime);
        
        // 断言平均执行时间在可接受范围内
        assertTrue(avgTime < 10, "平均执行时间过长: " + avgTime + "ms");
    }

    /**
     * 对比不同实现的性能
     */
    @Test
    void testComparePerformance() {
        int iterations = 1000;
        
        // 测试String拼接
        StopWatch stopWatch = new StopWatch("String Comparison");
        
        stopWatch.start("String concatenation");
        String stringResult = "";
        for (int i = 0; i < iterations; i++) {
            stringResult += "item" + i;
        }
        stopWatch.stop();
        
        // 测试StringBuilder
        stopWatch.start("StringBuilder");
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < iterations; i++) {
            stringBuilder.append("item").append(i);
        }
        String sbResult = stringBuilder.toString();
        stopWatch.stop();
        
        // 输出对比结果
        log.info(stopWatch.prettyPrint());
        
        // 验证两种方法结果一致
        assertEquals(stringResult, sbResult);
    }
}
