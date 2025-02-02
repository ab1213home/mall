SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for tb_addresses
-- ----------------------------
DROP TABLE IF EXISTS `tb_addresses`;
CREATE TABLE `tb_addresses`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键，唯一标识每一条记录',
  `user_id` bigint UNSIGNED NOT NULL COMMENT '用户ID，关联用户表',
  `first_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '收件人名字（名）',
  `last_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '收件人名字（姓）',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '收件人联系电话',
  `country` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '地址所在的国家',
  `area_code` bigint UNSIGNED NOT NULL COMMENT '行政代码',
  `address_detail` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '具体的街道、小区、门牌号等信息',
  `postal_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '邮政编码',
  `created_at` datetime NOT NULL COMMENT '创建的时间',
  `updated_at` datetime NOT NULL COMMENT '更新的时间',
  `is_del` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否删除 ',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `user`(`user_id` ASC) USING BTREE,
  INDEX `area_code`(`area_code` ASC) USING BTREE,
  INDEX `id`(`id` ASC, `user_id` ASC) USING BTREE,
  CONSTRAINT `addresser_user` FOREIGN KEY (`user_id`) REFERENCES `tb_users` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户地址表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for tb_banners
-- ----------------------------
DROP TABLE IF EXISTS `tb_banners`;
CREATE TABLE `tb_banners`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT,
  `img` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '轮播图图片地址',
  `url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '跳转URL',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '描述信息',
  `start_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '生效时间',
  `end_time` datetime NOT NULL DEFAULT '9999-12-31 23:59:59' COMMENT '失效时间',
  `creator` bigint UNSIGNED NOT NULL COMMENT '创建人',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `updater` bigint UNSIGNED NOT NULL COMMENT '更新人',
  `updated_at` datetime NOT NULL COMMENT '更新时间',
  `is_del` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `creator`(`creator` ASC) USING BTREE,
  INDEX `updater`(`updater` ASC) USING BTREE,
  CONSTRAINT `banners_creator` FOREIGN KEY (`creator`) REFERENCES `tb_users` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `banners_updater` FOREIGN KEY (`updater`) REFERENCES `tb_users` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '轮播图表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for tb_carts
-- ----------------------------
DROP TABLE IF EXISTS `tb_carts`;
CREATE TABLE `tb_carts`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT,
  `prod_id` bigint UNSIGNED NOT NULL COMMENT '商品id',
  `num` bigint UNSIGNED NOT NULL COMMENT '商品数量',
  `user_id` bigint UNSIGNED NOT NULL COMMENT '用户id',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `user`(`user_id` ASC) USING BTREE,
  INDEX `prod`(`prod_id` ASC) USING BTREE,
  CONSTRAINT `carts_prod` FOREIGN KEY (`prod_id`) REFERENCES `tb_products` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `carts_user` FOREIGN KEY (`user_id`) REFERENCES `tb_users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '购物车表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for tb_categories
