
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
-- 表的结构 `tb_categories`
--

CREATE TABLE `tb_categories` (
  `id` bigint UNSIGNED NOT NULL COMMENT '主键ID，自增',
  `parent_id` bigint NOT NULL DEFAULT '0' COMMENT '父级分类ID，默认为 0',
  `level` tinyint(1) NOT NULL DEFAULT '1' COMMENT '分类级别',
  `code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '编码，需唯一',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '分类名称',
  `creator` bigint UNSIGNED NOT NULL COMMENT '创建人',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `updater` bigint UNSIGNED NOT NULL COMMENT '更新人',
  `updated_at` datetime NOT NULL COMMENT '更新时间',
  `is_del` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='商品分类表' ROW_FORMAT=DYNAMIC;
