-- ----------------------------
-- Table structure for favorites
-- ----------------------------
IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID(N'[dbo].[favorites]') AND type IN ('U'))
	DROP TABLE [dbo].[favorites]
GO

CREATE TABLE [dbo].[favorites] (
  [id] int  IDENTITY(1,1) NOT NULL,
  [tourist_id] int  NOT NULL,
  [guide_id] int  NOT NULL,
  [create_time] datetime2(7) DEFAULT getdate() NULL
)
GO

ALTER TABLE [dbo].[favorites] SET (LOCK_ESCALATION = TABLE)
GO


-- ----------------------------
-- Records of favorites
-- ----------------------------
BEGIN TRANSACTION
GO

SET IDENTITY_INSERT [dbo].[favorites] ON
GO

INSERT INTO [dbo].[favorites] ([id], [tourist_id], [guide_id], [create_time]) VALUES (N'2', N'2', N'4', N'2025-12-21 18:59:43.7500000')
GO

INSERT INTO [dbo].[favorites] ([id], [tourist_id], [guide_id], [create_time]) VALUES (N'3', N'6', N'5', N'2025-12-24 18:18:38.6233333')
GO

INSERT INTO [dbo].[favorites] ([id], [tourist_id], [guide_id], [create_time]) VALUES (N'1002', N'7', N'5', N'2025-12-25 16:30:44.6633333')
GO

SET IDENTITY_INSERT [dbo].[favorites] OFF
GO

COMMIT
GO


-- ----------------------------
-- Table structure for guide_calendar
-- ----------------------------
IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID(N'[dbo].[guide_calendar]') AND type IN ('U'))
	DROP TABLE [dbo].[guide_calendar]
GO

CREATE TABLE [dbo].[guide_calendar] (
  [id] int  IDENTITY(1,1) NOT NULL,
  [guide_id] int  NOT NULL,
  [date] date  NOT NULL,
  [status] nvarchar(20) COLLATE SQL_Latin1_General_CP1_CI_AS  NOT NULL,
  [create_time] datetime2(7) DEFAULT getdate() NULL
)
GO

ALTER TABLE [dbo].[guide_calendar] SET (LOCK_ESCALATION = TABLE)
GO


-- ----------------------------
-- Records of guide_calendar
-- ----------------------------
BEGIN TRANSACTION
GO

SET IDENTITY_INSERT [dbo].[guide_calendar] ON
GO

INSERT INTO [dbo].[guide_calendar] ([id], [guide_id], [date], [status], [create_time]) VALUES (N'1', N'5', N'2025-12-23', N'booked', N'2025-12-22 02:55:40.8460000')
GO

INSERT INTO [dbo].[guide_calendar] ([id], [guide_id], [date], [status], [create_time]) VALUES (N'2', N'5', N'2025-12-24', N'booked', N'2025-12-22 02:55:53.7460000')
GO

INSERT INTO [dbo].[guide_calendar] ([id], [guide_id], [date], [status], [create_time]) VALUES (N'3', N'5', N'2025-12-25', N'booked', N'2025-12-22 02:55:55.2810000')
GO

INSERT INTO [dbo].[guide_calendar] ([id], [guide_id], [date], [status], [create_time]) VALUES (N'4', N'5', N'2025-12-26', N'booked', N'2025-12-22 02:55:56.5920000')
GO

INSERT INTO [dbo].[guide_calendar] ([id], [guide_id], [date], [status], [create_time]) VALUES (N'5', N'5', N'2025-12-27', N'booked', N'2025-12-22 02:55:57.8270000')
GO

INSERT INTO [dbo].[guide_calendar] ([id], [guide_id], [date], [status], [create_time]) VALUES (N'6', N'5', N'2025-12-28', N'booked', N'2025-12-22 02:55:59.0160000')
GO

INSERT INTO [dbo].[guide_calendar] ([id], [guide_id], [date], [status], [create_time]) VALUES (N'7', N'5', N'2025-12-29', N'booked', N'2025-12-22 02:56:00.3140000')
GO

INSERT INTO [dbo].[guide_calendar] ([id], [guide_id], [date], [status], [create_time]) VALUES (N'8', N'5', N'2025-12-30', N'booked', N'2025-12-22 02:56:01.3980000')
GO

INSERT INTO [dbo].[guide_calendar] ([id], [guide_id], [date], [status], [create_time]) VALUES (N'9', N'5', N'2025-12-31', N'booked', N'2025-12-22 02:56:02.7050000')
GO

INSERT INTO [dbo].[guide_calendar] ([id], [guide_id], [date], [status], [create_time]) VALUES (N'10', N'5', N'2026-01-01', N'booked', N'2025-12-22 02:56:11.6360000')
GO

INSERT INTO [dbo].[guide_calendar] ([id], [guide_id], [date], [status], [create_time]) VALUES (N'11', N'5', N'2026-01-25', N'available', N'2025-12-22 02:56:28.5760000')
GO

INSERT INTO [dbo].[guide_calendar] ([id], [guide_id], [date], [status], [create_time]) VALUES (N'12', N'5', N'2026-01-27', N'booked', N'2025-12-22 03:08:25.5170000')
GO

INSERT INTO [dbo].[guide_calendar] ([id], [guide_id], [date], [status], [create_time]) VALUES (N'13', N'5', N'2026-01-02', N'unavailable', N'2025-12-25 02:24:46.3480000')
GO

INSERT INTO [dbo].[guide_calendar] ([id], [guide_id], [date], [status], [create_time]) VALUES (N'14', N'5', N'2026-01-03', N'unavailable', N'2025-12-25 02:24:46.3800000')
GO

INSERT INTO [dbo].[guide_calendar] ([id], [guide_id], [date], [status], [create_time]) VALUES (N'15', N'5', N'2026-01-04', N'unavailable', N'2025-12-25 02:24:46.3970000')
GO

