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
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiang.mall.config.ProductConfig;
import com.jiang.mall.dao.ProductMapper;
import com.jiang.mall.domain.cache.ProductCache;
import com.jiang.mall.domain.entity.EsProduct;
import com.jiang.mall.domain.entity.Product;
import com.jiang.mall.domain.vo.CategoryVo;
import com.jiang.mall.domain.vo.ProductVo;
import com.jiang.mall.event.ProductChangedEvent;
import com.jiang.mall.service.ICategoryService;
import com.jiang.mall.service.IProductRedisService;
import com.jiang.mall.service.IProductService;
import com.jiang.mall.util.BeanCopyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

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
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements IProductService {

    private ProductMapper productMapper;

    @Autowired
    public void setProductMapper(ProductMapper productMapper) {
        this.productMapper = productMapper;
    }

	private IProductRedisService redisService;

	@Autowired
	private void setProductRedisService(IProductRedisService redisService) {
		this.redisService = redisService;
	}

	private ProductConfig coreConfig;

	@Autowired
	public void setCoreConfig(ProductConfig coreConfig) {
		this.coreConfig = coreConfig;
	}

	private ICategoryService categoryService;

	@Autowired
	public void setCategoryService(ICategoryService categoryService) {
		this.categoryService = categoryService;
	}

    private ElasticsearchOperations elasticsearchOperations;

	@Autowired
	public void setElasticsearchOperations(ElasticsearchOperations elasticsearchOperations) {
		this.elasticsearchOperations = elasticsearchOperations;
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
    public List<ProductVo> getProductList(String name, Long categoryId, Integer pageNum, Integer pageSize) {
        // 创建分页对象，指定页码和页面大小
        Page<Product> productPage = new Page<>(pageNum, pageSize);

        // 获取指定类别及其所有子类别的ID列表
        List<Long> categoryIds = categoryService.getCategoryIds(categoryId);

        // 创建查询构造器，用于模糊查询产品名称和精确查询类别ID
        LambdaQueryWrapper<Product> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(name != null, Product::getTitle, name).in(categoryId != null, Product::getCategoryId, categoryIds);

        // 执行分页查询，获取产品列表
        List<Product> products = productMapper.selectPage(productPage, queryWrapper).getRecords();

        List<ProductVo> productVos = new ArrayList<>();

        // 将产品实体列表转换为产品VO列表
        for (Product product : products) {
            // 遍历产品VO列表，设置每个产品的类别名称
            ProductVo productVo = BeanCopyUtils.copyBean(product, ProductVo.class);
			assert productVo != null;
            // 根据类别ID查询类别名称，并设置到产品VO中
            CategoryVo category = categoryService.getCategory(product.getCategoryId());
	        productVo.setCategory(category);
            productVos.add(productVo);
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
    public ProductVo getProduct(Long id) {
		if (coreConfig.isProductCacheEnabled() && redisService.hasProduct(id)){
			ProductCache productCache = redisService.getProduct(id);
			return BeanCopyUtils.copyBean(productCache, ProductVo.class);
		}
        // 通过ID从数据库中查询产品信息
        Product product = productMapper.selectById(id);
        // 如果数据库中存在该产品
        if (product != null) {
            // 将查询到的Product对象转换为ProductVo对象
            ProductVo productVo = BeanCopyUtils.copyBean(product, ProductVo.class);
			if (coreConfig.isProductCacheEnabled()){
				ProductCache productCache = BeanCopyUtils.copyBean(product, ProductCache.class);
				redisService.setProduct(productCache);
	        }
			// 根据类别ID查询类别名称，并设置到产品VO中
            CategoryVo category = categoryService.getCategory(product.getCategoryId());
	        assert productVo != null;
			productVo.setCategory(category);
			return productVo;
        }else{
            return null;
        }
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
    public Boolean insertProduct(Product product) {
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
    public Boolean updateProduct(Product product) {
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
     */
    @Override
    public Boolean deleteProduct(Long id) {
        return productMapper.deleteById(id) == 1;
    }

    /**
     * 根据产品ID查询库存数量
     *
     * @param productId 产品ID
     * @return 产品的库存数量
     */
    @Override
    public Long queryStoksById(Long productId) {
        // 返回产品的库存数量
	    //TODO:需要判断是否上架，防止爆破
        return productMapper.selectStocksById(productId);
    }

    /**
     * 根据商品编码查询商品信息
     *
     * @param code 商品编码
     * @return 如果找到商品返回true，否则返回false
     */
    @Override
    public Boolean queryCode(String code) {
        // 执行查询并判断结果是否为空，返回查询结果的布尔值
        return productMapper.selectCountByCode(code) != 0;
    }

    @Override
    public Long getProductNum() {
	    return productMapper.selectCount(null);
    }

    @Override
    public List<Product> queryAll() {
	    return productMapper.selectList(null);
    }

	@Override
	public void checkProduct() {

	}


	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handleProductChangedEvent(ProductChangedEvent event) {
	    try {
	        switch (event.getChangeType()) {
	            case CREATE:
	            case UPDATE:
	                syncProductToEs(event.getProductId());
	                break;
	            case DELETE:
	                deleteProductFromEs(event.getProductId());
	                break;
	        }
	        refreshCache(event.getProductId());
	    } catch (Exception e) {
	        log.error("处理商品变更事件失败: {}");
	        // 可添加重试逻辑
	    }
	}

	private void syncProductToEs(Long productId) {
	    Product product = baseMapper.selectById(productId);
	    if (product != null) {
	        EsProduct esProduct = BeanCopyUtils.copyBean(product, EsProduct.class);
		    assert esProduct != null;
		    elasticsearchOperations.save(esProduct);
	    }
	}

	private void deleteProductFromEs(Long productId) {
	    elasticsearchOperations.delete(productId.toString(), EsProduct.class);
	}

	private void refreshCache(Long productId) {
//	    String cacheKey = CACHE_PREFIX + productId;
//	    redisTemplate.delete(cacheKey);
//	    Product product = baseMapper.selectById(productId);
//	    if (product != null) {
//	        redisTemplate.opsForValue().set(cacheKey, product, 30, TimeUnit.MINUTES);
//	    }
	}

}
