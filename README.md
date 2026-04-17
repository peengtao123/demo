# 用户管理系统

基于 Spring Boot + H2 数据库的用户管理功能演示项目。

## 技术栈

- Spring Boot 4.0.5
- Spring Data JPA
- H2 内存数据库
- Java 17
- Maven

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