SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for users
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nickname` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL,
  `role` varchar(20) NOT NULL,
  `status` varchar(20) NOT NULL DEFAULT 'active',
  `full_name` varchar(100) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `phonenumber` varchar(20) DEFAULT NULL,
  `avatar_path` varchar(255) DEFAULT NULL,
  `lv` int DEFAULT 1,
  `exp` int DEFAULT 0,
  `create_time` datetime(6) DEFAULT CURRENT_TIMESTAMP(6),
  `avatar_status` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UQ_users_email` (`email`),
  UNIQUE KEY `UQ_users_nickname` (`nickname`),
  INDEX `IX_users_role` (`role`),
  INDEX `IX_users_status` (`status`),
  CONSTRAINT `CK_users_role` CHECK (role in ('admin','guide','tourist')),
  CONSTRAINT `CK_users_status` CHECK (status in ('pending','frozen','active')),
  CONSTRAINT `CK_users_avatar_status` CHECK (avatar_status IS NULL OR avatar_status in ('rejected','approved','pending'))
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4;

INSERT INTO `users` VALUES (1, 'admin', '$2a$10$.aadtiBAesQ0cyHDQoRsCeR5aN44pUXR7CtjrN6UIl7MwShtovksW', 'admin', 'active', '系统管理员', 'admin@guidematch.com', '13800000000', '/img/avatar/1766341340927_870.png', 1, 0, '2025-12-20 17:16:39.076666', 'approved');
INSERT INTO `users` VALUES (2, 'test', '$2a$10$fIQY/fAt2/6DAuhBxJgYeeU3YrgsO5aqjgPwLu9IEIaXQMwZtDKqu', 'tourist', 'active', 'testuser', 'test@123.com', '13111111111', '/img/avatar/1766321735568_815.png', 1, 60, '2025-12-20 19:31:33.070000', 'approved');
INSERT INTO `users` VALUES (3, 'test1', '$2a$10$fzp6klKAaZNqn6m2EKWxne8iwf7XZGrnV9xzDCnwiQWNBQciZDkXy', 'tourist', 'active', 'testuser1', 'test@133.com', '13111111111', NULL, 1, 0, '2025-12-20 20:01:46.643333', NULL);
INSERT INTO `users` VALUES (4, 'user2', '$2a$10$zfZekK8kzFjRll32HL.Tr.0EB7I4joojiVodBXyqDKXK/x1RaW7gC', 'tourist', 'active', 'testuser2', 'test@122.com', '1311111111', NULL, 1, 0, '2025-12-20 20:03:24.176666', NULL);
INSERT INTO `users` VALUES (5, 'guide1', '$2a$10$.aadtiBAesQ0cyHDQoRsCeR5aN44pUXR7CtjrN6UIl7MwShtovksW', 'guide', 'active', 'guide1', 'guide@123.com', '13111111111', '/img/avatar/1766337591457_927.png', 1, 60, '2025-12-20 20:05:06.390000', 'approved');
INSERT INTO `users` VALUES (6, '张三', '$2a$10$T1IwSnm.4B5m9U8b92aWmuQayGvnZ49/zsjTMyuoXcC9yRBJIO3Mi', 'tourist', 'active', '张三', 'zhangsan@123.com', '13111111111', '/img/avatar/1766600512014_679.png', 1, 40, '2025-12-24 17:13:46.043333', 'approved');
INSERT INTO `users` VALUES (7, 'lisi', '$2a$10$tQJ2U7/R9xMhAA2R2nw4wux2/oyEFYVfnbDiaiKS8Pwzfz9s6pOUO', 'tourist', 'active', '里斯', 'lisi@123.com', '123456', NULL, 1, 0, '2025-12-24 17:14:14.190000', NULL);
INSERT INTO `users` VALUES (8, 'wangwu', '$2a$10$UqUpTEkM7cl.r2U/wjNLWeciGXDFbxeS.C4FSEFbYZPf97kzLD1Rq', 'guide', 'active', 'wangwu', 'wangwu@123.com', '123456', NULL, 1, 0, '2025-12-24 17:14:42.436666', NULL);

