# PMD 代码质量检查工具使用指南

## 什么是 PMD?

PMD 是一个源代码静态分析工具,用于检测代码中的潜在问题,包括:
- 潜在的 bug(如空指针引用)
- 未使用的变量、参数或导入
- 重复代码
- 复杂的代码结构
- 性能问题
- 不良的编码实践

## 项目配置

本项目已集成 PMD 插件,配置文件位于:
- **插件配置**: `pom.xml` (maven-pmd-plugin)
- **规则集**: `pmd-ruleset.xml` (自定义规则集)

### 规则集说明

当前使用的规则集包含以下类别:

1. **最佳实践** (`bestpractices.xml`)
   - 检测常见的编程错误
   - 推荐使用更好的编程实践

2. **代码风格** (`codestyle.xml`)
   - 命名规范
   - 代码格式检查
   - 括号使用规范

3. **复杂度** (`design.xml`)
   - 循环复杂度检查(方法级别 ≤ 10,类级别 ≤ 80)
   - 过长的方法/类检测
   - 过多的参数检测

4. **文档注释** (`documentation.xml`)
   - Javadoc 注释检查

5. **错误预防** (`errorprone.xml`)
   - 常见错误模式检测
   - 资源泄漏检测

6. **多线程** (`multithreading.xml`)
   - 线程安全问题检测

7. **性能** (`performance.xml`)
   - 性能优化建议
   - 不必要的对象创建检测

8. **安全** (`security.xml`)
   - 安全漏洞检测

## 运行 PMD 检查

### 1. 基本检查命令

```bash
# 执行 PMD 检查(在 verify 阶段自动执行)
mvn verify

# 仅执行 PMD 检查
mvn pmd:check

# 生成 PMD 报告
mvn pmd:pmd
```

### 2. 查看详细报告

```bash
# 生成完整的项目站点报告(包含 PMD)
mvn site

# 报告位置: target/site/pmd.html
```

### 3. 忽略检查结果继续构建

如果 PMD 检查失败但您想继续构建:

```bash
mvn verify -Dpmd.skip=true
```

## 查看检查结果

### XML 报告
位置: `target/pmd.xml`

### HTML 报告
执行 `mvn site` 后,打开 `target/site/pmd.html` 查看可视化的报告。

### 控制台输出
PMD 会在控制台打印违规信息,包括:
- 文件路径
- 行号
- 违规描述
- 优先级

## 常见问题处理

### 1. 误报处理

如果某些规则不适合您的项目,可以在 `pmd-ruleset.xml` 中排除:

```xml
<rule ref="category/java/bestpractices.xml">
    <exclude name="规则名称"/>
</rule>
```

### 2. 单行忽略

在代码中使用注解忽略特定行的检查:

```java
@SuppressWarnings("PMD.UnusedLocalVariable")
public void someMethod() {
    // 代码
}
```

### 3. 调整规则阈值

在 `pmd-ruleset.xml` 中修改规则属性:

```xml
<rule ref="category/java/design.xml/CyclomaticComplexity">
    <properties>
        <property name="methodReportLevel" value="15"/> <!-- 提高阈值 -->
    </properties>
</rule>
```

## VSCode 集成

### 安装 PMD 插件

1. 在 VSCode 扩展市场搜索 "PMD"
2. 安装 "PMD for Java" 插件
3. 配置插件指向项目的规则集文件

### 实时检查

安装插件后,VSCode 会实时显示 PMD 警告和错误。

## 持续集成

PMD 已配置在 Maven 的 `verify` 阶段自动执行,确保:
- 每次构建都会进行代码质量检查
- 违反规则会导致构建失败
- 保证代码质量的一致性

## 最佳实践建议

1. **定期运行**: 在开发过程中定期运行 `mvn pmd:check`
2. **逐步改进**: 对于遗留代码,可以逐步修复问题
3. **团队共识**: 与团队讨论并确定适合的规则集
4. **结合其他工具**: 配合 Checkstyle、SpotBugs 等工具使用
5. **代码审查**: 将 PMD 报告作为代码审查的参考

## 相关资源

- [PMD 官方文档](https://pmd.github.io/)
- [PMD 规则列表](https://pmd.github.io/latest/pmd_rules_java.html)
- [Maven PMD Plugin](https://maven.apache.org/plugins/maven-pmd-plugin/)

## 示例输出

```
[INFO] --- maven-pmd-plugin:3.28.0:check (pmd-check) @ demo ---
[WARNING] PMD Failure: com.example.demo.controller.AdminController:45 
Rule:CyclomaticComplexity Priority:3 
Avoid methods with high cyclomatic complexity (12 > 10).
[INFO] ------------------------------------------------------------------------
[INFO] BUILD FAILURE
[INFO] ------------------------------------------------------------------------
```

这表明 `AdminController` 的第 45 行方法复杂度过高,需要重构。