-- ----------------------------
DROP TABLE IF EXISTS `tb_categories`;
CREATE TABLE `tb_categories`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID，自增',
  `parent_id` bigint NOT NULL DEFAULT 0 COMMENT '父级分类ID，默认为 0',
  `code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '编码，需唯一',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '分类名称',
  `creator` bigint UNSIGNED NOT NULL COMMENT '创建人',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `updater` bigint UNSIGNED NOT NULL COMMENT '更新人',
  `updated_at` datetime NOT NULL COMMENT '更新时间',
  `is_del` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `creator`(`creator` ASC) USING BTREE,
  INDEX `updater`(`updater` ASC) USING BTREE,
  CONSTRAINT `categories_creator` FOREIGN KEY (`creator`) REFERENCES `tb_users` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `categories_updater` FOREIGN KEY (`updater`) REFERENCES `tb_users` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '商品分类表' ROW_FORMAT = DYNAMIC;


-- ----------------------------
-- Table structure for tb_china_administrative_divisions
-- ----------------------------
DROP TABLE IF EXISTS `tb_china_administrative_divisions`;
CREATE TABLE `tb_china_administrative_divisions`  (
  `area_code` bigint UNSIGNED NOT NULL DEFAULT 0 COMMENT '行政代码',
  `level` tinyint UNSIGNED NOT NULL COMMENT '层级',
  `parent_code` bigint UNSIGNED NOT NULL DEFAULT 0 COMMENT '父级行政代码',
  `zip_code` mediumint(6) UNSIGNED ZEROFILL NOT NULL DEFAULT 000000 COMMENT '邮政编码',
  `city_code` char(6) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '区号',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '名称',
  `short_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '简称',
  `pinyin` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '拼音',
  `lng` decimal(10, 6) NOT NULL DEFAULT 0.000000 COMMENT '经度',
  `lat` decimal(10, 6) NOT NULL DEFAULT 0.000000 COMMENT '纬度',
  PRIMARY KEY (`area_code`) USING BTREE,
  INDEX `parent`(`parent_code`) USING BTREE
) ENGINE = MyISAM AUTO_INCREMENT = 7112658329048 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '中国行政地区表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_collections
-- ----------------------------
DROP TABLE IF EXISTS `tb_collections`;
CREATE TABLE `tb_collections`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键 ID，自增',
  `prod_id` bigint UNSIGNED NOT NULL COMMENT '商品 ID',
  `user_id` bigint UNSIGNED NOT NULL COMMENT '用户 ID',
  `created_at` datetime NOT NULL COMMENT '创建时间，自动填充',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `user`(`user_id` ASC) USING BTREE,
  INDEX `prod`(`prod_id` ASC) USING BTREE,
  CONSTRAINT `collections_prod` FOREIGN KEY (`prod_id`) REFERENCES `tb_products` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `collections_user` FOREIGN KEY (`user_id`) REFERENCES `tb_users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '收藏表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for tb_orderlists
-- ----------------------------
DROP TABLE IF EXISTS `tb_orderlists`;
CREATE TABLE `tb_orderlists`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '订单明细ID',
  `order_id` bigint UNSIGNED NOT NULL COMMENT '订单ID',
  `prod_id` bigint UNSIGNED NOT NULL COMMENT '商品快照ID',
  `num` int NOT NULL COMMENT '数量',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `updated_at` datetime NOT NULL COMMENT '更新时间',
  `is_del` tinyint(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除标志',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `order`(`order_id` ASC) USING BTREE,
  INDEX `orderlists_prod`(`prod_id` ASC) USING BTREE,
  CONSTRAINT `orderlists_order` FOREIGN KEY (`order_id`) REFERENCES `tb_orders` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `orderlists_prod` FOREIGN KEY (`prod_id`) REFERENCES `tb_product_snapshots` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '订单明细表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for tb_orders
-- ----------------------------
DROP TABLE IF EXISTS `tb_orders`;
CREATE TABLE `tb_orders`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  `address_id` bigint UNSIGNED NOT NULL COMMENT '地址ID',
  `user_id` bigint UNSIGNED NOT NULL COMMENT '用户ID',
  `date` datetime NOT NULL COMMENT '下单日期',
  `total_amount` decimal(10, 2) NOT NULL COMMENT '总金额',
  `status` tinyint(1) NOT NULL COMMENT '订单状态',
  `payment_method` tinyint(1) NOT NULL COMMENT '支付方式',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `updated_at` datetime NOT NULL COMMENT '更新时间',
  `is_del` tinyint(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除标志',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `user`(`user_id` ASC) USING BTREE,
  INDEX `orders_address`(`address_id` ASC, `user_id` ASC) USING BTREE,
  CONSTRAINT `orders_address` FOREIGN KEY (`address_id`, `user_id`) REFERENCES `tb_addresses` (`id`, `user_id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `orders_user` FOREIGN KEY (`user_id`) REFERENCES `tb_users` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '订单表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for tb_product_snapshots
-- ----------------------------
DROP TABLE IF EXISTS `tb_product_snapshots`;
CREATE TABLE `tb_product_snapshots`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '快照id',
  `prod_id` bigint UNSIGNED NOT NULL COMMENT '快照关联的商品id',
  `code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '当前商品编码',
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '当前商品标题',
  `category` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '当前商品分类快照',
  `img` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '当前商品图片',
  `price` decimal(10, 2) NOT NULL COMMENT '当前商品价格',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '当前商品描述',
  `creator` bigint UNSIGNED NOT NULL COMMENT '创建人',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `is_del` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `creator`(`creator` ASC) USING BTREE,
  INDEX `prod`(`prod_id` ASC) USING BTREE,
  CONSTRAINT `tb_product_snapshots_ibfk_1` FOREIGN KEY (`prod_id`) REFERENCES `tb_products` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `tb_product_snapshots_ibfk_2` FOREIGN KEY (`creator`) REFERENCES `tb_users` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '商品快照表' ROW_FORMAT = DYNAMIC;


-- ----------------------------
-- Table structure for tb_products
-- ----------------------------
DROP TABLE IF EXISTS `tb_products`;
CREATE TABLE `tb_products`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT,
  `code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '商品编码，不可重复',
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '商品标题',
  `category_id` bigint UNSIGNED NOT NULL COMMENT '商品分类id',
  `img` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '商品图片',
  `price` decimal(10, 2) NOT NULL COMMENT '商品价格',
  `stocks` int NOT NULL COMMENT '商品库存',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '商品描述',
  `creator` bigint UNSIGNED NOT NULL COMMENT '创建人',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `updater` bigint UNSIGNED NOT NULL COMMENT '更新人 ',
  `updated_at` datetime NOT NULL COMMENT '更新时间',
  `is_del` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `code`(`code` ASC) USING BTREE,
  INDEX `category`(`category_id` ASC) USING BTREE,
  INDEX `creator`(`creator` ASC) USING BTREE,
  INDEX `updater`(`updater` ASC) USING BTREE,
  FULLTEXT INDEX `title`(`title`),
  CONSTRAINT `products_category` FOREIGN KEY (`category_id`) REFERENCES `tb_categories` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `products_creator` FOREIGN KEY (`creator`) REFERENCES `tb_users` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `products_updater` FOREIGN KEY (`updater`) REFERENCES `tb_users` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '商品表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for tb_user_records
