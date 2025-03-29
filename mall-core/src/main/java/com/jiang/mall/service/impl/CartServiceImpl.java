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

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiang.mall.config.CoreConfig;
import com.jiang.mall.dao.CartMapper;
import com.jiang.mall.domain.cache.UserCache;
import com.jiang.mall.domain.dto.CartDto;
import com.jiang.mall.domain.entity.Cart;
import com.jiang.mall.domain.vo.CartVo;
import com.jiang.mall.domain.vo.CheckoutVo;
import com.jiang.mall.domain.vo.ProductVo;
import com.jiang.mall.service.*;
import com.jiang.mall.util.BeanCopyUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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

    private IUserService userService;

    @Autowired
    public void setUserService(IUserService userService) {
        this.userService = userService;
    }

    private ICartRedisService cartRedisService;

	@Autowired
	private void setCartRedisService(ICartRedisService cartRedisService) {
		this.cartRedisService = cartRedisService;
	}

    private ICheckoutRedisService checkoutRedisService;

    @Autowired
    public void setCheckoutRedisService(ICheckoutRedisService checkoutRedisService) {
        this.checkoutRedisService = checkoutRedisService;
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
     * @return 如果成功更新购物车则返回true，否则返回false
     */
    @Override
    public Boolean deleteCartByOrder(String sessionId, List<CheckoutVo> listCheckoutVo) {
        UserCache user = userService.getUserFromRedis(sessionId);
        if (coreConfig.isCartCacheEnabled()){
            return deleteCartByOrderInRedis(user.getId(), listCheckoutVo);
        }else{
            return deleteCartByOrderInMySQL(user.getId(), listCheckoutVo);
        }
    }

    private @NotNull Boolean deleteCartByOrderInRedis(Long userId, List<CheckoutVo> listCheckoutVo) {
        //Redis不存在该商品该用户的购物车记录，判断是冷数据还是空数据
        Long version_redis = null;
        //尝试获取版本号
        if (cartRedisService.hasVersion(userId)){
            version_redis = cartRedisService.getVersion(userId);
        }
        //获取数据库用户最新版本号
        Long version_mysql = cartMapper.getVersionByUserId(userId);
        if (version_redis == null || version_redis < version_mysql){
            checkCartFormMySQLToRedis(userId, version_mysql);
        }
        List<CartDto> cartList = cartRedisService.getCart(userId);
        for (CartDto cartDto : cartList) {
            // 遍历订单详情，对比购物车中的商品
            for (CheckoutVo checkoutVo : listCheckoutVo) {
                // 如果购物车商品ID与订单中的商品ID匹配
                if (cartDto.getProdId().equals(checkoutVo.getProduct().getId())) {
                    // 计算购物车中商品的新数量
                    cartRedisService.setCart(userId, cartDto.getProdId(), - checkoutVo.getNum());
                }
            }
        }
        return true;
    }

    private @NotNull Boolean deleteCartByOrderInMySQL(Long userId, List<CheckoutVo> listCheckoutVo) {
        // 根据购物车商品ID列表查询购物车商品信息
//        LambdaQueryWrapper<Cart> queryWrapper = new LambdaQueryWrapper<Cart>().in(Cart::getId, listCartId);
        QueryWrapper<Cart> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        List<Cart> carts = cartMapper.selectList(queryWrapper);
        // 如果没有找到对应的购物车商品，直接返回成功
        if (carts.isEmpty()) {
            return true;
        }
        // 遍历查询到的购物车商品
        for (Cart cart : carts) {
            // 检查购物车商品是否属于当前用户，如果不属于则返回失败
            if (!cart.getUserId().equals(userId)){
                return false;
            }
            // 遍历订单详情，对比购物车中的商品
            for (CheckoutVo checkoutVo : listCheckoutVo) {
                // 如果购物车商品ID与订单中的商品ID匹配
                if (cart.getProdId().equals(checkoutVo.getProduct().getId())) {
                    // 计算购物车中商品的新数量
                    long num = cart.getNum() - checkoutVo.getNum();
                    // 如果新数量大于0，则更新购物车商品数量
                    if (num > 0) {
                        cart.setNum(num);
                        cartMapper.updateById(cart);
                    } else {
                        // 否则，删除购物车中的商品
                        cartMapper.deleteById(cart);
                    }
                }
            }
        }
        return true;
    }

    @Override
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
        List<CartDto> cartList = cartRedisService.getCart(userId, pageNum, pageSize);
        List<CartVo> cartVos = new ArrayList<>();
        for (CartDto cartDto : cartList) {
            CartVo cartVo = BeanCopyUtils.copyBean(cartDto, CartVo.class);
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
            CartVo cartVo = BeanCopyUtils.copyBean(cart, CartVo.class);
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
        return (long) cartRedisService.getCartNum(userId);
    }

    /**
     * 插入购物车功能
     *
     * @param productId 产品ID
     * @param num 购买数量
     * @param sessionId 用户会话ID
     * @return 布尔值，表示购物车记录是否成功插入或更新
     */
    @Override
    public Boolean insertOrUpdateCart(Long productId, Long num, String sessionId) {
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
        if (cartRedisService.hasCart(userId)){
            //Redis存在该商品该用户的购物车记录，则更新购物车记录
            return cartRedisService.setCart(userId, productId, num);
        }else{
            //Redis不存在该商品该用户的购物车记录，判断是冷数据还是空数据
            Long version_redis = null;
            //尝试获取版本号
            if (cartRedisService.hasVersion(userId)){
                version_redis = cartRedisService.getVersion(userId);
            }
            //获取数据库用户最新版本号
            Long version_mysql = cartMapper.getVersionByUserId(userId);
            if (version_redis == null || version_redis < version_mysql){
                //数据库版本号大于redis版本号
                //以数据库为基准
                //冷数据
                checkCartFormMySQLToRedis(userId, version_mysql);
                return cartRedisService.setCart(userId, productId, num);
            }else {
                //redis版本号大于等于数据库版本号
                //则以redis为基准
                return cartRedisService.setCart(userId, productId, num);
            }
        }
    }

    private @NotNull Boolean insertOrUpdateCartToMySQL(Long productId, Long num, Long userId) {
         Cart cart = cartMapper.selectOneByProdIdAndUserId(productId, userId);
         // 如果记录存在，则增加商品数量
        if (cart != null) {
            // 更新购物车记录，并返回操作结果
            return cartMapper.updateNumById(cart.getId(), num) > 0;
        } else {
            // 如果记录不存在，则新建购物车记录
            cart = new Cart();
            cart.setProdId(productId);
            cart.setNum(num);
            cart.setUserId(userId);
            // 插入新的购物车记录，并返回操作结果
            return cartMapper.insert(cart) > 0;
        }
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
        if (cartRedisService.hasCart(userId)){
            //Redis存在该商品该用户的购物车记录，则更新购物车记录
            return cartRedisService.deleteCart(userId, productId);
        }else{
            //Redis不存在该商品该用户的购物车记录，判断是冷数据还是空数据
            Long version_redis = null;
            //尝试获取版本号
            if (cartRedisService.hasVersion(userId)){
                version_redis = cartRedisService.getVersion(userId);
            }
            //获取数据库用户最新版本号
            Long version_mysql = cartMapper.getVersionByUserId(userId);
            if (version_redis == null || version_redis < version_mysql){
                //数据库版本号大于redis版本号
                //以数据库为基准
                //冷数据
                checkCartFormMySQLToRedis(userId ,version_mysql);
                return cartRedisService.deleteCart(userId, productId);
            }else {
                //redis版本号大于等于数据库版本号
                //则以redis为基准
                return cartRedisService.deleteCart(userId, productId);
            }
        }
    }



    @Override
    public void setCheckoutListToRedis(@NotNull List<Long> listCartId, String sessionId) {
        UserCache user = userService.getUserFromRedis(sessionId);
        // 遍历购物车ID列表，检查每个购物车项是否属于当前用户
        for (Long cartId : listCartId){
            //检查是否合法
            if (!cartMapper.selectUserIdById(cartId).equals(user.getId())){
                logger.error("{}非法操作！试图添加不属于自己的购物车到预订单",user.getUsername());
                return;
            }
        }
        checkoutRedisService.setCheckoutList(user.getId(), listCartId);
    }

    @Override
    public List<Long> getCheckoutListFormRedis(String sessionId) {
        UserCache user = userService.getUserFromRedis(sessionId);
        return checkoutRedisService.getCheckoutList(user.getId());
    }

    @Override
    public void deleteCheckoutListInRedis(String sessionId) {
        UserCache user = userService.getUserFromRedis(sessionId);
        checkoutRedisService.deleteCheckoutList(user.getId());
    }

    @Override
    public List<CartVo> getCheckoutList(String sessionId, Integer pageNum, Integer pageSize) {
        List<Long> listCartId = getCheckoutListFormRedis(sessionId);
        // 创建分页对象，指定页码和页面大小
        Page<Cart> cartPage = new Page<>(pageNum, pageSize);
        // 创建查询构造器，条件是购物车项ID
        LambdaQueryWrapper<Cart> queryWrapper = new LambdaQueryWrapper<Cart>().in(Cart::getId, listCartId);
        // 执行分页查询，获取查询结果
        List<Cart> carts = cartMapper.selectPage(cartPage, queryWrapper).getRecords();
        // 如果查询结果为空，则返回null
        if (carts.isEmpty()) {
            return null;
        }
        return cartToCartVo(carts);
    }

    @Override
    public void checkCartFormMySQLToRedis() {
        List<Long> listUserId = cartMapper.selectUserIdList();
        for (Long userId : listUserId) {
            Long version_mysql = cartMapper.getVersionByUserId(userId);
            Long version_redis = null;
            if (cartRedisService.hasVersion(userId)){
                version_redis = cartRedisService.getVersion(userId);
            }
            if (version_redis == null || version_redis < version_mysql){
                checkCartFormMySQLToRedis(userId, version_mysql);
            }else {
                //redis版本号大于等于数据库版本号
                logger.info("Redis版本号大于等于MySQL数据库版本号，{}用户购物车缓存已同步", userId);
            }
        }
    }

    private void checkCartFormMySQLToRedis(Long userId ,Long version) {
        QueryWrapper<Cart> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.eq("version", version);
        List<Cart> carts = cartMapper.selectList(queryWrapper);
        List<CartDto> cartDtos = BeanCopyUtils.copyBeanList(carts, CartDto.class);
        cartRedisService.initCart(userId, cartDtos, version);
    }



    @Override
    public void checkCartFromRedisToMySQL() {
        List<Long> listUserId =cartRedisService.getChangeList();
        for (Long userId : listUserId) {
            Long version_redis = cartRedisService.getVersion(userId);
            Long version_mysql = cartMapper.getVersionByUserId(userId);
            if (version_redis > version_mysql){
                logger.debug("{}购物车Redis版本号大于数据库版本号，开始同步",userId);
                checkCartFormRedisToMySQL(userId, version_redis);
            }else {
                //redis版本号小于等于数据库版本号
                logger.error("{}购物车Redis版本号小于等于数据库版本号，无需同步",userId);
            }
        }
    }

    private void checkCartFormRedisToMySQL(Long userId, Long version) {
        List<CartDto> cartDtos = cartRedisService.getCart(userId);
        for (CartDto cartDto : cartDtos) {
            if (cartMapper.checkCart(userId, cartDto.getProdId(), cartDto.getNum(), version)>0){
                continue;
            }else{
                logger.error("MySQL数据插入出错{}", cartDto);
            }
        }
        cartRedisService.removeChangeList(userId);
    }

}
