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
import com.jiang.mall.domain.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;

/**
 * Order的映射接口，继承自BaseMapper<Order>
 * 本接口用于定义与数据库中tb_orders表进行交互的方法，专门用于处理Order实体的CRUD操作
 *
 * @author jiang
 * @email  jiangrongjun2004@163.com
 * @link <a href="https://github.com/ab1213home/mall">https://github.com/ab1213home/mall</a>
 * @apiNote Order的映射接口
 * @version 1.0
 * @since 2024年9月8日
 */
@Mapper
public interface OrderMapper extends BaseMapper<Order> {

	@Select("SELECT user_id FROM tb_orders WHERE id = #{id} LIMIT 1")
	Long selectOneUserIdById(Long id);

	@Select("SELECT SUM(total_amount) AS total_amount FROM tb_orders WHERE order_date BETWEEN #{firstDayOfMonth} AND #{lastDayOfMonth}")
	String getAmount(LocalDate firstDayOfMonth, LocalDate lastDayOfMonth);
}
