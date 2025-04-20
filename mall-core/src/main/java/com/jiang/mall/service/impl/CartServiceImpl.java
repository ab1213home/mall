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
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiang.mall.config.CoreConfig;
import com.jiang.mall.dao.CartMapper;
import com.jiang.mall.dao.CartRedisMapper;
import com.jiang.mall.domain.cache.UserCache;
import com.jiang.mall.domain.dto.CartDto;
import com.jiang.mall.domain.entity.Cart;
import com.jiang.mall.domain.entity.CartRedis;
import com.jiang.mall.domain.vo.CartVo;
import com.jiang.mall.domain.vo.CheckoutReceiverVo;
import com.jiang.mall.domain.vo.ProductVo;
import com.jiang.mall.service.ICartRedisService;
import com.jiang.mall.service.ICartService;
import com.jiang.mall.service.IProductService;
import com.jiang.mall.service.IUserService;
import com.jiang.mall.util.BeanCopyUtil;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author jiang
 * @since 2024年9月11日
 */
@Service
public class CartServiceImpl extends ServiceImpl<CartMapper, Cart> implements ICartService {

    private static final Logger logger = LoggerFactory.getLogger(CartServiceImpl.class);

    private CartMapper cartMapper;

    @Autowired
    public void setCartMapper(CartMapper cartMapper) {
        this.cartMapper = cartMapper;
    }

    private CartRedisMapper cartRedisMapper;

    @Autowired
    public void setCartRedisMapper(CartRedisMapper cartRedisMapper) {
        this.cartRedisMapper = cartRedisMapper;
    }

    private IUserService userService;

    @Autowired
    public void setUserService(IUserService userService) {
        this.userService = userService;
    }

    private ICartRedisService redisService;

	@Autowired
	private void setRedisService(ICartRedisService redisService) {
		this.redisService = redisService;
	}

    private IProductService productService;

    @Autowired
    public void setProductService(IProductService productService) {
        this.productService = productService;
    }

    private CoreConfig coreConfig;

    @Autowired
    public void setCoreConfig(CoreConfig coreConfig) {
        this.coreConfig = coreConfig;
    }

    /**
     * 根据订单删除购物车中的商品
     * 该方法主要用于在用户下单后，更新购物车中相关商品的数量或删除已购买的商品
     *
     * @param sessionId      会话ID，用于获取用户信息
     * @param listCheckoutVo 订单详情列表，包含已购买的商品信息
     */
    @Override
    @Transactional
    public void deleteCartByOrder(String sessionId, List<CheckoutReceiverVo> listCheckoutVo) {
        UserCache user = userService.getUserFromRedis(sessionId);
        if (coreConfig.isCartCacheEnabled()){
            deleteCartByOrderInRedis(user.getId(), listCheckoutVo);
        }else{
            deleteCartByOrderInMySQL(user.getId(), listCheckoutVo);
        }
    }

    private void deleteCartByOrderInRedis(Long userId, List<CheckoutReceiverVo> listCheckoutVo) {
        //Redis不存在该商品该用户的购物车记录，判断是冷数据还是空数据
        Long version_redis = null;
        //尝试获取版本号
        if (redisService.hasVersion(userId)){
            version_redis = redisService.getVersion(userId);
        }
        //获取数据库用户最新版本号
        Long version_mysql = cartRedisMapper.getVersionByUserId(userId);
        if (version_redis == null || version_redis < version_mysql){
            checkCartFromMySQLToRedis(userId, version_mysql);
        }
        List<CartDto> listCartDto = new ArrayList<>();
        for (CheckoutReceiverVo checkoutVo : listCheckoutVo) {
            CartDto cartDto = new CartDto();
            cartDto.setProdId(checkoutVo.getProdId());
            cartDto.setNum(-checkoutVo.getNum());
            listCartDto.add(cartDto);
        }
        redisService.setCart(userId,listCartDto);
    }

