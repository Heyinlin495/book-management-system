-- 创建数据库
CREATE DATABASE IF NOT EXISTS `book_management` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `book_management`;

-- 创建用户表
CREATE TABLE IF NOT EXISTS `users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(100) NOT NULL UNIQUE,
  `password` varchar(255) NOT NULL,
  `email` varchar(100) NOT NULL UNIQUE,
  `role` varchar(50) DEFAULT 'USER',
  `is_active` tinyint(1) DEFAULT 1,
  `avatar` longtext,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_username` (`username`),
  KEY `idx_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 创建图书表
CREATE TABLE IF NOT EXISTS `books` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(255) NOT NULL,
  `author` varchar(100) NOT NULL,
  `isbn` varchar(50),
  `publisher` varchar(100),
  `publish_date` varchar(50),
  `price` decimal(10,2) NOT NULL,
  `description` longtext,
  `category` varchar(50),
  `stock_quantity` int NOT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_title` (`title`),
  KEY `idx_author` (`author`),
  KEY `idx_category` (`category`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 插入示例管理员用户（密码：admin123 - 需要使用 BCrypt 加密）
-- 何胤霖账户密码为 123456
INSERT INTO `users` (`username`, `email`, `password`, `role`, `is_active`) VALUES
('admin', 'admin@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcg7b3XeKeUxWdeS86E36p4rWG2', 'ADMIN', 1),
('何胤霖', 'heyinlin@example.com', '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW', 'ADMIN', 1),
('user1', 'user1@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcg7b3XeKeUxWdeS86E36p4rWG2', 'USER', 1),
('张三', 'zhangsan@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcg7b3XeKeUxWdeS86E36p4rWG2', 'USER', 1),
('李四', 'lisi@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcg7b3XeKeUxWdeS86E36p4rWG2', 'USER', 1),
('王五', 'wangwu@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcg7b3XeKeUxWdeS86E36p4rWG2', 'USER', 1),
('赵六', 'zhaoliu@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcg7b3XeKeUxWdeS86E36p4rWG2', 'USER', 1),
('孙七', 'sunqi@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcg7b3XeKeUxWdeS86E36p4rWG2', 'USER', 1),
('周八', 'zhouba@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcg7b3XeKeUxWdeS86E36p4rWG2', 'USER', 1);

-- 插入丰富的图书数据
INSERT INTO `books` (`title`, `author`, `isbn`, `publisher`, `publish_date`, `price`, `description`, `category`, `stock_quantity`) VALUES
-- 编程技术类
('Java 并发编程实战', '布莱恩·格茨', '978-7-111-27805-5', '中国工信出版集团', '2012-02-01', 89.00, '专门讨论 Java 多线程编程的书籍。作为 Java 开发者，理解并发编程是非常重要的。', '编程技术', 15),
('Effective Java 中文版', 'Joshua Bloch', '978-7-111-25583-3', '机械工业出版社', '2018-12-01', 119.00, 'Java 编程的最佳实践指南，涵盖90个经典条目，帮助你写出更优雅的代码。', '编程技术', 20),
('Python编程：从入门到实践', 'Eric Matthes', '978-7-115-42802-8', '人民邮电出版社', '2016-07-01', 89.00, 'Python入门经典教程，通过实际项目学习Python编程。', '编程技术', 25),
('JavaScript高级程序设计', 'Nicholas C. Zakas', '978-7-115-27579-0', '人民邮电出版社', '2012-03-01', 99.00, '前端开发者必读经典，深入讲解JavaScript核心概念。', '编程技术', 18),
-- 计算机科学类
('深入理解计算机系统', '兰德尔·布莱恩特', '978-7-111-54895-0', '中国工信出版集团', '2016-01-01', 159.00, '详细讲解了计算机系统的工作原理，包括处理器、内存、I/O 系统等。', '计算机科学', 20),
('计算机网络：自顶向下方法', 'James F. Kurose', '978-7-111-59931-0', '机械工业出版社', '2018-01-01', 89.00, '计算机网络经典教材，从应用层到物理层全面讲解。', '计算机科学', 15),
('操作系统导论', 'Abraham Silberschatz', '978-7-111-60436-6', '机械工业出版社', '2018-06-01', 79.00, '操作系统经典教材，涵盖进程、内存、文件系统等核心概念。', '计算机科学', 22),
-- 算法与数据结构类
('算法导论', '托马斯·科尔曼', '978-7-111-40701-0', '中国工信出版集团', '2013-01-01', 128.00, '经典的算法教科书，涵盖了大多数重要的算法和数据结构。', '算法与数据结构', 18),
('大话数据结构', '程杰', '978-7-302-25565-9', '清华大学出版社', '2011-06-01', 59.00, '用通俗易懂的语言讲解数据结构，适合初学者。', '算法与数据结构', 30),
('算法图解', 'Aditya Bhargava', '978-7-115-44763-0', '人民邮电出版社', '2017-03-01', 49.00, '用图解方式讲解算法，生动有趣，很容易理解。', '算法与数据结构', 35),
-- 设计模式类
('设计模式', '四人帮', '978-0-201-63361-0', 'Addison-Wesley', '1994-10-31', 85.00, '讲述了 23 种设计模式，是面向对象设计的经典书籍。', '设计模式', 12),
('Head First设计模式', 'Eric Freeman', '978-7-5083-7060-9', '中国电力出版社', '2007-09-01', 79.00, '用轻松诉谐的方式讲解设计模式，非常适合初学者。', '设计模式', 25),
-- 软件工程类
('代码大全', 'Steve McConnell', '978-0-7356-1674-6', 'Microsoft Press', '2004-06-09', 89.99, '关于软件构建最完整的资源。涵盖了项目管理、技术问题和思维法等内容。', '软件工程', 10),
('清晰代码', 'Robert C. Martin', '978-7-115-21687-8', '人民邮电出版社', '2010-01-01', 59.00, '教你写出整洁、可读、可维护的代码，程序员必读。', '软件工程', 28),
('重构：改善既有代码的设计', 'Martin Fowler', '978-7-115-50864-5', '人民邮电出版社', '2019-03-01', 99.00, '重构领域的经典著作，教你如何改善代码质量。', '软件工程', 20),
-- 文学小说类
('百年孤独', '加西亚·马尔克斯', '978-7-5442-5534-8', '南海出版公司', '2011-06-01', 39.50, '魔幻现实主义文学的代表作，述说布恩迪亚家族的百年兴衰。', '文学小说', 40),
('活着', '余华', '978-7-5063-3034-3', '作家出版社', '2012-08-01', 25.00, '讲述了一个人历尽世间沧桑和磨难的故事，感人至深。', '文学小说', 50),
('三体', '刘慈欣', '978-7-229-04238-0', '重庆出版社', '2008-01-01', 23.00, '中国科幻文学的巅峰之作，获雨果奖。', '文学小说', 60),
-- 历史传记类
('万历十五年', '黄仁宇', '978-7-101-05432-1', '中华书局', '2007-01-01', 36.00, '明代社会的深度解读，历史研究的经典之作。', '历史传记', 35),
('明朝那些事儿', '当年明月', '978-7-213-03168-1', '浙江人民出版社', '2006-09-01', 29.80, '用通俗易懂的语言讲述明朝历史，读起来像小说一样有趣。', '历史传记', 45),
-- 经济管理类
('经济学原理', '曼昂', '978-7-301-15094-8', '北京大学出版社', '2015-05-01', 88.00, '经济学入门经典教材，通俗易懂，全球畅销。', '经济管理', 30),
('穷查理', '艾斯特·迪弗洛', '978-7-5086-5082-5', '中信出版社', '2015-04-01', 68.00, '研究贫穷问题的经典著作，获诺贝尔经济学奖。', '经济管理', 25);

-- 创建借阅记录表
CREATE TABLE IF NOT EXISTS `borrow_records` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `book_id` bigint NOT NULL,
  `borrow_date` datetime NOT NULL,
  `due_date` datetime NOT NULL,
  `return_date` datetime DEFAULT NULL,
  `status` varchar(20) DEFAULT 'BORROWED',
  `notes` longtext,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_book_id` (`book_id`),
  KEY `idx_status` (`status`),
  CONSTRAINT `fk_borrow_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_borrow_book` FOREIGN KEY (`book_id`) REFERENCES `books` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 插入丰富的借阅记录数据
INSERT INTO `borrow_records` (`user_id`, `book_id`, `borrow_date`, `due_date`, `return_date`, `status`) VALUES
-- 已还回的记录
(2, 1, DATE_SUB(NOW(), INTERVAL 30 DAY), DATE_SUB(NOW(), INTERVAL 16 DAY), DATE_SUB(NOW(), INTERVAL 18 DAY), 'RETURNED'),
(3, 5, DATE_SUB(NOW(), INTERVAL 28 DAY), DATE_SUB(NOW(), INTERVAL 14 DAY), DATE_SUB(NOW(), INTERVAL 15 DAY), 'RETURNED'),
(4, 8, DATE_SUB(NOW(), INTERVAL 25 DAY), DATE_SUB(NOW(), INTERVAL 11 DAY), DATE_SUB(NOW(), INTERVAL 12 DAY), 'RETURNED'),
(5, 16, DATE_SUB(NOW(), INTERVAL 22 DAY), DATE_SUB(NOW(), INTERVAL 8 DAY), DATE_SUB(NOW(), INTERVAL 10 DAY), 'RETURNED'),
(6, 17, DATE_SUB(NOW(), INTERVAL 20 DAY), DATE_SUB(NOW(), INTERVAL 6 DAY), DATE_SUB(NOW(), INTERVAL 7 DAY), 'RETURNED'),
(7, 18, DATE_SUB(NOW(), INTERVAL 18 DAY), DATE_SUB(NOW(), INTERVAL 4 DAY), DATE_SUB(NOW(), INTERVAL 5 DAY), 'RETURNED'),
(3, 2, DATE_SUB(NOW(), INTERVAL 35 DAY), DATE_SUB(NOW(), INTERVAL 21 DAY), DATE_SUB(NOW(), INTERVAL 22 DAY), 'RETURNED'),
(4, 3, DATE_SUB(NOW(), INTERVAL 32 DAY), DATE_SUB(NOW(), INTERVAL 18 DAY), DATE_SUB(NOW(), INTERVAL 19 DAY), 'RETURNED'),
(5, 9, DATE_SUB(NOW(), INTERVAL 28 DAY), DATE_SUB(NOW(), INTERVAL 14 DAY), DATE_SUB(NOW(), INTERVAL 16 DAY), 'RETURNED'),
(6, 10, DATE_SUB(NOW(), INTERVAL 26 DAY), DATE_SUB(NOW(), INTERVAL 12 DAY), DATE_SUB(NOW(), INTERVAL 14 DAY), 'RETURNED'),
(2, 18, DATE_SUB(NOW(), INTERVAL 40 DAY), DATE_SUB(NOW(), INTERVAL 26 DAY), DATE_SUB(NOW(), INTERVAL 28 DAY), 'RETURNED'),
(3, 18, DATE_SUB(NOW(), INTERVAL 50 DAY), DATE_SUB(NOW(), INTERVAL 36 DAY), DATE_SUB(NOW(), INTERVAL 38 DAY), 'RETURNED'),
(4, 18, DATE_SUB(NOW(), INTERVAL 60 DAY), DATE_SUB(NOW(), INTERVAL 46 DAY), DATE_SUB(NOW(), INTERVAL 48 DAY), 'RETURNED'),
-- 借阅中的记录
(2, 4, DATE_SUB(NOW(), INTERVAL 10 DAY), DATE_ADD(NOW(), INTERVAL 4 DAY), NULL, 'BORROWED'),
(3, 11, DATE_SUB(NOW(), INTERVAL 8 DAY), DATE_ADD(NOW(), INTERVAL 6 DAY), NULL, 'BORROWED'),
(4, 13, DATE_SUB(NOW(), INTERVAL 6 DAY), DATE_ADD(NOW(), INTERVAL 8 DAY), NULL, 'BORROWED'),
(5, 14, DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_ADD(NOW(), INTERVAL 9 DAY), NULL, 'BORROWED'),
(6, 19, DATE_SUB(NOW(), INTERVAL 4 DAY), DATE_ADD(NOW(), INTERVAL 10 DAY), NULL, 'BORROWED'),
(7, 20, DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_ADD(NOW(), INTERVAL 11 DAY), NULL, 'BORROWED'),
(8, 21, DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_ADD(NOW(), INTERVAL 12 DAY), NULL, 'BORROWED'),
(2, 6, DATE_SUB(NOW(), INTERVAL 7 DAY), DATE_ADD(NOW(), INTERVAL 7 DAY), NULL, 'BORROWED'),
(3, 7, DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_ADD(NOW(), INTERVAL 9 DAY), NULL, 'BORROWED'),
-- 逾期的记录
(8, 15, DATE_SUB(NOW(), INTERVAL 20 DAY), DATE_SUB(NOW(), INTERVAL 6 DAY), NULL, 'OVERDUE'),
(7, 12, DATE_SUB(NOW(), INTERVAL 18 DAY), DATE_SUB(NOW(), INTERVAL 4 DAY), NULL, 'OVERDUE');

-- 创建图书分类表
CREATE TABLE IF NOT EXISTS `categories` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL UNIQUE,
  `description` varchar(255),
  `icon` varchar(50),
  `sort_order` int DEFAULT 0,
  `is_active` tinyint(1) DEFAULT 1,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 插入默认分类
INSERT INTO `categories` (`name`, `description`, `icon`, `sort_order`) VALUES
('编程技术', '编程语言与开发技术', '💻', 1),
('计算机科学', '计算机基础理论', '🖥️', 2),
('算法与数据结构', '算法设计与分析', '🧮', 3),
('设计模式', '软件设计模式', '🏗️', 4),
('软件工程', '软件开发方法论', '⚙️', 5),
('文学小说', '文学作品与小说', '📖', 6),
('历史传记', '历史与人物传记', '📜', 7),
('经济管理', '经济与管理学', '💼', 8);

-- 创建社区版块表
CREATE TABLE IF NOT EXISTS `forum_sections` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `description` varchar(255),
  `icon` varchar(50),
  `sort_order` int DEFAULT 0,
  `is_active` tinyint(1) DEFAULT 1,
  `post_count` int DEFAULT 0,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 插入默认版块
INSERT INTO `forum_sections` (`name`, `description`, `icon`, `sort_order`) VALUES
('读书分享', '分享你的读书心得', '📚', 1),
('技术交流', '技术问题讨论', '💻', 2),
('新书推荐', '推荐优秀书籍', '⭐', 3),
('活动讨论', '图书馆活动讨论', '🎉', 4);

-- 创建帖子表
CREATE TABLE IF NOT EXISTS `posts` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(200) NOT NULL,
  `content` text NOT NULL,
  `section_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `view_count` int DEFAULT 0,
  `comment_count` int DEFAULT 0,
  `like_count` int DEFAULT 0,
  `is_top` tinyint(1) DEFAULT 0,
  `is_hot` tinyint(1) DEFAULT 0,
  `status` varchar(20) DEFAULT 'PUBLISHED',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_section_id` (`section_id`),
  KEY `idx_user_id` (`user_id`),
  CONSTRAINT `fk_post_section` FOREIGN KEY (`section_id`) REFERENCES `forum_sections` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_post_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 创建评论表
CREATE TABLE IF NOT EXISTS `comments` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `content` text NOT NULL,
  `post_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `parent_id` bigint,
  `like_count` int DEFAULT 0,
  `status` varchar(20) DEFAULT 'PUBLISHED',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_post_id` (`post_id`),
  KEY `idx_user_id` (`user_id`),
  CONSTRAINT `fk_comment_post` FOREIGN KEY (`post_id`) REFERENCES `posts` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_comment_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 创建帖子点赞表
CREATE TABLE IF NOT EXISTS `post_likes` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `post_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_post_user` (`post_id`, `user_id`),
  KEY `idx_user_id` (`user_id`),
  CONSTRAINT `fk_like_post` FOREIGN KEY (`post_id`) REFERENCES `posts` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_like_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 创建帖子收藏表
CREATE TABLE IF NOT EXISTS `post_favorites` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `post_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_favorite_post_user` (`post_id`, `user_id`),
  KEY `idx_user_id` (`user_id`),
  CONSTRAINT `fk_favorite_post` FOREIGN KEY (`post_id`) REFERENCES `posts` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_favorite_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 插入丰富的示例帖子
INSERT INTO `posts` (`title`, `content`, `section_id`, `user_id`, `view_count`, `like_count`, `comment_count`, `is_hot`) VALUES
('《深入理解Java虚拟机》读后感', '这本书确实是Java开发者必读的经典之作！\n\n主要收获：\n1. 理解了JVM内存结构\n2. 学习了垃圾回收算法\n3. 掌握了性能调优方法\n\n强烈推荐给想深入学习Java的同学！', 1, 1, 256, 42, 8, 1),
('Spring Boot 3.0 新特性讨论', 'Spring Boot 3.0正式发布，带来了许多新特性：\n\n1. 要求Java 17最低版本\n2. 原生GraalVM支持\n3. 改进的观察性\n\n大家在实际项目中使用了吗？有什么心得可以分享？', 2, 2, 512, 68, 15, 1),
('推荐几本算法入门书籍', '刚开始学习算法的同学可以看看这几本：\n\n1. 《大话数据结构》 - 通俗易懂\n2. 《算法图解》 - 图文并茂\n3. 《算法导论》 - 进阶必备\n\n你们觉得还有哪些好书值得推荐？', 3, 1, 189, 35, 12, 1),
('读书会活动回顾', '上周的读书分享会非常成功！\n\n感谢所有参与的同学，大家分享了很多有价值的观点。\n\n下次活动计划讨论《代码大全》，欢迎提前准备！', 4, 2, 98, 22, 6, 0),
('《三体》读后感：中国科幻的巅峰', '终于读完了《三体》三部曲，感触良多！\n\n刘慈欣的想象力太惊人了，从黑暗森林法则到二向箔，每一个概念都让人叹为观止。\n\n有没有读过的朋友一起来讨论？', 1, 3, 345, 89, 23, 1),
('前端开发学习路线分享', '整理了一下前端学习路线：\n\n1. HTML/CSS 基础\n2. JavaScript 核心\n3. Vue/React 框架\n4. 工程化工具\n\n希望对大家有帮助！', 2, 4, 278, 56, 18, 1),
('《活着》读后感', '余华的《活着》真的太戳心了。\n\n福贵的一生让我思考了很久，什么是生活的意义？又是什么支撑我们继续前行？\n\n这本书值得每个人读一读。', 1, 5, 167, 48, 11, 0),
('如何准备技术面试？', '最近在准备校招，整理了一些面试经验：\n\n1. 算法题：LeetCode每日2题\n2. 项目经验：STAR法则\n3. 基础知识：操作系统、网络、数据库\n\n欢迎大家补充！', 2, 6, 423, 92, 28, 1),
('新书推荐：《Effective Java》', '强烈推荐《Effective Java》这本书！\n\n90个经典条目，每一条都是宝贵的编程经验。\n\n特别是关于泄漏、并发、序列化的内容，非常实用！', 3, 7, 234, 67, 14, 0),
('图书馆新书到货通知', '本周新到一批好书：\n\n1. 《重构》第2版\n2. 《穷查理》\n3. 《设计数据密集型应用》\n\n欢迎大家来借阅！', 3, 1, 156, 34, 7, 0);

-- 插入丰富的示例评论
INSERT INTO `comments` (`content`, `post_id`, `user_id`) VALUES
('这本书确实很棒，我也在看！', 1, 2),
('请问哪个章节最难理解？', 1, 3),
('已经升级到Spring Boot 3.0了，確实很香！', 2, 1),
('《算法图解》确实适合入门', 3, 4),
('GC那章我看了三遍才理解', 1, 5),
('推荐看看周志明老师的《深入理解JVM虚拟机》', 1, 6),
('原生编译启动速度真的快很多', 2, 4),
('Java 17的新特性也很棒', 2, 5),
('我觉得《LeetCode Cookbook》也不错', 3, 6),
('可以结合视频教程一起学', 3, 7),
('期待下次活动！', 4, 3),
('下次我也要参加', 4, 4),
('三体真的是神作！', 5, 6),
('我最喜欢黑暗森林法则那段', 5, 7),
('前端学习路线很清晰，谢谢分享', 6, 8),
('Vue3和React选哪个比较好？', 6, 3),
('余华的书都很戳心', 7, 4),
('推荐《许三观卖血记》', 7, 6),
('LeetCode刷多少题比较合适？', 8, 5),
('建议200题以上', 8, 7),
('Effective Java确实是经典', 9, 8),
('新书我已经借了！', 10, 3);

-- 创建活动表
CREATE TABLE IF NOT EXISTS `activities` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(200) NOT NULL,
  `description` text,
  `cover_image` longtext,
  `start_time` datetime NOT NULL,
  `end_time` datetime NOT NULL,
  `location` varchar(200),
  `max_participants` int,
  `current_participants` int DEFAULT 0,
  `registration_deadline` datetime,
  `status` varchar(20) DEFAULT 'UPCOMING',
  `is_hot` tinyint(1) DEFAULT 0,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 插入丰富的示例活动
INSERT INTO `activities` (`title`, `description`, `start_time`, `end_time`, `location`, `max_participants`, `status`, `is_hot`) VALUES
('读书分享会', '每月一次的读书分享活动，欢迎大家参与。本期主题：《算法导论》中的经典算法讨论', DATE_ADD(NOW(), INTERVAL 7 DAY), DATE_ADD(DATE_ADD(NOW(), INTERVAL 7 DAY), INTERVAL 2 HOUR), '图书馆一楼报告厅', 50, 'UPCOMING', 1),
('Java技术沙龙', '深入探讨Spring Boot 3.0新特性，包括GraalVM原生支持、性能优化等话题。欢迎各位Java开发者参加！', DATE_ADD(NOW(), INTERVAL 14 DAY), DATE_ADD(DATE_ADD(NOW(), INTERVAL 14 DAY), INTERVAL 3 HOUR), '图书馆二楼会议室', 30, 'UPCOMING', 1),
('Python编程工作坊', '从零开始学习Python，适合初学者。我们将一起完成一个小项目！', DATE_ADD(NOW(), INTERVAL 10 DAY), DATE_ADD(DATE_ADD(NOW(), INTERVAL 10 DAY), INTERVAL 4 HOUR), '图书馆三楼电子阅览室', 25, 'UPCOMING', 0),
('世界读书日特别活动', '庆祝4月23日世界读书日，多位作家将与读者面对面交流，分享创作心得。', DATE_ADD(NOW(), INTERVAL 21 DAY), DATE_ADD(DATE_ADD(NOW(), INTERVAL 21 DAY), INTERVAL 5 HOUR), '图书馆大报告厅', 200, 'UPCOMING', 1),
('科幻文学讲座', '探索科幻文学的魅力，从《三体》到《流浪地球》，感受中国科幻的崛起。', DATE_ADD(NOW(), INTERVAL 28 DAY), DATE_ADD(DATE_ADD(NOW(), INTERVAL 28 DAY), INTERVAL 2 HOUR), '图书馆二楼报告厅', 80, 'UPCOMING', 0),
('技术面试技巧分享', '邀请大厂面试官分享技术面试经验，包括算法、系统设计、行为面试等内容。', DATE_ADD(NOW(), INTERVAL 35 DAY), DATE_ADD(DATE_ADD(NOW(), INTERVAL 35 DAY), INTERVAL 3 HOUR), '图书馆一楼会议室', 40, 'UPCOMING', 1);

-- 创建活动报名表
CREATE TABLE IF NOT EXISTS `activity_registrations` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `activity_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `registration_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `check_in_time` datetime,
  `status` varchar(20) DEFAULT 'REGISTERED',
  `is_collected` tinyint(1) DEFAULT 0,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_activity_id` (`activity_id`),
  KEY `idx_user_id` (`user_id`),
  CONSTRAINT `fk_reg_activity` FOREIGN KEY (`activity_id`) REFERENCES `activities` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_reg_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 插入丰富的示例活动报名
INSERT INTO `activity_registrations` (`activity_id`, `user_id`, `registration_time`, `status`) VALUES
(1, 1, DATE_SUB(NOW(), INTERVAL 5 DAY), 'REGISTERED'),
(1, 2, DATE_SUB(NOW(), INTERVAL 4 DAY), 'REGISTERED'),
(1, 3, DATE_SUB(NOW(), INTERVAL 3 DAY), 'REGISTERED'),
(1, 4, DATE_SUB(NOW(), INTERVAL 2 DAY), 'REGISTERED'),
(1, 5, DATE_SUB(NOW(), INTERVAL 1 DAY), 'REGISTERED'),
(2, 1, DATE_SUB(NOW(), INTERVAL 3 DAY), 'REGISTERED'),
(2, 3, DATE_SUB(NOW(), INTERVAL 2 DAY), 'REGISTERED'),
(2, 6, DATE_SUB(NOW(), INTERVAL 1 DAY), 'REGISTERED'),
(3, 2, DATE_SUB(NOW(), INTERVAL 2 DAY), 'REGISTERED'),
(3, 4, DATE_SUB(NOW(), INTERVAL 1 DAY), 'REGISTERED'),
(4, 1, DATE_SUB(NOW(), INTERVAL 1 DAY), 'REGISTERED'),
(4, 2, DATE_SUB(NOW(), INTERVAL 1 DAY), 'REGISTERED'),
(4, 5, DATE_SUB(NOW(), INTERVAL 1 DAY), 'REGISTERED'),
(4, 7, DATE_SUB(NOW(), INTERVAL 1 DAY), 'REGISTERED'),
(5, 3, DATE_SUB(NOW(), INTERVAL 1 DAY), 'REGISTERED'),
(6, 4, DATE_SUB(NOW(), INTERVAL 1 DAY), 'REGISTERED'),
(6, 6, DATE_SUB(NOW(), INTERVAL 1 DAY), 'REGISTERED');

-- 更新活动的当前参与人数
UPDATE `activities` SET `current_participants` = 5 WHERE `id` = 1;
UPDATE `activities` SET `current_participants` = 3 WHERE `id` = 2;
UPDATE `activities` SET `current_participants` = 2 WHERE `id` = 3;
UPDATE `activities` SET `current_participants` = 4 WHERE `id` = 4;
UPDATE `activities` SET `current_participants` = 1 WHERE `id` = 5;
UPDATE `activities` SET `current_participants` = 2 WHERE `id` = 6;

-- 创建阅览室表
CREATE TABLE IF NOT EXISTS `reading_rooms` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `location` varchar(200),
  `description` varchar(255),
  `total_seats` int DEFAULT 0,
  `available_seats` int DEFAULT 0,
  `open_time` varchar(10) DEFAULT '08:00',
  `close_time` varchar(10) DEFAULT '22:00',
  `is_active` tinyint(1) DEFAULT 1,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 插入示例阅览室
INSERT INTO `reading_rooms` (`name`, `location`, `description`, `total_seats`, `available_seats`, `open_time`, `close_time`) VALUES
('综合阅览室', '图书馆一楼东区', '综合类图书阅览室', 100, 100, '08:00', '22:00'),
('自习室A', '图书馆二楼', '安静自习区域', 80, 80, '07:00', '23:00'),
('电子阅览室', '图书馆三楼', '电子资源阅览区', 50, 50, '08:00', '21:00');

-- 创建座位表
CREATE TABLE IF NOT EXISTS `seats` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `room_id` bigint NOT NULL,
  `seat_number` varchar(20) NOT NULL,
  `row_number` int,
  `column_number` int,
  `has_power` tinyint(1) DEFAULT 0,
  `near_window` tinyint(1) DEFAULT 0,
  `status` varchar(20) DEFAULT 'AVAILABLE',
  `is_active` tinyint(1) DEFAULT 1,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_room_id` (`room_id`),
  CONSTRAINT `fk_seat_room` FOREIGN KEY (`room_id`) REFERENCES `reading_rooms` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 创建座位预约表
CREATE TABLE IF NOT EXISTS `seat_reservations` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `seat_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `reservation_date` date NOT NULL,
  `start_time` varchar(10),
  `end_time` varchar(10),
  `check_in_time` datetime,
  `check_out_time` datetime,
  `status` varchar(20) DEFAULT 'RESERVED',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_seat_id` (`seat_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_reservation_date` (`reservation_date`),
  CONSTRAINT `fk_reservation_seat` FOREIGN KEY (`seat_id`) REFERENCES `seats` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_reservation_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