INSERT INTO [dbo].[guide_calendar] ([id], [guide_id], [date], [status], [create_time]) VALUES (N'16', N'5', N'2026-01-24', N'available', N'2025-12-25 02:24:54.6500000')
GO

INSERT INTO [dbo].[guide_calendar] ([id], [guide_id], [date], [status], [create_time]) VALUES (N'17', N'5', N'2026-01-23', N'available', N'2025-12-25 02:24:54.6790000')
GO

SET IDENTITY_INSERT [dbo].[guide_calendar] OFF
GO

COMMIT
GO


-- ----------------------------
-- Table structure for guides
-- ----------------------------
IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID(N'[dbo].[guides]') AND type IN ('U'))
	DROP TABLE [dbo].[guides]
GO

CREATE TABLE [dbo].[guides] (
  [id] int  IDENTITY(1,1) NOT NULL,
  [user_id] int  NOT NULL,
  [languages] nvarchar(500) COLLATE SQL_Latin1_General_CP1_CI_AS  NULL,
  [cities] nvarchar(500) COLLATE SQL_Latin1_General_CP1_CI_AS  NULL,
  [tags] nvarchar(500) COLLATE SQL_Latin1_General_CP1_CI_AS  NULL,
  [bio] nvarchar(1000) COLLATE SQL_Latin1_General_CP1_CI_AS  NULL,
  [hourly_rate] decimal(10,2) DEFAULT 0 NULL,
  [daily_rate] decimal(10,2) DEFAULT 0 NULL,
  [certificates] nvarchar(1000) COLLATE SQL_Latin1_General_CP1_CI_AS  NULL,
  [verification_status] nvarchar(20) COLLATE SQL_Latin1_General_CP1_CI_AS DEFAULT 'pending' NULL,
  [rejection_reason] nvarchar(500) COLLATE SQL_Latin1_General_CP1_CI_AS  NULL,
  [rating] decimal(3,2) DEFAULT 0 NULL,
  [total_reviews] int DEFAULT 0 NULL,
  [total_orders] int DEFAULT 0 NULL,
  [total_income] decimal(12,2) DEFAULT 0 NULL,
  [create_time] datetime2(7) DEFAULT getdate() NULL,
  [update_time] datetime2(7) DEFAULT getdate() NULL
)
GO

ALTER TABLE [dbo].[guides] SET (LOCK_ESCALATION = TABLE)
GO


-- ----------------------------
-- Records of guides
-- ----------------------------
BEGIN TRANSACTION
GO

SET IDENTITY_INSERT [dbo].[guides] ON
GO

INSERT INTO [dbo].[guides] ([id], [user_id], [languages], [cities], [tags], [bio], [hourly_rate], [daily_rate], [certificates], [verification_status], [rejection_reason], [rating], [total_reviews], [total_orders], [total_income], [create_time], [update_time]) VALUES (N'1', N'2', N'["中文", "英文"]', N'["北京", "上海", "深圳"]', N'["计算机", "留学", "CS"]', N'我是Alice，拥有5年计算机科学教学经验，专注于帮助学生在CS领域取得成功。', N'200.00', N'1500.00', N'[]', N'approved', NULL, N'4.80', N'15', N'8', N'12000.00', N'2025-12-21 16:52:02.2933333', N'2025-12-21 16:52:02.2933333')
GO

INSERT INTO [dbo].[guides] ([id], [user_id], [languages], [cities], [tags], [bio], [hourly_rate], [daily_rate], [certificates], [verification_status], [rejection_reason], [rating], [total_reviews], [total_orders], [total_income], [create_time], [update_time]) VALUES (N'2', N'3', N'["中文", "英文"]', N'["北京", "杭州"]', N'["数学", "考研", "经验"]', N'我是Bob，数学专业博士，擅长考研数学辅导，已帮助100+学生成功上岸。', N'150.00', N'1200.00', N'[]', N'approved', NULL, N'4.90', N'23', N'12', N'14400.00', N'2025-12-21 16:52:02.3233333', N'2025-12-21 16:52:02.3233333')
GO

INSERT INTO [dbo].[guides] ([id], [user_id], [languages], [cities], [tags], [bio], [hourly_rate], [daily_rate], [certificates], [verification_status], [rejection_reason], [rating], [total_reviews], [total_orders], [total_income], [create_time], [update_time]) VALUES (N'3', N'4', N'["中文", "日文", "韩文"]', N'["北京", "上海", "广州", "深圳"]', N'["旅游", "美食", "文化"]', N'我是Charlie，资深旅游向导，熟悉各地文化美食，带您深度体验城市魅力。', N'100.00', N'800.00', N'[]', N'approved', NULL, N'4.70', N'18', N'10', N'8000.00', N'2025-12-21 16:52:02.3266667', N'2025-12-21 16:52:02.3266667')
GO

INSERT INTO [dbo].[guides] ([id], [user_id], [languages], [cities], [tags], [bio], [hourly_rate], [daily_rate], [certificates], [verification_status], [rejection_reason], [rating], [total_reviews], [total_orders], [total_income], [create_time], [update_time]) VALUES (N'4', N'5', N'["中文","英文"]', N'["杭州","上海"]', N'["经验","留学"]', N'我是Diana，正在申请成为向导，期待为您提供优质服务。', N'180.00', N'1300.00', N'["导游合格证书"]', N'approved', NULL, N'5.00', N'2', N'2', N'70200.00', N'2025-12-21 16:52:02.3266667', N'2025-12-22 02:31:00.6940000')
GO

