
--
-- 转储表的索引
--

--
-- 表的索引 `tb_addresses`
--
ALTER TABLE `tb_addresses`
  ADD PRIMARY KEY (`id`) USING BTREE,
  ADD KEY `user` (`user_id`),
  ADD KEY `area_code` (`area_code`),
  ADD KEY `id` (`id`,`user_id`);

--
-- 表的索引 `tb_banners`
--
ALTER TABLE `tb_banners`
  ADD PRIMARY KEY (`id`) USING BTREE,
  ADD KEY `creator` (`creator`) USING BTREE,
  ADD KEY `updater` (`updater`);

--
-- 表的索引 `tb_carts`
--
ALTER TABLE `tb_carts`
  ADD PRIMARY KEY (`id`) USING BTREE,
  ADD KEY `user` (`user_id`),
  ADD KEY `prod` (`prod_id`);

--
-- 表的索引 `tb_categories`
--
ALTER TABLE `tb_categories`
  ADD PRIMARY KEY (`id`) USING BTREE,
  ADD KEY `creator` (`creator`),
  ADD KEY `updater` (`updater`) USING BTREE;

--
-- 表的索引 `tb_china_administrative_divisions`
--
ALTER TABLE `tb_china_administrative_divisions`
  ADD PRIMARY KEY (`area_code`) USING BTREE,
  ADD KEY `parent` (`parent_code`) USING BTREE;

--
-- 表的索引 `tb_collections`
--
ALTER TABLE `tb_collections`
  ADD PRIMARY KEY (`id`) USING BTREE,
  ADD KEY `user` (`user_id`),
  ADD KEY `prod` (`prod_id`);

--
-- 表的索引 `tb_orderlists`
--
ALTER TABLE `tb_orderlists`
  ADD PRIMARY KEY (`id`) USING BTREE,
  ADD KEY `order` (`order_id`),
  ADD KEY `orderlists_prod` (`prod_id`);

--
-- 表的索引 `tb_orders`
--
ALTER TABLE `tb_orders`
  ADD PRIMARY KEY (`id`) USING BTREE,
  ADD KEY `user` (`user_id`),
  ADD KEY `orders_address` (`address_id`,`user_id`);

--
-- 表的索引 `tb_products`
--
ALTER TABLE `tb_products`
  ADD PRIMARY KEY (`id`) USING BTREE,
  ADD UNIQUE KEY `code` (`code`),
  ADD KEY `category` (`category_id`),
  ADD KEY `creator` (`creator`),
  ADD KEY `updater` (`updater`);
ALTER TABLE `tb_products` ADD FULLTEXT KEY `title` (`title`);

--
-- 表的索引 `tb_product_snapshots`
--
ALTER TABLE `tb_product_snapshots`
  ADD PRIMARY KEY (`id`) USING BTREE,
  ADD KEY `creator` (`creator`),
  ADD KEY `prod` (`prod_id`);

--
-- 表的索引 `tb_users`
--
ALTER TABLE `tb_users`
  ADD PRIMARY KEY (`id`) USING BTREE,
  ADD UNIQUE KEY `username` (`username`) USING BTREE,
  ADD UNIQUE KEY `email` (`email`) USING BTREE,
  ADD KEY `updater` (`updater`);

--
-- 表的索引 `tb_user_records`
--
ALTER TABLE `tb_user_records`
  ADD PRIMARY KEY (`id`),
  ADD KEY `username` (`username`),
  ADD KEY `fingerprint` (`fingerprint`),
  ADD KEY `ip` (`ip`);

--
-- 表的索引 `tb_verification_codes`
--
ALTER TABLE `tb_verification_codes`
  ADD PRIMARY KEY (`id`) USING BTREE,
  ADD KEY `email` (`email`),
  ADD KEY `verification_user` (`user_id`);

--
-- 在导出的表使用AUTO_INCREMENT
--

--
-- 使用表AUTO_INCREMENT `tb_addresses`
--
ALTER TABLE `tb_addresses`
  MODIFY `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键，唯一标识每一条记录';

--
-- 使用表AUTO_INCREMENT `tb_banners`
--
ALTER TABLE `tb_banners`
  MODIFY `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- 使用表AUTO_INCREMENT `tb_carts`
--
ALTER TABLE `tb_carts`
  MODIFY `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- 使用表AUTO_INCREMENT `tb_categories`
--
ALTER TABLE `tb_categories`
  MODIFY `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID，自增';

