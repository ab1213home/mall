
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
-- 表的结构 `tb_user_records`
--

CREATE TABLE `tb_user_records` (
  `id` bigint UNSIGNED NOT NULL COMMENT '日志主键，自动增长',
  `ip` varchar(45) COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户IP地址',
  `fingerprint` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '浏览器指纹',
  `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '登录使用的用户名或者邮箱',
  `old_email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '旧邮箱',
  `user_id` bigint UNSIGNED DEFAULT NULL COMMENT '关联用户id',
  `state` tinyint(1) NOT NULL COMMENT '状态',
  `trigger_time` datetime NOT NULL COMMENT '触发时间',
  `updater` bigint UNSIGNED DEFAULT NULL COMMENT '触发人'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户日志';