INSERT INTO [dbo].[guides] ([id], [user_id], [languages], [cities], [tags], [bio], [hourly_rate], [daily_rate], [certificates], [verification_status], [rejection_reason], [rating], [total_reviews], [total_orders], [total_income], [create_time], [update_time]) VALUES (N'5', N'6', N'["中文"]', N'["北京"]', N'["经验"]', N'我是Eve，申请成为向导。', N'100.00', N'800.00', N'[]', N'rejected', N'?????,???????', N'0.00', N'0', N'0', N'0.00', N'2025-12-21 16:52:02.3300000', N'2025-12-21 16:52:02.3300000')
GO

SET IDENTITY_INSERT [dbo].[guides] OFF
GO

COMMIT
GO


-- ----------------------------
-- Table structure for notifications
-- ----------------------------
IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID(N'[dbo].[notifications]') AND type IN ('U'))
	DROP TABLE [dbo].[notifications]
GO

CREATE TABLE [dbo].[notifications] (
  [id] int  IDENTITY(1,1) NOT NULL,
  [user_id] int  NOT NULL,
  [type] nvarchar(20) COLLATE SQL_Latin1_General_CP1_CI_AS  NOT NULL,
  [title] nvarchar(100) COLLATE SQL_Latin1_General_CP1_CI_AS  NOT NULL,
  [content] nvarchar(500) COLLATE SQL_Latin1_General_CP1_CI_AS  NOT NULL,
  [related_id] int  NULL,
  [read] bit DEFAULT 0 NOT NULL,
  [create_time] datetime2(7) DEFAULT getdate() NULL,
  [update_time] datetime2(7) DEFAULT getdate() NULL
)
GO

ALTER TABLE [dbo].[notifications] SET (LOCK_ESCALATION = TABLE)
GO


-- ----------------------------
-- Records of notifications
-- ----------------------------
BEGIN TRANSACTION
GO

SET IDENTITY_INSERT [dbo].[notifications] ON
GO

INSERT INTO [dbo].[notifications] ([id], [user_id], [type], [title], [content], [related_id], [read], [create_time], [update_time]) VALUES (N'1', N'6', N'system', N'头像审核通过', N'恭喜！您的头像已通过审核，现已正常显示。', NULL, N'1', N'2025-12-25 02:22:16.3430000', N'2025-12-25 02:22:37.3460000')
GO

SET IDENTITY_INSERT [dbo].[notifications] OFF
GO

COMMIT
GO


-- ----------------------------
-- Table structure for orders
-- ----------------------------
IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID(N'[dbo].[orders]') AND type IN ('U'))
	DROP TABLE [dbo].[orders]
GO

CREATE TABLE [dbo].[orders] (
  [id] int  IDENTITY(1,1) NOT NULL,
  [tourist_id] int  NOT NULL,
  [guide_id] int  NOT NULL,
  [order_type] nvarchar(20) COLLATE SQL_Latin1_General_CP1_CI_AS  NOT NULL,
  [route_name] nvarchar(200) COLLATE SQL_Latin1_General_CP1_CI_AS  NULL,
  [route_id] int  NULL,
  [start_date] date  NOT NULL,
  [end_date] date  NOT NULL,
  [participants] int DEFAULT 1 NULL,
  [special_requirements] nvarchar(1000) COLLATE SQL_Latin1_General_CP1_CI_AS  NULL,
  [total_price] decimal(10,2)  NOT NULL,
  [commission_rate] decimal(5,2) DEFAULT 0.10 NULL,
  [commission_amount] decimal(10,2) DEFAULT 0 NULL,
  [guide_income] decimal(10,2) DEFAULT 0 NULL,
  [status] nvarchar(20) COLLATE SQL_Latin1_General_CP1_CI_AS DEFAULT 'pending' NOT NULL,
  [cancel_reason] nvarchar(500) COLLATE SQL_Latin1_General_CP1_CI_AS  NULL,
  [reject_reason] nvarchar(500) COLLATE SQL_Latin1_General_CP1_CI_AS  NULL,
  [create_time] datetime2(7) DEFAULT getdate() NULL,
  [update_time] datetime2(7) DEFAULT getdate() NULL
)
GO

ALTER TABLE [dbo].[orders] SET (LOCK_ESCALATION = TABLE)
GO


-- ----------------------------
-- Records of orders
-- ----------------------------
BEGIN TRANSACTION
GO

SET IDENTITY_INSERT [dbo].[orders] ON
GO

INSERT INTO [dbo].[orders] ([id], [tourist_id], [guide_id], [order_type], [route_name], [route_id], [start_date], [end_date], [participants], [special_requirements], [total_price], [commission_rate], [commission_amount], [guide_income], [status], [cancel_reason], [reject_reason], [create_time], [update_time]) VALUES (N'1', N'2', N'5', N'custom', NULL, NULL, N'2025-12-22', N'2026-01-22', N'1', N'', N'41600.00', N'0.10', N'4160.00', N'37440.00', N'completed', NULL, NULL, N'2025-12-22 02:36:24.6400000', N'2025-12-22 02:57:47.9860000')
GO

INSERT INTO [dbo].[orders] ([id], [tourist_id], [guide_id], [order_type], [route_name], [route_id], [start_date], [end_date], [participants], [special_requirements], [total_price], [commission_rate], [commission_amount], [guide_income], [status], [cancel_reason], [reject_reason], [create_time], [update_time]) VALUES (N'2', N'6', N'5', N'custom', NULL, NULL, N'2026-01-28', N'2026-01-30', N'1', N'', N'3900.00', N'0.10', N'390.00', N'3510.00', N'cancelled', NULL, N'当天已排满', N'2025-12-25 01:20:52.1890000', N'2025-12-25 01:21:18.1910000')
GO

