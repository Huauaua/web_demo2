# MyBlog - 个人博客系统

## 项目简介

MyBlog 是一个基于 Spring Boot 的个人博客系统，提供文章发布、评论管理、Markdown 支持以及后台管理功能。该系统采用传统的 MVC 架构，使用 Thymeleaf 模板引擎渲染页面，MySQL 作为数据库存储。

## 技术栈

- **后端框架**: Spring Boot 2.7.17
- **模板引擎**: Thymeleaf
- **数据库**: MySQL 8.0
- **ORM**: MyBatis + JDBC
- **Markdown 解析**: CommonMark
- **邮件服务**: Spring Mail
- **构建工具**: Maven
- **开发语言**: Java 17
- **其他**: Lombok, Spring DevTools

## 主要功能

### 前台功能
- 首页展示最新文章列表
- 文章详情页（支持 Markdown 渲染）
- 文章分类浏览
- 标签云浏览
- 按年份归档
- 文章搜索
- 评论功能（提交评论）
- 联系表单（发送邮件）
- 关于页面

### 后台管理功能
- 文章管理（增删改查）
- 评论管理（审核、拒绝、删除）
- 批量操作评论
- 文章统计信息
- 分页管理

### API 接口
- RESTful API 支持
- 文章 CRUD 操作
- 评论管理 API
- 分类/标签查询

## 项目结构

```
src/
├── main/
│   ├── java/com/zts/web_demo2/
│   │   ├── Config/                 # 配置类
│   │   │   ├── MailConfig.java     # 邮件配置
│   │   │   └── ViewConfig.java     # 视图配置
│   │   ├── Controller/             # 控制器层
│   │   │   ├── HomeController.java         # 首页控制器
│   │   │   ├── ArticleController.java      # 文章控制器
│   │   │   ├── CommentController.java      # 评论控制器
│   │   │   ├── AdminController.java        # 后台管理控制器
│   │   │   └── AdminCommentController.java # 后台评论管理
│   │   ├── Entity/                 # 实体类
│   │   │   ├── Article.java        # 文章实体
│   │   │   └── Comment.java        # 评论实体
│   │   ├── Repository/             # 数据访问层
│   │   │   ├── UserRepository.java
│   │   │   ├── UserRepositoryImpl.java
│   │   │   ├── CommentRepository.java
│   │   │   └── impl/CommentRepositoryImpl.java
│   │   ├── Service/                # 业务逻辑层
│   │   │   ├── ArticleService.java
│   │   │   ├── CommentService.java
│   │   │   └── impl/
│   │   │       ├── ArticleServiceImpl.java
│   │   │       └── CommentServiceImpl.java
│   │   ├── UserService/            # 用户服务
│   │   │   └── UserService.java
│   │   ├── Util/                   # 工具类
│   │   │   ├── MarkdownUtil.java           # Markdown 工具
│   │   │   └── CustomAttributeProvider.java # 自定义属性提供者
│   │   └── WebDemo2Application.java        # 启动类
│   └── resources/
│       ├── static/                 # 静态资源
│       │   └── index.css
│       ├── templates/              # Thymeleaf 模板
│       │   ├── index.html          # 首页
│       │   ├── articles.html       # 文章列表页
│       │   ├── article.html        # 文章详情页
│       │   ├── about.html          # 关于页
│       │   ├── contact.html        # 联系页
│       │   ├── write-article.html  # 写文章页
│       │   ├── write-markdown-article.html # Markdown 写文章页
│       │   └── admin/              # 后台管理模板
│       │       ├── index.html      # 后台首页
│       │       ├── articles.html   # 文章管理
│       │       ├── edit-article.html # 编辑文章
│       │       └── comments.html   # 评论管理
│       ├── application.properties  # 应用配置
│       ├── schema.sql              # 数据库结构
│       └── data.sql                # 初始数据
└── test/                           # 测试代码
```

## 数据库设计

### 主要表结构

