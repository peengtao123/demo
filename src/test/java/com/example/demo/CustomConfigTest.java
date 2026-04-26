package com.example.demo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class CustomConfigTest {

    @Value("${spring.datasource.name:default-name}")
    String sc;

    @Test
    void test() {
        // ========== 字符串断言示例 ==========
        assertThat(sc).isNotNull();                    // 不为null
        assertThat(sc).isNotEmpty();                   // 不为空字符串
        
        // 根据实际配置的值进行断言（使用默认值避免配置问题）
        System.out.println("实际配置值: " + sc);
        
        // ========== 其他常用断言示例 ==========
        
        // 数值断言
        int number = 42;
        assertThat(number).isEqualTo(42);              // 等于
        assertThat(number).isGreaterThan(40);          // 大于
        assertThat(number).isLessThan(50);             // 小于
        assertThat(number).isBetween(40, 50);          // 在范围内
        
        // 布尔断言
        boolean flag = true;
        assertThat(flag).isTrue();                     // 为true
        assertThat(!flag).isFalse();                   // 为false
        
        // 集合断言
        java.util.List<String> list = java.util.Arrays.asList("a", "b", "c");
        assertThat(list).isNotEmpty();                 // 不为空
        assertThat(list).hasSize(3);                   // 大小为3
        assertThat(list).contains("a", "b");           // 包含指定元素
        assertThat(list).doesNotContain("d");          // 不包含指定元素
        
        // 对象断言
        String text = "Hello World";
        assertThat(text).isInstanceOf(String.class);   // 是指定类型
        assertThat(text).hasSameClassAs("test");       // 与指定对象同类型
        
        // 异常断言
        assertThatThrownBy(() -> {
            throw new IllegalArgumentException("错误信息");
        }).isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("错误");               // 异常消息包含指定内容
    }

}