INSERT INTO [dbo].[orders] ([id], [tourist_id], [guide_id], [order_type], [route_name], [route_id], [start_date], [end_date], [participants], [special_requirements], [total_price], [commission_rate], [commission_amount], [guide_income], [status], [cancel_reason], [reject_reason], [create_time], [update_time]) VALUES (N'3', N'6', N'5', N'custom', NULL, NULL, N'2026-02-01', N'2026-02-28', N'1', N'', N'36400.00', N'0.10', N'3640.00', N'32760.00', N'completed', NULL, NULL, N'2025-12-25 02:25:58.1840000', N'2025-12-25 02:27:16.4300000')
GO

SET IDENTITY_INSERT [dbo].[orders] OFF
GO

COMMIT
GO


-- ----------------------------
-- Table structure for password_reset_codes
-- ----------------------------
IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID(N'[dbo].[password_reset_codes]') AND type IN ('U'))
	DROP TABLE [dbo].[password_reset_codes]
GO

CREATE TABLE [dbo].[password_reset_codes] (
  [id] int  IDENTITY(1,1) NOT NULL,
  [email] nvarchar(255) COLLATE SQL_Latin1_General_CP1_CI_AS  NOT NULL,
  [code] nvarchar(10) COLLATE SQL_Latin1_General_CP1_CI_AS  NOT NULL,
  [expire_time] datetime  NOT NULL,
  [used] bit DEFAULT 0 NULL,
  [create_time] datetime DEFAULT getdate() NULL
)
GO

ALTER TABLE [dbo].[password_reset_codes] SET (LOCK_ESCALATION = TABLE)
GO


-- ----------------------------
-- Records of password_reset_codes
-- ----------------------------
BEGIN TRANSACTION
GO

SET IDENTITY_INSERT [dbo].[password_reset_codes] ON
GO

SET IDENTITY_INSERT [dbo].[password_reset_codes] OFF
GO

COMMIT
GO


-- ----------------------------
-- Table structure for post_likes
-- ----------------------------
IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID(N'[dbo].[post_likes]') AND type IN ('U'))
	DROP TABLE [dbo].[post_likes]
GO

CREATE TABLE [dbo].[post_likes] (
  [id] int  IDENTITY(1,1) NOT NULL,
  [post_id] int  NOT NULL,
  [user_id] int  NOT NULL,
  [create_time] datetime2(7) DEFAULT getdate() NULL
)
GO

ALTER TABLE [dbo].[post_likes] SET (LOCK_ESCALATION = TABLE)
GO


-- ----------------------------
-- Records of post_likes
-- ----------------------------
BEGIN TRANSACTION
GO

SET IDENTITY_INSERT [dbo].[post_likes] ON
GO

INSERT INTO [dbo].[post_likes] ([id], [post_id], [user_id], [create_time]) VALUES (N'1', N'2', N'2', N'2025-12-22 03:28:12.2090000')
GO

SET IDENTITY_INSERT [dbo].[post_likes] OFF
GO

COMMIT
GO


-- ----------------------------
-- Table structure for posts
-- ----------------------------
IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID(N'[dbo].[posts]') AND type IN ('U'))
	DROP TABLE [dbo].[posts]
GO

CREATE TABLE [dbo].[posts] (
  [id] int  IDENTITY(1,1) NOT NULL,
  [user_id] int  NOT NULL,
  [user_role] nvarchar(20) COLLATE SQL_Latin1_General_CP1_CI_AS  NOT NULL,
  [title] nvarchar(200) COLLATE SQL_Latin1_General_CP1_CI_AS  NOT NULL,
  [content] nvarchar(max) COLLATE SQL_Latin1_General_CP1_CI_AS  NOT NULL,
  [images] nvarchar(2000) COLLATE SQL_Latin1_General_CP1_CI_AS  NULL,
  [tags] nvarchar(500) COLLATE SQL_Latin1_General_CP1_CI_AS  NULL,
  [likes_count] int DEFAULT 0 NULL,
  [views_count] int DEFAULT 0 NULL,
  [status] nvarchar(20) COLLATE SQL_Latin1_General_CP1_CI_AS DEFAULT 'pending' NULL,
  [rejection_reason] nvarchar(500) COLLATE SQL_Latin1_General_CP1_CI_AS  NULL,
  [exp_awarded] int DEFAULT 0 NULL,
  [create_time] datetime2(7) DEFAULT getdate() NULL,
  [update_time] datetime2(7) DEFAULT getdate() NULL
)
GO

ALTER TABLE [dbo].[posts] SET (LOCK_ESCALATION = TABLE)
GO


-- ----------------------------
-- Records of posts
-- ----------------------------
BEGIN TRANSACTION
GO

SET IDENTITY_INSERT [dbo].[posts] ON
GO

INSERT INTO [dbo].[posts] ([id], [user_id], [user_role], [title], [content], [images], [tags], [likes_count], [views_count], [status], [rejection_reason], [exp_awarded], [create_time], [update_time]) VALUES (N'1', N'2', N'tourist', N'1', N'1', NULL, NULL, N'0', N'0', N'rejected', N'内容太简单', N'0', N'2025-12-21 04:27:01.4650000', N'2025-12-21 04:32:17.3170000')
GO

INSERT INTO [dbo].[posts] ([id], [user_id], [user_role], [title], [content], [images], [tags], [likes_count], [views_count], [status], [rejection_reason], [exp_awarded], [create_time], [update_time]) VALUES (N'2', N'2', N'tourist', N'这是一个测试贴', N'今天天气很好，我希望我得测试顺利通过，这样我就可以早点出去玩', NULL, NULL, N'4', N'0', N'approved', NULL, N'20', N'2025-12-21 04:33:43.4730000', N'2025-12-21 04:34:06.4220000')
GO

SET IDENTITY_INSERT [dbo].[posts] OFF
GO

COMMIT
GO


-- ----------------------------
-- Table structure for reviews
-- ----------------------------
IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID(N'[dbo].[reviews]') AND type IN ('U'))
	DROP TABLE [dbo].[reviews]
