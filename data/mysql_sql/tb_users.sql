
/*
 * Copyright (c) 2024 Jiang RongJun
 * Jiang Mall is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan
 * PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY
 * KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

-- --------------------------------------------------------

--
-- 表的结构 `tb_users`
--

CREATE TABLE `tb_users` (
  `id` bigint UNSIGNED NOT NULL COMMENT '用户唯一编号',
  `username` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户名',
  `password` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '密码',
  `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户的电子邮件地址',
  `phone` varchar(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '用户的电话号码',
  `first_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '用户的名字',
  `last_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '用户的姓氏',
  `birth_date` date DEFAULT NULL COMMENT '用户的生日',
  `img` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '用户的头像',
  `default_address_id` bigint UNSIGNED DEFAULT NULL COMMENT '默认地址编号',
  `role_id` int NOT NULL DEFAULT '1' COMMENT '用户的角色ID，用于权限管理',
  `created_at` datetime NOT NULL COMMENT '创建的时间戳',
  `updater` bigint UNSIGNED NOT NULL COMMENT '更新人',
  `updated_at` datetime NOT NULL COMMENT '更新的时间戳',
  `is_active` tinyint(1) NOT NULL DEFAULT '0' COMMENT '用户账户的状态，是否激活'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户表' ROW_FORMAT=DYNAMIC;
