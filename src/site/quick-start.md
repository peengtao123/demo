# 快速开始指南

## 环境准备

### 必需工具

- **JDK 17** 或更高版本
- **Maven 3.6+** 或使用项目自带的 Maven Wrapper
- **IDE**: IntelliJ IDEA、Eclipse 或 VS Code（推荐安装 Java 扩展包）

### 验证环境

```bash
# 检查 Java 版本
java -version

# 检查 Maven 版本
mvn -version
```

## 项目初始化

### 1. 获取项目代码

```bash
# 克隆仓库
git clone https://github.com/example/demo.git

# 进入项目目录
cd demo
```

### 2. 编译项目

```bash
# 使用 Maven Wrapper (推荐)
./mvnw clean compile

# 或使用系统 Maven
mvn clean compile
```

### 3. 运行测试

```bash
# 执行所有测试
./mvnw test

# 生成测试报告
./mvnw surefire-report:report
```

### 4. 启动应用

```bash
# 开发模式运行（支持热重载）
./mvnw spring-boot:run
```

应用启动后，控制台会显示类似信息：
```
Started DemoApplication in X.XXX seconds
```

### 5. 访问应用

打开浏览器访问：**http://localhost:8080**

#### 默认账号

| 角色 | 用户名 | 密码 | 说明 |
|------|--------|------|------|
| 管理员 | admin | admin123 | 拥有所有权限 |
| 编辑者 | editor | editor123 | 拥有编辑权限 |
| 访客 | viewer | viewer123 | 仅查看权限 |

## 主要功能

### 用户管理

访问路径：`/admin/users`

- ✅ 创建新用户
- ✅ 编辑用户信息
- ✅ 删除用户
- ✅ 分配角色
- ✅ 查看用户详情

### 角色管理

访问路径：`/admin/roles`

- ✅ 创建新角色
- ✅ 编辑角色信息
- ✅ 删除角色
- ✅ 分配权限
- ✅ 从模板创建角色

### 权限管理

访问路径：`/admin/permissions`

- ✅ 创建权限（菜单/按钮）
- ✅ 配置权限图标和排序
- ✅ 编辑权限信息
- ✅ 删除权限

### 审计日志

访问路径：`/admin/audit-logs`

- ✅ 查看所有操作记录
- ✅ 按操作人筛选
- ✅ 按操作类型筛选
- ✅ 批量删除日志

## 常用命令

### 构建相关

```bash
# 清理项目
./mvnw clean

# 编译项目
./mvnw compile

# 打包（跳过测试）
./mvnw package -DskipTests

# 打包（包含测试）
./mvnw package

# 安装到本地仓库
./mvnw install
```

### 测试相关

```bash
# 运行所有测试
./mvnw test

# 运行特定测试类
./mvnw test -Dtest=UserControllerTest

# 生成代码覆盖率报告
./mvnw jacoco:report

# 生成 Javadoc
./mvnw javadoc:javadoc
```

### 站点相关

```bash
# 生成项目站点
./mvnw site

# 预览站点（启动本地服务器）
./mvnw site:run
# 访问 http://localhost:8080 查看站点

# 清理站点
./mvnw site-clean
```

### 运行相关

```bash
# 开发模式运行
./mvnw spring-boot:run

# 指定端口运行
./mvnw spring-boot:run -Dspring-boot.run.arguments="--server.port=8081"

# 打包后运行
java -jar target/demo-0.0.1-SNAPSHOT.jar
```

## 项目结构

```
demo/
├── src/
│   ├── main/
│   │   ├── java/com/example/demo/
│   │   │   ├── annotation/        # 自定义注解
│   │   │   ├── config/            # 配置类
│   │   │   ├── controller/        # 控制器
│   │   │   ├── dialect/           # Thymeleaf 方言
│   │   │   ├── dto/               # 数据传输对象
│   │   │   ├── entity/            # 实体类
│   │   │   ├── interceptor/       # 拦截器
│   │   │   ├── repository/        # 数据访问层
│   │   │   └── service/           # 业务逻辑层
│   │   └── resources/
│   │       ├── templates/         # Thymeleaf 模板
│   │       └── application.properties  # 配置文件
│   └── test/                      # 测试代码
├── doc/                           # 项目文档
├── pom.xml                        # Maven 配置
└── README.md                      # 项目说明
```

## 常见问题

### Q1: 启动时端口被占用？

**解决方案：**
```bash
# 方法1：修改配置文件
# 在 application.properties 中添加
server.port=8081

# 方法2：命令行指定
./mvnw spring-boot:run -Dspring-boot.run.arguments="--server.port=8081"
```

### Q2: 如何重置数据库？

**解决方案：**
```bash
# 停止应用
# 删除 H2 数据库文件
rm -rf ./data/*

# 重新启动应用，DataInitializer 会自动初始化数据
./mvnw spring-boot:run
```

### Q3: 如何查看 H2 数据库控制台？

**解决方案：**
1. 启动应用后访问：http://localhost:8080/h2-console
2. 配置连接：
   - JDBC URL: `jdbc:h2:file:./data/demo`
   - 用户名: `sa`
   - 密码: （留空）

### Q4: 测试失败怎么办？

**解决方案：**
```bash
# 查看详细测试报告
./mvnw test

# 查看 HTML 报告
open target/site/surefire-report.html

# 单独运行失败的测试
./mvnw test -Dtest=FailedTestClass
```

## 下一步

- 📖 阅读 [[用户指南][user-guide.html]] 了解详细功能
- 📚 查看 [[API文档][apidocs/index.html]] 了解技术细节
- 🔍 分析 [[测试报告][surefire-report.html]] 了解代码质量
- 📊 查看 [[代码覆盖率][jacoco/index.html]] 了解测试覆盖情况

---

**需要帮助？**

- 查看项目文档：`doc/` 目录
- 提交 Issue：https://github.com/example/demo/issues
- 阅读更新日志：`doc/01-入门指南/UPDATE_SUMMARY.md`