GO

CREATE TABLE [dbo].[reviews] (
  [id] int  IDENTITY(1,1) NOT NULL,
  [order_id] int  NOT NULL,
  [tourist_id] int  NOT NULL,
  [guide_id] int  NOT NULL,
  [rating] int  NOT NULL,
  [comment] nvarchar(1000) COLLATE SQL_Latin1_General_CP1_CI_AS  NULL,
  [create_time] datetime2(7) DEFAULT getdate() NULL
)
GO

ALTER TABLE [dbo].[reviews] SET (LOCK_ESCALATION = TABLE)
GO


-- ----------------------------
-- Records of reviews
-- ----------------------------
BEGIN TRANSACTION
GO

SET IDENTITY_INSERT [dbo].[reviews] ON
GO

INSERT INTO [dbo].[reviews] ([id], [order_id], [tourist_id], [guide_id], [rating], [comment], [create_time]) VALUES (N'1', N'1', N'2', N'5', N'5', N'好评，非常满意', N'2025-12-22 02:58:56.4240000')
GO

INSERT INTO [dbo].[reviews] ([id], [order_id], [tourist_id], [guide_id], [rating], [comment], [create_time]) VALUES (N'2', N'3', N'6', N'5', N'5', N'很nice', N'2025-12-25 02:27:42.2960000')
GO

SET IDENTITY_INSERT [dbo].[reviews] OFF
GO

COMMIT
GO


-- ----------------------------
-- Table structure for users
-- ----------------------------
IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID(N'[dbo].[users]') AND type IN ('U'))
	DROP TABLE [dbo].[users]
GO

CREATE TABLE [dbo].[users] (
  [id] int  IDENTITY(1,1) NOT NULL,
  [nickname] nvarchar(50) COLLATE SQL_Latin1_General_CP1_CI_AS  NOT NULL,
  [password] nvarchar(255) COLLATE SQL_Latin1_General_CP1_CI_AS  NOT NULL,
  [role] nvarchar(20) COLLATE SQL_Latin1_General_CP1_CI_AS  NOT NULL,
  [status] nvarchar(20) COLLATE SQL_Latin1_General_CP1_CI_AS DEFAULT 'active' NOT NULL,
  [full_name] nvarchar(100) COLLATE SQL_Latin1_General_CP1_CI_AS  NULL,
  [email] nvarchar(100) COLLATE SQL_Latin1_General_CP1_CI_AS  NULL,
  [phonenumber] nvarchar(20) COLLATE SQL_Latin1_General_CP1_CI_AS  NULL,
  [avatar_path] nvarchar(255) COLLATE SQL_Latin1_General_CP1_CI_AS  NULL,
  [lv] int DEFAULT 1 NULL,
  [exp] int DEFAULT 0 NULL,
  [create_time] datetime2(7) DEFAULT getdate() NULL,
  [avatar_status] nvarchar(20) COLLATE SQL_Latin1_General_CP1_CI_AS  NULL
)
GO

ALTER TABLE [dbo].[users] SET (LOCK_ESCALATION = TABLE)
GO


-- ----------------------------
-- Records of users
-- ----------------------------
BEGIN TRANSACTION
GO

SET IDENTITY_INSERT [dbo].[users] ON
GO

INSERT INTO [dbo].[users] ([id], [nickname], [password], [role], [status], [full_name], [email], [phonenumber], [avatar_path], [lv], [exp], [create_time], [avatar_status]) VALUES (N'1', N'admin', N'$2a$10$.aadtiBAesQ0cyHDQoRsCeR5aN44pUXR7CtjrN6UIl7MwShtovksW', N'admin', N'active', N'系统管理员', N'admin@guidematch.com', N'13800000000', N'/img/avatar/1766341340927_870.png', N'1', N'0', N'2025-12-20 17:16:39.0766667', N'approved')
GO

INSERT INTO [dbo].[users] ([id], [nickname], [password], [role], [status], [full_name], [email], [phonenumber], [avatar_path], [lv], [exp], [create_time], [avatar_status]) VALUES (N'2', N'test', N'$2a$10$fIQY/fAt2/6DAuhBxJgYeeU3YrgsO5aqjgPwLu9IEIaXQMwZtDKqu', N'tourist', N'active', N'testuser', N'test@123.com', N'13111111111', N'/img/avatar/1766321735568_815.png', N'1', N'60', N'2025-12-20 19:31:33.0700000', N'approved')
GO

INSERT INTO [dbo].[users] ([id], [nickname], [password], [role], [status], [full_name], [email], [phonenumber], [avatar_path], [lv], [exp], [create_time], [avatar_status]) VALUES (N'3', N'test1', N'$2a$10$fzp6klKAaZNqn6m2EKWxne8iwf7XZGrnV9xzDCnwiQWNBQciZDkXy', N'tourist', N'active', N'testuser1', N'test@133.com', N'13111111111', NULL, N'1', N'0', N'2025-12-20 20:01:46.6433333', NULL)
GO

INSERT INTO [dbo].[users] ([id], [nickname], [password], [role], [status], [full_name], [email], [phonenumber], [avatar_path], [lv], [exp], [create_time], [avatar_status]) VALUES (N'4', N'user2', N'$2a$10$zfZekK8kzFjRll32HL.Tr.0EB7I4joojiVodBXyqDKXK/x1RaW7gC', N'tourist', N'active', N'testuser2', N'test@122.com', N'1311111111', NULL, N'1', N'0', N'2025-12-20 20:03:24.1766667', NULL)
GO

