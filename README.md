# 📚 图书管理系统 (Library Management System)

一个功能完整的智能图书管理平台，融合图书借阅、社区互动、阅览室预约等丰富功能。

采用现代化技术栈架构：后端 Spring Boot 3.3.2 + JPA + Security，前端 Vue 3 + Vite + Element Plus，提供完整的 RBAC 权限管理、REST API、响应式设计和数据安全保障。

**✨ 核心亮点：**
- 📖 **完整的图书管理系统** - 图书管理、分类、搜索、借还
- 💬 **社区互动平台** - 发帖、评论、点赞、收藏、搜索
- 🎯 **活动管理模块** - 活动发布、报名、签到
- 📚 **阅览室预约系统** - 座位预约、签到签退
- 👤 **用户个人中心** - 头像上传、信息编辑、借阅历史
- 🔐 **权限管理体系** - 基于角色的访问控制 (RBAC)

---

## ⚡ 快速开始

### 系统要求

| 环境 | 版本要求 | 说明 |
|------|---------|------|
| JDK | 21+ | 必需 |
| MySQL | 8.0+ | 需启动服务 |
| Node.js | 16+ | 前端开发 |
| npm | 7.0+ | 包管理器 |

## 🔧 初始化步骤

### 1. 数据库初始化

```bash
# 执行初始化脚本
mysql -u root -p < init.sql
```

**默认账户**

| 账户 | 密码 | 角色 |
|------|--------|--------|
| admin | admin123 | 管理员 |
| user1 | admin123 | 普通用户 |

### 2. 后端配置

编辑 `src/main/resources/application.properties`：

```properties
# 数据库连接
spring.datasource.url=jdbc:mysql://localhost:3306/book_management?useSSL=false&serverTimezone=UTC&characterEncoding=utf8mb4
spring.datasource.username=root
spring.datasource.password=你的MySQL密码

# 应用配置
server.port=8080
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
```

### 3. 前端依赖

```bash
cd frontend
npm install
```

## 🚀 启动项目

### 方案 A: 命令行启动

**后端**
```bash
mvn spring-boot:run
```
访问: http://localhost:8080

**前端（新终端）**
```bash
cd frontend
npm run dev
```
访问: http://localhost:5173

### 方案 B: IDE 启动（IntelliJ IDEA）

1. 打开 `Application.java` 并运行 `main` 方法
2. 前端终端执行 `cd frontend && npm run dev`
3. 浏览器访问 http://localhost:5173

### 方案 C: 一键启动（Windows）

```batch
start-all.bat
```

等待 30 秒后：

```batch
get-ip.bat
```

在手机浏览器中使用打印的 IP 地址访问

---

## 📦 核心功能

### 用户管理
- ✅ 用户注册与登录
- ✅ 用户信息管理（管理员）
- ✅ 密码修改
- ✅ 角色权限管理（USER / ADMIN）
- ✅ 用户头像上传

### 图书管理
- ✅ 图书列表浏览（支持分页）
- ✅ 多维度搜索（标题、作者、分类）
- ✅ 图书详情展示
- ✅ 图书维护（新增、编辑、删除）
- ✅ 库存管理
- ✅ 借还功能

### 借阅管理
- ✅ 借书功能
- ✅ 还书功能
- ✅ 借阅历史记录
- ✅ 状态管理（借阅中 / 已返还 / 逾期）
- ✅ 逾期检测
- ✅ 自定义借期（7-90 天）
- ✅ 借阅排行榜

### 社区功能
- ✅ 多版块社区（读书分享、技术交流、新书推荐、活动讨论）
- ✅ 帖子发布与编辑
- ✅ 评论回复（支持嵌套回复）
- ✅ 点赞功能（帖子和评论）
- ✅ 收藏功能（我的收藏列表）
- ✅ 帖子搜索（全站和版块内搜索）
- ✅ 排序筛选（最新、最热、点赞数）
- ✅ 热门标记和置顶功能

### 活动管理
- ✅ 活动发布与展示
- ✅ 活动报名与管理
- ✅ 签到功能
- ✅ 活动收藏
- ✅ 活动分类筛选

### 阅览室预约
- ✅ 阅览室列表展示
- ✅ 座位实时显示
- ✅ 在线预约座位
- ✅ 预约签到签退
- ✅ 预约管理

---

## 🌐 API 端点

### 用户 API
```
POST   /api/users/login          - 登录
POST   /api/users/register       - 注册
GET    /api/users                - 获取所有用户 [管理员]
GET    /api/users/{id}           - 获取用户
PUT    /api/users/{id}           - 修改用户 [管理员]
DELETE /api/users/{id}           - 删除用户 [管理员]
PUT    /api/users/{id}/password  - 改密
GET    /api/users/{id}/profile   - 获取个人资料
PUT    /api/users/{id}/avatar    - 上传头像
```

