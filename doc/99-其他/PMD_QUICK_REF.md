# PMD 快速参考

## 🎯 常用命令

```bash
# 生成报告(不会失败)
mvn pmd:pmd

# 执行检查(发现问题会失败)
mvn pmd:check

# 完整构建+检查
mvn verify -DskipTests

# 生成站点报告
mvn site

# 跳过检查
mvn verify -Dpmd.skip=true
```

## 📍 报告位置

- **XML**: `target/pmd.xml`
- **HTML**: `target/site/pmd.html` (执行 `mvn site` 后)

## 🔧 忽略规则

### 方法级别
```java
@SuppressWarnings("PMD.UnusedLocalVariable")
public void myMethod() {
    // 代码
}
```

### 文件级别
在 `pmd-ruleset.xml` 中排除:
```xml
<rule ref="category/java/bestpractices.xml">
    <exclude name="规则名称"/>
</rule>
```

## 📊 优先级说明

| 优先级 | 说明 | 建议处理时间 |
|--------|------|-------------|
| 1 | 最高 - 严重问题 | 立即修复 |
| 2 | 高 - 重要问题 | 尽快修复 |
| 3 | 中 - 一般问题 | 计划修复 |
| 4 | 低 - 轻微问题 | 有空再修 |
| 5 | 最低 - 建议 | 可选修复 |

## 🚨 常见问题 Top 5

1. **UnitTestAssertionsShouldIncludeMessage** - 断言缺少消息
2. **LocalVariableCouldBeFinal** - 变量可声明为 final
3. **UnitTestContainsTooManyAsserts** - 测试断言过多
4. **AvoidDuplicateLiterals** - 重复字符串
5. **CyclomaticComplexity** - 复杂度过高

## 💡 快速修复示例

### 添加断言消息
```java
// ❌ Before
assertEquals(expected, actual);

// ✅ After
assertEquals("用户ID应该匹配", expected, actual);
```

### 添加 final
```java
// ❌ Before
String name = "test";

// ✅ After
final String name = "test";
```

### 提取常量
```java
// ❌ Before
if (status.equals("ACTIVE")) { ... }
if (status.equals("ACTIVE")) { ... }

// ✅ After
private static final String STATUS_ACTIVE = "ACTIVE";
if (status.equals(STATUS_ACTIVE)) { ... }
```

## 🔗 相关链接

- [详细使用指南](./PMD_USAGE_GUIDE.md)
- [配置总结](./PMD_SETUP_SUMMARY.md)
- [PMD 官方文档](https://pmd.github.io/)