-- ----------------------------
DROP TABLE IF EXISTS `tb_user_records`;
CREATE TABLE `tb_user_records`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '日志主键，自动增长',
  `ip` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户IP地址',
  `fingerprint` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '浏览器指纹',
  `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '登录使用的用户名或者邮箱',
  `old_email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '旧邮箱',
  `user_id` bigint UNSIGNED NULL DEFAULT NULL COMMENT '关联用户id',
  `state` tinyint(1) NOT NULL COMMENT '状态',
  `trigger_time` datetime NOT NULL COMMENT '触发时间',
  `updater` bigint UNSIGNED NULL DEFAULT NULL COMMENT '触发人',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `username`(`username` ASC) USING BTREE,
  INDEX `fingerprint`(`fingerprint` ASC) USING BTREE,
  INDEX `ip`(`ip` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户日志' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_users
-- ----------------------------
DROP TABLE IF EXISTS `tb_users`;
CREATE TABLE `tb_users`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '用户唯一编号',
  `username` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户名',
  `password` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '密码',
  `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户的电子邮件地址',
  `phone` varchar(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户的电话号码',
  `first_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户的名字',
  `last_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户的姓氏',
  `birth_date` date NULL DEFAULT NULL COMMENT '用户的生日',
  `img` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户的头像',
  `default_address_id` bigint UNSIGNED NULL DEFAULT NULL COMMENT '默认地址编号',
  `role_id` int NOT NULL DEFAULT 1 COMMENT '用户的角色ID，用于权限管理',
  `created_at` datetime NOT NULL COMMENT '创建的时间戳',
  `updater` bigint UNSIGNED NOT NULL COMMENT '更新人',
  `updated_at` datetime NOT NULL COMMENT '更新的时间戳',
  `is_active` tinyint(1) NOT NULL DEFAULT 0 COMMENT '用户账户的状态，是否激活',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `username`(`username` ASC) USING BTREE,
  UNIQUE INDEX `email`(`email` ASC) USING BTREE,
  INDEX `updater`(`updater` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tb_users
-- ----------------------------
INSERT INTO `tb_users` VALUES (1, 'admin', 'fdb1df71cdb845719f9aecd68fb190bca45df8533debc61135462871cc2c223d', 'jiangrongjun2004@163.com', '13509331891', 'rongjun', 'jiang', '2024-09-28', '/faces/20240928-173417-1.png', 1, 21, '2024-09-28 17:33:56', 1, '2025-01-29 16:09:03', 1);

-- ----------------------------
-- Table structure for tb_verification_codes
-- ----------------------------
DROP TABLE IF EXISTS `tb_verification_codes`;
CREATE TABLE `tb_verification_codes`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID，自增',
  `user_id` bigint UNSIGNED NULL DEFAULT NULL COMMENT '关联用户id',
  `username` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户名',
  `password` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '密码',
  `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '邮箱',
  `code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '验证码',
  `purpose` bigint NOT NULL COMMENT '用途',
  `status` bigint NOT NULL DEFAULT 0 COMMENT '状态',
  `trigger_time` datetime NOT NULL COMMENT '创建时间，自动填充',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `email`(`email` ASC) USING BTREE,
  INDEX `verification_user`(`user_id` ASC) USING BTREE,
  CONSTRAINT `verification_user` FOREIGN KEY (`user_id`) REFERENCES `tb_users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '存储用户邮箱验证码与信息表用' ROW_FORMAT = DYNAMIC;

SET FOREIGN_KEY_CHECKS = 1;