-- ----------------------------
-- Table structure for favorites
-- ----------------------------
DROP TABLE IF EXISTS `favorites`;
CREATE TABLE `favorites` (
  `id` int NOT NULL AUTO_INCREMENT,
  `tourist_id` int NOT NULL,
  `guide_id` int NOT NULL,
  `create_time` datetime(6) DEFAULT CURRENT_TIMESTAMP(6),
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1002 DEFAULT CHARSET=utf8mb4;

INSERT INTO `favorites` VALUES (2, 2, 4, '2025-12-21 18:59:43.750000');
INSERT INTO `favorites` VALUES (3, 6, 5, '2025-12-24 18:18:38.623333');
INSERT INTO `favorites` VALUES (1002, 7, 5, '2025-12-25 16:30:44.663333');

-- ----------------------------
-- Table structure for guide_calendar
-- ----------------------------
DROP TABLE IF EXISTS `guide_calendar`;
CREATE TABLE `guide_calendar` (
  `id` int NOT NULL AUTO_INCREMENT,
  `guide_id` int NOT NULL,
  `date` date NOT NULL,
  `status` varchar(20) NOT NULL,
  `create_time` datetime(6) DEFAULT CURRENT_TIMESTAMP(6),
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1000 DEFAULT CHARSET=utf8mb4;

INSERT INTO `guide_calendar` VALUES (1, 5, '2025-12-23', 'booked', '2025-12-22 02:55:40.846000');
INSERT INTO `guide_calendar` VALUES (2, 5, '2025-12-24', 'booked', '2025-12-22 02:55:53.746000');
INSERT INTO `guide_calendar` VALUES (3, 5, '2025-12-25', 'booked', '2025-12-22 02:55:55.281000');
INSERT INTO `guide_calendar` VALUES (4, 5, '2025-12-26', 'booked', '2025-12-22 02:55:56.592000');
INSERT INTO `guide_calendar` VALUES (5, 5, '2025-12-27', 'booked', '2025-12-22 02:55:57.827000');
INSERT INTO `guide_calendar` VALUES (6, 5, '2025-12-28', 'booked', '2025-12-22 02:55:59.016000');
INSERT INTO `guide_calendar` VALUES (7, 5, '2025-12-29', 'booked', '2025-12-22 02:56:00.314000');
INSERT INTO `guide_calendar` VALUES (8, 5, '2025-12-30', 'booked', '2025-12-22 02:56:01.398000');
INSERT INTO `guide_calendar` VALUES (9, 5, '2025-12-31', 'booked', '2025-12-22 02:56:02.705000');
INSERT INTO `guide_calendar` VALUES (10, 5, '2026-01-01', 'booked', '2025-12-22 02:56:11.636000');
INSERT INTO `guide_calendar` VALUES (11, 5, '2026-01-25', 'available', '2025-12-22 02:56:28.576000');
INSERT INTO `guide_calendar` VALUES (12, 5, '2026-01-27', 'booked', '2025-12-22 03:08:25.517000');
INSERT INTO `guide_calendar` VALUES (13, 5, '2026-01-02', 'unavailable', '2025-12-25 02:24:46.348000');
INSERT INTO `guide_calendar` VALUES (14, 5, '2026-01-03', 'unavailable', '2025-12-25 02:24:46.380000');
INSERT INTO `guide_calendar` VALUES (15, 5, '2026-01-04', 'unavailable', '2025-12-25 02:24:46.397000');
INSERT INTO `guide_calendar` VALUES (16, 5, '2026-01-24', 'available', '2025-12-25 02:24:54.650000');
INSERT INTO `guide_calendar` VALUES (17, 5, '2026-01-23', 'available', '2025-12-25 02:24:54.679000');

-- ----------------------------
-- Table structure for guides
-- ----------------------------
DROP TABLE IF EXISTS `guides`;
CREATE TABLE `guides` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `languages` varchar(500) DEFAULT NULL,
  `cities` varchar(500) DEFAULT NULL,
  `tags` varchar(500) DEFAULT NULL,
  `bio` varchar(1000) DEFAULT NULL,
  `hourly_rate` decimal(10,2) DEFAULT '0.00',
  `daily_rate` decimal(10,2) DEFAULT '0.00',
  `certificates` varchar(1000) DEFAULT NULL,
  `verification_status` varchar(20) DEFAULT 'pending',
  `rejection_reason` varchar(500) DEFAULT NULL,
  `rating` decimal(3,2) DEFAULT '0.00',
  `total_reviews` int DEFAULT '0',
  `total_orders` int DEFAULT '0',
  `total_income` decimal(12,2) DEFAULT '0.00',
  `create_time` datetime(6) DEFAULT CURRENT_TIMESTAMP(6),
  `update_time` datetime(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1000 DEFAULT CHARSET=utf8mb4;

INSERT INTO `guides` VALUES (1, 2, '["中文", "英文"]', '["北京", "上海", "深圳"]', '["计算机", "留学", "CS"]', '我是Alice，拥有5年计算机科学教学经验，专注于帮助学生在CS领域取得成功。', 200.00, 1500.00, '[]', 'approved', NULL, 4.80, 15, 8, 12000.00, '2025-12-21 16:52:02.293333', '2025-12-21 16:52:02.293333');
INSERT INTO `guides` VALUES (2, 3, '["中文", "英文"]', '["北京", "杭州"]', '["数学", "考研", "经验"]', '我是Bob，数学专业博士，擅长考研数学辅导，已帮助100+学生成功上岸。', 150.00, 1200.00, '[]', 'approved', NULL, 4.90, 23, 12, 14400.00, '2025-12-21 16:52:02.323333', '2025-12-21 16:52:02.323333');
INSERT INTO `guides` VALUES (3, 4, '["中文", "日文", "韩文"]', '["北京", "上海", "广州", "深圳"]', '["旅游", "美食", "文化"]', '我是Charlie，资深旅游向导，熟悉各地文化美食，带您深度体验城市魅力。', 100.00, 800.00, '[]', 'approved', NULL, 4.70, 18, 10, 8000.00, '2025-12-21 16:52:02.326666', '2025-12-21 16:52:02.326666');
INSERT INTO `guides` VALUES (4, 5, '["中文","英文"]', '["杭州","上海"]', '["经验","留学"]', '我是Diana，正在申请成为向导，期待为您提供优质服务。', 180.00, 1300.00, '["导游合格证书"]', 'approved', NULL, 5.00, 2, 2, 70200.00, '2025-12-21 16:52:02.326666', '2025-12-22 02:31:00.694000');
INSERT INTO `guides` VALUES (5, 6, '["中文"]', '["北京"]', '["经验"]', '我是Eve，申请成为向导。', 100.00, 800.00, '[]', 'rejected', '?????,???????', 0.00, 0, 0, 0.00, '2025-12-21 16:52:02.330000', '2025-12-21 16:52:02.330000');

-- ----------------------------
-- Table structure for orders
-- ----------------------------
DROP TABLE IF EXISTS `orders`;
CREATE TABLE `orders` (
  `id` int NOT NULL AUTO_INCREMENT,
  `tourist_id` int NOT NULL,
  `guide_id` int NOT NULL,
  `order_type` varchar(20) NOT NULL,
  `route_name` varchar(200) DEFAULT NULL,
  `route_id` int DEFAULT NULL,
  `start_date` date NOT NULL,
  `end_date` date NOT NULL,
  `participants` int DEFAULT '1',
  `special_requirements` varchar(1000) DEFAULT NULL,
  `total_price` decimal(10,2) NOT NULL,
  `commission_rate` decimal(5,2) DEFAULT '0.10',
  `commission_amount` decimal(10,2) DEFAULT '0.00',
  `guide_income` decimal(10,2) DEFAULT '0.00',
  `status` varchar(20) NOT NULL DEFAULT 'pending',
  `cancel_reason` varchar(500) DEFAULT NULL,
  `reject_reason` varchar(500) DEFAULT NULL,
  `create_time` datetime(6) DEFAULT CURRENT_TIMESTAMP(6),
  `update_time` datetime(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
  PRIMARY KEY (`id`),
  INDEX `IX_orders_tourist_id` (`tourist_id`),
  INDEX `IX_orders_guide_id` (`guide_id`),
  INDEX `IX_orders_status` (`status`),
  INDEX `IX_orders_dates` (`start_date`, `end_date`),
  CONSTRAINT `CK_orders_type` CHECK (order_type in ('custom','fixed')),
  CONSTRAINT `CK_orders_status` CHECK (status in ('cancelled','completed','in_progress','confirmed','pending')),
  CONSTRAINT `FK_orders_tourist` FOREIGN KEY (`tourist_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FK_orders_guide` FOREIGN KEY (`guide_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1002 DEFAULT CHARSET=utf8mb4;

INSERT INTO `orders` VALUES (1, 2, 5, 'custom', NULL, NULL, '2025-12-22', '2026-01-22', 1, '', 41600.00, 0.10, 4160.00, 37440.00, 'completed', NULL, NULL, '2025-12-22 02:36:24.640000', '2025-12-22 02:57:47.986000');
INSERT INTO `orders` VALUES (2, 6, 5, 'custom', NULL, NULL, '2026-01-28', '2026-01-30', 1, '', 3900.00, 0.10, 390.00, 3510.00, 'cancelled', NULL, '当天已排满', '2025-12-25 01:20:52.189000', '2025-12-25 01:21:18.191000');
INSERT INTO `orders` VALUES (3, 6, 5, 'custom', NULL, NULL, '2026-02-01', '2026-02-28', 1, '', 36400.00, 0.10, 3640.00, 32760.00, 'completed', NULL, NULL, '2025-12-25 02:25:58.184000', '2025-12-25 02:27:16.430000');

-- ----------------------------
-- Table structure for posts
-- ----------------------------
DROP TABLE IF EXISTS `posts`;
CREATE TABLE `posts` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `user_role` varchar(20) NOT NULL,
  `title` varchar(200) NOT NULL,
  `content` longtext NOT NULL,
  `images` varchar(2000) DEFAULT NULL,
  `tags` varchar(500) DEFAULT NULL,
  `likes_count` int DEFAULT '0',
  `views_count` int DEFAULT '0',
  `status` varchar(20) DEFAULT 'pending',
  `rejection_reason` varchar(500) DEFAULT NULL,
  `exp_awarded` int DEFAULT '0',
  `create_time` datetime(6) DEFAULT CURRENT_TIMESTAMP(6),
  `update_time` datetime(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
  PRIMARY KEY (`id`),
  INDEX `IX_posts_user_id` (`user_id`),
  INDEX `IX_posts_status` (`status`),
  INDEX `IX_posts_create_time` (`create_time` DESC),
  CONSTRAINT `CK_posts_status` CHECK (status in ('deleted','rejected','approved','pending')),
  CONSTRAINT `FK_posts_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1001 DEFAULT CHARSET=utf8mb4;

INSERT INTO `posts` VALUES (1, 2, 'tourist', '1', '1', NULL, NULL, 0, 0, 'rejected', '内容太简单', 0, '2025-12-21 04:27:01.465000', '2025-12-21 04:32:17.317000');
INSERT INTO `posts` VALUES (2, 2, 'tourist', '这是一个测试贴', '今天天气很好，我希望我得测试顺利通过，这样我就可以早点出去玩', NULL, NULL, 4, 0, 'approved', NULL, 20, '2025-12-21 04:33:43.473000', '2025-12-21 04:34:06.422000');

-- ----------------------------
-- Table structure for post_likes
-- ----------------------------
DROP TABLE IF EXISTS `post_likes`;
CREATE TABLE `post_likes` (
  `id` int NOT NULL AUTO_INCREMENT,
  `post_id` int NOT NULL,
  `user_id` int NOT NULL,
  `create_time` datetime(6) DEFAULT CURRENT_TIMESTAMP(6),
  PRIMARY KEY (`id`),
  UNIQUE KEY `UQ_post_likes_post_user` (`post_id`,`user_id`),
  CONSTRAINT `FK_post_likes_post` FOREIGN KEY (`post_id`) REFERENCES `posts` (`id`) ON DELETE CASCADE,
  CONSTRAINT `FK_post_likes_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4;

INSERT INTO `post_likes` VALUES (1, 2, 2, '2025-12-22 03:28:12.209000');

-- ----------------------------
-- Table structure for reviews
-- ----------------------------
DROP TABLE IF EXISTS `reviews`;
CREATE TABLE `reviews` (
  `id` int NOT NULL AUTO_INCREMENT,
  `order_id` int NOT NULL,
  `tourist_id` int NOT NULL,
  `guide_id` int NOT NULL,
  `rating` int NOT NULL,
  `comment` varchar(1000) DEFAULT NULL,
  `create_time` datetime(6) DEFAULT CURRENT_TIMESTAMP(6),
  PRIMARY KEY (`id`),
  UNIQUE KEY `UQ_reviews_order_id` (`order_id`),
  INDEX `IX_reviews_guide_id` (`guide_id`),
  INDEX `IX_reviews_tourist_id` (`tourist_id`),
  CONSTRAINT `CK_reviews_rating` CHECK (rating >= 1 AND rating <= 5),
  CONSTRAINT `FK_reviews_order` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`),
  CONSTRAINT `FK_reviews_tourist` FOREIGN KEY (`tourist_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FK_reviews_guide` FOREIGN KEY (`guide_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1001 DEFAULT CHARSET=utf8mb4;

INSERT INTO `reviews` VALUES (1, 1, 2, 5, 5, '好评，非常满意', '2025-12-22 02:58:56.424000');
INSERT INTO `reviews` VALUES (2, 3, 6, 5, 5, '很nice', '2025-12-25 02:27:42.296000');

-- ----------------------------
-- Table structure for notifications
-- ----------------------------
DROP TABLE IF EXISTS `notifications`;
CREATE TABLE `notifications` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `type` varchar(20) NOT NULL,
  `title` varchar(100) NOT NULL,
  `content` varchar(500) NOT NULL,
  `related_id` int DEFAULT NULL,
  `read` tinyint(1) NOT NULL DEFAULT '0',
  `create_time` datetime(6) DEFAULT CURRENT_TIMESTAMP(6),
  `update_time` datetime(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

INSERT INTO `notifications` VALUES (1, 6, 'system', '头像审核通过', '恭喜！您的头像已通过审核，现已正常显示。', NULL, 1, '2025-12-25 02:22:16.343000', '2025-12-25 02:22:37.346000');

-- ----------------------------
-- Table structure for password_reset_codes
-- ----------------------------
DROP TABLE IF EXISTS `password_reset_codes`;
CREATE TABLE `password_reset_codes` (
  `id` int NOT NULL AUTO_INCREMENT,
  `email` varchar(255) NOT NULL,
  `code` varchar(10) NOT NULL,
  `expire_time` datetime NOT NULL,
  `used` tinyint(1) DEFAULT '0',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  INDEX `IX_password_reset_codes_email` (`email`),
  INDEX `IX_password_reset_codes_code` (`code`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for withdrawals
-- ----------------------------
DROP TABLE IF EXISTS `withdrawals`;
CREATE TABLE `withdrawals` (
  `id` int NOT NULL AUTO_INCREMENT,
  `guide_id` int NOT NULL,
  `amount` decimal(10,2) NOT NULL,
  `bank_name` varchar(100) DEFAULT NULL,
  `bank_account` varchar(50) DEFAULT NULL,
  `account_holder` varchar(100) DEFAULT NULL,
  `status` varchar(20) DEFAULT 'pending',
  `rejection_reason` varchar(500) DEFAULT NULL,
  `process_time` datetime(6) DEFAULT NULL,
  `create_time` datetime(6) DEFAULT CURRENT_TIMESTAMP(6),
  PRIMARY KEY (`id`),
  INDEX `IX_withdrawals_guide_id` (`guide_id`),
  INDEX `IX_withdrawals_status` (`status`),
  CONSTRAINT `CK_withdrawals_status` CHECK (status in ('completed','rejected','approved','pending')),
  CONSTRAINT `FK_withdrawals_guide` FOREIGN KEY (`guide_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

INSERT INTO `withdrawals` VALUES (1, 5, 30000.00, '中国银行', '131111111111111111111', '1111', 'completed', NULL, '2025-12-22 03:09:29.145000', '2025-12-22 03:09:29.145000');

SET FOREIGN_KEY_CHECKS = 1;