    private void deleteCartByOrderInMySQL(Long userId, @NotNull List<CheckoutReceiverVo> listCheckoutVo) {
        if (listCheckoutVo.isEmpty()) {
            return;
        }
        // 将订单商品转换为Map，合并相同prodId的数量
        Map<Long, Long> prodIdToNumMap = listCheckoutVo.stream()
            .collect(Collectors.toMap(
                CheckoutReceiverVo::getProdId,
                CheckoutReceiverVo::getNum
            ));
        // 根据用户和商品ID查询购物车
        LambdaQueryWrapper<Cart> queryWrapper = new LambdaQueryWrapper<Cart>()
            .eq(Cart::getUserId, userId)
            .in(Cart::getProdId, prodIdToNumMap.keySet());
        List<Cart> carts = cartMapper.selectList(queryWrapper);

        if (carts.isEmpty()) {
            return;
        }

        List<Cart> cartsToUpdate = new ArrayList<>();
        List<Long> cartIdsToDelete = new ArrayList<>();

        // 计算新数量并分类处理
        for (Cart cart : carts) {
            long checkoutNum = prodIdToNumMap.get(cart.getProdId());
            long newNum = cart.getNum() - checkoutNum;
            if (newNum > 0) {
                cart.setNum(newNum);
                cartsToUpdate.add(cart);
            } else {
                cartIdsToDelete.add(cart.getId());
            }
        }

        // 批量处理数据库操作
        if (!cartsToUpdate.isEmpty()) {
            cartMapper.updateById(cartsToUpdate);
        }
        if (!cartIdsToDelete.isEmpty()) {
            cartMapper.deleteByIds(cartIdsToDelete);
        }
    }

    @Override
    @Transactional
    public List<CartVo> getCartList(String sessionId, Integer pageNum, Integer pageSize) {
        UserCache user = userService.getUserFromRedis(sessionId);
        Page<Cart> cartPage = new Page<>(pageNum, pageSize);
        if (coreConfig.isCartCacheEnabled()){
            return getCartListInRedis(user.getId(), pageNum, pageSize);
        }else{
            return getCartListInMySQL(user.getId(), cartPage);
        }

    }

    private @NotNull List<CartVo> getCartListInRedis(Long userId, Integer pageNum, Integer pageSize) {
        List<CartDto> cartList = redisService.getCart(userId, pageNum, pageSize);
        List<CartVo> cartVos = new ArrayList<>();
        for (CartDto cartDto : cartList) {
            CartVo cartVo = BeanCopyUtil.copyBean(cartDto, CartVo.class);
	        assert cartVo != null;
	        cartVo.setProduct(productService.getProduct(cartDto.getProdId()));
            cartVos.add(cartVo);
        }
        return cartVos;
    }

    private @NotNull List<CartVo> getCartListInMySQL(Long userId, Page<Cart> cartPage) {
        LambdaQueryWrapper<Cart> queryWrapper = new LambdaQueryWrapper<Cart>().eq(Cart::getUserId, userId);
        List<Cart> carts = cartMapper.selectPage(cartPage, queryWrapper).getRecords();
        return cartToCartVo(carts);
    }

    private @NotNull List<CartVo> cartToCartVo(@NotNull List<Cart> carts) {
        List<CartVo> cartVos = new ArrayList<>();
        for (Cart cart : carts) {
            CartVo cartVo = BeanCopyUtil.copyBean(cart, CartVo.class);
            assert cartVo != null;
            // 根据购物车项中的产品ID，查询产品信息
            ProductVo product = productService.getProduct(cart.getProdId());
            cartVo.setProduct(product);
            cartVos.add(cartVo);
        }
        return cartVos;
    }

    /**
     * 重写获取购物车商品数量的方法
     *
     * @param sessionId 会话ID，用于识别用户
     * @return 返回购物车中的商品数量
     */
    @Override
    @Transactional
    public Long getCartNum(String sessionId) {
        UserCache user = userService.getUserFromRedis(sessionId);
        if (coreConfig.isCartCacheEnabled()){
            return getCartNumInRedis(user.getId());
        }else{
            return getCartNumInMySQL(user.getId());
        }
    }

