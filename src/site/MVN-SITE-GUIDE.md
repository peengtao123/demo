# Maven Site 使用指南

## 📋 概述

本项目已配置 Maven Site 插件，可以自动生成项目文档、报告和统计信息。

## 📁 文件结构

```
src/site/
├── site.xml              # 站点配置文件（导航菜单、主题等）
├── index.apt             # 首页（APT 格式）
├── quick-start.md        # 快速开始指南（Markdown 格式）
└── user-guide.fml        # 用户指南（FML/XML 格式）
```

## 🚀 快速开始

### 1. 生成站点

```bash
# 生成完整的站点文档
./mvnw site

# 跳过测试加快速度
./mvnw site -DskipTests
```

生成的站点位于：`target/site/` 目录

### 2. 预览站点

```bash
# 启动本地服务器预览站点
./mvnw site:run

# 然后在浏览器中访问
http://localhost:8080
```

### 3. 清理站点

```bash
# 清理生成的站点文件
./mvnw site-clean
```

## 📊 可用报告

当前配置包含以下报告：

### 项目信息报告
- **index.html** - 项目首页
- **summary.html** - 项目摘要
- **dependencies.html** - 依赖列表
- **team.html** - 团队信息
- **scm.html** - 源码管理信息

### 可选报告（需要时启用）

如需添加更多报告，在 `pom.xml` 的 `<reporting>` 部分添加：

#### Javadoc 报告
```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-javadoc-plugin</artifactId>
    <version>3.6.0</version>
</plugin>
```

#### 测试报告
```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-report-plugin</artifactId>
    <version>3.1.2</version>
</plugin>
```

#### 代码覆盖率（JaCoCo）
```xml
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.11</version>
</plugin>
```

#### 代码质量检查
```xml
<!-- Checkstyle -->
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-checkstyle-plugin</artifactId>
    <version>3.3.1</version>
</plugin>

<!-- PMD -->
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-pmd-plugin</artifactId>
    <version>3.21.0</version>
</plugin>

<!-- SpotBugs -->
<plugin>
    <groupId>com.github.spotbugs</groupId>
    <artifactId>spotbugs-maven-plugin</artifactId>
    <version>4.7.3.6</version>
</plugin>
```

## 📝 文档格式支持

Maven Site 支持多种文档格式：

| 格式 | 扩展名 | 说明 |
|------|--------|------|
| APT | `.apt` | Almost Plain Text，简单易用 |
| Markdown | `.md` | 流行的标记语言 |
| FML | `.fml` | Faq Markup Language，基于 XML |
| XDoc | `.xml` | Maven XDoc 格式 |
| XHTML | `.xhtml` | 标准 HTML |

### 示例：创建新页面

**APT 格式示例** (`src/site/my-page.apt`):
```
------
我的页面标题
------

这是一个段落。

* 列表项 1
* 列表项 2
* 列表项 3

{{{
代码块
}}}

[[链接文本][other-page.html]]
```

**Markdown 格式示例** (`src/site/my-page.md`):
```markdown
# 我的页面标题

这是一个段落。

- 列表项 1
- 列表项 2
- 列表项 3

```
代码块
```

[链接文本](other-page.html)
```

## 🎨 自定义站点外观

### 修改主题

在 `site.xml` 中更改皮肤：

```xml
<skin>
    <groupId>org.apache.maven.skins</groupId>
    <artifactId>maven-fluido-skin</artifactId>
    <version>1.11.1</version>
</skin>
```

可用皮肤：
- `maven-fluido-skin` - 现代响应式主题（推荐）
- `maven-classic-skin` - 经典主题
- `maven-stylus-skin` - Stylus 主题

### 自定义导航菜单

编辑 `site.xml` 中的 `<body><menu>` 部分：

```xml
<body>
    <menu name="我的菜单">
        <item name="页面1" href="page1.html"/>
        <item name="页面2" href="page2.html"/>
    </menu>
</body>
```

### 添加自定义 CSS

在 `src/site/resources/css/` 目录下创建自定义样式文件，然后在 `site.xml` 中引用：

```xml
<custom>
    <fluidoSkin>
        <cssSourceFile>custom.css</cssSourceFile>
    </fluidoSkin>
</custom>
```

## 🔧 常见问题

### Q1: 生成站点时网络超时？

**解决方案：**
1. 检查网络连接
2. 配置 Maven 代理（如果需要）
3. 使用离线模式：`./mvnw site -o`（需要之前已下载依赖）

### Q2: 如何只生成特定报告？

**解决方案：**
```bash
# 只生成 Javadoc
./mvnw javadoc:javadoc

# 只生成测试报告
./mvnw surefire-report:report

# 只生成覆盖率报告
./mvnw jacoco:report
```

### Q3: 中文乱码？

**解决方案：**
确保在 `pom.xml` 中配置了正确的编码：

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-site-plugin</artifactId>
    <configuration>
        <locales>zh_CN</locales>
        <outputEncoding>UTF-8</outputEncoding>
    </configuration>
</plugin>
```

### Q4: 如何部署站点到服务器？

**解决方案：**

1. 配置部署目标（在 `pom.xml` 中添加）：
```xml
<distributionManagement>
    <site>
        <id>website</id>
        <url>scp://your-server.com/var/www/html</url>
    </site>
</distributionManagement>
```

2. 执行部署：
```bash
./mvnw site-deploy
```

## 📚 更多信息

- [Maven Site Plugin 官方文档](https://maven.apache.org/plugins/maven-site-plugin/)
- [Maven Doxia 文档格式](https://maven.apache.org/doxia/references/index.html)
- [Fluido Skin 主题](https://maven.apache.org/skins/maven-fluido-skin/)

## 🎯 最佳实践

1. **保持文档更新**：每次功能变更后同步更新文档
2. **使用合适的格式**：简单文档用 Markdown，复杂结构用 APT/FML
3. **定期生成报告**：在 CI/CD 流程中集成站点生成
4. **版本控制**：将 `src/site/` 纳入 Git 管理，但排除 `target/site/`
5. **本地预览**：发布前使用 `site:run` 预览效果

---

**最后更新**: 2026-04-18
