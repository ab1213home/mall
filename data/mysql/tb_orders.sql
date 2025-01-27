
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
-- 表的结构 `tb_orders`
--

CREATE TABLE `tb_orders` (
  `id` bigint UNSIGNED NOT NULL COMMENT '订单ID',
  `address_id` bigint UNSIGNED NOT NULL COMMENT '地址ID',
  `user_id` bigint UNSIGNED NOT NULL COMMENT '用户ID',
  `date` datetime NOT NULL COMMENT '下单日期',
  `total_amount` decimal(10,2) NOT NULL COMMENT '总金额',
  `status` tinyint(1) NOT NULL COMMENT '订单状态',
  `payment_method` tinyint(1) NOT NULL COMMENT '支付方式',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `updated_at` datetime NOT NULL COMMENT '更新时间',
  `is_del` tinyint(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除标志'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='订单表' ROW_FORMAT=DYNAMIC;