### 图书 API
```
GET    /api/books                - 获取列表（分页）
GET    /api/books/{id}           - 获取详情
POST   /api/books                - 新增 [管理员]
PUT    /api/books/{id}           - 编辑 [管理员]
DELETE /api/books/{id}           - 删除 [管理员]
GET    /api/books/search/title   - 按标题搜索
GET    /api/books/search/author  - 按作者搜索
GET    /api/books/search/category - 按分类搜索
PUT    /api/books/{id}/stock     - 更新库存 [管理员]
```

### 借阅 API
```
GET    /api/borrows              - 获取所有记录 [管理员]
GET    /api/borrows/user/{id}    - 获取用户记录
GET    /api/borrows/book/{id}    - 获取图书记录 [管理员]
GET    /api/borrows/status/{status} - 按状态查询
GET    /api/borrows/ranking      - 获取借阅排行
POST   /api/borrows              - 借书
PUT    /api/borrows/{id}/return  - 还书
PUT    /api/borrows/update-overdue - 更新逾期 [管理员]
```

### 社区 API
```
# 版块
GET    /api/forum/sections       - 获取所有版块
GET    /api/forum/sections/active - 获取活跃版块
POST   /api/forum/sections       - 创建版块 [管理员]
PUT    /api/forum/sections/{id}  - 更新版块 [管理员]
DELETE /api/forum/sections/{id}  - 删除版块 [管理员]

# 帖子
GET    /api/forum/posts          - 获取帖子列表
GET    /api/forum/posts/{id}     - 获取帖子详情
GET    /api/forum/posts/search   - 搜索帖子
GET    /api/forum/posts/latest   - 最新发布
GET    /api/forum/posts/hot      - 热门帖子
GET    /api/forum/posts/likes    - 按点赞排序
POST   /api/forum/posts          - 发布帖子
PUT    /api/forum/posts/{id}     - 编辑帖子
DELETE /api/forum/posts/{id}     - 删除帖子

# 点赞
POST   /api/forum/posts/{id}/like   - 点赞/取消
GET    /api/forum/posts/{id}/like   - 检查点赞状态

# 收藏
POST   /api/forum/posts/{id}/favorite     - 收藏/取消
GET    /api/forum/posts/{id}/favorite     - 检查收藏状态
GET    /api/forum/posts/favorites/{userId} - 我的收藏

# 评论
GET    /api/forum/comments/post/{postId} - 获取评论
POST   /api/forum/comments        - 发表评论
DELETE /api/forum/comments/{id}   - 删除评论
```

### 活动 API
```
GET    /api/activities           - 获取活动列表
GET    /api/activities/upcoming  - 获取即将开始
GET    /api/activities/hot       - 获取热门活动
GET    /api/activities/{id}      - 获取活动详情
POST   /api/activities           - 创建活动 [管理员]
PUT    /api/activities/{id}      - 编辑活动 [管理员]
DELETE /api/activities/{id}      - 删除活动 [管理员]

POST   /api/activities/{id}/register      - 报名活动
DELETE /api/activities/{id}/register      - 取消报名
GET    /api/activities/{id}/registrations - 获取报名列表
PUT    /api/activities/registrations/{id}/checkin - 签到
```

### 阅览室 API
```
GET    /api/reading-rooms        - 获取阅览室列表
GET    /api/reading-rooms/{id}   - 获取阅览室详情
GET    /api/reading-rooms/{id}/seats - 获取座位
GET    /api/reading-rooms/{id}/seats/available - 获取可用座位

POST   /api/reading-rooms/seats/{id}/reserve  - 预约座位
PUT    /api/reading-rooms/reservations/{id}/checkin  - 签到
PUT    /api/reading-rooms/reservations/{id}/checkout - 签退
DELETE /api/reading-rooms/reservations/{id}   - 取消预约
GET    /api/reading-rooms/reservations/user/{id} - 我的预约
```

---

## 📁 项目结构