INSERT INTO [dbo].[users] ([id], [nickname], [password], [role], [status], [full_name], [email], [phonenumber], [avatar_path], [lv], [exp], [create_time], [avatar_status]) VALUES (N'5', N'guide1', N'$2a$10$.aadtiBAesQ0cyHDQoRsCeR5aN44pUXR7CtjrN6UIl7MwShtovksW', N'guide', N'active', N'guide1', N'guide@123.com', N'13111111111', N'/img/avatar/1766337591457_927.png', N'1', N'60', N'2025-12-20 20:05:06.3900000', N'approved')
GO

INSERT INTO [dbo].[users] ([id], [nickname], [password], [role], [status], [full_name], [email], [phonenumber], [avatar_path], [lv], [exp], [create_time], [avatar_status]) VALUES (N'6', N'张三', N'$2a$10$T1IwSnm.4B5m9U8b92aWmuQayGvnZ49/zsjTMyuoXcC9yRBJIO3Mi', N'tourist', N'active', N'张三', N'zhangsan@123.com', N'13111111111', N'/img/avatar/1766600512014_679.png', N'1', N'40', N'2025-12-24 17:13:46.0433333', N'approved')
GO

INSERT INTO [dbo].[users] ([id], [nickname], [password], [role], [status], [full_name], [email], [phonenumber], [avatar_path], [lv], [exp], [create_time], [avatar_status]) VALUES (N'7', N'lisi', N'$2a$10$tQJ2U7/R9xMhAA2R2nw4wux2/oyEFYVfnbDiaiKS8Pwzfz9s6pOUO', N'tourist', N'active', N'里斯', N'lisi@123.com', N'123456', NULL, N'1', N'0', N'2025-12-24 17:14:14.1900000', NULL)
GO

INSERT INTO [dbo].[users] ([id], [nickname], [password], [role], [status], [full_name], [email], [phonenumber], [avatar_path], [lv], [exp], [create_time], [avatar_status]) VALUES (N'8', N'wangwu', N'$2a$10$UqUpTEkM7cl.r2U/wjNLWeciGXDFbxeS.C4FSEFbYZPf97kzLD1Rq', N'guide', N'active', N'wangwu', N'wangwu@123.com', N'123456', NULL, N'1', N'0', N'2025-12-24 17:14:42.4366667', NULL)
GO

SET IDENTITY_INSERT [dbo].[users] OFF
GO

COMMIT
GO


-- ----------------------------
-- Table structure for withdrawals
-- ----------------------------
IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID(N'[dbo].[withdrawals]') AND type IN ('U'))
	DROP TABLE [dbo].[withdrawals]
GO

CREATE TABLE [dbo].[withdrawals] (
  [id] int  IDENTITY(1,1) NOT NULL,
  [guide_id] int  NOT NULL,
  [amount] decimal(10,2)  NOT NULL,
  [bank_name] nvarchar(100) COLLATE SQL_Latin1_General_CP1_CI_AS  NULL,
  [bank_account] nvarchar(50) COLLATE SQL_Latin1_General_CP1_CI_AS  NULL,
  [account_holder] nvarchar(100) COLLATE SQL_Latin1_General_CP1_CI_AS  NULL,
  [status] nvarchar(20) COLLATE SQL_Latin1_General_CP1_CI_AS DEFAULT 'pending' NULL,
  [rejection_reason] nvarchar(500) COLLATE SQL_Latin1_General_CP1_CI_AS  NULL,
  [process_time] datetime2(7)  NULL,
  [create_time] datetime2(7) DEFAULT getdate() NULL
)
GO

ALTER TABLE [dbo].[withdrawals] SET (LOCK_ESCALATION = TABLE)
GO


-- ----------------------------
-- Records of withdrawals
-- ----------------------------
BEGIN TRANSACTION
GO

SET IDENTITY_INSERT [dbo].[withdrawals] ON
GO

INSERT INTO [dbo].[withdrawals] ([id], [guide_id], [amount], [bank_name], [bank_account], [account_holder], [status], [rejection_reason], [process_time], [create_time]) VALUES (N'1', N'5', N'30000.00', N'中国银行', N'131111111111111111111', N'1111', N'completed', NULL, N'2025-12-22 03:09:29.1450000', N'2025-12-22 03:09:29.1450000')
GO

SET IDENTITY_INSERT [dbo].[withdrawals] OFF
GO

COMMIT
GO


-- ----------------------------
-- Auto increment value for favorites
-- ----------------------------
DBCC CHECKIDENT ('[dbo].[favorites]', RESEED, 1002)
GO


-- ----------------------------
-- Auto increment value for guide_calendar
-- ----------------------------
DBCC CHECKIDENT ('[dbo].[guide_calendar]', RESEED, 1000)
GO


-- ----------------------------
-- Auto increment value for guides
-- ----------------------------
DBCC CHECKIDENT ('[dbo].[guides]', RESEED, 1000)
GO


-- ----------------------------
-- Auto increment value for notifications
-- ----------------------------
DBCC CHECKIDENT ('[dbo].[notifications]', RESEED, 1)
GO


-- ----------------------------
-- Auto increment value for orders
-- ----------------------------
DBCC CHECKIDENT ('[dbo].[orders]', RESEED, 1002)
GO


-- ----------------------------
-- Indexes structure for table orders
-- ----------------------------
CREATE NONCLUSTERED INDEX [IX_orders_tourist_id]
ON [dbo].[orders] (
  [tourist_id] ASC
)
GO

CREATE NONCLUSTERED INDEX [IX_orders_guide_id]
ON [dbo].[orders] (
  [guide_id] ASC
)
GO

CREATE NONCLUSTERED INDEX [IX_orders_status]
ON [dbo].[orders] (
  [status] ASC
)
GO

CREATE NONCLUSTERED INDEX [IX_orders_dates]
ON [dbo].[orders] (
  [start_date] ASC,
  [end_date] ASC
)
GO


