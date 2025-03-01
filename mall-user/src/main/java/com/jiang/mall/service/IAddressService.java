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

	AddressVo getAddress(Address address);

	/**
	 * 根据会话ID获取用户的地址列表
	 *
	 * @param sessionId 会话ID，用于识别用户
	 * @param pageNum 页码，表示请求的地址列表的页数
	 * @param pageSize 页面大小，表示每页地址的数量
	 * @return 返回一个包含地址信息的列表
	 */
	List<AddressVo> getAddressList(String sessionId, Integer pageNum, Integer pageSize);

	/**
	 * 获取用户的地址数量
	 *
	 * @param sessionId 会话ID，用于从Redis中获取用户信息
	 * @return 返回用户的地址数量
	 */
	Long getAddressNum(String sessionId);

	/**
	 * 插入新地址
	 * <p>
	 * 此方法用于将一个新的地址对象插入到数据库中，并根据情况更新用户的默认地址
	 * 如果插入成功且指定新地址为默认地址，则同时更新用户信息中的默认地址ID
	 *
	 * @param address 不可为空的地址对象，包含待插入的地址信息
	 * @param isDefault 布尔值，指示新地址是否应设置为默认地址
	 * @param sessionId 用户会话ID，用于从Redis中获取用户信息
	 * @return 返回一个布尔值，表示地址插入操作是否成功
	 */
	Boolean insertAddress(Address address, boolean isDefault, String sessionId);

	/**
	 * 更新用户地址信息
	 *
	 * @param address 地址对象，包含要更新的地址信息，不能为空
	 * @param isDefault 是否将此地址设置为默认地址
	 * @param sessionId 用户会话ID，用于从Redis中获取用户信息
	 * @return 返回一个布尔值，表示地址信息是否更新成功如果用户尝试更新非自己的地址，方法返回null
	 */
	Boolean updateAddress(Address address, boolean isDefault, String sessionId);

	/**
	 * 删除地址信息
	 *
	 * @param id 地址ID
	 * @param sessionId 用户会话ID
	 * @return 删除是否成功，成功返回true，否则返回false
	 */
	Boolean deleteAddress(Long id, String sessionId);
}
