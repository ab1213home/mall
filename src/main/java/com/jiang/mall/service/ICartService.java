package com.jiang.mall.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jiang.mall.domain.ResponseResult;
import com.jiang.mall.domain.entity.Cart;
import com.jiang.mall.domain.vo.CartVo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author WH
 * @since 2023-06-25
 */
public interface ICartService extends IService<Cart> {

    ResponseResult getCartList(String userId, Integer pageNum, Integer pageSize);

    ResponseResult getCart(Integer id);

    ResponseResult insertOrUpdate(Cart cart);

    ResponseResult updateCart(Cart cart);

    ResponseResult deleteCart(List<Integer> ids);

	boolean addCart(Integer productId, Integer num, Integer userId);

    Integer queryStoksById(Integer productId);

    Integer getCartNum(Integer userId);

	List<CartVo> getCartList(Integer userId, Integer pageNum, Integer pageSize, List<Integer> listProdId);
}
