-- =============================================
-- 社区功能更新SQL脚本
-- 用于在已有数据库上添加帖子点赞和收藏功能
-- =============================================

USE `book_management`;
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;

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

-- 插入示例帖子数据（使用存在的用户ID）
INSERT INTO `posts` (`title`, `content`, `section_id`, `user_id`, `view_count`, `like_count`, `comment_count`, `is_hot`)
SELECT * FROM (
  SELECT '《深入理解Java虚拟机》读后感' as title, 
         '这本书确实是Java开发者必读的经典之作！\n\n主要收获：\n1. 理解了JVM内存结构\n2. 学习了垃圾回收算法\n3. 掌握了性能调优方法\n\n强烈推荐给想深入学习Java的同学！' as content,
         1 as section_id, 9 as user_id, 128 as view_count, 25 as like_count, 5 as comment_count, 1 as is_hot
) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM `posts` WHERE title = '《深入理解Java虚拟机》读后感');

INSERT INTO `posts` (`title`, `content`, `section_id`, `user_id`, `view_count`, `like_count`, `comment_count`, `is_hot`)
SELECT * FROM (
  SELECT 'Spring Boot 3.0 新特性讨论' as title,
         'Spring Boot 3.0正式发布，带来了许多新特性：\n\n1. 要求Java 17最低版本\n2. 原生GraalVM支持\n3. 改进的观察性\n\n大家在实际项目中使用了吗？有什么心得可以分享？' as content,
         2 as section_id, 8 as user_id, 256 as view_count, 42 as like_count, 12 as comment_count, 1 as is_hot
) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM `posts` WHERE title = 'Spring Boot 3.0 新特性讨论');

INSERT INTO `posts` (`title`, `content`, `section_id`, `user_id`, `view_count`, `like_count`, `comment_count`, `is_hot`)
SELECT * FROM (
  SELECT '推荐几本算法入门书籍' as title,
         '刚开始学习算法的同学可以看看这几本：\n\n1. 《大话数据结构》 - 通俗易懂\n2. 《算法图解》 - 图文并茂\n3. 《算法导论》 - 进阶必备\n\n你们觉得还有哪些好书值得推荐？' as content,
         3 as section_id, 7 as user_id, 89 as view_count, 18 as like_count, 8 as comment_count, 0 as is_hot
) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM `posts` WHERE title = '推荐几本算法入门书籍');

INSERT INTO `posts` (`title`, `content`, `section_id`, `user_id`, `view_count`, `like_count`, `comment_count`, `is_hot`)
SELECT * FROM (
  SELECT '读书会活动回顾' as title,
         '上周的读书分享会非常成功！\n\n感谢所有参与的同学，大家分享了很多有价值的观点。\n\n下次活动计划讨论《代码大全》，欢迎提前准备！' as content,
         4 as section_id, 8 as user_id, 67 as view_count, 15 as like_count, 3 as comment_count, 0 as is_hot
) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM `posts` WHERE title = '读书会活动回顾');

INSERT INTO `posts` (`title`, `content`, `section_id`, `user_id`, `view_count`, `like_count`, `comment_count`, `is_hot`)
SELECT * FROM (
  SELECT '如何高效阅读技术书籍？' as title,
         '分享一下我阅读技术书籍的心得：\n\n1. 先看目录，了解整体结构\n2. 快速浏览，标记重点章节\n3. 精读重点，做好笔记\n4. 实践验证，加深理解\n5. 定期回顾，巩固记忆\n\n大家有什么好的阅读方法吗？' as content,
         1 as section_id, 9 as user_id, 156 as view_count, 32 as like_count, 15 as comment_count, 1 as is_hot
) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM `posts` WHERE title = '如何高效阅读技术书籍？');

INSERT INTO `posts` (`title`, `content`, `section_id`, `user_id`, `view_count`, `like_count`, `comment_count`, `is_hot`)
SELECT * FROM (
  SELECT 'MySQL性能优化经验分享' as title,
         '最近在做数据库优化，总结了一些经验：\n\n1. 合理使用索引\n2. 避免SELECT *\n3. 使用EXPLAIN分析慢查询\n4. 适当使用缓存\n5. 分库分表策略\n\n欢迎大家补充！' as content,
         2 as section_id, 7 as user_id, 198 as view_count, 45 as like_count, 20 as comment_count, 1 as is_hot
) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM `posts` WHERE title = 'MySQL性能优化经验分享');

-- 插入示例评论数据
INSERT INTO `comments` (`content`, `post_id`, `user_id`)
SELECT '这本书确实很棒，我也在看！', p.id, 8
FROM `posts` p WHERE p.title = '《深入理解Java虚拟机》读后感'
AND NOT EXISTS (SELECT 1 FROM `comments` c WHERE c.post_id = p.id AND c.content = '这本书确实很棒，我也在看！');

INSERT INTO `comments` (`content`, `post_id`, `user_id`)
SELECT '请问哪个章节最难理解？', p.id, 7
FROM `posts` p WHERE p.title = '《深入理解Java虚拟机》读后感'
AND NOT EXISTS (SELECT 1 FROM `comments` c WHERE c.post_id = p.id AND c.content = '请问哪个章节最难理解？');

INSERT INTO `comments` (`content`, `post_id`, `user_id`)
SELECT '已经升级到Spring Boot 3.0了，确实很香！', p.id, 9
FROM `posts` p WHERE p.title = 'Spring Boot 3.0 新特性讨论'
AND NOT EXISTS (SELECT 1 FROM `comments` c WHERE c.post_id = p.id AND c.content LIKE '%Spring Boot 3.0%');

INSERT INTO `comments` (`content`, `post_id`, `user_id`)
SELECT '《算法图解》确实适合入门', p.id, 8
FROM `posts` p WHERE p.title = '推荐几本算法入门书籍'
AND NOT EXISTS (SELECT 1 FROM `comments` c WHERE c.post_id = p.id AND c.content = '《算法图解》确实适合入门');

INSERT INTO `comments` (`content`, `post_id`, `user_id`)
SELECT '非常实用的经验，感谢分享！', p.id, 9
FROM `posts` p WHERE p.title = 'MySQL性能优化经验分享'
AND NOT EXISTS (SELECT 1 FROM `comments` c WHERE c.post_id = p.id AND c.content = '非常实用的经验，感谢分享！');

INSERT INTO `comments` (`content`, `post_id`, `user_id`)
SELECT '做笔记真的很重要，我之前总是忘记', p.id, 8
FROM `posts` p WHERE p.title = '如何高效阅读技术书籍？'
AND NOT EXISTS (SELECT 1 FROM `comments` c WHERE c.post_id = p.id AND c.content LIKE '%做笔记%');

SELECT '数据库更新完成！' AS message;
