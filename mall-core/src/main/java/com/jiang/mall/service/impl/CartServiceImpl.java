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
import com.jiang.mall.dao.CartMapper;
import com.jiang.mall.dao.CategoryMapper;
import com.jiang.mall.dao.ProductMapper;
import com.jiang.mall.domain.entity.Cart;
import com.jiang.mall.domain.entity.Category;
import com.jiang.mall.domain.entity.Product;
import com.jiang.mall.domain.vo.*;
import com.jiang.mall.service.ICartRedisService;
import com.jiang.mall.service.ICartService;
import com.jiang.mall.service.IProductRedisService;
import com.jiang.mall.service.IUserService;
import com.jiang.mall.util.BeanCopyUtils;
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

    private CartMapper cartMapper;

    @Autowired
    public void setCartMapper(CartMapper cartMapper) {
        this.cartMapper = cartMapper;
    }

    private ProductMapper productMapper;

    @Autowired
    public void setProductMapper(ProductMapper productMapper) {
        this.productMapper = productMapper;
    }

    private CategoryMapper categoryMapper;

    @Autowired
    public void setCategoryMapper(CategoryMapper categoryMapper) {
        this.categoryMapper = categoryMapper;
    }

    private IUserService userService;

    @Autowired
    public void setUserService(IUserService userService) {
        this.userService = userService;
    }

    private ICartRedisService redisService;

	@Autowired
	private void setCartRedisService(ICartRedisService redisService) {
		this.redisService = redisService;
	}

    /**
     * 根据用户ID、页码、页面大小和购物车项ID列表，获取购物车项列表的视图对象
     *
     * @param sessionId
     * @param pageNum    页码，用于分页查询
     * @param pageSize   页面大小，用于分页查询
     * @param listCartId 购物车项ID列表，用于查询特定的购物车项
     * @return 返回购物车项的视图列表，如果列表为空或不属于该用户，则返回null
     */
    @Override
    public List<CartVo> getCartList(String sessionId, Integer pageNum, Integer pageSize, List<Long> listCartId) {
        UserVo user = userService.getUserFromRedis(sessionId);
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
        // 遍历查询结果，验证购物车项是否属于指定的用户
        for (Cart cart : carts) {
            if (!cart.getUserId().equals(user.getId())){
                return null;
            }
        }
        // 将购物车项列表转换为购物车项视图对象列表
        List<CartVo> cartVos = new ArrayList<>();
        for (Cart cart : carts) {
            CartVo cartVo = BeanCopyUtils.copyBean(cart, CartVo.class);
            // 根据购物车项中的产品ID，查询产品信息
            Product product = productMapper.selectById(cart.getProdId());
            ProductVo productVo = BeanCopyUtils.copyBean(product, ProductVo.class);
            Category category = categoryMapper.selectById(product.getCategoryId());
            CategoryVo categoryVo = BeanCopyUtils.copyBean(category, CategoryVo.class);
	        assert productVo != null;
	        productVo.setCategory(categoryVo);
	        assert cartVo != null;
	        cartVo.setProduct(productVo);
            cartVos.add(cartVo);
        }
        // 返回购物车项视图对象列表
        return cartVos;
    }

    /**
     * 根据订单删除购物车中的商品
     * 该方法主要用于在用户下单后，更新购物车中相关商品的数量或删除已购买的商品
     *
     * @param listCartId     购物车商品ID列表，用于定位需要更新的购物车商品
     * @param sessionId
     * @param listCheckoutVo 订单详情列表，包含已购买的商品信息
     * @return 如果成功更新购物车则返回true，否则返回false
     */
    @Override
    public Boolean deleteCartByOrder(List<Long> listCartId, String sessionId, List<CheckoutVo> listCheckoutVo) {
        UserVo user = userService.getUserFromRedis(sessionId);
        // 根据购物车商品ID列表查询购物车商品信息
        LambdaQueryWrapper<Cart> queryWrapper = new LambdaQueryWrapper<Cart>().in(Cart::getId, listCartId);
        List<Cart> carts = cartMapper.selectList(queryWrapper);

        // 如果没有找到对应的购物车商品，直接返回成功
        if (carts.isEmpty()) {
            return true;
        }

        // 遍历查询到的购物车商品
        for (Cart cart : carts) {
            // 检查购物车商品是否属于当前用户，如果不属于则返回失败
            if (!cart.getUserId().equals(user.getId())){
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
        UserVo user = userService.getUserFromRedis(sessionId);
        Page<Cart> cartPage = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Cart> queryWrapper = new LambdaQueryWrapper<Cart>().eq(Cart::getUserId, user.getId());
        List<Cart> carts = cartMapper.selectPage(cartPage, queryWrapper).getRecords();
        List<CartVo> cartVos = new ArrayList<>();
        for (Cart cart : carts) {
            CartVo cartVo = BeanCopyUtils.copyBean(cart, CartVo.class);
            // 根据购物车项中的产品ID，查询产品信息
            Product product = productMapper.selectById(cart.getProdId());
            ProductVo productVo = BeanCopyUtils.copyBean(product, ProductVo.class);
            Category category = categoryMapper.selectById(product.getCategoryId());
            CategoryVo categoryVo = BeanCopyUtils.copyBean(category, CategoryVo.class);
	        assert productVo != null;
	        productVo.setCategory(categoryVo);
	        assert cartVo != null;
	        cartVo.setProduct(productVo);
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
        // 创建查询包装器，用于查询条件的设置
        QueryWrapper<Cart> queryWrapper = new QueryWrapper<>();
        // 设置查询条件，查找特定用户ID的购物车记录
        queryWrapper.eq("user_id", userService.getUserFromRedis(sessionId).getId());
        // 返回购物车列表的大小，即商品数量
        return cartMapper.selectCount(queryWrapper);
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
    public Boolean insertCart(Long productId, Long num, String sessionId) {
        // 从Redis中获取用户信息
        UserVo user = userService.getUserFromRedis(sessionId);
        // 根据商品ID和用户ID查询购物车记录
        Cart cart = cartMapper.selectOneByProdIdAndUserId(productId, user.getId());

        // 如果记录存在，则增加商品数量
        if (cart != null) {
            // 更新购物车记录，并返回操作结果
            return cartMapper.updateNumById(cart.getId(), num) > 0;
        } else {
            // 如果记录不存在，则新建购物车记录
            cart = new Cart(productId, num, user.getId());
            // 插入新的购物车记录，并返回操作结果
            return cartMapper.insert(cart) > 0;
        }
    }

    /**
     * 更新购物车中商品的数量
     * 此方法首先验证给定的商品ID是否属于当前用户，以防止跨用户修改
     * 如果商品不属于当前用户，方法返回null
     * 如果验证通过，方法将尝试更新商品的数量，并返回更新是否成功的布尔值
     *
     * @param id 商品在购物车中的ID
     * @param num 新的商品数量
     * @param sessionId 用户的会话ID，用于识别和验证用户
     * @return 如果商品不属于当前用户，返回null；否则，返回更新是否成功的布尔值
     */
    @Override
    public Boolean updateCart(Long id, Long num, String sessionId) {
        // 验证购物车项的拥有者是否为当前用户
        if (!cartMapper.selectUserIdById(id).equals(userService.getUserFromRedis(sessionId).getId())){
            return null;
        }
        // 更新购物车中商品的数量，并返回更新结果
        return cartMapper.updateNumById(id, num) > 0;
    }

    /**
     * 删除购物车项
     * <p>
     * 此方法旨在删除指定的购物车项它首先确保只有该项的拥有者才能删除它，
     * 通过比较购物车项关联的用户ID和当前会话标识对应的用户ID如果两者不匹配，
     * 方法返回null，表示删除操作未经授权如果用户ID匹配，则执行删除操作，
     * 并返回一个布尔值，指示删除操作是否成功
     *
     * @param id 购物车项的唯一标识符
     * @param sessionId 当前用户的会话标识符，用于识别用户
     * @return 如果删除成功，返回true；如果删除失败或未经授权，返回false或null
     */
    @Override
    public Boolean deleteCart(Long id, String sessionId) {
        // 检查购物车项的用户ID是否与当前会话用户ID匹配，确保只有拥有者可以删除
        if (!cartMapper.selectUserIdById(id).equals(userService.getUserFromRedis(sessionId).getId())){
            return null;
        }
        // 删除购物车项，并返回操作是否成功的布尔值
        return cartMapper.deleteById(id) > 0;
    }

    @Override
    public void checkoutToRedis(List<Long> listCartId, String sessionId) {
        redisService.setCartIdList(sessionId, listCartId);
    }

    @Override
    public List<Long> getCartIdListFormRedis(String sessionId) {
        return redisService.getCartIdList(sessionId);
    }

    @Override
    public void deleteCartIdListInRedis(String sessionId) {
        redisService.deleteCartIdList(sessionId);
    }

}
