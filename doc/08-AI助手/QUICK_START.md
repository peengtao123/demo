# AI助手快速开始指南 🤖

## 5分钟快速上手

### 1️⃣ 配置API密钥（必需）

打开 `src/main/resources/application.properties`，添加：

```properties
spring.ai.openai.api-key=sk-your-api-key-here
```

**获取API Key：**
- 访问 https://platform.openai.com/api-keys
- 创建新的API密钥
- 复制并替换配置文件中的值

### 2️⃣ 启动应用

```bash
mvn spring-boot:run
```

### 3️⃣ 登录系统

- 访问: http://localhost:8080
- 账号: `admin`
- 密码: `admin123`

### 4️⃣ 访问AI助手

在左侧菜单找到：**系统管理 → AI助手 → AI聊天**

### 5️⃣ 开始对话

在输入框输入问题，例如：
- "请介绍一下Spring Boot"
- "如何创建REST API？"
- "解释一下依赖注入"

---

## 常见问题

### ❓ 看到"AI服务未配置"提示？

**原因**: 未配置OpenAI API密钥

**解决**: 
1. 检查 `application.properties` 中是否有 `spring.ai.openai.api-key`
2. 确认API密钥格式正确（以 `sk-` 开头）
3. 重启应用

### ❓ 发送消息后返回错误？

**可能原因**:
- API密钥无效或过期
- 账户余额不足
- 网络连接问题

**解决**:
1. 验证API密钥是否有效
2. 检查OpenAI账户余额
3. 查看控制台日志获取详细错误

### ❓ 菜单中没有AI助手？

**原因**: 当前用户角色没有权限

**解决**:
1. 使用admin账号登录
2. 进入"角色管理"
3. 为相应角色分配 `ai:menu` 和 `ai:chat` 权限

---

## 下一步

- 📖 查看详细文档: [README.md](./README.md)
- 🔧 自定义AI功能: 查看开发指南部分
- 🎨 优化界面: 修改 `templates/admin/ai/chat.html`

---

**提示**: 首次使用建议先用测试账号体验完整功能！
