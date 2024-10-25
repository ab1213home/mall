package com.jiang.mall.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jiang.mall.domain.entity.Product;
import com.jiang.mall.domain.vo.ProductVo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author jiang
 * @since 2024年9月11日
 */
public interface IProductService extends IService<Product> {

    List<ProductVo> getProductList(String name, Long categoryId, Integer pageNum, Integer pageSize);

    ProductVo getProduct(Long id);

    boolean insertProduct(Product banner);

    boolean updateProduct(Product banner);

    boolean deleteProduct(Long id);

    Integer queryStoksById(Long productId);

    boolean queryCode(String code);

	Long getProductNum();

	List<Product> queryAll();
}
