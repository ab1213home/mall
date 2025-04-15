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

package com.jiang.mall.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jiang.mall.domain.entity.Cart;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

/**
 * Cart的映射接口，继承自BaseMapper<Cart>
 * 本接口用于定义与数据库中tb_carts表进行交互的方法，专门用于处理Cart实体的CRUD操作
 *
 * @author jiang
 * @email  jiangrongjun2004@163.com
 * @link <a href="https://github.com/ab1213home/mall">https://github.com/ab1213home/mall</a>
 * @apiNote Cart的映射接口
 * @version 1.0
 * @since 2024年9月8日
 */
@Mapper
public interface CartMapper extends BaseMapper<Cart> {

	@Insert("INSERT INTO tb_carts (prod_id, num, user_id) VALUES (#{prodId}, #{num}, #{userId}) ON DUPLICATE KEY UPDATE num = num + #{num}")
	int insertOrUpdateCart(Long prodId, Long num, Long userId);

	@Delete("DELETE FROM tb_carts WHERE prod_id = #{prodId} AND user_id = #{userId}")
	int deleteByProdIdAndUserId(Long prodId, Long userId);

	@Delete("DELETE FROM tb_carts")
	void cleanAllCart();
}
