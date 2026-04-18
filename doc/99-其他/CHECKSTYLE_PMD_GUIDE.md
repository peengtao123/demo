# Checkstyle 与 PMD 代码质量管理指南

## 📖 概述

本项目同时使用 **Checkstyle** 和 **PMD** 两个工具进行代码质量管理，它们各司其职、互补协作：

- **Checkstyle**：专注于代码格式和编码规范（命名、缩进、空格等）
- **PMD**：专注于代码质量和潜在 Bug（未使用变量、空 catch 块、复杂度过高等）

## 🔧 配置文件

### Checkstyle 配置
- **文件位置**：`checkstyle.xml`（项目根目录）
- **基于标准**：Google Java Style Guide
- **主要规则**：
  - 命名规范（驼峰命名、包名小写等）
  - 导入规范（禁止星号导入、移除未使用导入）
  - 代码块规范（大括号位置、必要的大括号）
  - 空白检查（空行分隔、操作符周围空格）
  - 复杂度限制（方法圈复杂度 ≤ 15，NCSS ≤ 80）

### PMD 配置
- **文件位置**：`pmd-ruleset.xml`（项目根目录）
- **涵盖类别**：
  - 最佳实践（Best Practices）
  - 代码风格（Code Style）
  - 设计复杂度（Design）
  - 文档注释（Documentation）
  - 错误预防（Error Prone）
  - 多线程（Multithreading）
  - 性能优化（Performance）
  - 安全规则（Security）

## 🚀 使用方法

### 1. 构建时自动检查（推荐）

在 Maven 构建的 `verify` 阶段自动执行检查：

```bash
mvn verify
```

如果检查发现问题：
- **PMD**：会根据 `failurePriority` 配置决定是否失败（当前设置为 5，较宽松）
- **Checkstyle**：当前配置为警告模式（`failsOnError=false`），不会中断构建

### 2. 单独运行检查

#### Checkstyle
```bash
# 仅检查，不生成报告
mvn checkstyle:check

# 生成 HTML 报告
mvn checkstyle:checkstyle
```

报告位置：`target/site/checkstyle.html`

#### PMD
```bash
# 仅检查，不生成报告
mvn pmd:check

# 生成 HTML 报告
mvn pmd:pmd
```

报告位置：`target/site/pmd.html`

### 3. 生成完整站点报告

生成包含所有质量报告的完整站点：

```bash
mvn site
```

报告位置：`target/site/`

包含的报告：
- Checkstyle 报告
- PMD 报告
- CPD（重复代码检测）报告
- JaCoCo 代码覆盖率报告
- 测试报告
- 项目信息报告

## ⚙️ 配置优化建议

### Checkstyle 调整

#### 严格模式（生产环境推荐）
修改 `pom.xml` 中的 Checkstyle 配置：
```xml
<configuration>
    <failsOnError>true</failsOnError>  <!-- 改为 true -->
</configuration>
```

#### 放宽某些规则
编辑 `checkstyle.xml`，注释掉或调整过于严格的规则：
```xml
<!-- 例如：放宽行长度限制 -->
<module name="LineLength">
    <property name="max" value="150"/>  <!-- 从 120 改为 150 -->
</module>
```

### PMD 调整

#### 自定义规则优先级
编辑 `pmd-ruleset.xml`，排除不需要的规则：
```xml
<rule ref="category/java/bestpractices.xml">
    <exclude name="GuardLogStatement"/>  <!-- 排除日志语句检查 -->
    <exclude name="SystemPrintln"/>       <!-- 排除 System.out 检查 -->
</rule>
```

#### 调整失败阈值
修改 `pom.xml`：
```xml
<configuration>
    <failurePriority>3</failurePriority>  <!-- 1-5，数字越小越严格 -->
</configuration>
```

## 📊 常见问题处理

### 1. Checkstyle 报错太多怎么办？

**方案一**：临时禁用某些规则
在 `checkstyle.xml` 中注释掉相关模块

**方案二**：降低检查严格度
```xml
<property name="severity" value="info"/>  <!-- 从 warning 改为 info -->
```

**方案三**：排除特定文件或目录
```xml
<configuration>
    <excludes>**/dto/**/*,**/entity/**/*</excludes>
</configuration>
```

### 2. PMD 误报如何处理？

**方案一**：使用 `@SuppressWarnings` 注解
```java
@SuppressWarnings("PMD.UnusedLocalVariable")
public void method() {
    int temp = 10; // PMD 会报未使用，但实际有特殊用途
}
```

**方案二**：在规则集中排除特定规则
```xml
<rule ref="category/java/bestpractices.xml/UnusedLocalVariable">
    <properties>
        <property name="violationSuppressRegex" value=".*temp.*"/>
    </properties>
</rule>
```

### 3. 如何在 IDE 中实时检查？

#### VS Code
1. 安装扩展：**Checkstyle for Java**
2. 安装扩展：**PMD**
3. 配置 `.vscode/settings.json`：
```json
{
    "checkstyle.configuration": "${workspaceFolder}/checkstyle.xml",
    "pmd.rulesets": ["${workspaceFolder}/pmd-ruleset.xml"]
}
```

#### IntelliJ IDEA
1. 安装插件：**CheckStyle-IDEA**
2. 安装插件：**PMDPlugin**
3. 在设置中导入配置文件

## 🎯 最佳实践

### 1. 渐进式引入
不要一次性启用所有规则，建议分阶段：
- **第一阶段**：仅启用基础命名和格式规则
- **第二阶段**：添加复杂度检查和最佳实践
- **第三阶段**：启用所有规则并设为严格模式

### 2. 团队协作
- 将配置文件纳入版本控制
- 在 CI/CD 流水线中集成检查
- 定期review和调整规则

### 3. 持续改进
- 每月查看质量报告趋势
- 针对高频问题制定团队规范
- 根据项目特点定制规则

## 📝 示例场景

### 场景 1：新项目初始化
```bash
# 1. 使用宽松配置开始
mvn verify

# 2. 修复所有警告
# 3. 逐步提高严格度
```

### 场景 2：遗留项目改造
```bash
# 1. 先排除问题较多的模块
<excludes>**/legacy/**/*</excludes>

# 2. 逐个模块整改
# 3. 最终移除所有排除项
```

### 场景 3：CI/CD 集成
```yaml
# GitHub Actions 示例
- name: Code Quality Check
  run: mvn verify
  
- name: Generate Reports
  run: mvn site
  
- name: Upload Reports
  uses: actions/upload-artifact@v3
  with:
    name: code-quality-reports
    path: target/site/
```

## 🔗 相关资源

- [Checkstyle 官方文档](https://checkstyle.org/)
- [PMD 官方文档](https://pmd.github.io/)
- [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html)
- [Maven Checkstyle Plugin](https://maven.apache.org/plugins/maven-checkstyle-plugin/)
- [Maven PMD Plugin](https://maven.apache.org/plugins/maven-pmd-plugin/)

## 📞 技术支持

如有问题，请查阅：
1. 项目文档：`doc/99-其他/PMD_*` 相关文件
2. Maven 输出日志
3. 生成的 HTML 报告

---

**最后更新**：2026-04-19  
**维护者**：开发团队
