package com.example.demo.dto;

import java.time.LocalDateTime;

/**
 * 统一API响应封装类
 * <p>用于标准化REST API的返回格式，提供统一的响应结构。
 * 所有Controller方法都应使用此类包装返回数据，确保前后端交互的一致性。</p>
 * 
 * <h2>响应结构</h2>
 * <pre>{@code
 * {
 *   "success": true,
 *   "message": "操作成功",
 *   "data": {...},
 *   "timestamp": "2024-01-01T12:00:00"
 * }
 * }</pre>
 * 
 * <h2>字段说明</h2>
 * <ul>
 *   <li>success - 操作是否成功（true/false）</li>
 *   <li>message - 响应消息，描述操作结果</li>
 *   <li>data - 实际返回的数据，支持泛型</li>
 *   <li>timestamp - 响应时间戳，ISO 8601格式</li>
 * </ul>
 * 
 * <h2>使用示例</h2>
 * <pre>{@code
 * // 成功响应（带数据）
 * return ApiResponse.success(userList);
 * 
 * // 成功响应（自定义消息）
 * return ApiResponse.success("用户创建成功", newUser);
 * 
 * // 错误响应
 * return ApiResponse.error("用户名已存在");
 * 
 * // 错误响应（带数据）
 * return ApiResponse.error("验证失败", validationErrors);
 * }</pre>
 * 
 * <h3>最佳实践</h3>
 * <ul>
 *   <li>成功操作使用success()静态方法</li>
 *   <li>失败操作使用error()静态方法</li>
 *   <li>保持message简洁明了，便于前端展示</li>
 *   <li>敏感信息不应包含在data中</li>
 *   <li>异常情况下应返回适当的HTTP状态码和错误信息</li>
 * </ul>
 * 
 * @param <T> 响应数据的类型，支持任意类型
 * 
 * @author Demo Team
 * @version 1.0
 * @since 2024-01-01
 */
public class ApiResponse<T> {

    /**
     * 操作是否成功
     * <p>true表示操作成功，false表示操作失败</p>
     */
    private boolean success;

    /**
     * 响应消息
     * <p>描述操作结果的信息，如"操作成功"、"用户名已存在"等</p>
     */
    private String message;

    /**
     * 响应数据
     * <p>实际的业务数据，支持泛型，可以是对象、集合或null</p>
     */
    private T data;

    /**
     * 响应时间戳
     * <p>记录响应生成的时间，使用ISO 8601格式</p>
     */
    private LocalDateTime timestamp;

    /**
     * 无参构造函数
     * <p>自动设置时间戳为当前时间</p>
     */
    public ApiResponse() {
        this.timestamp = LocalDateTime.now();
    }

    /**
     * 全参构造函数
     * 
     * @param success 操作是否成功
     * @param message 响应消息
     * @param data 响应数据
     */
    public ApiResponse(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.timestamp = LocalDateTime.now();
    }

    /**
     * 创建成功响应（默认消息）
     * 
     * @param data 响应数据
     * @param <T> 数据类型
     * @return 成功的ApiResponse实例
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, "操作成功", data);
    }

    /**
     * 创建成功响应（自定义消息）
     * 
     * @param message 响应消息
     * @param data 响应数据
     * @param <T> 数据类型
     * @return 成功的ApiResponse实例
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data);
    }

    /**
     * 创建错误响应
     * 
     * @param message 错误消息
     * @param <T> 数据类型
     * @return 失败的ApiResponse实例
     */
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message, null);
    }

    /**
     * 创建错误响应（带数据）
     * 
     * @param message 错误消息
     * @param data 错误详情或相关数据
     * @param <T> 数据类型
     * @return 失败的ApiResponse实例
     */
    public static <T> ApiResponse<T> error(String message, T data) {
        return new ApiResponse<>(false, message, data);
    }

    // Getters and Setters
    
    /**
     * 获取操作成功状态
     * 
     * @return true表示成功，false表示失败
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * 设置操作成功状态
     * 
     * @param success 成功状态
     */
    public void setSuccess(boolean success) {
        this.success = success;
    }

    /**
     * 获取响应消息
     * 
     * @return 响应消息
     */
    public String getMessage() {
        return message;
    }

    /**
     * 设置响应消息
     * 
     * @param message 响应消息
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * 获取响应数据
     * 
     * @return 响应数据
     */
    public T getData() {
        return data;
    }

    /**
     * 设置响应数据
     * 
     * @param data 响应数据
     */
    public void setData(T data) {
        this.data = data;
    }

    /**
     * 获取响应时间戳
     * 
     * @return 时间戳
     */
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    /**
     * 设置响应时间戳
     * 
     * @param timestamp 时间戳
     */
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