- `posts` - 文章表（包含标题、内容、摘要、状态、阅读量等）
- `categories` - 分类表
- `tags` - 标签表
- `post_categories` - 文章-分类关联表
- `post_tags` - 文章-标签关联表
- `comments` - 评论表（包含作者信息、内容、状态等）

### 数据库特性

- 支持文章状态管理（草稿、发布、归档）
- 自动更新分类和标签计数
- 全文搜索索引
- 评论审核机制（待审核、已批准、垃圾评论）

## 安装和运行

### 环境要求

- Java 17+
- Maven 3.6+
- MySQL 8.0+

### 配置数据库

1. 确保 MySQL 服务正在运行
2. 修改 `src/main/resources/application.properties` 中的数据库配置：

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/demo?useUnicode=true&characterEncoding=utf-8&useSSL=false&allowPublicKeyRetrieval=true
spring.datasource.username=your_username
spring.datasource.password=your_password
```

### 编译和运行

```bash
# 克隆项目
git clone <repository-url>
cd web_demo2

# 编译项目
mvn clean compile

# 运行项目
mvn spring-boot:run
```

或者直接运行主类 `WebDemo2Application.java`。

### 访问应用

启动成功后，访问：
- 前台首页: http://localhost:8080/
- 文章列表: http://localhost:8080/articles
- 后台管理: http://localhost:8080/admin

## 配置说明

### 邮件配置

在 `application.properties` 中配置邮件服务：

```properties
spring.mail.host=smtp.qq.com
spring.mail.port=587
spring.mail.username=your_email@qq.com
spring.mail.password=your_authorization_code
contact.email.to=recipient@example.com
contact.email.from=sender@example.com
```

### Markdown 支持

系统内置 Markdown 渲染功能，支持：
- 标准 Markdown 语法
- 表格扩展
- 标题锚点
- 任务列表
- 代码块高亮

## API 接口

### 文章相关

| 方法 | 路径 | 描述 |
|------|------|------|
| GET | `/articles` | 文章列表页 |
| GET | `/articles/api/all` | 获取所有文章（JSON） |
| GET | `/articles/api/{id}` | 获取单篇文章 |
| GET | `/articles/api/category/{category}` | 按分类获取文章 |
| GET | `/articles/api/tag/{tag}` | 按标签获取文章 |
| GET | `/articles/api/year/{year}` | 按年份获取文章 |
| POST | `/articles/api/create` | 创建文章 |
| PUT | `/articles/api/update/{id}` | 更新文章 |
| DELETE | `/articles/api/delete/{id}` | 删除文章 |

### 评论相关

| 方法 | 路径 | 描述 |
|------|------|------|
| GET | `/comments/api/post/{postId}` | 获取文章评论 |
| POST | `/comments/api/submit` | 提交评论 |
| GET | `/comments/api/count/{postId}` | 获取评论数量 |

## 特色功能

1. **文章序号管理**: 文章具有连续的序号，便于导航
2. **上下篇导航**: 文章详情页提供上一篇/下一篇导航
3. **分页支持**: 文章列表和评论列表均支持分页
4. **Markdown 写作**: 提供专门的 Markdown 写作界面
5. **评论审核**: 评论需经管理员审核后才能显示
6. **邮件通知**: 联系表单通过邮件发送消息
7. **统计信息**: 后台提供文章、分类、标签统计

## 开发指南

### 添加新功能

1. 在 `Entity` 包中创建实体类
2. 在 `Repository` 包中创建数据访问接口和实现
3. 在 `Service` 包中创建业务逻辑接口和实现
4. 在 `Controller` 包中创建控制器
5. 在 `templates` 目录中创建视图模板

### 数据库迁移

修改 `schema.sql` 和 `data.sql` 文件来管理数据库结构和初始数据。

## 注意事项

1. 请妥善保管邮件配置中的密码信息
2. 生产环境建议添加用户认证和权限控制
3. 建议对敏感配置使用环境变量或配置中心
4. 数据库密码请根据实际情况修改

## 许可证

本项目仅供学习交流使用。

## 联系方式

如有问题或建议，请联系开发者。