    private Long getCartNumInMySQL(Long userId) {
        // 创建查询包装器，用于查询条件的设置
        QueryWrapper<Cart> queryWrapper = new QueryWrapper<>();
        // 设置查询条件，查找特定用户ID的购物车记录
        queryWrapper.eq("user_id", userId);
        // 返回购物车列表的大小，即商品数量
        return cartMapper.selectCount(queryWrapper);
    }

    private Long getCartNumInRedis(Long userId) {
        return (long) redisService.getCartNum(userId);
    }

    /**
     * 插入购物车功能
     *
     * @param productId 产品ID
     * @param num       购买数量
     * @param sessionId 用户会话ID
     * @return 布尔值，表示购物车记录是否成功插入或更新
     */
    @Override
    @Transactional
    public boolean insertOrUpdateCart(Long productId, Long num, String sessionId) {
        // 从Redis中获取用户信息
        UserCache user = userService.getUserFromRedis(sessionId);
        // 根据商品ID和用户ID查询购物车记录
        if (coreConfig.isCartCacheEnabled()){
            return insertOrUpdateCartToRedis(productId, num, user.getId());
        }else{
            //购物车缓存mysql
            return insertOrUpdateCartToMySQL(productId, num, user.getId());
        }
    }

    private @NotNull Boolean insertOrUpdateCartToRedis(Long productId, Long num, Long userId) {
        //购物车缓存redis
        if (redisService.hasCart(userId)){
            //Redis存在该商品该用户的购物车记录，则更新购物车记录
            return redisService.setCart(userId, productId, num);
        }else{
            //Redis不存在该商品该用户的购物车记录，判断是冷数据还是空数据
            Long version_redis = null;
            //尝试获取版本号
            if (redisService.hasVersion(userId)){
                version_redis = redisService.getVersion(userId);
            }
            //获取数据库用户最新版本号
            Long version_mysql = cartRedisMapper.getVersionByUserId(userId);
            if (version_mysql == null){
                checkCartFromRedisToMySQL(userId, version_redis);
                return redisService.setCart(userId, productId, num);
            }else if (version_redis == null || version_redis <= version_mysql){
                //数据库版本号大于redis版本号
                //以数据库为基准
                //冷数据
                checkCartFromMySQLToRedis(userId, version_mysql);
                return redisService.setCart(userId, productId, num);
            }else {
                //redis版本号大于等于数据库版本号
                //则以redis为基准
                return redisService.setCart(userId, productId, num);
            }
        }
    }

    private @NotNull Boolean insertOrUpdateCartToMySQL(Long productId, Long num, Long userId) {
        return cartMapper.insertOrUpdateCart(productId, num, userId)>0;
    }

    /**
     * 删除购物车项
     * <p>
     * 此方法旨在删除指定的购物车项它首先确保只有该项的拥有者才能删除它，
     * 通过比较购物车项关联的用户ID和当前会话标识对应的用户ID如果两者不匹配，
     * 方法返回null，表示删除操作未经授权如果用户ID匹配，则执行删除操作，
     * 并返回一个布尔值，指示删除操作是否成功
     *
     * @param productId 购物车项的唯一标识符
     * @param sessionId 当前用户的会话标识符，用于识别用户
     * @return 如果删除成功，返回true；如果删除失败或未经授权，返回false或null
     */
    @Override
    @Transactional
    public Boolean deleteCart(Long productId, String sessionId) {
        // 从Redis中获取用户信息
        UserCache user = userService.getUserFromRedis(sessionId);
        // 根据商品ID和用户ID查询购物车记录
        if (coreConfig.isCartCacheEnabled()){
            return deleteCartInRedis(productId, user.getId());
        }else{
            //购物车缓存mysql
            return deleteCartInMySQL(productId, user.getId());
        }
    }

    private @NotNull Boolean deleteCartInMySQL(Long productId, Long userId) {
        return cartMapper.deleteByProdIdAndUserId(productId, userId) > 0;
    }