-- ----------------------------
-- Checks structure for table orders
-- ----------------------------
ALTER TABLE [dbo].[orders] ADD CONSTRAINT [CK__orders__order_ty__4D94879B] CHECK ([order_type]='custom' OR [order_type]='fixed')
GO

ALTER TABLE [dbo].[orders] ADD CONSTRAINT [CK__orders__status__534D60F1] CHECK ([status]='cancelled' OR [status]='completed' OR [status]='in_progress' OR [status]='confirmed' OR [status]='pending')
GO


-- ----------------------------
-- Primary Key structure for table orders
-- ----------------------------
ALTER TABLE [dbo].[orders] ADD CONSTRAINT [PK__orders__3213E83F196D90EB] PRIMARY KEY CLUSTERED ([id])
WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)  
ON [PRIMARY]
GO


-- ----------------------------
-- Auto increment value for password_reset_codes
-- ----------------------------
DBCC CHECKIDENT ('[dbo].[password_reset_codes]', RESEED, 1)
GO


-- ----------------------------
-- Indexes structure for table password_reset_codes
-- ----------------------------
CREATE NONCLUSTERED INDEX [IX_password_reset_codes_email]
ON [dbo].[password_reset_codes] (
  [email] ASC
)
GO

CREATE NONCLUSTERED INDEX [IX_password_reset_codes_code]
ON [dbo].[password_reset_codes] (
  [code] ASC
)
GO


-- ----------------------------
-- Primary Key structure for table password_reset_codes
-- ----------------------------
ALTER TABLE [dbo].[password_reset_codes] ADD CONSTRAINT [PK__password__3213E83F11E1AC51] PRIMARY KEY CLUSTERED ([id])
WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)  
ON [PRIMARY]
GO


-- ----------------------------
-- Auto increment value for post_likes
-- ----------------------------
DBCC CHECKIDENT ('[dbo].[post_likes]', RESEED, 13)
GO


-- ----------------------------
-- Indexes structure for table post_likes
-- ----------------------------
CREATE NONCLUSTERED INDEX [IX_post_likes_post_id]
ON [dbo].[post_likes] (
  [post_id] ASC
)
GO

CREATE NONCLUSTERED INDEX [IX_post_likes_user_id]
ON [dbo].[post_likes] (
  [user_id] ASC
)
GO


-- ----------------------------
-- Uniques structure for table post_likes
-- ----------------------------
ALTER TABLE [dbo].[post_likes] ADD CONSTRAINT [UQ_post_likes_post_user] UNIQUE NONCLUSTERED ([post_id] ASC, [user_id] ASC)
WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)  
ON [PRIMARY]
GO


-- ----------------------------
-- Primary Key structure for table post_likes
-- ----------------------------
ALTER TABLE [dbo].[post_likes] ADD CONSTRAINT [PK__post_lik__3213E83F823FCCEC] PRIMARY KEY CLUSTERED ([id])
WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)  
ON [PRIMARY]
GO


-- ----------------------------
-- Auto increment value for posts
-- ----------------------------
DBCC CHECKIDENT ('[dbo].[posts]', RESEED, 1001)
GO


-- ----------------------------
-- Indexes structure for table posts
-- ----------------------------
CREATE NONCLUSTERED INDEX [IX_posts_user_id]
ON [dbo].[posts] (
  [user_id] ASC
)
GO

CREATE NONCLUSTERED INDEX [IX_posts_status]
ON [dbo].[posts] (
  [status] ASC
)
GO

CREATE NONCLUSTERED INDEX [IX_posts_create_time]
ON [dbo].[posts] (
  [create_time] DESC
)
GO


-- ----------------------------
-- Checks structure for table posts
-- ----------------------------
ALTER TABLE [dbo].[posts] ADD CONSTRAINT [CK__posts__status__6A30C649] CHECK ([status]='deleted' OR [status]='rejected' OR [status]='approved' OR [status]='pending')
GO


-- ----------------------------
-- Primary Key structure for table posts
-- ----------------------------
ALTER TABLE [dbo].[posts] ADD CONSTRAINT [PK__posts__3213E83F5CDF2F29] PRIMARY KEY CLUSTERED ([id])
WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)  
ON [PRIMARY]
GO


-- ----------------------------
-- Auto increment value for reviews
-- ----------------------------
DBCC CHECKIDENT ('[dbo].[reviews]', RESEED, 1001)
GO


-- ----------------------------
-- Indexes structure for table reviews
-- ----------------------------
CREATE NONCLUSTERED INDEX [IX_reviews_guide_id]
ON [dbo].[reviews] (
  [guide_id] ASC
)
GO

CREATE NONCLUSTERED INDEX [IX_reviews_tourist_id]
ON [dbo].[reviews] (
  [tourist_id] ASC
)
GO


-- ----------------------------
-- Uniques structure for table reviews
-- ----------------------------
ALTER TABLE [dbo].[reviews] ADD CONSTRAINT [UQ_reviews_order_id] UNIQUE NONCLUSTERED ([order_id] ASC)
WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)  
ON [PRIMARY]
GO


-- ----------------------------
-- Checks structure for table reviews
-- ----------------------------
ALTER TABLE [dbo].[reviews] ADD CONSTRAINT [CK__reviews__rating__5AEE82B9] CHECK ([rating]>=(1) AND [rating]<=(5))
GO


-- ----------------------------
-- Primary Key structure for table reviews
-- ----------------------------
ALTER TABLE [dbo].[reviews] ADD CONSTRAINT [PK__reviews__3213E83F10AD569A] PRIMARY KEY CLUSTERED ([id])
WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)  
ON [PRIMARY]
GO


-- ----------------------------
-- Auto increment value for users
-- ----------------------------
DBCC CHECKIDENT ('[dbo].[users]', RESEED, 8)
GO


