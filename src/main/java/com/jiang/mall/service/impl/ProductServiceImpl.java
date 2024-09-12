package com.jiang.mall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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
 * @author jiang
 * @since 2024年9月11日
 */
@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements IProductService {

    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private CategoryMapper categoryMapper;

    /**
     * 根据名称、类别ID、页码和页面大小获取产品列表
     *
     * @param name       产品名称
     * @param categoryId 产品类别ID
     * @param pageNum    页码
     * @param pageSize   页面大小
     * @return 返回产品列表（ProductVo类型）
     */
    @Override
    public List<ProductVo> getProductList(String name, Integer categoryId, Integer pageNum, Integer pageSize) {
        // 创建分页对象，指定页码和页面大小
        Page<Product> productPage = new Page<>(pageNum, pageSize);

        // 创建查询构造器，用于模糊查询产品名称和精确查询类别ID
        LambdaQueryWrapper<Product> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(name != null, Product::getTitle, name).eq(categoryId != null, Product::getCategoryId, categoryId);

        // 执行分页查询，获取产品列表
        List<Product> products = productMapper.selectPage(productPage, queryWrapper).getRecords();

        // 将产品实体列表转换为产品VO列表
        List<ProductVo> productVos = BeanCopyUtils.copyBeanList(products, ProductVo.class);

        // 遍历产品VO列表，设置每个产品的类别名称
        for (ProductVo productVo : productVos) {
            // 根据类别ID查询类别名称，并设置到产品VO中
            productVo.setCategoryName(categoryMapper.selectById(productVo.getCategoryId()).getName());
        }

        // 返回产品VO列表
        return productVos;
    }

    @Override
    public ProductVo getProduct(Integer id) {
        Product product = productMapper.selectById(id);
        if (product != null) {
            ProductVo productVo = BeanCopyUtils.copyBean(product, ProductVo.class);
            productVo.setCategoryName(categoryMapper.selectById(productVo.getCategoryId()).getName());
            return productVo;
        }
        return null;
    }

    @Override
    public boolean insertProduct(Product product) {
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
    public boolean updateProduct(Product product) {
        int res = productMapper.updateById(product);
        if (res == 1) {
            return ResponseResult.okResult();
        }
        return ResponseResult.failResult();
    }

    @Override
    public boolean deleteProduct(Integer ids) {
        int res = productMapper.deleteByIds(ids);
        if (res > 0) {
            return ResponseResult.okResult();
        }
        return ResponseResult.failResult();
    }

    @Override
    public Integer queryStoksById(Integer productId) {
        QueryWrapper<Product> queryWrapper = new QueryWrapper<>();
	    queryWrapper.eq("id", productId);

	    Product product = productMapper.selectOne(queryWrapper);
        return product.getStocks();
    }


}
