SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

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
  `creator` bigint NOT NULL COMMENT '创建人',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `updater` bigint NOT NULL COMMENT '更新人',
  `updated_at` datetime NOT NULL COMMENT '更新时间',
  `is_del` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `creator`(`creator` ASC) USING BTREE,
  INDEX `updater`(`updater` ASC) USING BTREE,
  CONSTRAINT `banners_creator` FOREIGN KEY (`creator`) REFERENCES `tb_users` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `banners_updater` FOREIGN KEY (`updater`) REFERENCES `tb_users` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '轮播图表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for tb_templates
-- ----------------------------
DROP TABLE IF EXISTS `tb_templates`;
CREATE TABLE `tb_templates`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '模板名称',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '模板内容',
  `channel` int NOT NULL COMMENT '通道',
  `purpose` int NOT NULL COMMENT '用途',
  `creator` bigint NOT NULL COMMENT '创建人',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` bigint NOT NULL COMMENT '更新人',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_del` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否删除, 逻辑删除标志位',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `unique_channel_purpose`(`channel` ASC, `purpose` ASC , `is_del` ASC) USING BTREE,
  INDEX `idx_name`(`name` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '模板表' ROW_FORMAT = Dynamic;


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
  INDEX `verification_user`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '存储用户邮箱验证码' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for tb_messages
-- ----------------------------
DROP TABLE IF EXISTS `tb_messages`;
CREATE TABLE `tb_messages`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `is_read` tinyint(1) NOT NULL,
  `create_time` datetime NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_notice_logs
-- ----------------------------
DROP TABLE IF EXISTS `tb_notice_logs`;
CREATE TABLE `tb_notice_logs`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `templateId` bigint NOT NULL DEFAULT NULL COMMENT '通知模板ID',
  `properties` json NULL COMMENT '通知模板属性',
  `receiver` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '通知接收者',
  `status` int NOT NULL COMMENT '通知状态',
  `trigger_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '触发时间',
  `trigger_person` bigint NOT NULL COMMENT '触发人',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_receiver`(`receiver` ASC) USING BTREE,
  INDEX `idx_templateId`(`templateId` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '通知日志表' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;