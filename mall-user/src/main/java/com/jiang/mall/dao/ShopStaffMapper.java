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
import com.jiang.mall.domain.dto.ShopPermissionDto;
import com.jiang.mall.domain.entity.ShopStaff;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ShopStaffMapper extends BaseMapper<ShopStaff> {

	@Select("select shop_id,permission FROM tb_shop_staffs WHERE user_id = #{userId}")
	List<ShopPermissionDto> selectShopPermissionByUserId(Long userId);

//	@Select("select shop_id,permission FROM tb_shop_staffs WHERE user_id = #{userId}")
//	Map<Long, String> selectShopPermissionByUserId(Long userId);
}