```
D:\2025021238-HEYINLIN\
│
├── src/main/java/com/example/_025021238heyinlin/
│   ├── config/                  # Spring 配置
│   │   ├── CorsConfig.java
│   │   ├── SecurityConfig.java
│   │   └── DataInitializer.java
│   ├── controller/              # REST 接口
│   │   ├── UserController.java
│   │   ├── BookController.java
│   │   ├── BorrowController.java
│   │   ├── ForumController.java      # 社区接口
│   │   ├── ActivityController.java   # 活动接口
│   │   ├── ReadingRoomController.java # 阅览室接口
│   │   ├── CategoryController.java
│   │   └── StatisticsController.java
│   ├── service/                 # 业务逻辑
│   │   ├── UserService.java
│   │   ├── BookService.java
│   │   ├── BorrowService.java
│   │   ├── ForumService.java        # 社区服务
│   │   ├── ActivityService.java     # 活动服务
│   │   ├── ReadingRoomService.java  # 阅览室服务
│   │   ├── CategoryService.java
│   │   └── StatisticsService.java
│   ├── entity/                  # JPA 实体
│   │   ├── User.java
│   │   ├── Book.java
│   │   ├── BorrowRecord.java
│   │   ├── Post.java               # 帖子
│   │   ├── Comment.java            # 评论
│   │   ├── PostLike.java           # 点赞
│   │   ├── PostFavorite.java       # 收藏
│   │   ├── ForumSection.java       # 版块
│   │   ├── Activity.java           # 活动
│   │   ├── ActivityRegistration.java
│   │   ├── ReadingRoom.java        # 阅览室
│   │   ├── Seat.java              # 座位
│   │   ├── SeatReservation.java    # 座位预约
│   │   └── Category.java
│   ├── repository/              # 数据访问层
│   │   ├── UserRepository.java
│   │   ├── BookRepository.java
│   │   ├── BorrowRecordRepository.java
│   │   ├── PostRepository.java
│   │   ├── CommentRepository.java
│   │   ├── PostLikeRepository.java
│   │   ├── PostFavoriteRepository.java
│   │   ├── ForumSectionRepository.java
│   │   ├── ActivityRepository.java
│   │   ├── ReadingRoomRepository.java
│   │   └── SeatRepository.java
│   ├── dto/                     # 数据传输对象
│   │   ├── LoginRequest.java
│   │   ├── UserDTO.java
│   │   ├── BookDTO.java
│   │   ├── BorrowRecordDTO.java
│   │   ├── PostDTO.java
│   │   ├── CommentDTO.java
│   │   ├── ForumSectionDTO.java
│   │   ├── ActivityDTO.java
│   │   ├── ReadingRoomDTO.java
│   │   ├── ApiResponse.java
│   │   └── 其他 DTO 类
│   └── Application.java         # 启动类
│
├── frontend/                    # Vue 3 前端项目
│   ├── src/
│   │   ├── api/                 # API 接口
│   │   │   ├── index.js         # 所有 API 定义
│   │   │   └── request.js       # Axios 请求配置
│   │   ├── assets/              # 静态资源
│   │   ├── components/          # 公共组件
│   │   │   └── HeaderNav.vue    # 导航栏
│   │   ├── router/              # 路由配置
│   │   │   └── index.js
│   │   ├── store/               # Pinia 状态管理
│   │   │   ├── user.js          # 用户状态
│   │   │   └── book.js          # 图书状态
│   │   ├── views/               # 页面组件
│   │   │   ├── Home.vue              # 首页
│   │   │   ├── Login.vue             # 用户登录
│   │   │   ├── Register.vue          # 用户注册
│   │   │   ├── AdminLogin.vue        # 管理员登录
│   │   │   ├── BookList.vue          # 图书列表
│   │   │   ├── BookDetail.vue        # 图书详情
│   │   │   ├── BookForm.vue          # 图书表单 [管理员]
│   │   │   ├── BookRanking.vue       # 借阅排行
│   │   │   ├── Profile.vue           # 个人资料
│   │   │   ├── UserManage.vue        # 用户管理 [管理员]
│   │   │   ├── Forum.vue             # 社区首页
│   │   │   ├── PostDetail.vue        # 帖子详情
│   │   │   ├── Activities.vue        # 活动列表
│   │   │   ├── ActivityDetail.vue    # 活动详情
│   │   │   ├── ReadingRoom.vue       # 阅览室预约
│   │   │   └── 其他页面
│   │   ├── App.vue              # 根组件
│   │   ├── main.js              # 入口文件
│   │   └── style.css            # 全局样式
│   ├── index.html               # HTML 入口
│   ├── package.json             # 依赖配置
│   └── vite.config.js           # Vite 配置
│
├── pom.xml                      # Maven 配置
├── init.sql                     # 数据库初始化脚本
├── update_forum.sql             # 社区功能更新脚本
├── start-all.bat                # 一键启动所有服务 (Windows)
├── stop-all.bat                 # 停止所有服务 (Windows)
├── get-ip.bat                   # 获取局域网 IP 地址
├── mvnw / mvnw.cmd              # Maven Wrapper 脚本
├── MOBILE_ACCESS_GUIDE.md       # 手机访问指南
└── README.md                    # 项目文档 (本文件)
```

---

