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
import org.apache.ibatis.annotations.*;

import java.util.List;

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

	@Select("SELECT * FROM tb_carts WHERE prod_id = #{productId} AND user_id = #{userId} LIMIT 1")
	Cart selectOneByProdIdAndUserId(Long productId, Long userId);

	@Update("UPDATE tb_carts SET num = num + #{num} WHERE id = #{id}")
	int updateNumById(Long id, Long num);

	@Select("SELECT user_id FROM tb_carts WHERE id = #{id} LIMIT 1")
	Long selectUserIdById(Long id);

	@Update("UPDATE tb_carts SET num =#{num} WHERE id = #{id}")
	void setNumById(Long id, Long num);

	@Insert("INSERT INTO tb_carts (prod_id, num, user_id) VALUES (#{productId}, #{num}, #{userId})")
	void insert(Long productId, Long num, Long userId);

	@Select("SELECT * FROM tb_carts WHERE prod_id = #{productId} AND user_id = #{userId} AND version = #{version} LIMIT 1")
	Cart selectOneByProdIdAndUserIdAndVersion(Long productId, Long userId, Long version);

	@Select("SELECT MAX(version) AS version FROM tb_carts WHERE user_id = #{userId}")
	Long getVersionByUserId(Long userId);

	@Delete("DELETE FROM tb_carts WHERE prod_id = #{productId} AND user_id = #{userId}")
	int deleteByProdIdAndUserId(Long productId, Long userId);

	@Insert("INSERT INTO tb_carts (prod_id, num, user_id, version) VALUES (#{productId}, #{num}, #{userId}, #{version})")
	int checkCart(Long userId, Long productId, Long num, Long version);

	@Select("SELECT user_id FROM tb_carts GROUP BY user_id")
	List<Long> selectUserIdList();
}
