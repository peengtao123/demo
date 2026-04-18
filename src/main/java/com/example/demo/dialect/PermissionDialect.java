package com.example.demo.dialect;

import org.thymeleaf.context.IExpressionContext;
import org.thymeleaf.dialect.AbstractProcessorDialect;
import org.thymeleaf.processor.IProcessor;
import org.thymeleaf.standard.StandardDialect;

import java.util.HashSet;
import java.util.Set;

/**
 * 权限控制方言
 * <p>这是一个Thymeleaf自定义方言，用于在模板中实现细粒度的权限控制功能。
 * 该方言注册了自定义属性处理器，允许在HTML模板中使用权限相关的属性来控制元素的显示。</p>
 * 
 * <h2>功能特性</h2>
 * <ul>
 *   <li>提供"perm"作为方言前缀</li>
 *   <li>支持hasPermission属性进行权限验证</li>
 *   <li>与Spring Security集成，基于当前用户权限控制视图元素</li>
 *   <li>无权限时自动从DOM中移除元素，而非仅隐藏</li>
 * </ul>
 * 
 * <h2>使用方式</h2>
 * <p>在Thymeleaf模板中引入该方言后，可以使用以下语法：</p>
 * <pre>{@code
 * <!-- 使用perm前缀 -->
 * <button perm:hasPermission="user:create">创建用户</button>
 * <a href="/admin/roles" perm:hasPermission="role:view">角色管理</a>
 * }</pre>
 * 
 * <h2>注册机制</h2>
 * <p>该方言通过Spring Boot的自动配置机制被注册到Thymeleaf引擎中。
 * 只需在配置类中声明一个PermissionDialect类型的Bean即可启用：</p>
 * <pre>{@code
 * @Configuration
 * public class ThymeleafConfig {
 *     @Bean
 *     public PermissionDialect permissionDialect() {
 *         return new PermissionDialect();
 *     }
 * }
 * }</pre>
 * 
 * <h2>扩展性</h2>
 * <p>如需添加更多权限相关的模板功能，可以在{@link #getProcessors(String)}方法中
 * 注册新的处理器。例如可以添加：</p>
 * <ul>
 *   <li>hasRole - 角色检查处理器</li>
 *   <li>hasAnyPermission - 多权限或逻辑检查处理器</li>
 *   <li>lacksPermission - 反向权限检查处理器</li>
 * </ul>
 * 
 * @author Demo Team
 * @version 1.0
 * @since 2024-01-01
 * @see AbstractProcessorDialect
 * @see HasPermissionAttributeTagProcessor
 * @see org.thymeleaf.dialect.IDialect
 */
public class PermissionDialect extends AbstractProcessorDialect {

    /**
     * 方言名称
     * <p>用于标识和日志记录，通常不会直接在模板中使用</p>
     */
    private static final String DIALECT_NAME = "Permission Dialect";

    /**
     * 构造函数
     * <p>初始化权限控制方言，设置方言名称、前缀和优先级。</p>
     * 
     * <p><b>规范要求：</b>使用StandardDialect.PROCESSOR_PRECEDENCE作为优先级，
     *           确保与其他标准处理器协调工作</p>
     */
    public PermissionDialect() {
        super(DIALECT_NAME, "perm", StandardDialect.PROCESSOR_PRECEDENCE);
    }

    /**
     * 获取该方言注册的所有处理器
     * <p>该方法在Thymeleaf引擎初始化时被调用，返回此方言提供的所有处理器集合。
     * 目前仅注册了hasPermission属性处理器，未来可扩展更多权限相关处理器。</p>
     * 
     * @param dialectPrefix 方言前缀，由父类传入，通常为"perm"
     * @return 处理器集合，包含所有需要注册的IProcessor实例
     * 
     * <p><b>当前实现：</b>
     * <ol>
     *   <li>创建一个新的HashSet用于存储处理器</li>
     *   <li>实例化HasPermissionAttributeTagProcessor并添加到集合</li>
     *   <li>返回处理器集合供Thymeleaf引擎注册</li>
     * </ol>
     * 
     * @see HasPermissionAttributeTagProcessor
     */
    @Override
    public Set<IProcessor> getProcessors(String dialectPrefix) {
        Set<IProcessor> processors = new HashSet<>();
        // 添加hasPermission属性处理器
        processors.add(new HasPermissionAttributeTagProcessor(dialectPrefix));
        return processors;
    }
}
