package com.jiang.mall.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jiang.mall.domain.ResponseResult;
import com.jiang.mall.domain.entity.Product;

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

    ResponseResult getProductList(String name, Integer categoryId, Integer pageNum, Integer pageSize);

    ResponseResult getProduct(Integer id);

    ResponseResult insertProduct(Product banner);

    ResponseResult updateProduct(Product banner);

    ResponseResult deleteProduct(List<Integer> ids);

    Integer queryStoksById(Integer productId);
}