--
-- 使用表AUTO_INCREMENT `tb_collections`
--
ALTER TABLE `tb_collections`
  MODIFY `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键 ID，自增';

--
-- 使用表AUTO_INCREMENT `tb_orderlists`
--
ALTER TABLE `tb_orderlists`
  MODIFY `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '订单明细ID';

--
-- 使用表AUTO_INCREMENT `tb_orders`
--
ALTER TABLE `tb_orders`
  MODIFY `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '订单ID';

--
-- 使用表AUTO_INCREMENT `tb_products`
--
ALTER TABLE `tb_products`
  MODIFY `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- 使用表AUTO_INCREMENT `tb_product_snapshots`
--
ALTER TABLE `tb_product_snapshots`
  MODIFY `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '快照id';

--
-- 使用表AUTO_INCREMENT `tb_users`
--
ALTER TABLE `tb_users`
  MODIFY `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '用户唯一编号';

--
-- 使用表AUTO_INCREMENT `tb_user_records`
--
ALTER TABLE `tb_user_records`
  MODIFY `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '日志主键，自动增长';

--
-- 使用表AUTO_INCREMENT `tb_verification_codes`
--
ALTER TABLE `tb_verification_codes`
  MODIFY `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID，自增';

--
-- 限制导出的表
--

--
-- 限制表 `tb_addresses`
--
ALTER TABLE `tb_addresses`
  ADD CONSTRAINT `addresser_user` FOREIGN KEY (`user_id`) REFERENCES `tb_users` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE;

--
-- 限制表 `tb_banners`
--
ALTER TABLE `tb_banners`
  ADD CONSTRAINT `banners_creator` FOREIGN KEY (`creator`) REFERENCES `tb_users` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  ADD CONSTRAINT `banners_updater` FOREIGN KEY (`updater`) REFERENCES `tb_users` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE;

--
-- 限制表 `tb_carts`
--
ALTER TABLE `tb_carts`
  ADD CONSTRAINT `carts_prod` FOREIGN KEY (`prod_id`) REFERENCES `tb_products` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `carts_user` FOREIGN KEY (`user_id`) REFERENCES `tb_users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- 限制表 `tb_categories`
--
ALTER TABLE `tb_categories`
  ADD CONSTRAINT `categories_creator` FOREIGN KEY (`creator`) REFERENCES `tb_users` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  ADD CONSTRAINT `categories_updater` FOREIGN KEY (`updater`) REFERENCES `tb_users` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE;

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

--
-- 限制表 `tb_collections`
--
ALTER TABLE `tb_collections`
  ADD CONSTRAINT `collections_prod` FOREIGN KEY (`prod_id`) REFERENCES `tb_products` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `collections_user` FOREIGN KEY (`user_id`) REFERENCES `tb_users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- 限制表 `tb_orderlists`
--
ALTER TABLE `tb_orderlists`
  ADD CONSTRAINT `orderlists_order` FOREIGN KEY (`order_id`) REFERENCES `tb_orders` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `orderlists_prod` FOREIGN KEY (`prod_id`) REFERENCES `tb_product_snapshots` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- 限制表 `tb_orders`
--
ALTER TABLE `tb_orders`
  ADD CONSTRAINT `orders_address` FOREIGN KEY (`address_id`,`user_id`) REFERENCES `tb_addresses` (`id`, `user_id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  ADD CONSTRAINT `orders_user` FOREIGN KEY (`user_id`) REFERENCES `tb_users` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE;

--
-- 限制表 `tb_products`
--
ALTER TABLE `tb_products`
  ADD CONSTRAINT `products_category` FOREIGN KEY (`category_id`) REFERENCES `tb_categories` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  ADD CONSTRAINT `products_creator` FOREIGN KEY (`creator`) REFERENCES `tb_users` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  ADD CONSTRAINT `products_updater` FOREIGN KEY (`updater`) REFERENCES `tb_users` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE;

--
-- 限制表 `tb_product_snapshots`
--
ALTER TABLE `tb_product_snapshots`
  ADD CONSTRAINT `tb_product_snapshots_ibfk_1` FOREIGN KEY (`prod_id`) REFERENCES `tb_products` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  ADD CONSTRAINT `tb_product_snapshots_ibfk_2` FOREIGN KEY (`creator`) REFERENCES `tb_users` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE;

--
-- 限制表 `tb_verification_codes`
--
ALTER TABLE `tb_verification_codes`
  ADD CONSTRAINT `verification_user` FOREIGN KEY (`user_id`) REFERENCES `tb_users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;
