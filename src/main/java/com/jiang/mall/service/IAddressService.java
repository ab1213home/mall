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

package com.jiang.mall.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jiang.mall.domain.entity.Address;
import com.jiang.mall.domain.vo.AddressVo;

import java.util.List;


/**
 * <p>
 *  服务类
 * </p>
 *
 * @author jiang
 * @since 2024年9月11日
 */
public interface IAddressService extends IService<Address> {
	/**
	 * 根据用户ID分页查询地址列表
	 *
	 * @param userId 用户ID，用于标识哪个用户的相关地址列表
	 * @param pageNum 页码，用于分页查询，从1开始计数
	 * @param pageSize 每页大小，用于控制返回结果的数量
	 * @return 返回一个分页的地址列表，包括该用户的所有地址
	 */
	List<AddressVo> getAddressList(Long userId, Integer pageNum, Integer pageSize);

	/**
	 * 根据用户ID获取地址数量
	 *
	 * @param userId 用户ID，用于标识用户的唯一性
	 * @return 用户的地址数量
	 */
	Long getAddressNum(Long userId);


	/**
	 * 更新地址信息
	 * <p>
	 * 该方法用于在系统中更新一个地址对象 它提供了一种机制，来确保地址数据的最新和准确性
	 *
	 * @param address   要更新的地址对象 不能为空，包含所有必要的地址信息
	 * @param isDefault 是否将此地址设置为默认地址
	 * @return true 如果更新操作成功；否则返回 false
	 */
	Boolean updateAddress(Address address, boolean isDefault);

	/**
	 * 删除地址
	 *
	 * @param id     地址ID
	 * @param userId 用户ID，用于确认有权删除该地址的用户
	 * @return 删除成功返回true，否则返回false
	 */
	Boolean deleteAddress(Long id, Long userId);

	Boolean insertAddress(Address address, boolean isDefault);

	AddressVo getAddress(Address address);
}
