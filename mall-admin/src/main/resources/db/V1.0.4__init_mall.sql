SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for tb_categories
-- ----------------------------
DROP TABLE IF EXISTS `tb_categories`;
CREATE TABLE `tb_categories`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID，自增',
  `parent_id` bigint NOT NULL DEFAULT 0 COMMENT '父级分类ID，默认为 0',
  `level` tinyint NOT NULL DEFAULT 1 COMMENT '分类层级（1-一级分类 2-二级分类）',
  `sort` int NOT NULL DEFAULT 0 COMMENT '分类排序',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '分类名称',
  `creator` bigint NOT NULL COMMENT '创建人',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `updater` bigint NOT NULL COMMENT '更新人',
  `updated_at` datetime NOT NULL COMMENT '更新时间',
  `is_del` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `category_creator`(`creator` ASC) USING BTREE,
  INDEX `category_updater`(`updater` ASC) USING BTREE,
  INDEX `category_parent_id`(`parent_id` ASC) USING BTREE,
  CONSTRAINT `category_creator` FOREIGN KEY (`creator`) REFERENCES `tb_users` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `category_updater` FOREIGN KEY (`updater`) REFERENCES `tb_users` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1557 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '商品分类表' ROW_FORMAT = DYNAMIC;

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
  `attributes` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '商品属性，通过JSON格式存储',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '商品描述',
  `properties` json NULL COMMENT '属性',
  `status` int NOT NULL DEFAULT 4 COMMENT '商品状态，1-上架，2-下架，3-审核中，4-编辑中',
  `creator` bigint NOT NULL COMMENT '创建人',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `updater` bigint NOT NULL COMMENT '更新人 ',
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
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '商品表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for tb_shop_staffs
-- ----------------------------
DROP TABLE IF EXISTS `tb_shop_staffs`;
CREATE TABLE `tb_shop_staffs`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `shop_id` bigint UNSIGNED NOT NULL COMMENT '店铺ID',
  `user_id` bigint UNSIGNED NOT NULL COMMENT '员工用户ID',
  `permission` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '员工权限',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `creator` bigint  NOT NULL COMMENT '创建人',
  `updater` bigint  NOT NULL COMMENT '更新人',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_shop_id`(`shop_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '店铺员工关系表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_shops
-- ----------------------------
DROP TABLE IF EXISTS `tb_shops`;
CREATE TABLE `tb_shops`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '店铺ID',
  `user_id` bigint UNSIGNED NOT NULL COMMENT '店主用户ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '店铺名称',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT NULL COMMENT '店铺描述',
  `logo` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT NULL COMMENT '店铺LOGO URL',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '状态：0-正常 1-已关闭 2-审核中',
  `creator` bigint NOT NULL COMMENT '创建人',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` bigint NOT NULL COMMENT '更新人',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_del` tinyint NOT NULL DEFAULT 0 COMMENT '删除标记：0-未删除 1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '店铺表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_orderlists
-- ----------------------------
DROP TABLE IF EXISTS `tb_orderlists`;
CREATE TABLE `tb_orderlists`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '订单明细ID',
  `order_id` bigint UNSIGNED NOT NULL COMMENT '订单ID',
  `prod_id` bigint UNSIGNED NOT NULL COMMENT '商品快照ID',
  `num` int NOT NULL COMMENT '数量',
  `is_del` tinyint(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除标志',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `order`(`order_id` ASC) USING BTREE,
  INDEX `orderlists_prod`(`prod_id` ASC) USING BTREE,
  CONSTRAINT `orderlists_order` FOREIGN KEY (`order_id`) REFERENCES `tb_orders` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `orderlists_prod` FOREIGN KEY (`prod_id`) REFERENCES `tb_product_snapshots` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 15 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '订单明细表' ROW_FORMAT = DYNAMIC;

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
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '订单表' ROW_FORMAT = DYNAMIC;

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
  `creator` bigint NOT NULL COMMENT '创建人',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `is_del` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `creator`(`creator` ASC) USING BTREE,
  INDEX `prod`(`prod_id` ASC) USING BTREE,
  CONSTRAINT `tb_product_snapshots_ibfk_1` FOREIGN KEY (`prod_id`) REFERENCES `tb_products` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `tb_product_snapshots_ibfk_2` FOREIGN KEY (`creator`) REFERENCES `tb_users` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '商品快照表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for tb_carts
-- ----------------------------
DROP TABLE IF EXISTS `tb_carts`;
CREATE TABLE `tb_carts`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT,
  `prod_id` bigint UNSIGNED NOT NULL COMMENT '商品id',
  `num` bigint UNSIGNED NOT NULL COMMENT '商品数量',
  `user_id` bigint UNSIGNED NOT NULL COMMENT '用户id',
  `version` bigint UNSIGNED NOT NULL DEFAULT 0 COMMENT '版本号，用于redis缓存同步锁',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_prod_user_version`(`prod_id` ASC, `user_id` ASC, `version` ASC) USING BTREE,
  INDEX `idx_user`(`user_id` ASC) USING BTREE,
  INDEX `idx_prod`(`prod_id` ASC) USING BTREE,
  CONSTRAINT `carts_prod` FOREIGN KEY (`prod_id`) REFERENCES `tb_products` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `carts_user` FOREIGN KEY (`user_id`) REFERENCES `tb_users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '购物车表' ROW_FORMAT = DYNAMIC;

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
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '收藏表' ROW_FORMAT = DYNAMIC;

SET FOREIGN_KEY_CHECKS = 1;