# Thymeleaf 视图功能说明

## 概述

本项目已集成 Thymeleaf 模板引擎，提供现代化的服务端渲染视图功能。

## 技术栈

- **Spring Boot 4.0.5**
- **Thymeleaf**: 服务端模板引擎
- **H2 Database**: 内存数据库（用于演示）

## 页面路由

### 1. 首页
- **URL**: `/pages/`
- **描述**: 系统首页，显示用户统计信息
- **模板**: `templates/index.html`

### 2. 用户列表页
- **URL**: `/pages/users`
- **描述**: 展示所有用户的列表
- **模板**: `templates/users/list.html`
- **特性**: 
  - 卡片式布局
  - 悬停动画效果
  - 空状态提示

### 3. 用户详情页
- **URL**: `/pages/users/{id}`
- **描述**: 展示单个用户的详细信息
- **模板**: `templates/users/detail.html`
- **特性**:
  - 头像显示
  - 完整用户信息
  - 响应式设计

### 4. 错误页面
- **URL**: 自动处理错误情况
- **模板**: `templates/error.html`

## API 路由（保持不变）

所有原有的 REST API 仍然可用：

- `GET /api/users` - 获取所有用户
- `GET /api/users/{id}` - 获取指定用户
- `POST /api/users` - 创建用户
- `PUT /api/users/{id}` - 更新用户
- `DELETE /api/users/{id}` - 删除用户
- `GET /api/users/username/{username}` - 根据用户名查询
- `GET /api/users/search?name={name}` - 搜索用户

## 启动应用

```bash
mvn spring-boot:run
```

访问以下地址：
- 首页: http://localhost:8080/pages/
- 用户列表: http://localhost:8080/pages/users
- API接口: http://localhost:8080/api/users

## Thymeleaf 特性使用

### 1. 数据绑定
```html
<!-- 显示用户姓名 -->
<span th:text="${user.name}">默认值</span>
```

### 2. 条件渲染
```html
<!-- 空状态判断 -->
<div th:if="${users.empty}">暂无数据</div>
```

### 3. 循环遍历
```html
<!-- 遍历用户列表 -->
<div th:each="user : ${users}" class="user-card">
    <!-- 用户卡片内容 -->
</div>
```

### 4. 日期格式化
```html
<!-- 格式化日期时间 -->
<span th:text="${#temporals.format(user.createTime, 'yyyy-MM-dd HH:mm:ss')}"></span>
```

### 5. URL 构建
```html
<!-- 带参数的URL -->
<a th:href="@{/pages/users/{id}(id=${user.id})}">查看详情</a>
```

### 6. 字符串操作
```html
<!-- 提取首字母并转大写 -->
<span th:text="${#strings.substring(user.name, 0, 1).toUpperCase()}"></span>
```

## 项目结构

```
src/main/
├── java/com/example/demo/
│   ├── controller/
│   │   ├── UserController.java      # REST API控制器
│   │   └── PageController.java       # 页面控制器（新增）
│   ├── service/
│   ├── repository/
│   ├── entity/
│   └── dto/
└── resources/
    ├── templates/                    # Thymeleaf模板目录
    │   ├── index.html                # 首页
    │   ├── error.html                # 错误页
    │   └── users/
    │       ├── list.html             # 用户列表页
    │       └── detail.html           # 用户详情页
    └── application.properties
```

## 设计特点

1. **现代化UI**: 使用渐变色、圆角、阴影等现代设计元素
2. **响应式布局**: 适配不同屏幕尺寸
3. **交互体验**: 悬停动画、平滑过渡效果
4. **语义化HTML**: 良好的可访问性
5. **错误处理**: 友好的错误提示页面

## 扩展建议

可以进一步添加以下功能：
- 用户创建/编辑表单页面
- 分页功能
- 搜索和过滤
- 用户头像上传
- 数据可视化图表
- 国际化支持

## 注意事项

1. `@Controller` 用于返回视图，`@RestController` 用于返回JSON数据
2. Thymeleaf模板默认位于 `src/main/resources/templates/` 目录
3. 模板文件扩展名为 `.html`
4. 使用 `th:` 前缀的Thymeleaf属性在服务器端处理
