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

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import co.elastic.clients.elasticsearch.core.DeleteRequest;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiang.mall.config.ProductConfig;
import com.jiang.mall.dao.ProductMapper;
import com.jiang.mall.dao.ProductSnapshotMapper;
import com.jiang.mall.domain.cache.ProductCache;
import com.jiang.mall.domain.cache.ProductSnapshotCache;
import com.jiang.mall.domain.entity.EsProduct;
import com.jiang.mall.domain.entity.Product;
import com.jiang.mall.domain.entity.ProductSnapshot;
import com.jiang.mall.domain.vo.CategoryVo;
import com.jiang.mall.domain.vo.ProductSnapshotVo;
import com.jiang.mall.domain.vo.ProductVo;
import com.jiang.mall.event.ProductChangedEvent;
import com.jiang.mall.service.ICategoryService;
import com.jiang.mall.service.II18nService;
import com.jiang.mall.service.IProductRedisService;
import com.jiang.mall.service.IProductService;
import com.jiang.mall.util.BeanCopyUtil;
import com.jiang.mall.util.SecureUtil;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.*;

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

	private static final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);

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

	private ProductConfig productConfig;

	@Autowired
	public void setProductConfig(ProductConfig productConfig) {
		this.productConfig = productConfig;
	}

	private ICategoryService categoryService;

	@Autowired
	public void setCategoryService(ICategoryService categoryService) {
		this.categoryService = categoryService;
	}

	private ProductSnapshotMapper productSnapshotMapper;

	@Autowired
	public void setProductSnapshotMapper(ProductSnapshotMapper productSnapshotMapper) {
		this.productSnapshotMapper = productSnapshotMapper;
	}

    private ElasticsearchClient esClient;

	@Autowired
	public void setElasticsearchOperations(ElasticsearchClient esClient) {
		this.esClient = esClient;
	}

	private II18nService ii18nService;

	@Autowired
	public void setIi18nService(II18nService ii18nService) {
		this.ii18nService = ii18nService;
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
    @Transactional
    public List<ProductVo> getProductList(String name, Long categoryId, Integer pageNum, Integer pageSize) {
		// 参数校验
	    if (pageNum == null || pageNum < 1) pageNum = 1;
	    if (pageSize == null || pageSize < 1) pageSize = 10;
		try {
			// 获取分类及其子分类的 ID 列表
			List<Long> categoryIds = new ArrayList<>();
			if (categoryId != null) {
				categoryIds = categoryService.getCategoryIds(categoryId);
			}

			// 构建布尔查询
			BoolQuery.Builder boolQueryBuilder = QueryBuilders.bool();
			if (name != null && !name.trim().isEmpty()) {
				boolQueryBuilder.must(
						query -> query.match(m -> m
								.field("name")
								.query(name)
						)
				);
			}
			if (!categoryIds.isEmpty()) {
			     List<FieldValue> fieldValue = categoryIds.stream()
			            .map(FieldValue::of)
			            .toList();
				 boolQueryBuilder.filter(
			            query -> query.terms(t -> t
			                .field("categoryId")
			                .terms(ts -> ts.value(fieldValue))
			            )
			     );
			}


			// 构建分页参数
			int from = (pageNum - 1) * pageSize;

			// 构造搜索请求
			int finalPageSize = pageSize;
			SearchRequest request = SearchRequest.of(b -> b
					.index("products")
					.query(boolQueryBuilder.build()._toQuery())
					.from(from)
					.size(finalPageSize)
					.trackTotalHits(t -> t.enabled(true))
					.sort(s -> s
						.field(f -> f
						    .field("id.keyword")
						    .order(SortOrder.Asc)
						)
					)
			);

			// 执行搜索
			SearchResponse<EsProduct> response = esClient.search(request, EsProduct.class);

			// 提取结果
			List<EsProduct> hits = response.hits().hits().stream()
					.map(Hit::source)
					.toList();

			if (hits.isEmpty()) {
				return Collections.emptyList();
			}

			// 提取商品 ID 列表
			List<Long> productIds = hits.stream()
					.map(EsProduct::getId)
					.toList();

			List<ProductVo> productVos = new ArrayList<>();

	        // 将产品实体列表转换为产品VO列表
	        for (Long id : productIds) {
				ProductVo product = getProduct(id);
	            productVos.add(product);
	        }

	        // 返回产品VO列表
	        return productVos;
		}catch (Exception e) {
			logger.error("Elasticsearch 查询商品列表失败: {}", e.getMessage(), e);
            // 可选：降级到数据库查询
            return fallbackGetProductList(name, categoryId, pageNum, pageSize);
		}
    }

	@Transactional
	protected List<ProductVo> fallbackGetProductList(String name, Long categoryId, Integer pageNum, Integer pageSize) {
		// 创建分页对象，指定页码和页面大小
        Page<Product> productPage = new Page<>(pageNum, pageSize);

        // 创建查询构造器，用于模糊查询产品名称和精确查询类别ID
        LambdaQueryWrapper<Product> queryWrapper = new LambdaQueryWrapper<>();
		if (name != null && !name.trim().isEmpty()) {
	        queryWrapper.like(Product::getTitle, name);
	    }
		if (categoryId != null) {
			// 获取指定类别及其所有子类别的ID列表
	        List<Long> categoryIds = categoryService.getCategoryIds(categoryId);
	        if (!categoryIds.isEmpty()) {
	            queryWrapper.in(Product::getCategoryId, categoryIds);
	        }
	    }
		queryWrapper.select(Product::getId);

        // 执行分页查询，获取产品列表
        List<Product> products = productMapper.selectPage(productPage, queryWrapper).getRecords();

        List<ProductVo> productVos = new ArrayList<>();

        // 将产品实体列表转换为产品VO列表
        for (Product product : products) {
            // 遍历产品VO列表，设置每个产品的类别名称
            ProductVo productVo = getProduct(product.getId());
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
    @Transactional
    public ProductVo getProduct(Long id) {
		if (productConfig.isProductCacheEnabled() && redisService.hasProduct(id)){
			ProductCache productCache = redisService.getProduct(id);
			ProductVo product = BeanCopyUtil.copyBean(productCache, ProductVo.class);
			assert product != null;
			CategoryVo category = categoryService.getCategory(productCache.getCategoryId());
			product.setCategory(category);
			redisService.refreshProduct(id);
			return product;
		}
        // 通过ID从数据库中查询产品信息
        Product product = productMapper.selectById(id);
        // 如果数据库中存在该产品
        if (product != null) {
            // 将查询到的Product对象转换为ProductVo对象
            ProductVo productVo = BeanCopyUtil.copyBean(product, ProductVo.class);
			if (productConfig.isProductCacheEnabled()){
				ProductCache productCache = BeanCopyUtil.copyBean(product, ProductCache.class);
				assert productCache != null;
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
    @Transactional
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
    @Transactional
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
    @Transactional
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
    @Transactional
    public Long queryStoksById(Long productId) {
        // 返回产品的库存数量
	    //TODO:需要判断是否上架，防止爆破
        return productMapper.getStocksById(productId);
    }

    /**
     * 根据商品编码查询商品信息
     *
     * @param code 商品编码
     * @return 如果找到商品返回true，否则返回false
     */
    @Override
    @Transactional
    public Boolean queryCode(String code) {
        // 执行查询并判断结果是否为空，返回查询结果的布尔值
        return productMapper.getCountByCode(code) != 0;
    }

    @Override
    @Transactional
    public Long getProductNum() {
	    return productMapper.selectCount(null);
    }

    @Override
    @Transactional
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
	        EsProduct esProduct = BeanCopyUtil.copyBean(product, EsProduct.class);
//		    assert esProduct != null;
		     try {
		        if (esProduct == null) {
		            logger.error("商品转换失败，无法生成ES实体: {}", productId);
		            return;
		        }

		        // 构建 Index 请求
		        IndexRequest<EsProduct> request = IndexRequest.of(b -> b
		            .index("products")         // 索引名称
		            .id(productId.toString())   // 使用商品ID作为文档ID
		            .document(esProduct)        // 设置文档内容
		        );

		        // 执行请求
		        esClient.index(request);

		        logger.info("商品已同步至Elasticsearch: {}", productId);
		    } catch (Exception e) {
		        logger.error("同步商品到Elasticsearch失败，商品ID: {}", productId, e);
		    }
	    }
	}

	private void deleteProductFromEs(@NotNull Long productId) {
	    try {
	        DeleteRequest request = DeleteRequest.of(b -> b
	            .index("products")
	            .id(productId.toString())
	        );
	        esClient.delete(request);
	        logger.info("商品已从Elasticsearch中删除: {}", productId);
	    } catch (Exception e) {
	        logger.error("删除商品在Elasticsearch中失败，商品ID: {}", productId, e);
	    }
	}

	private void refreshCache(Long productId) {
//	    String cacheKey = CACHE_PREFIX + productId;
//	    redisTemplate.delete(cacheKey);
//	    Product product = baseMapper.selectById(productId);
//	    if (product != null) {
//	        redisTemplate.opsForValue().set(cacheKey, product, 30, TimeUnit.MINUTES);
//	    }
	}

	@Override
	@Transactional
	public ProductSnapshotVo getSnapshot(Long id) {
		//TODO:使用拦截器判断是否合法
		if (productConfig.isProductCacheEnabled() && redisService.hasSnapshotCache(id)){
			ProductSnapshotCache productCache = redisService.getSnapshotCache(id);
			ProductSnapshotVo product = BeanCopyUtil.copyBean(productCache, ProductSnapshotVo.class);
			assert product != null;
			CategoryVo category = JSON.parseObject(productCache.getCategory(), CategoryVo.class);
			product.setCategory(category);
			redisService.refreshSnapshot(id);
			return product;
		}
        // 通过ID从数据库中查询产品信息
        ProductSnapshot product = productSnapshotMapper.selectById(id);
        // 如果数据库中存在该产品
        if (product != null) {
            ProductSnapshotVo productVo = BeanCopyUtil.copyBean(product, ProductSnapshotVo.class);
			if (productConfig.isProductCacheEnabled()){
				ProductSnapshotCache productCache = BeanCopyUtil.copyBean(product, ProductSnapshotCache.class);
				assert productCache != null;
				redisService.setSnapshotCache(productCache);
	        }
            CategoryVo category = JSON.parseObject(product.getCategory(), CategoryVo.class);
	        assert productVo != null;
			productVo.setCategory(category);
			return productVo;
        }else{
            return null;
        }
	}

	@Override
	@Transactional
	public Long getSnapshotId(ProductVo product) {
	    String hash = getHash(product);
	    Long id = productSnapshotMapper.getIdByHash(hash);
	    if (id != null) {
	        return id;
	    } else {
	        ProductSnapshot productSnapshot = new ProductSnapshot();
	        productSnapshot.setHash(hash);
	        productSnapshot.setProdId(product.getId());
	        productSnapshot.setCode(product.getCode());
	        productSnapshot.setTitle(product.getTitle());
	        productSnapshot.setCategory(JSON.toJSONString(product.getCategory()));
	        productSnapshot.setImg(product.getImg());
	        productSnapshot.setPrice(product.getPrice());
	        productSnapshot.setDescription(product.getDescription());
	        productSnapshot.setProperties(product.getProperties());

	        if (productSnapshotMapper.insert(productSnapshot) > 0) {
	            Long insertedId = productSnapshot.getId();
	            if (insertedId != null) {
	                return insertedId;
	            } else {
	                logger.error("插入产品快照成功，但未获取到主键ID，请检查Mapper配置！");
	                return -1L;
	            }
	        } else {
	            logger.warn("插入产品快照失败，影响行数为0");
	            return -1L;
	        }
	    }
	}

	@Deprecated
	private @NotNull String getHash(@NotNull ProductVo product) {
		Map<String, Object> map = new HashMap<>();
		map.put("id", product.getId());
		map.put("code", product.getCode());
		map.put("title", product.getTitle());
		map.put("price", product.getPrice());
		map.put("img", product.getImg());
		map.put("description", product.getDescription());
		map.put("category", JSON.toJSONString(product.getCategory()));
		map.put("properties", product.getProperties());
		return SecureUtil.sha256Hex(JSON.toJSONString(map));
	}

}
