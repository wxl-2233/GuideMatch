-- GuideMatch Database Schema - MySQL Version
-- Migration: V1__Create_initial_schema.sql

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- =============================
-- users
-- =============================
DROP TABLE IF EXISTS users;

CREATE TABLE users (
  id INT PRIMARY KEY AUTO_INCREMENT,
  nickname VARCHAR(50) NOT NULL UNIQUE,
  password VARCHAR(255) NOT NULL,
  role VARCHAR(20) NOT NULL COMMENT 'admin/guide/tourist',
  status VARCHAR(20) NOT NULL DEFAULT 'active' COMMENT 'pending/frozen/active',
  full_name VARCHAR(100),
  email VARCHAR(100) UNIQUE,
  phonenumber VARCHAR(20),
  avatar_path VARCHAR(255),
  lv INT DEFAULT 1,
  exp INT DEFAULT 0,
  avatar_status VARCHAR(20) COMMENT 'rejected/approved/pending',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =============================
-- guides
-- =============================
DROP TABLE IF EXISTS guides;

CREATE TABLE guides (
  id INT PRIMARY KEY AUTO_INCREMENT,
  user_id INT NOT NULL,
  languages TEXT,
  cities TEXT,
  tags TEXT,
  bio TEXT,
  hourly_rate DECIMAL(10,2) DEFAULT 0,
  daily_rate DECIMAL(10,2) DEFAULT 0,
  certificates TEXT,
  verification_status VARCHAR(20) DEFAULT 'pending',
  rejection_reason VARCHAR(500),
  rating DECIMAL(3,2) DEFAULT 0,
  total_reviews INT DEFAULT 0,
  total_orders INT DEFAULT 0,
  total_income DECIMAL(12,2) DEFAULT 0,
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_guides_user FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =============================
-- orders
-- =============================
DROP TABLE IF EXISTS orders;

CREATE TABLE orders (
  id INT PRIMARY KEY AUTO_INCREMENT,
  tourist_id INT NOT NULL,
  guide_id INT NOT NULL,
  order_type VARCHAR(20) NOT NULL,
  route_name VARCHAR(200),
  route_id INT,
  start_date DATE NOT NULL,
  end_date DATE NOT NULL,
  participants INT DEFAULT 1,
  special_requirements TEXT,
  total_price DECIMAL(10,2) NOT NULL,
  commission_rate DECIMAL(5,2) DEFAULT 0.10,
  commission_amount DECIMAL(10,2) DEFAULT 0,
  guide_income DECIMAL(10,2) DEFAULT 0,
  status VARCHAR(20) DEFAULT 'pending',
  cancel_reason VARCHAR(500),
  reject_reason VARCHAR(500),
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_orders_tourist FOREIGN KEY (tourist_id) REFERENCES users(id),
  CONSTRAINT fk_orders_guide FOREIGN KEY (guide_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =============================
-- reviews
-- =============================
DROP TABLE IF EXISTS reviews;

CREATE TABLE reviews (
  id INT PRIMARY KEY AUTO_INCREMENT,
  order_id INT NOT NULL UNIQUE,
  tourist_id INT NOT NULL,
  guide_id INT NOT NULL,
  rating INT NOT NULL,
  comment TEXT,
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_reviews_order FOREIGN KEY (order_id) REFERENCES orders(id),
  CONSTRAINT fk_reviews_tourist FOREIGN KEY (tourist_id) REFERENCES users(id),
  CONSTRAINT fk_reviews_guide FOREIGN KEY (guide_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =============================
-- posts
-- =============================
DROP TABLE IF EXISTS posts;

CREATE TABLE posts (
  id INT PRIMARY KEY AUTO_INCREMENT,
  user_id INT NOT NULL,
  user_role VARCHAR(20) NOT NULL,
  title VARCHAR(200) NOT NULL,
  content LONGTEXT NOT NULL,
  images TEXT,
  tags TEXT,
  likes_count INT DEFAULT 0,
  views_count INT DEFAULT 0,
  status VARCHAR(20) DEFAULT 'pending',
  rejection_reason VARCHAR(500),
  exp_awarded INT DEFAULT 0,
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_posts_user FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =============================
-- post_likes
-- =============================
DROP TABLE IF EXISTS post_likes;

CREATE TABLE post_likes (
  id INT PRIMARY KEY AUTO_INCREMENT,
  post_id INT NOT NULL,
  user_id INT NOT NULL,
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uq_post_user (post_id, user_id),
  CONSTRAINT fk_postlikes_post FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE,
  CONSTRAINT fk_postlikes_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =============================
-- 基础用户数据（示例）
-- =============================
INSERT INTO users (nickname,password,role,status,lv,exp)
VALUES
('admin','$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKVhqoPwHw6C2/J.4RKHnJk4rJq','admin','active',10,9999),
('test1','$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKVhqoPwHw6C2/J.4RKHnJk4rJq','guide','active',1,0),
('test2','$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKVhqoPwHw6C2/J.4RKHnJk4rJq','tourist','active',1,0);

SET FOREIGN_KEY_CHECKS = 1;
