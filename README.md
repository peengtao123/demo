# 用户管理系统

基于 Spring Boot + H2 数据库的用户管理功能演示项目。

## 📚 文档导航

详细的技术文档已整理至 [docs](./docs/) 目录：

- **Spring Security**: [完整指南](./docs/SPRING_SECURITY_GUIDE.md) | [总结](./docs/SPRING_SECURITY_SUMMARY.md) | [快速参考](./docs/SECURITY_QUICK_REF.md)
- **Thymeleaf**: [入门指南](./docs/THYMELEAF_README.md) | [总结](./docs/THYMELEAF_SUMMARY.md) | [快速开始](./docs/THYMELEAF_QUICKSTART.md)
- **测试相关**: [单元测试](./docs/TEST_README.md) | [Web MVC测试](./docs/WEBMVC_TEST_README.md)
- **其他**: [@RestController原理](./docs/RESTCONTROLLER_PRINCIPLE.md) | [帮助文档](./docs/HELP.md)

更多详情请查看 [docs/README.md](./docs/README.md)

---

## 技术栈

- Spring Boot 4.0.5
- Spring Data JPA
- H2 内存数据库
- Java 17
- Maven
- JUnit 5 (测试框架)

## 功能特性

- ✅ 用户 CRUD 操作（创建、查询、更新、删除）
- ✅ 数据验证（使用 Jakarta Validation）
- ✅ 统一 API 响应格式
- ✅ H2 数据库控制台
- ✅ 自动初始化测试数据
- ✅ JPA 审计（自动记录创建和更新时间）

## 快速开始

### 1. 运行项目

```bash
mvn spring-boot:run
```

或者

```bash
./mvnw spring-boot:run
```

### 2. 访问 H2 数据库控制台

浏览器打开：http://localhost:8080/h2-console

连接信息：
- JDBC URL: `jdbc:h2:mem:userdb`
- 用户名: `sa`
- 密码: (留空)

### 3. API 接口测试

#### 查询所有用户
```bash
curl http://localhost:8080/api/users
```

#### 根据ID查询用户
```bash
curl http://localhost:8080/api/users/1
```

#### 创建新用户
```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "name": "测试用户",
    "phone": "13800138000",
    "age": 20
  }'
```

#### 更新用户
```bash
curl -X PUT http://localhost:8080/api/users/1 \
  -H "Content-Type: application/json" \
  -d '{
    "username": "zhangsan",
    "email": "zhangsan@example.com",
    "name": "张三丰",
    "phone": "13800138001",
    "age": 26
  }'
```

#### 删除用户
```bash
curl -X DELETE http://localhost:8080/api/users/1
```

#### 根据用户名查询
```bash
curl http://localhost:8080/api/users/username/zhangsan
```

#### 根据姓名搜索
```bash
curl "http://localhost:8080/api/users/search?name=张"
```

## 项目结构

```
src/main/java/com/example/demo/
├── config/
│   └── DataInitializer.java          # 数据初始化配置
├── controller/
│   └── UserController.java           # REST API 控制器
├── dto/
│   ├── ApiResponse.java              # 统一响应封装
│   └── UserDTO.java                  # 用户数据传输对象
├── entity/
│   └── User.java                     # 用户实体类
├── repository/
│   └── UserRepository.java           # 数据访问层
├── service/
│   └── UserService.java              # 业务逻辑层
└── DemoApplication.java              # 应用启动类
```

## 数据库表结构

**users 表：**

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | BIGINT | 主键，自增 |
| username | VARCHAR(50) | 用户名，唯一 |
| email | VARCHAR(100) | 邮箱，唯一 |
| name | VARCHAR(100) | 姓名 |
| phone | VARCHAR(20) | 电话 |
| age | INT | 年龄 |
| create_time | TIMESTAMP | 创建时间（自动） |
| update_time | TIMESTAMP | 更新时间（自动） |

## API 响应格式

成功响应：
```json
{
  "success": true,
  "message": "操作成功",
  "data": { ... },
  "timestamp": "2026-04-17T23:00:00"
}
```

失败响应：
```json
{
  "success": false,
  "message": "错误信息",
  "data": null,
  "timestamp": "2026-04-17T23:00:00"
}
```

## 注意事项

1. H2 是内存数据库，应用重启后数据会丢失
2. 如需持久化数据，可修改 `application.properties` 中的数据库 URL 为文件模式：
   ```properties
   spring.datasource.url=jdbc:h2:file:./data/userdb
   ```
3. 生产环境建议禁用 H2 控制台：
   ```properties
   spring.h2.console.enabled=false
   ```

## 测试数据

应用启动时会自动创建 3 个测试用户：
- zhangsan / 张三
- lisi / 李四
- wangwu / 王五

