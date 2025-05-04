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

package com.jiang.mall.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jiang.mall.config.GeneralConfig;
import com.jiang.mall.config.WechatConfig;
import com.jiang.mall.dao.UserOauthMapper;
import com.jiang.mall.domain.cache.UserCache;
import com.jiang.mall.domain.cache.WechatCache;
import com.jiang.mall.domain.entity.Address;
import com.jiang.mall.domain.entity.UserOauth;
import com.jiang.mall.domain.enums.OAuthProvider;
import com.jiang.mall.domain.vo.*;
import com.jiang.mall.service.*;
import com.jiang.mall.util.BeanCopyUtil;
import com.jiang.mall.util.SecureUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class WechatServiceImpl implements IWechatService {

	private static final Logger logger = LoggerFactory.getLogger(WechatServiceImpl.class);

	private final WebClient webClient = WebClient.create();

	private GeneralConfig generalConfig;

	@Autowired
	public void setGeneralConfig(GeneralConfig generalConfig) {
		this.generalConfig = generalConfig;
	}

	private WechatConfig wechatConfig;

	@Autowired
	public void setWeChatConfig(WechatConfig wechatConfig) {
		this.wechatConfig = wechatConfig;
	}

	private IUserService userService;

	@Autowired
	public void setUserService(IUserService userService) {
		this.userService = userService;
	}

	private UserOauthMapper userOauthMapper;

	@Autowired
	public void setUserOauth2Mapper(UserOauthMapper userOauthMapper) {
		this.userOauthMapper = userOauthMapper;
	}

	private IWechatRedisService redisService;

	@Autowired
	public void setRedisService(IWechatRedisService redisService) {
		this.redisService = redisService;
	}

	private IAddressService addressService;

	@Autowired
	public void setAddressService(IAddressService addressService) {
		this.addressService = addressService;
	}

	private ICartService cartService;

    @Autowired
    public void setCartService(ICartService cartService) {
        this.cartService = cartService;
    }

	private ICollectionService collectionService;

	@Autowired
	public void setCollectionService(ICollectionService collectionService) {
		this.collectionService = collectionService;
	}

	private IOrderService orderService;

	@Autowired
	public void setOrderService(IOrderService orderService) {
	    this.orderService = orderService;
	}

	@Override
	@Transactional
	public Map<String, Object> login(String code, String clientIp) {
		Map<String, Object> map = new HashMap<>();
		String url = "https://api.weixin.qq.com/sns/jscode2session" +
			"?appid=" + wechatConfig.getWeChatAppId() +
	        "&secret=" + wechatConfig.getWeChatAppSecret() +
	        "&js_code=" + code +
	        "&grant_type=authorization_code";

	    // 调用微信接口
		String response = webClient
				.get()
				.uri(url)
				.retrieve()
				.bodyToMono(String.class)
				.block();
		if (response==null){
			map.put("message","微信接口调用失败" );
			logger.error("微信接口调用失败");
			return map;
		}
		JSONObject json = JSON.parseObject(response);
		if (json.containsKey("errcode")) {
			map.put("message","微信接口调用失败:"+json.getString("errmsg") );
			map.put("errcode", json.getString("errcode"));
			logger.error("微信接口调用失败:{}",json.getString("errmsg"));
			return map;
		}
		String openid = json.getString("openid");
		if (json.containsKey("unionid")) {
			logger.debug("UnionID: {}", json.getString("unionid"));
			openid = json.getString("unionid");
		}
		QueryWrapper<UserOauth> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("provider_type", OAuthProvider.WECHAT.getKey());
		queryWrapper.eq("provider_user_id", openid);
		UserOauth userOauth = userOauthMapper.selectOne(queryWrapper);
		if (userOauth==null){
			String token = UUID.randomUUID().toString();
			map.put("message","未绑定微信" );
			map.put("token", token);
			map.put("state", "bind");
			logger.info("未绑定微信");
			WechatCache cache = new WechatCache();
			cache.setOpenid(openid);
			cache.setSessionKey(json.getString("session_key"));
			if (json.containsKey("unionid")) {
				cache.setUnionid(json.getString("unionid"));
			}
			redisService.setWechatUser(cache,token);
			return map;
		}else {
			String token = UUID.randomUUID().toString();
			map.put("message","登录成功" );
			map.put("state", "login");
			map.put("token", token);
			UserCache user = userService.wechatLogin(userOauth.getUserId(), clientIp);
			redisService.setUser(token,user);
			UserVo userVo = BeanCopyUtil.copyBean(user, UserVo.class);
			map.put("userInfo", userVo);
			return map;
		}
	}

	@Override
	@Transactional
	public Map<String, Object> bind(String username, String password, String token, String clientIp) {
		Map<String, Object> map = new HashMap<>();
		if (redisService.hasWechatUser(token)){
			WechatCache cache = redisService.getWechatUser(token);
			String openid = cache.getOpenid();
			if (cache.getUnionid()!=null){
				openid = cache.getUnionid();
			}
			UserCache user =userService.wechatLogin(username,password,clientIp);
			if (user ==null){
				map.put("message","用户名或密码错误");
				return map;
			}
			String token_new = UUID.randomUUID().toString();
			UserOauth userOauth = new UserOauth();
			userOauth.setUserId(user.getId());
			userOauth.setProviderType(OAuthProvider.WECHAT.getKey());
			userOauth.setProviderUserId(openid);
			userOauth.setAnnotations(JSON.toJSONString(cache));
			userOauth.setHash(SecureUtil.sha256Hex(JSON.toJSONString(cache)));
			if (userOauthMapper.insert(userOauth)>0){
					// 登录成功，记录登录记录
				//		Map<String,Object> map = new HashMap<>();
				//		map.put("provider", "wechat");
				//		userLogService.defaultLog(user.getUsername(), clientIp, "wechat", UserStatus.SUCCESS_LOGIN, map);
				redisService.setUser(token,user);
				map.put("token", token_new);
				UserVo userVo = BeanCopyUtil.copyBean(user, UserVo.class);
				map.put("userInfo", userVo);
				return map;
			}else {
				map.put("message","绑定失败,系统错误" );
				return map;
			}
		}else{
			map.put("message","微信信息不存在或过期，请重新绑定" );
			logger.error("微信信息不存在或过期，请重新绑定");
			return map;
		}
	}

	@Override
	public Map<String, Object> check(String token) {
		Map<String, Object> map = new HashMap<>();
		if (redisService.hasUser(token)){
			redisService.refreshUserLoginStatus(token);
			map.put("state", true);
			UserCache user = redisService.getUser(token);
			UserVo userVo = BeanCopyUtil.copyBean(user, UserVo.class);
			map.put("userInfo", userVo);
			return map;
		}
		map.put("message","未登录");
		map.put("state", false);
		return map;
	}

	public UserCache getUserFromRedis(String token){
		return redisService.getUser(token);
	}

	@Override
	@Transactional
	public Long getAddressNum(String token) {
		UserCache user = getUserFromRedis(token);
		return addressService.getAddressNum(user.getId());
	}

	@Override
	@Transactional
	public List<AddressVo> getAddressList(String token, Integer pageNum, Integer pageSize) {
		UserCache user = getUserFromRedis(token);
		return addressService.getAddressList(user.getId(), user.getDefaultAddressId(), pageNum, pageSize);
	}

	@Override
	@Transactional
	public boolean insertAddress(Address address, boolean isDefault, String token) {
		UserCache user = getUserFromRedis(token);
		return addressService.insertAddress(address, isDefault, user);
	}

	@Override
	@Transactional
	public Boolean updateAddress(Address address, boolean isDefault, String token) {
		UserCache user = getUserFromRedis(token);
		return addressService.updateAddress(address, isDefault, user);
	}

	@Override
	@Transactional
	public Boolean deleteAddress(Long id, String token) {
		UserCache user = getUserFromRedis(token);
		return addressService.deleteAddress(id, user);
	}

	@Override
	@Transactional
	public Long getCartNum(String token) {
		UserCache user = getUserFromRedis(token);
		return cartService.getCartNum(user.getId());
	}

	@Override
	@Transactional
	public List<CartVo> getCartList(String token, Integer pageNum, Integer pageSize) {
		UserCache user = getUserFromRedis(token);
		return cartService.getCartList(user.getId(), pageNum, pageSize);
	}

	@Override
	@Transactional
	public boolean insertOrUpdateCart(Long productId, Long num, String token) {
		UserCache user = getUserFromRedis(token);
		return cartService.insertOrUpdateCart(productId, num, user.getId());
	}

	@Override
	@Transactional
	public Boolean deleteCart(Long productId, String token) {
		UserCache user = getUserFromRedis(token);
		return cartService.deleteCart(productId, user.getId());
	}

	@Override
	@Transactional
	public Boolean insertCollection(Long productId, String token) {
		UserCache user = getUserFromRedis(token);
		return collectionService.insertCollection(productId, user.getId());
	}

	@Override
	@Transactional
	public Boolean deleteCollection(Long productId, String token) {
		UserCache user = getUserFromRedis(token);
		return collectionService.deleteCollection(productId, user.getId());
	}

	@Override
	@Transactional
	public List<CollectionVo> getCollectionList(Integer pageNum, Integer pageSize, String token) {
		UserCache user = getUserFromRedis(token);
		return collectionService.getCollectionList(pageNum, pageSize, user.getId());
	}

	@Override
	@Transactional
	public Long getCollectionNum(String token) {
		UserCache user = getUserFromRedis(token);
		return collectionService.getCollectionNum(user.getId());
	}

	@Override
	@Transactional
	public boolean isCollect(Long productId, String token) {
		UserCache user = getUserFromRedis(token);
		return collectionService.isCollect(productId, user.getId());
	}

	@Override
	@Transactional
	public List<OrderVo> getOrderList(String token, Integer pageNum, Integer pageSize) {
		UserCache user = getUserFromRedis(token);
		return orderService.getOrderList(user.getId(), pageNum, pageSize);
	}

	@Override
	@Transactional
	public Long getOrderNum(String token) {
		UserCache user = getUserFromRedis(token);
		return orderService.getOrderNum(user.getId());
	}

	@Override
	@Transactional
	public OrderVo getOrder(Long id, String token) {
		UserCache user = getUserFromRedis(token);
		return orderService.getOrder(id, user.getId());
	}

	@Override
	public Long newOrder(String token, Long addressId, List<CheckoutReceiverVo> listCheckoutVo) {
		UserCache user = getUserFromRedis(token);
//		return orderService.
		return 0L;
	}


}
