package com.jiang.mall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiang.mall.dao.CategoryMapper;
import com.jiang.mall.dao.ProductMapper;
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

    /**
     * 根据ID获取产品信息
     * <p>
     * 此方法通过调用数据库查询接口，获取特定产品的详细信息，并返回一个ProductVo对象
     * 如果数据库中存在该产品，则将产品信息转换为ProductVo对象，并附带类别名称
     * 如果数据库中不存在该产品，则返回null
     *
     * @param id 产品的ID
     * @return ProductVo对象，包含产品的详细信息和类别名称；如果产品不存在，则返回null
     */
    @Override
    public ProductVo getProduct(Integer id) {
        // 通过ID从数据库中查询产品信息
        Product product = productMapper.selectById(id);
        // 如果数据库中存在该产品
        if (product != null) {
            // 将查询到的Product对象转换为ProductVo对象
            ProductVo productVo = BeanCopyUtils.copyBean(product, ProductVo.class);
            // 通过ID查询产品类别，并设置产品类别的名称
            productVo.setCategoryName(categoryMapper.selectById(productVo.getCategoryId()).getName());
            // 返回转换后的ProductVo对象
            return productVo;
        }
        // 如果数据库中不存在该产品，返回null
        return null;
    }

    /**
     * 插入产品信息
     * <p>
     * 该方法通过调用ProductMapper的insert方法来实现产品信息的插入
     * 它接受一个Product对象作为参数，表示要插入的产品信息
     *
     * @param product 要插入的产品对象，包含产品的所有相关信息
     * @return 操作是否成功执行的布尔值，成功返回true，失败返回false
     */
    @Override
    public boolean insertProduct(Product product) {
        return productMapper.insert(product)==1;
    }


    /**
     * 更新产品信息
     * <p>
     * 该方法通过调用productMapper的updateById方法来实现产品信息的更新
     * 它判断更新操作是否成功的依据是：如果updateById方法返回的结果为1，则表示更新成功
     *
     * @param product 要更新的产品对象，包含新的产品信息
     * @return boolean 表示产品信息更新是否成功
     */
    @Override
    public boolean updateProduct(Product product) {
        return productMapper.updateById(product)==1;
    }

    /**
     * 删除产品信息
     * <p>
     * 说明：
     * 该方法通过调用productMapper的deleteById方法来删除产品信息，
     * 参数id用于指定要删除的产品。返回值表示删除操作是否成功，
     * 成功时返回true，否则返回false。
     *
     * @param id 产品的ID
     * @return 删除操作是否成功
     *
     */
    @Override
    public boolean deleteProduct(Integer id) {
        return productMapper.deleteById(id) == 1;
    }

    /**
     * 根据产品ID查询库存数量
     *
     * @param productId 产品ID
     * @return 产品的库存数量
     */
    @Override
    public Integer queryStoksById(Integer productId) {
        // 创建查询包装器并设置条件：产品ID必须匹配
        QueryWrapper<Product> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", productId);

        // 根据查询条件获取产品信息
        Product product = productMapper.selectOne(queryWrapper);
        // 返回产品的库存数量
        return product.getStocks();
    }

    /**
     * 根据商品编码查询商品信息
     *
     * @param code 商品编码
     * @return 如果找到商品返回true，否则返回false
     */
    @Override
    public boolean queryCode(String code) {
        // 创建查询包装器，用于指定查询条件
        QueryWrapper<Product> queryWrapper = new QueryWrapper<>();
        // 设置查询条件为商品编码
        queryWrapper.eq("code", code);
        // 执行查询并判断结果是否为空，返回查询结果的布尔值
        return productMapper.selectOne(queryWrapper) != null;
    }

    @Override
    public Long getProductNum() {
	    return productMapper.selectCount(null);
    }

    @Override
    public List<Product> queryAll() {
        List<Product> products = productMapper.selectList(null);
        return products;
    }


}
