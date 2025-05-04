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

	Map<String, Object> login(String code, String clientIp);

	Map<String, Object> bind(String username, String password, String token, String clientIp);

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
}
