SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

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
) ENGINE = MyISAM AUTO_INCREMENT = 7112658329048 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '中国行政地区表' ROW_FORMAT = DYNAMIC;

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
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建的时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新的时间',
  `is_del` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否删除 ',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `user`(`user_id` ASC) USING BTREE,
  INDEX `area_code`(`area_code` ASC) USING BTREE,
  INDEX `id`(`id` ASC, `user_id` ASC) USING BTREE,
  CONSTRAINT `addresser_user` FOREIGN KEY (`user_id`) REFERENCES `tb_users` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户地址表' ROW_FORMAT = DYNAMIC;

SET FOREIGN_KEY_CHECKS = 1;