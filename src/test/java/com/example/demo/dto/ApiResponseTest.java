package com.example.demo.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ApiResponse DTO单元测试
 */
class ApiResponseTest {

    @Test
    void testDefaultConstructor() {
        ApiResponse<String> response = new ApiResponse<>();
        assertNotNull(response);
        assertFalse(response.isSuccess());
        assertNull(response.getMessage());
        assertNull(response.getData());
        assertNotNull(response.getTimestamp());
    }

    @Test
    void testParameterizedConstructor() {
        LocalDateTime now = LocalDateTime.now();
        ApiResponse<String> response = new ApiResponse<>(true, "测试消息", "测试数据");
        
        assertTrue(response.isSuccess());
        assertEquals("测试消息", response.getMessage());
        assertEquals("测试数据", response.getData());
        assertNotNull(response.getTimestamp());
    }

    @Test
    void testSuccessWithOnlyData() {
        String data = "测试数据";
        ApiResponse<String> response = ApiResponse.success(data);
        
        assertTrue(response.isSuccess());
        assertEquals("操作成功", response.getMessage());
        assertEquals(data, response.getData());
        assertNotNull(response.getTimestamp());
    }

    @Test
    void testSuccessWithMessageAndData() {
        String message = "自定义成功消息";
        String data = "测试数据";
        ApiResponse<String> response = ApiResponse.success(message, data);
        
        assertTrue(response.isSuccess());
        assertEquals(message, response.getMessage());
        assertEquals(data, response.getData());
    }

    @Test
    void testErrorWithOnlyMessage() {
        String errorMessage = "发生错误";
        ApiResponse<String> response = ApiResponse.error(errorMessage);
        
        assertFalse(response.isSuccess());
        assertEquals(errorMessage, response.getMessage());
        assertNull(response.getData());
    }

    @Test
    void testErrorWithMessageAndData() {
        String errorMessage = "发生错误";
        String errorData = "错误详情";
        ApiResponse<String> response = ApiResponse.error(errorMessage, errorData);
        
        assertFalse(response.isSuccess());
        assertEquals(errorMessage, response.getMessage());
        assertEquals(errorData, response.getData());
    }

    @Test
    void testSetters() {
        ApiResponse<Integer> response = new ApiResponse<>();
        
        response.setSuccess(true);
        response.setMessage("更新后的消息");
        response.setData(42);
        
        LocalDateTime customTime = LocalDateTime.of(2024, 1, 1, 12, 0, 0);
        response.setTimestamp(customTime);
        
        assertTrue(response.isSuccess());
        assertEquals("更新后的消息", response.getMessage());
        assertEquals(42, response.getData());
        assertEquals(customTime, response.getTimestamp());
    }

    @Test
    void testGenericTypes() {
        // 测试不同类型的泛型支持
        ApiResponse<Integer> intResponse = ApiResponse.success(100);
        assertEquals(100, intResponse.getData());
        
        ApiResponse<String> stringResponse = ApiResponse.success("文本");
        assertEquals("文本", stringResponse.getData());
        
        ApiResponse<Boolean> boolResponse = ApiResponse.success(true);
        assertTrue(boolResponse.getData());
    }

    @Test
    void testNullData() {
        ApiResponse<Void> response = ApiResponse.success(null);
        assertTrue(response.isSuccess());
        assertNull(response.getData());
    }

    @Test
    void testTimestampIsAutomaticallySet() {
        ApiResponse<String> response1 = new ApiResponse<>();
        ApiResponse<String> response2 = new ApiResponse<>();
        
        assertNotNull(response1.getTimestamp());
        assertNotNull(response2.getTimestamp());
        // 时间戳应该不同或相同（取决于执行速度）
        assertTrue(response1.getTimestamp().isBefore(LocalDateTime.now().plusSeconds(1)));
    }
}
