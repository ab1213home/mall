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

import com.jiang.mall.domain.entity.Address;
import com.jiang.mall.domain.vo.*;

import java.util.List;
import java.util.Map;

public interface IWechatService {

	/**
	 * 微信登录方法
	 * 该方法通过微信提供的code和客户端IP进行登录，返回登录结果和用户信息
	 *
	 * @param code 微信返回的临时登录凭证
	 * @param clientIp 客户端的IP地址
	 * @return 包含登录结果和用户信息的Map对象
	 */
	Map<String, Object> login(String code, String clientIp);

	/**
	 * 绑定微信用户到系统用户
	 *
	 * @param username 用户名
	 * @param password 密码
	 * @param token 微信临时登录凭证
	 * @param clientIp 客户端IP地址
	 * @return 包含绑定结果和用户信息的映射表
	 */
	Map<String, Object> bind(String username, String password, String token, String clientIp);

	/**
	 * 检查用户登录状态并获取用户信息
	 *
	 * @param token 用户登录令牌，用于标识用户会话
	 * @return 返回一个包含用户登录状态和信息的Map对象
	 *         如果用户已登录，包含键"state"值为true和用户信息"userInfo"；
	 *         如果用户未登录，包含键"state"值为false和提示消息"message"
	 */
	Map<String, Object> check(String token);

	Long getAddressNum(String token);

	List<AddressVo> getAddressList(String token, Integer pageNum, Integer pageSize);

	boolean insertAddress(Address address, boolean isDefault, String token);

	Boolean updateAddress(Address address, boolean isDefault, String token);

	Boolean deleteAddress(Long id, String token);

	Long getCartNum(String token);

	List<CartVo> getCartList(String token, Integer pageNum, Integer pageSize);

	boolean insertOrUpdateCart(Long productId, Long num, String token);

	Boolean deleteCart(Long productId, String token);

	Boolean insertCollection(Long productId, String token);

	Boolean deleteCollection(Long productId, String token);

	List<CollectionVo> getCollectionList(Integer pageNum, Integer pageSize, String token);

	Long getCollectionNum(String token);

	boolean isCollect(Long productId, String token);

	List<OrderVo> getOrderList(String token, Integer pageNum, Integer pageSize);

	Long getOrderNum(String token);

	OrderVo getOrder(Long id, String token);

	Long newOrder(String token, Long addressId, List<CheckoutReceiverVo> listCheckoutVo);

	void setCheckoutList(List<CheckoutReceiverVo> listCheckoutVo, String token);

	List<CheckoutVo> getCheckoutList(String token);

	void logout(String token);

	ProductSnapshotVo getSnapshot(Long id, String token);
}
