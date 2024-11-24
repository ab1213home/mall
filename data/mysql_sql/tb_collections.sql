
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
-- 表的结构 `tb_collections`
--

CREATE TABLE `tb_collections` (
  `id` bigint UNSIGNED NOT NULL COMMENT '主键 ID，自增',
  `prod_id` bigint UNSIGNED NOT NULL COMMENT '商品 ID',
  `user_id` bigint UNSIGNED NOT NULL COMMENT '用户 ID',
  `created_at` datetime NOT NULL COMMENT '创建时间，自动填充'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='收藏表' ROW_FORMAT=DYNAMIC;
