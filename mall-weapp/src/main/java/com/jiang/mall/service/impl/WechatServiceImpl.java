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

	/**
	 * 微信登录方法
	 * 该方法通过微信提供的code和客户端IP进行登录，返回登录结果和用户信息
	 *
	 * @param code 微信返回的临时登录凭证
	 * @param clientIp 客户端的IP地址
	 * @return 包含登录结果和用户信息的Map对象
	 */
	@Override
	@Transactional
	public Map<String, Object> login(String code, String clientIp) {
	    // 创建一个Map对象用于存储返回的结果
	    Map<String, Object> map = new HashMap<>();

	    // 构建微信接口的URL
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

	    // 检查微信接口是否调用成功
	    if (response == null) {
	        map.put("message", "微信接口调用失败");
	        logger.error("微信接口调用失败");
	        return map;
	    }

	    // 解析微信接口返回的JSON数据
	    JSONObject json = JSON.parseObject(response);

	    // 检查微信接口返回的数据中是否包含错误码
	    if (json.containsKey("errcode")) {
	        map.put("message", "微信接口调用失败:" + json.getString("errmsg"));
	        map.put("errcode", json.getString("errcode"));
	        logger.error("微信接口调用失败:{}", json.getString("errmsg"));
	        return map;
	    }

	    // 获取微信用户的openid
	    String openid = json.getString("openid");

	    // 如果JSON数据中包含unionid，则使用unionid作为openid
	    if (json.containsKey("unionid")) {
	        logger.debug("UnionID: {}", json.getString("unionid"));
	        openid = json.getString("unionid");
	    }

	    // 根据providerType和providerUserId查询用户OAuth信息
	    QueryWrapper<UserOauth> queryWrapper = new QueryWrapper<>();
	    queryWrapper.eq("provider_type", OAuthProvider.WECHAT.getKey());
	    queryWrapper.eq("provider_user_id", openid);
	    UserOauth userOauth = userOauthMapper.selectOne(queryWrapper);

	    // 如果用户OAuth信息不存在，表示未绑定微信
	    if (userOauth == null) {
	        // 生成一个随机的token
	        String token = UUID.randomUUID().toString();
	        map.put("message", "未绑定微信");
	        map.put("token", token);
	        map.put("state", "bind");
	        logger.info("未绑定微信");

	        // 创建一个WechatCache对象，用于缓存微信用户信息
	        WechatCache cache = new WechatCache();
	        cache.setOpenid(openid);
	        cache.setSessionKey(json.getString("session_key"));
	        if (json.containsKey("unionid")) {
	            cache.setUnionid(json.getString("unionid"));
	        }

	        // 将微信用户信息缓存到Redis中
	        redisService.setWechatUser(cache, token);
	        return map;
	    } else {
	        // 生成一个随机的token
	        String token = UUID.randomUUID().toString();
	        map.put("message", "登录成功");
	        map.put("state", "login");
	        map.put("token", token);

	        // 调用userService的wechatLogin方法进行微信登录
	        UserCache user = userService.wechatLogin(userOauth.getUserId(), clientIp);

	        // 将用户信息缓存到Redis中
	        redisService.setUser(token, user);

	        // 将用户信息转换为UserVo对象，并添加到返回的Map中
	        UserVo userVo = BeanCopyUtil.copyBean(user, UserVo.class);
	        map.put("userInfo", userVo);
	        return map;
	    }
	}

	/**
	 * 绑定微信用户到系统用户
	 *
	 * @param username 用户名
	 * @param password 密码
	 * @param token 微信临时登录凭证
	 * @param clientIp 客户端IP地址
	 * @return 包含绑定结果和用户信息的映射表
	 */
	@Override
	@Transactional
	public Map<String, Object> bind(String username, String password, String token, String clientIp) {
	    Map<String, Object> map = new HashMap<>();
	    // 检查微信令牌是否有效
	    if (redisService.hasWechatUser(token)){
	        // 从缓存中获取微信用户信息
	        WechatCache cache = redisService.getWechatUser(token);
	        String openid = cache.getOpenid();
	        // 如果存在unionid，则使用unionid作为标识
	        if (cache.getUnionid()!=null){
	            openid = cache.getUnionid();
	        }
	        // 调用用户服务进行微信登录
	        UserCache user =userService.wechatLogin(username,password,clientIp);
	        if (user ==null){
	            // 用户名或密码错误
	            map.put("message","用户名或密码错误");
	            return map;
	        }
			// 判断用户是否已经绑定了微信
		    QueryWrapper<UserOauth> queryWrapper_user = new QueryWrapper<>();
			queryWrapper_user.eq("provider_type", OAuthProvider.WECHAT.getKey());
			queryWrapper_user.eq("provider_user_id",openid);
			if (userOauthMapper.selectCount(queryWrapper_user)>0L){
	            // 该微信已经被绑定
	            map.put("message","该微信已经被绑定");
	            return map;
	        }
			QueryWrapper<UserOauth> queryWrapper_oauth = new QueryWrapper<>();
			queryWrapper_oauth.eq("user_id",user.getId());
			queryWrapper_oauth.eq("provider_type",OAuthProvider.WECHAT.getKey());
			if (userOauthMapper.selectCount(queryWrapper_oauth)>0L){
	            // 已经绑定了微信
	            map.put("message","用户已经绑定了微信");
	            return map;
	        }
	        // 生成新的令牌
	        String token_new = UUID.randomUUID().toString();
	        // 创建用户OAuth信息对象
	        UserOauth userOauth = new UserOauth();
	        userOauth.setUserId(user.getId());
	        userOauth.setProviderType(OAuthProvider.WECHAT.getKey());
	        userOauth.setProviderUserId(openid);
	        userOauth.setAnnotations(JSON.toJSONString(cache));
	        userOauth.setHash(SecureUtil.sha256Hex(JSON.toJSONString(cache)));
	        // 插入用户OAuth信息到数据库
	        if (userOauthMapper.insert(userOauth)>0){
	            // 登录成功，记录登录记录
	            // Map<String,Object> map = new HashMap<>();
	            // map.put("provider", "wechat");
	            // userLogService.defaultLog(user.getUsername(), clientIp, "wechat", UserStatus.SUCCESS_LOGIN, map);
	            // 更新缓存，删除微信临时信息
	            redisService.setUser(token,user);
	            redisService.deleteWechatUser(token);
	            map.put("token", token_new);
	            // 将用户信息添加到返回结果中
	            UserVo userVo = BeanCopyUtil.copyBean(user, UserVo.class);
	            map.put("userInfo", userVo);
	            return map;
	        }else {
	            // 绑定失败，系统错误
	            map.put("message","绑定失败,系统错误");
	            return map;
	        }
	    }else{
	        // 微信信息不存在或过期
	        map.put("message","微信信息不存在或过期，请重新绑定" );
	        logger.error("微信信息不存在或过期，请重新绑定");
	        return map;
	    }
	}

	/**
	 * 检查用户登录状态并获取用户信息
	 *
	 * @param token 用户登录令牌，用于标识用户会话
	 * @return 返回一个包含用户登录状态和信息的Map对象
	 *         如果用户已登录，包含键"state"值为true和用户信息"userInfo"；
	 *         如果用户未登录，包含键"state"值为false和提示消息"message"
	 */
	@Override
	public Map<String, Object> check(String token) {
	    Map<String, Object> map = new HashMap<>();
	    // 检查Redis中是否存在该用户
	    if (redisService.hasUser(token)){
	        // 刷新用户登录状态，延长会话有效期
	        redisService.refreshUserLoginStatus(token);
	        map.put("state", true);
	        // 从Redis获取用户缓存信息
	        UserCache user = redisService.getUser(token);
	        // 将用户缓存信息转换为用户信息对象
	        UserVo userVo = BeanCopyUtil.copyBean(user, UserVo.class);
	        map.put("userInfo", userVo);
	        return map;
	    }
	    // 如果用户未登录，设置错误提示信息和状态
	    map.put("message","未登录");
	    map.put("state", false);
	    return map;
	}

	/**
	 * 从Redis中获取用户信息
	 *
	 * @param token 用户的令牌，用于在Redis中唯一标识用户信息
	 * @return UserCache对象，包含从Redis中获取的用户信息如果未找到，则返回null
	 */
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

	@Override
	public void setCheckoutList(List<CheckoutReceiverVo> listCheckoutVo, String token) {
		UserCache user = getUserFromRedis(token);
		orderService.setCheckoutList(listCheckoutVo, user.getId());
	}

	@Override
	public List<CheckoutVo> getCheckoutList(String token) {
		UserCache user = getUserFromRedis(token);
		return orderService.getCheckoutList(user.getId());
	}


}
