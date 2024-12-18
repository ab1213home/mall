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
import com.jiang.mall.domain.vo.CartVo;
import com.jiang.mall.domain.vo.CategoryVo;
import com.jiang.mall.domain.vo.CheckoutVo;
import com.jiang.mall.domain.vo.ProductVo;
import com.jiang.mall.service.ICartService;
import com.jiang.mall.util.BeanCopyUtils;
import org.jetbrains.annotations.NotNull;
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

    @Override
    public List<CartVo> getCartList(Long userId, Integer pageNum, Integer pageSize) {
        Page<Cart> cartPage = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Cart> queryWrapper = new LambdaQueryWrapper<Cart>().eq(Cart::getUserId, userId);
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

	@Override
    public Boolean insertOrUpdate(@NotNull Cart cart) {
        if (cart.getUserId() == null || cart.getProdId() == null) {
            return false;
        }
        Cart cart1 = cartMapper.selectOne(new LambdaQueryWrapper<Cart>().eq(Cart::getUserId, cart.getUserId()).eq(Cart::getProdId, cart.getProdId()));
        if (cart1 == null) {
            int res = cartMapper.insert(cart);
	        return res == 1;
        } else {
            cart1.setNum(cart.getNum() + cart.getNum());
            int res = cartMapper.updateById(cart1);
	        return res == 1;
        }
	}

    @Override
    public Boolean updateCart(Cart cart) {
        int res = cartMapper.updateById(cart);
	    return res == 1;
    }

    /**
     * 将指定商品添加到用户的购物车
     * 如果商品已经在购物车中，则增加商品数量；如果商品不在购物车中，则新建一条购物车记录
     *
     * @param productId 商品ID
     * @param num       添加的商品数量
     * @param userId    用户ID
     * @return 操作是否成功
     */
    @Override
    public Boolean addCart(Long productId, Integer num, Long userId) {
        // 根据商品ID和用户ID查询购物车记录
        QueryWrapper<Cart> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("prod_id", productId);
        queryWrapper.eq("user_id", userId);
        Cart cart = cartMapper.selectOne(queryWrapper);

        // 如果记录存在，则增加商品数量
        if (cart != null) {
            Integer sum = cart.getNum() + num;
            cart.setNum(sum);
            // 更新购物车记录，并返回操作结果
            return cartMapper.updateById(cart) == 1;
        } else {
            // 如果记录不存在，则新建购物车记录
            cart = new Cart(productId, num, userId);
            // 插入新的购物车记录，并返回操作结果
            return cartMapper.insert(cart) == 1;
        }
    }

    /**
     * 获取指定用户的购物车商品数量
     *
     * @param userId 用户ID
     * @return 购物车中的商品数量
     */
    @Override
    public Long getCartNum(Long userId) {
        // 创建查询包装器，用于查询条件的设置
        QueryWrapper<Cart> queryWrapper = new QueryWrapper<>();

        // 设置查询条件，查找特定用户ID的购物车记录
        queryWrapper.eq("user_id", userId);

	    // 返回购物车列表的大小，即商品数量
        return cartMapper.selectCount(queryWrapper);
    }

    /**
     * 根据用户ID、页码、页面大小和购物车项ID列表，获取购物车项列表的视图对象
     *
     * @param userId 用户ID，用于验证购物车项属于该用户
     * @param pageNum 页码，用于分页查询
     * @param pageSize 页面大小，用于分页查询
     * @param listCartId 购物车项ID列表，用于查询特定的购物车项
     * @return 返回购物车项的视图列表，如果列表为空或不属于该用户，则返回null
     */
    @Override
    public List<CartVo> getCartList(Long userId, Integer pageNum, Integer pageSize, List<Long> listCartId) {
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
            if (!cart.getUserId().equals(userId)){
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
     * @param userId         用户ID，用于验证购物车商品是否属于当前用户
     * @param listCheckoutVo 订单详情列表，包含已购买的商品信息
     * @return 如果成功更新购物车则返回true，否则返回false
     */
    @Override
    public Boolean deleteCartByOrder(List<Long> listCartId, Long userId, List<CheckoutVo> listCheckoutVo) {
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
            if (!cart.getUserId().equals(userId)){
                return false;
            }

            // 遍历订单详情，对比购物车中的商品
            for (CheckoutVo checkoutVo : listCheckoutVo) {
                // 如果购物车商品ID与订单中的商品ID匹配
                if (cart.getProdId().equals(checkoutVo.getProduct().getId())) {
                    // 计算购物车中商品的新数量
                    int num = cart.getNum() - checkoutVo.getNum();

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

    /**
     * 删除购物车记录
     *
     * @param id     购物车记录的ID
     * @param userId 用户ID，用于验证购物车记录的所有者
     * @return 删除操作是否成功
     */
    @Override
    public Boolean deleteCart(Long id, Long userId) {
        // 创建查询包装器，用于查询条件的设置
        QueryWrapper<Cart> queryWrapper = new QueryWrapper<>();

        // 设置查询条件，查找特定用户ID的购物车记录
        queryWrapper.eq("id", id);

        // 执行查询并返回购物车列表
        Cart cart = cartMapper.selectOne(queryWrapper);
        // 检查购物车记录是否存在以及是否属于当前用户
        if (cart == null || !cart.getUserId().equals(userId)){
            // 如果记录不存在或不属于当前用户，返回删除失败
            return false;
        }
        // 执行删除操作，返回删除是否成功
        return cartMapper.deleteById(id) == 1;
    }

}
