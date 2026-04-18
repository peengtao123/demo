# 测试报告和代码覆盖率指南

## 概述

本项目已配置完整的自动化测试报告和代码覆盖率生成工具，使用 Maven 插件自动生成 HTML 格式的报告。

## 配置的插件

### 1. Maven Surefire Plugin (3.2.5)
- **功能**：执行单元测试
- **配置位置**：`pom.xml` 的 `<build><plugins>` 部分
- **测试文件匹配**：`**/*Test.java` 和 `**/*Tests.java`

### 2. Maven Surefire Report Plugin (3.2.5)
- **功能**：生成 HTML 格式的测试报告
- **配置位置**：`pom.xml` 的 `<reporting><plugins>` 部分
- **报告内容**：测试通过率、失败详情、执行时间等

### 3. JaCoCo Maven Plugin (0.8.11)
- **功能**：生成代码覆盖率报告
- **配置位置**：`pom.xml` 的 `<build><plugins>` 和 `<reporting><plugins>` 部分
- **覆盖率要求**：最低 60% 指令覆盖率
- **执行阶段**：
  - `prepare-agent`：在测试前准备 JaCoCo agent
  - `report`：在测试后生成覆盖率报告
  - `check`：验证覆盖率是否达到要求

## 生成报告

### 完整命令
```bash
mvn clean test site
```

### 忽略测试失败继续生成报告
```bash
mvn clean test site "-Dmaven.test.failure.ignore=true"
```

### 仅生成测试报告
```bash
mvn surefire-report:report
```

### 仅生成覆盖率报告
```bash
mvn jacoco:report
```

## 报告位置

生成的报告位于 `target/site/` 目录下：

| 报告类型 | 文件路径 | 说明 |
|---------|---------|------|
| 站点首页 | `target/site/index.html` | Maven 站点导航页 |
| 测试报告 | `target/site/surefire-report.html` | 详细的测试结果和统计 |
| 项目概要 | `target/site/summary.html` | 项目基本信息 |
| 项目依赖 | `target/site/dependencies.html` | 依赖列表 |
| 项目团队 | `target/site/team.html` | 团队成员信息 |
| 覆盖率报告 | `target/site/jacoco/index.html` | 代码覆盖率详细报告（需要测试成功执行） |

原始测试报告位于 `target/surefire-reports/` 目录：
- XML 格式：`TEST-*.xml`
- 文本格式：`*.txt`

## 当前测试状态

### 测试统计
- **总测试数**：111
- **成功**：68
- **失败**：1
- **错误**：42
- **跳过**：0

### 已知问题

#### 1. WebMvcBeanTest 和 WebMvcCustomizationTest
- **问题**：无法注入 ObjectMapper Bean
- **原因**：Spring Boot 4.x 中 ObjectMapper 的自动配置变更
- **影响**：26 个测试用例失败
- **建议**：修复 ObjectMapper 配置或使用 @SpringBootTest 替代切片测试

#### 2. UserRepositoryTest
- **问题**：用户实体密码字段验证失败
- **原因**：测试数据未设置密码字段
- **状态**：已修复（添加了密码字段）
- **剩余问题**：部分测试仍有 ConstraintViolation 错误

#### 3. UserServiceTest
- **问题**：缺少 PasswordEncoder 和 AuditLogService 的 Mock
- **状态**：已修复（添加了 Mock 并启用 lenient 模式）
- **结果**：16 个测试全部通过 ✓

#### 4. PerformanceTest
- **问题**：批量创建用户性能测试超时
- **原因**：9611ms 超过预期的阈值
- **建议**：调整性能阈值或优化批量创建逻辑

## 查看报告

### 方法 1：直接在文件系统中打开
```bash
# Windows
start target\site\surefire-report.html

# macOS
open target/site/surefire-report.html

# Linux
xdg-open target/site/surefire-report.html
```

### 方法 2：使用 Maven Site 插件启动本地服务器
```bash
mvn site:run
```
然后在浏览器中访问 `http://localhost:8080`

### 方法 3：在 VSCode 中预览
1. 安装 "Live Server" 扩展
2. 右键点击 HTML 文件
3. 选择 "Open with Live Server"

## 改进建议

### 短期改进
1. ✅ 修复 UserServiceTest 的 Mock 配置
2. ✅ 修复 UserRepositoryTest 的密码字段问题
3. ⏳ 修复 WebMvcBeanTest 的 ObjectMapper 注入问题
4. ⏳ 调整 PerformanceTest 的性能阈值

### 长期改进
1. 提高代码覆盖率目标（从 60% 提升到 80%）
2. 添加集成测试和端到端测试
3. 配置 CI/CD 流水线自动运行测试并上传覆盖率报告
4. 使用 SonarQube 进行代码质量分析

## 常见问题

### Q1: JaCoCo 报告为什么没有生成？
**A**: 如果测试失败导致 JVM 异常退出，JaCoCo agent 可能无法写入执行数据文件（jacoco.exec）。解决方法：
1. 修复失败的测试
2. 使用 `-Dmaven.test.failure.ignore=true` 忽略失败
3. 检查 `target/jacoco.exec` 文件是否存在

### Q2: 如何排除某些测试类？
**A**: 使用 `-Dtest` 参数：
```bash
mvn test "-Dtest=!WebMvcBeanTest,!WebMvcCustomizationTest"
```

### Q3: 如何提高测试执行速度？
**A**: 
1. 使用并行测试：在 surefire-plugin 中添加 `<parallel>classes</parallel>`
2. 减少 Spring 上下文加载次数
3. 使用 @MockBean 替代真实的 Bean

### Q4: 覆盖率报告中的红色/黄色/绿色代表什么？
**A**:
- 🔴 红色：未覆盖的代码
- 🟡 黄色：部分覆盖的代码
- 🟢 绿色：完全覆盖的代码

## 参考资源

- [Maven Surefire Plugin](https://maven.apache.org/surefire/maven-surefire-plugin/)
- [JaCoCo Maven Plugin](https://www.eclemma.org/jacoco/trunk/doc/maven.html)
- [Maven Site Plugin](https://maven.apache.org/plugins/maven-site-plugin/)
- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)
- [Mockito Documentation](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html)

## 更新日志

- **2026-04-18**: 初始版本，配置测试报告和代码覆盖率插件
- **2026-04-18**: 修复 UserServiceTest 和 UserRepositoryTest 的问题