## 🔐 技术栈

### 后端技术
- **Spring Boot 3.3.2** - Web 框架
- **Spring Security 6.x** - 权限认证
- **Spring Data JPA** - 数据访问
- **Hibernate** - ORM 框架
- **MySQL 8.0** - 关系数据库
- **BCrypt** - 密码加密
- **Maven** - 项目构建

### 前端技术
- **Vue 3** - UI 框架
- **Vite 5.0** - 构建工具
- **Vue Router 4** - 路由管理
- **Pinia 2.1** - 状态管理
- **Axios** - HTTP 客户端
- **Element Plus** - UI 组件库
- **Glassmorphism** - 设计风格

---

## 🏗️ 架构特点

| 特性 | 说明 |
|------|------|
| **三层架构** | Controller-Service-Repository 清晰分层 |
| **RBAC** | 基于角色的访问控制，支持 USER 和 ADMIN |
| **REST API** | 规范的 RESTful 接口设计 |
| **CORS** | 支持跨域请求 |
| **权限注解** | @PreAuthorize 细粒度权限控制 |
| **事务管理** | @Transactional 保证数据一致性 |
| **异常处理** | 统一的错误处理和响应格式 |
| **响应式设计** | 适配桌面端和移动端 |

---

## 🚀 生产构建

### 前端
```bash
cd frontend
npm install
npm run build
# 输出到 dist/ 目录
```

### 后端
```bash
mvn clean package
# 生成 JAR: target/2025021238-HEYINLIN-0.0.1-SNAPSHOT.jar
java -jar target/2025021238-HEYINLIN-0.0.1-SNAPSHOT.jar
```

---

## 🐛 常见问题

| 问题 | 解决方案 |
|------|--------|
| MySQL 连接错误 | 确认服务运行，检查配置文件中的连接信息 |
| 前端无法连接后端 | 确保后端在 8080 运行，检查 CORS 配置 |
| 登录失败 | 确认默认账户（admin/admin123），检查用户是否被禁用 |
| 端口被占用 | 修改 application.properties 中的 server.port |
| npm 依赖错误 | 删除 node_modules 和 package-lock.json，重新 npm install |
| 手机无法访问 | 确保同一 WiFi，检查防火墙设置 |
| 数据库初始化失败 | 确保用户有创建数据库权限，使用 root 用户执行 |

---

## 📝 功能清单

### ✅ 已实现功能

**用户与权限**
- [x] 用户注册与登录
- [x] 管理员登录
- [x] 权限管理 (RBAC - USER/ADMIN)
- [x] 用户信息编辑
- [x] 头像上传与显示

**图书管理**
- [x] 图书增删改查
- [x] 图书分类管理
- [x] 多维度搜索（标题、作者、分类）
- [x] 库存管理
- [x] 借阅排行榜

**借阅功能**
- [x] 借书功能
- [x] 还书功能
- [x] 借阅历史
- [x] 逾期检测
- [x] 自定义借期

**社区功能** 🆕
- [x] 多版块社区（读书分享、技术交流、新书推荐、活动讨论）
- [x] 帖子发布与编辑
- [x] 评论回复（支持嵌套）
- [x] 帖子点赞
- [x] 帖子收藏
- [x] 帖子搜索（全站和版块内）
- [x] 多种排序筛选
- [x] 热门标记和置顶

**活动管理** 🆕
- [x] 活动发布与展示
- [x] 活动报名与取消
- [x] 签到功能
- [x] 活动收藏

**阅览室预约** 🆕
- [x] 阅览室列表显示
- [x] 座位实时查询
- [x] 在线座位预约
- [x] 预约签到签退

**技术特性**
- [x] 前后端分离（Spring Boot + Vue 3）
- [x] REST API 设计
- [x] CORS 跨域配置
- [x] 响应式布局
- [x] 权限注解控制
- [x] 事务管理
- [x] 统一异常处理

### 🚀 优化建议
- [ ] 单元测试覆盖
- [ ] 集成测试
- [ ] Docker 部署
- [ ] 邮件通知（逾期提醒、活动提醒）
- [ ] 数据统计和报表
- [ ] 用户评价体系
- [ ] 图书推荐算法
- [ ] Redis 缓存优化
- [ ] Swagger API 文档
- [ ] 消息队列异步处理
- [ ] 性能指标监控

---

## 📞 支持与反馈

- 项目主路径: `D:\2025021238-HEYINLIN`
- 手机访问: 参考 [MOBILE_ACCESS_GUIDE.md](MOBILE_ACCESS_GUIDE.md)
- 问题反馈: 查看后端日志或浏览器控制台

---

**祝你使用愉快！** 🎉

*最后更新: 2025 年 12 月*
