package com.example.demo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 用户数据传输对象（DTO）
 * <p>用于在表现层和服务层之间传输用户数据，包含验证注解以确保数据完整性。
 * 与User实体类不同，DTO专注于数据传输和验证，不包含JPA注解和业务逻辑。</p>
 * 
 * <h2>主要功能</h2>
 * <ul>
 *   <li>封装用户表单数据，用于创建和更新用户</li>
 *   <li>提供Bean Validation验证注解，确保数据合法性</li>
 *   <li>支持分组验证（CreateGroup/UpdateGroup），区分创建和更新场景</li>
 *   <li>简化API接口参数传递</li>
 * </ul>
 * 
 * <h2>字段说明</h2>
 * <ul>
 *   <li><strong>id：</strong>用户ID，仅在更新时使用，创建时为null</li>
 *   <li><strong>username：</strong>用户名，3-50个字符，必填，全局唯一</li>
 *   <li><strong>email：</strong>邮箱地址，标准邮箱格式，必填，全局唯一</li>
 *   <li><strong>name：</strong>真实姓名，最多100个字符，必填</li>
 *   <li><strong>phone：</strong>联系电话，最多20个字符，可选</li>
 *   <li><strong>age：</strong>年龄，整数类型，可选</li>
 *   <li><strong>password：</strong>密码，6-100个字符，创建时必填，更新时可选</li>
 * </ul>
 * 
 * <h2>验证分组</h2>
 * <ul>
 *   <li><strong>CreateGroup：</strong>创建用户时的验证规则（密码必填）</li>
 *   <li><strong>UpdateGroup：</strong>更新用户时的验证规则（密码可选）</li>
 * </ul>
 * 
 * <h3>使用示例</h3>
 * <pre>{@code
 * // 创建用户
 * UserDTO userDTO = new UserDTO();
 * userDTO.setUsername("admin");
 * userDTO.setEmail("admin@example.com");
 * userDTO.setName("管理员");
 * userDTO.setPassword("123456");
 * User user = userService.createUser(userDTO);
 * 
 * // 更新用户
 * UserDTO updateDTO = new UserDTO();
 * updateDTO.setId(1L);
 * updateDTO.setName("新名字");
 * updateDTO.setPhone("13800138000");
 * userService.updateUser(1L, updateDTO);
 * }</pre>
 * 
 * <h3>与User实体的区别</h3>
 * <ul>
 *   <li>UserDTO：用于数据传输，包含验证注解，无JPA注解</li>
 *   <li>User：持久化实体，包含JPA注解，关联其他实体（Role等）</li>
 *   <li>转换：通过UserService.convertToEntity()和convertToDTO()方法进行转换</li>
 * </ul>
 * 
 * @author Demo Team
 * @version 1.0
 * @since 2024-01-01
 * @see com.example.demo.entity.User
 * @see com.example.demo.service.UserService
 */
public class UserDTO {

    /**
     * 用户ID
     * <p>仅在更新操作时使用，创建新用户时为null</p>
     */
    private Long id;

    /**
     * 用户名
     * <p>约束：3-50个字符，必填，全局唯一</p>
     * <p>用途：用于登录和身份标识</p>
     */
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 50, message = "用户名长度必须在3-50个字符之间")
    private String username;

    /**
     * 邮箱地址
     * <p>约束：标准邮箱格式，必填，全局唯一</p>
     * <p>用途：用于联系和密码找回</p>
     */
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;

    /**
     * 真实姓名
     * <p>约束：最多100个字符，必填</p>
     * <p>用途：用于显示和用户识别</p>
     */
    @NotBlank(message = "姓名不能为空")
    @Size(max = 100, message = "姓名长度不能超过100个字符")
    private String name;

    /**
     * 联系电话
     * <p>约束：最多20个字符，可选</p>
     * <p>用途：用于紧急联系或通知</p>
     */
    @Size(max = 20, message = "电话号码长度不能超过20个字符")
    private String phone;

    /**
     * 年龄
     * <p>约束：可选，整数类型</p>
     * <p>用途：用于用户画像和统计分析</p>
     */
    private Integer age;

    /**
     * 密码
     * <p>约束：6-100个字符，创建时必填（CreateGroup），更新时可选</p>
     * <p>注意：存储到数据库前应使用BCrypt加密</p>
     */
    @NotBlank(message = "密码不能为空", groups = CreateGroup.class)
    @Size(min = 6, max = 100, message = "密码长度必须在6-100个字符之间", groups = CreateGroup.class)
    private String password;

    /**
     * 创建用户验证组
     * <p>用于区分创建和更新场景的验证规则</p>
     * <p>创建时：密码必填</p>
     */
    public interface CreateGroup {}
    
    /**
     * 更新用户验证组
     * <p>用于区分创建和更新场景的验证规则</p>
     * <p>更新时：密码可选（不修改密码时可不传）</p>
     */
    public interface UpdateGroup {}

    /**
     * 无参构造函数
     */
    public UserDTO() {
    }

    /**
     * 便捷构造函数
     * 
     * @param username 用户名
     * @param email 邮箱
     * @param name 姓名
     */
    public UserDTO(String username, String email, String name) {
        this.username = username;
        this.email = email;
        this.name = name;
    }

    // Getters and Setters
    
    /**
     * 获取用户ID
     * 
     * @return 用户ID
     */
    public Long getId() {
        return id;
    }

    /**
     * 设置用户ID
     * 
     * @param id 用户ID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 获取用户名
     * 
     * @return 用户名
     */
    public String getUsername() {
        return username;
    }

    /**
     * 设置用户名
     * 
     * @param username 用户名
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * 获取邮箱
     * 
     * @return 邮箱地址
     */
    public String getEmail() {
        return email;
    }

    /**
     * 设置邮箱
     * 
     * @param email 邮箱地址
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * 获取姓名
     * 
     * @return 真实姓名
     */
    public String getName() {
        return name;
    }

    /**
     * 设置姓名
     * 
     * @param name 真实姓名
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取电话
     * 
     * @return 联系电话
     */
    public String getPhone() {
        return phone;
    }

    /**
     * 设置电话
     * 
     * @param phone 联系电话
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * 获取年龄
     * 
     * @return 年龄
     */
    public Integer getAge() {
        return age;
    }

    /**
     * 设置年龄
     * 
     * @param age 年龄
     */
    public void setAge(Integer age) {
        this.age = age;
    }

    /**
     * 获取密码
     * 
     * @return 明文密码（应在Service层加密后存储）
     */
    public String getPassword() {
        return password;
    }

    /**
     * 设置密码
     * 
     * @param password 明文密码
     */
    public void setPassword(String password) {
        this.password = password;
    }
}
