package com.jiang.mall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiang.mall.dao.CategoryMapper;
import com.jiang.mall.dao.ProductMapper;
import com.jiang.mall.domain.ResponseResult;
import com.jiang.mall.domain.entity.Product;
import com.jiang.mall.domain.vo.ProductVo;
import com.jiang.mall.service.IProductService;
import com.jiang.mall.util.BeanCopyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author WH
 * @since 2023-06-25
 */
@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements IProductService {

    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public ResponseResult getProductList(String name, Integer categoryId, Integer pageNum, Integer pageSize) {
        Page<Product> productPage = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Product> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(name != null, Product::getTitle, name).eq(categoryId != null, Product::getCategoryId, categoryId);
        List<Product> products = productMapper.selectPage(productPage, queryWrapper).getRecords();
        List<ProductVo> productVos = BeanCopyUtils.copyBeanList(products, ProductVo.class);
        for (ProductVo productVo : productVos) {
            productVo.setCategoryName(categoryMapper.selectById(productVo.getCategoryId()).getName());
        }
        return ResponseResult.okResult(productVos);
    }

    @Override
    public ResponseResult getProduct(Integer id) {
        Product product = productMapper.selectById(id);
        if (product != null) {
            ProductVo productVo = BeanCopyUtils.copyBean(product, ProductVo.class);
            productVo.setCategoryName(categoryMapper.selectById(productVo.getCategoryId()).getName());
            return ResponseResult.okResult(productVo);
        }
        return ResponseResult.failResult();
    }

    @Override
    public ResponseResult insertProduct(Product product) {
        if (product.getCode() == null) {
            return ResponseResult.failResult("商品编码不能为空");
        }
        Product prod = productMapper.selectOne(new LambdaQueryWrapper<Product>().eq(Product::getCode, product.getCode()));
        if (prod == null) {
            int res = productMapper.insert(product);
            if (res == 1) {
                return ResponseResult.okResult();
            }
        }
        return ResponseResult.failResult(501, "商品编码已存在，新增失败");
    }

    @Override
    public ResponseResult updateProduct(Product product) {
        int res = productMapper.updateById(product);
        if (res == 1) {
            return ResponseResult.okResult();
        }
        return ResponseResult.failResult();
    }

    @Override
    public ResponseResult deleteProduct(List<Integer> ids) {
        int res = productMapper.deleteByIds(ids);
        if (res > 0) {
            return ResponseResult.okResult();
        }
        return ResponseResult.failResult();
    }


}