    private @NotNull Boolean deleteCartInRedis(Long productId, Long userId) {
        //购物车缓存redis
        if (redisService.hasCart(userId)){
            //Redis存在该商品该用户的购物车记录，则更新购物车记录
            return redisService.deleteCart(userId, productId);
        }else{
            //Redis不存在该商品该用户的购物车记录，判断是冷数据还是空数据
            Long version_redis = null;
            //尝试获取版本号
            if (redisService.hasVersion(userId)){
                version_redis = redisService.getVersion(userId);
            }
            //获取数据库用户最新版本号
            Long version_mysql = cartRedisMapper.getVersionByUserId(userId);
            if (version_redis == null || version_redis <= version_mysql){
                //数据库版本号大于redis版本号
                //以数据库为基准
                //冷数据
                checkCartFromMySQLToRedis(userId ,version_mysql);
                return redisService.deleteCart(userId, productId);
            }else {
                //redis版本号大于等于数据库版本号
                //则以redis为基准
                return redisService.deleteCart(userId, productId);
            }
        }
    }

    @Override
    @Transactional
    public void cleanAllCart() {
        cartMapper.cleanAllCart();
        redisService.cleanAllCart();
        cartRedisMapper.cleanAllCart();
    }


    @Override
    @Transactional
    public void checkCartFromMySQLToRedis() {
        List<Long> listUserId = cartRedisMapper.getUserIdList();
        for (Long userId : listUserId) {
            Long version_mysql = cartRedisMapper.getVersionByUserId(userId);
            Long version_redis = null;
            if (redisService.hasVersion(userId)){
                version_redis = redisService.getVersion(userId);
            }
            if (version_redis == null || version_redis <= version_mysql){
                checkCartFromMySQLToRedis(userId, version_mysql);
            }else {
                //redis版本号大于数据库版本号
                logger.info("Redis版本号大于MySQL数据库版本号，{}用户购物车缓存已同步", userId);
            }
        }
    }

    private void checkCartFromMySQLToRedis(Long userId ,Long version) {
        QueryWrapper<CartRedis> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.eq("version", version);
        CartRedis cartRedis = cartRedisMapper.selectOne(queryWrapper);
        if (!Objects.equals(cartRedis.getContent(), "")){
            List<CartDto> cartDtos = JSON.parseArray(cartRedis.getContent(), CartDto.class);
            redisService.initCart(userId, cartDtos, version);
        }else{
            redisService.initCart(userId,version);
        }
    }



    @Override
    @Transactional
    public void checkCartFromRedisToMySQL() {
        List<Long> listUserId = redisService.getChangeList();
        for (Long userId : listUserId) {
            Long version_redis = redisService.getVersion(userId);
            Long version_mysql = cartRedisMapper.getVersionByUserId(userId);
            if (version_mysql == null) {
                // 处理 version_mysql 为 null 的情况
                logger.warn("用户 {} 的数据库版本号为 null，需要重新同步购物车数据", userId);
                checkCartFromRedisToMySQL(userId, version_redis);
            } else if (version_redis != null && version_redis > version_mysql) {
                // 如果 version_redis 不为 null 且大于 version_mysql，则进行同步
                logger.debug("用户 {} 购物车 Redis 版本号大于数据库版本号，开始同步", userId);
                checkCartFromRedisToMySQL(userId, version_redis);
            } else {
                // redis 版本号小于等于数据库版本号
                logger.error("用户 {} 购物车 Redis 版本号小于等于数据库版本号，无需同步", userId);
            }
        }
    }


    private void checkCartFromRedisToMySQL(Long userId, Long version) {
        List<CartDto> cartDtos = redisService.getCart(userId);
        CartRedis cartRedis = new CartRedis();
        if(!cartDtos.isEmpty()){
            String content = JSON.toJSONString(cartDtos);
            cartRedis.setContent(content);
        }else{
            cartRedis.setContent("");
        }
        cartRedis.setUserId(userId);
        cartRedis.setVersion(version);
        if (cartRedisMapper.insert(cartRedis) > 0){
            logger.info("用户 {} 购物车Redis数据同步到MySQL成功", userId);
            redisService.removeChangeList(userId);
        }else {
            logger.error("用户 {} 购物车Redis数据同步到MySQL失败", userId);
        }
    }

}
