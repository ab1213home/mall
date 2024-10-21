package com.jiang.mall.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jiang.mall.domain.ResponseResult;
import com.jiang.mall.domain.entity.Cart;
import com.jiang.mall.domain.vo.CartVo;
import com.jiang.mall.domain.vo.CheckoutVo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author jiang
 * @since 2024年9月11日
 */
public interface ICartService extends IService<Cart> {

    List<CartVo> getCartList(Long userId, Integer pageNum, Integer pageSize);

	ResponseResult insertOrUpdate(Cart cart);

    boolean updateCart(Cart cart);

	/**
	 * 将指定商品添加到指定用户的购物车中
	 *
	 * @param productId 商品ID，标识要添加到购物车的商品
	 * @param num 添加到购物车的商品数量
	 * @param userId 用户ID，标识商品将被添加到哪个用户的购物车中
	 * @return 如果添加成功，返回true；否则返回false
	 */
	boolean addCart(Long productId, Integer num, Long userId);

	/**
	 * 根据用户ID获取购物车数量
	 *
	 * @param userId 用户ID，用于标识特定用户
	 * @return 返回用户的购物车数量如果用户不存在或发生错误，可能返回null
	 */
	Long getCartNum(Long userId);

	/**
	 * 根据用户ID、页码、页面大小和购物车项ID列表获取购物车项列表
	 *
	 * @param userId 用户ID，用于识别用户
	 * @param pageNum 页码，用于分页查询
	 * @param pageSize 页面大小，用于分页查询
	 * @param listCartId 购物车项ID列表，用于筛选特定的购物车项
	 * @return 返回符合筛选条件的购物车项列表
	 *
	 * 该方法的主要作用是根据用户的请求，从数据库中查询特定的购物车项，并返回给前端展示
	 * 通过分页和特定ID筛选，可以有效提高查询效率和减少数据库压力
	 */
	List<CartVo> getCartList(Long userId, Integer pageNum, Integer pageSize, List<Long> listCartId);

	/**
	 * 根据订单删除购物车记录
	 * <p>
	 * 此方法的目的是在用户完成订单购买后，根据订单信息删除相应的购物车记录
	 * 它接收一个购物车ID列表、一个用户ID和一个结账列表作为参数，以确保只有属于当前用户的购物车项目被删除
	 *
	 * @param listCartId 购物车ID列表，标识需要删除的购物车记录
	 * @param userId 用户ID，用于验证购物车记录属于当前用户
	 * @param listCheckoutVo 结账列表，可能包含与购物车ID相关的信息
	 * @return 返回一个布尔值，表示删除操作是否成功
	 */
	boolean deleteCartByOrder(List<Long> listCartId, Long userId, List<CheckoutVo> listCheckoutVo);

	boolean deleteCart(Long id, Long userId);
}