## 性能测试

本项目提供了多种性能测试方法，帮助您评估系统性能。

### 1. 基础性能测试（JUnit + StopWatch）

在 `DemoTest.java` 中包含了基础的性能测试示例：

```bash
# 运行所有测试（包括性能测试）
mvn test

# 或在VSCode中直接运行测试类
```

**测试内容包括：**
- ✅ 使用 `StopWatch` 测量代码执行时间
- ✅ 多次迭代测试，计算平均/最小/最大执行时间
- ✅ 不同实现方式的性能对比（如 String vs StringBuilder）

### 2. 集成性能测试

`PerformanceTest.java` 提供了完整的业务场景性能测试：

```bash
# 运行性能测试（需要启动Spring Boot上下文）
mvn test -Dtest=PerformanceTest
```

**测试场景包括：**
- ✅ 批量创建用户性能测试
- ✅ 查询操作性能测试（单个查询、批量查询、搜索查询）
- ✅ 并发读取性能模拟测试

**查看测试结果：**
- 在VSCode中打开"输出"面板（Ctrl+Shift+U）
- 选择 "Java Test Runner" 通道
- 查看详细的时间统计和性能指标

### 3. 高级微基准测试（JMH）

对于需要精确测量的方法级性能测试，可以使用 JMH：

#### 添加 JMH 依赖到 pom.xml：

```xml
<properties>
    <jmh.version>1.37</jmh.version>
</properties>

<dependencies>
    <!-- JMH 核心库 -->
    <dependency>
        <groupId>org.openjdk.jmh</groupId>
        <artifactId>jmh-core</artifactId>
        <version>${jmh.version}</version>
        <scope>test</scope>
    </dependency>
    
    <!-- JMH 注解处理器 -->
    <dependency>
        <groupId>org.openjdk.jmh</groupId>
        <artifactId>jmh-generator-annprocess</artifactId>
        <version>${jmh.version}</version>
        <scope>test</scope>
    </dependency>
</dependencies>

<build>
    <plugins>
        <!-- JMH 插件 -->
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-shade-plugin</artifactId>
            <version>3.5.1</version>
            <executions>
                <execution>
                    <phase>package</phase>
                    <goals>
                        <goal>shade</goal>
                    </goals>
                    <configuration>
                        <finalName>benchmarks</finalName>
                        <transformers>
                            <transformer implementation="org.apache.maven.plugins.shade.resource.ManifestResourceTransformer">
                                <mainClass>org.openjdk.jmh.Main</mainClass>
                            </transformer>
                        </transformers>
                    </configuration>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

#### 创建 JMH 基准测试：

```java
import org.openjdk.jmh.annotations.*;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Thread)
@Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
public class UserServiceBenchmark {
    
    @Benchmark
    public void testUserCreation() {
        // 基准测试代码
    }
}
```

#### 运行 JMH 测试：

```bash
# 构建并运行
mvn clean package
java -jar target/benchmarks.jar
```

### 4. API 压力测试（外部工具）

对于生产级别的压力测试，推荐使用专业工具：

#### 使用 Apache Bench (ab)：
```bash
# 发送1000个请求，10个并发
ab -n 1000 -c 10 http://localhost:8080/api/users
```

#### 使用 wrk：
```bash
# 持续压测30秒，12个线程，100个连接
wrk -t12 -c100 -d30s http://localhost:8080/api/users
```

#### 使用 JMeter：
- 下载 JMeter：https://jmeter.apache.org/
- 创建测试计划，配置 HTTP 请求
- 设置线程组（虚拟用户数、循环次数）
- 运行测试并查看报告

### 性能测试最佳实践

1. **隔离性能测试**：使用 `@Tag("performance")` 标记性能测试，避免在CI/CD中自动执行
2. **设置合理阈值**：根据业务需求设定性能断言，如响应时间 < 100ms
3. **预热JVM**：多次运行测试，丢弃前几次的结果（JVM预热）
4. **监控资源**：同时监控CPU、内存、GC等指标
5. **真实数据**：使用接近生产环境的数据量进行测试
6. **多次测量**：至少运行3次，取平均值作为最终结果

### Maven Profile 配置（可选）

在 `pom.xml` 中添加性能测试的独立profile：

```xml
<profiles>
    <profile>
        <id>performance-test</id>
        <build>
            <plugins>
                <plugin>
                    <groupId>org.apache.maven.plugins</groupId>
                    <artifactId>maven-surefire-plugin</artifactId>
                    <configuration>
                        <groups>performance</groups>
                    </configuration>
                </plugin>
            </plugins>
        </build>
    </profile>
</profiles>
```

运行性能测试：
```bash
mvn test -P performance-test
```