-- ----------------------------
-- Indexes structure for table users
-- ----------------------------
CREATE NONCLUSTERED INDEX [IX_users_role]
ON [dbo].[users] (
  [role] ASC
)
GO

CREATE NONCLUSTERED INDEX [IX_users_status]
ON [dbo].[users] (
  [status] ASC
)
GO


-- ----------------------------
-- Uniques structure for table users
-- ----------------------------
ALTER TABLE [dbo].[users] ADD CONSTRAINT [UQ_users_email] UNIQUE NONCLUSTERED ([email] ASC)
WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)  
ON [PRIMARY]
GO

ALTER TABLE [dbo].[users] ADD CONSTRAINT [UQ_users_nickname] UNIQUE NONCLUSTERED ([nickname] ASC)
WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)  
ON [PRIMARY]
GO


-- ----------------------------
-- Checks structure for table users
-- ----------------------------
ALTER TABLE [dbo].[users] ADD CONSTRAINT [CK__users__role__398D8EEE] CHECK ([role]='admin' OR [role]='guide' OR [role]='tourist')
GO

ALTER TABLE [dbo].[users] ADD CONSTRAINT [CK__users__status__3B75D760] CHECK ([status]='pending' OR [status]='frozen' OR [status]='active')
GO

ALTER TABLE [dbo].[users] ADD CONSTRAINT [CK_users_avatar_status] CHECK ([avatar_status] IS NULL OR ([avatar_status]='rejected' OR [avatar_status]='approved' OR [avatar_status]='pending'))
GO


-- ----------------------------
-- Primary Key structure for table users
-- ----------------------------
ALTER TABLE [dbo].[users] ADD CONSTRAINT [PK__users__3213E83F8380252F] PRIMARY KEY CLUSTERED ([id])
WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)  
ON [PRIMARY]
GO


-- ----------------------------
-- Auto increment value for withdrawals
-- ----------------------------
DBCC CHECKIDENT ('[dbo].[withdrawals]', RESEED, 1)
GO


-- ----------------------------
-- Indexes structure for table withdrawals
-- ----------------------------
CREATE NONCLUSTERED INDEX [IX_withdrawals_guide_id]
ON [dbo].[withdrawals] (
  [guide_id] ASC
)
GO

CREATE NONCLUSTERED INDEX [IX_withdrawals_status]
ON [dbo].[withdrawals] (
  [status] ASC
)
GO


-- ----------------------------
-- Checks structure for table withdrawals
-- ----------------------------
ALTER TABLE [dbo].[withdrawals] ADD CONSTRAINT [CK__withdrawa__statu__0E6E26BF] CHECK ([status]='completed' OR [status]='rejected' OR [status]='approved' OR [status]='pending')
GO


-- ----------------------------
-- Primary Key structure for table withdrawals
-- ----------------------------
ALTER TABLE [dbo].[withdrawals] ADD CONSTRAINT [PK__withdraw__3213E83F9F04D60A] PRIMARY KEY CLUSTERED ([id])
WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)  
ON [PRIMARY]
GO


-- ----------------------------
-- Foreign Keys structure for table orders
-- ----------------------------
ALTER TABLE [dbo].[orders] ADD CONSTRAINT [FK_orders_tourist_id] FOREIGN KEY ([tourist_id]) REFERENCES [dbo].[users] ([id]) ON DELETE NO ACTION ON UPDATE NO ACTION
GO

ALTER TABLE [dbo].[orders] ADD CONSTRAINT [FK_orders_guide_id] FOREIGN KEY ([guide_id]) REFERENCES [dbo].[users] ([id]) ON DELETE NO ACTION ON UPDATE NO ACTION
GO


-- ----------------------------
-- Foreign Keys structure for table post_likes
-- ----------------------------
ALTER TABLE [dbo].[post_likes] ADD CONSTRAINT [FK_post_likes_post_id] FOREIGN KEY ([post_id]) REFERENCES [dbo].[posts] ([id]) ON DELETE CASCADE ON UPDATE NO ACTION
GO

ALTER TABLE [dbo].[post_likes] ADD CONSTRAINT [FK_post_likes_user_id] FOREIGN KEY ([user_id]) REFERENCES [dbo].[users] ([id]) ON DELETE CASCADE ON UPDATE NO ACTION
GO


-- ----------------------------
-- Foreign Keys structure for table posts
-- ----------------------------
ALTER TABLE [dbo].[posts] ADD CONSTRAINT [FK_posts_user_id] FOREIGN KEY ([user_id]) REFERENCES [dbo].[users] ([id]) ON DELETE NO ACTION ON UPDATE NO ACTION
GO


-- ----------------------------
-- Foreign Keys structure for table reviews
-- ----------------------------
ALTER TABLE [dbo].[reviews] ADD CONSTRAINT [FK_reviews_order_id] FOREIGN KEY ([order_id]) REFERENCES [dbo].[orders] ([id]) ON DELETE NO ACTION ON UPDATE NO ACTION
GO

ALTER TABLE [dbo].[reviews] ADD CONSTRAINT [FK_reviews_tourist_id] FOREIGN KEY ([tourist_id]) REFERENCES [dbo].[users] ([id]) ON DELETE NO ACTION ON UPDATE NO ACTION
GO

ALTER TABLE [dbo].[reviews] ADD CONSTRAINT [FK_reviews_guide_id] FOREIGN KEY ([guide_id]) REFERENCES [dbo].[users] ([id]) ON DELETE NO ACTION ON UPDATE NO ACTION
GO


-- ----------------------------
-- Foreign Keys structure for table withdrawals
-- ----------------------------
ALTER TABLE [dbo].[withdrawals] ADD CONSTRAINT [FK_withdrawals_guide_id] FOREIGN KEY ([guide_id]) REFERENCES [dbo].[users] ([id]) ON DELETE NO ACTION ON UPDATE NO ACTION
GO

