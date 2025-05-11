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
import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.RangeRelation;
import co.elastic.clients.elasticsearch.core.DeleteRequest;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiang.mall.config.CoreConfig;
import com.jiang.mall.dao.ProductMapper;
import com.jiang.mall.dao.ProductSnapshotMapper;
import com.jiang.mall.domain.cache.ProductCache;
import com.jiang.mall.domain.cache.ProductSnapshotCache;
import com.jiang.mall.domain.cache.UserCache;
import com.jiang.mall.domain.entity.EsProduct;
import com.jiang.mall.domain.entity.Product;
import com.jiang.mall.domain.entity.ProductSnapshot;
import com.jiang.mall.domain.vo.CategoryVo;
import com.jiang.mall.domain.vo.ProductSnapshotVo;
import com.jiang.mall.domain.vo.ProductVo;
import com.jiang.mall.service.*;
import com.jiang.mall.util.BeanCopyUtil;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
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

	private CoreConfig coreConfig;

	@Autowired
	private void setCoreConfig(CoreConfig coreConfig) {
		this.coreConfig = coreConfig;
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

	private IUserService userService;

	@Autowired
	public void setUserService(IUserService userService) {
		this.userService = userService;
	}

	private IOrderService orderService;

	@Autowired
	public void setOrderService(@Lazy IOrderService orderService) {
		this.orderService = orderService;
	}

	@Transactional
	protected List<ProductVo> fallbackGetProductList(String name, Long categoryId, Integer pageNum, Integer pageSize, List<Integer> status) {
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
		queryWrapper.in(Product::getStatus, status);
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

	@Override
	public boolean hasProduct(Long id) {
		if (coreConfig.isProductCacheEnabled() && redisService.hasProduct(id)){
			redisService.refreshProduct(id);
			return true;
		}
		QueryWrapper<Product> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("id", id);
        return productMapper.selectCount(queryWrapper)>0;
	}

//	@Override
//	public boolean hasSnapshot(Long id, Long userId) {
//		return false;
//	}

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
		if (coreConfig.isProductCacheEnabled() && redisService.hasProduct(id)){
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
			if (coreConfig.isProductCacheEnabled()){
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
    public Boolean insertProduct(@NotNull Product product) {
		if (!categoryService.hasCategory(product.getCategoryId())){
			return null;
		}
        if (productMapper.insert(product) == 1){
			if  (coreConfig.isProductCacheEnabled() && product.getStatus()==1){
				ProductCache productCache = BeanCopyUtil.copyBean(product, ProductCache.class);
				assert productCache != null;
				redisService.setProduct(productCache);
			}
			EsProduct esProduct = BeanCopyUtil.copyBean(product, EsProduct.class);
			try {
				assert esProduct != null;
				IndexRequest<EsProduct> request = esProduct.toIndexRequest("products");
		        esClient.index(request);
			} catch (IOException e) {
				logger.error("Elasticsearch 插入商品失败: {}", e.getMessage(), e);
			}
			return true;
        }else{
			return false;
		}
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
    public Boolean updateProduct(@NotNull Product product) {
		if (!categoryService.hasCategory(product.getCategoryId())){
			return null;
		}
        if (productMapper.updateById(product)==1){
			if (coreConfig.isProductCacheEnabled() && redisService.hasProduct(product.getId()) && product.getStatus()==1){
				ProductCache productCache = redisService.getProduct(product.getId());
				assert productCache != null;
				redisService.setProduct(productCache);
			}
			try {
				EsProduct esProduct = BeanCopyUtil.copyBean(product, EsProduct.class);
				assert esProduct != null;
				esClient.update(u -> u
				        .index("products")
				        .id(esProduct.getId().toString())
				        .upsert(esProduct),
				    EsProduct.class
				);
			} catch (IOException e) {
				logger.error("Elasticsearch 更新商品失败: {}", e.getMessage(), e);
			}
	        return true;
        }else{
			return false;
		}
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
        if (productMapper.deleteById(id) == 1){
			if (coreConfig.isProductCacheEnabled() && redisService.hasProduct(id)){
				redisService.deleteProduct(id);
			}
			try {
		        DeleteRequest request = DeleteRequest.of(b -> b
		            .index("products")
		            .id(id.toString())
		        );
		        esClient.delete(request);
		    } catch (Exception e) {
		        logger.error("删除商品在Elasticsearch中失败，商品ID: {}", id, e);
		    }
			//TODO:删除用户购物车收藏的商品

	        return true;
        }else{
			return false;
		}
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
		try {
			if (!esClient.indices().exists(ex -> ex.index("products")).value()) {
				esClient.indices().delete(d -> d.index("products"));
			}
		} catch (IOException e) {
			logger.error("删除索引失败: {}", e.getMessage());
		}
		try {
			// 创建新索引并指定映射
			esClient.indices().create(c -> c
			    .index("products")
			    .mappings(m -> m
			         .properties("id", p -> p.long_(l -> l))
			         .properties("title", p -> p.text(t -> t.analyzer("ik_max_word")))
			         .properties("category_id", p -> p.long_(l -> l))
			         .properties("price", p -> p.double_(d -> d))
					 .properties("description", p -> p.text(t -> t.analyzer("ik_max_word")))
					 .properties("code", p -> p.text(t -> t))
					 .properties("status", p -> p.integer(i -> i))
			    )
			);
		} catch (ElasticsearchException | IOException e) {
			logger.error("创建索引失败: {}", e.getMessage());
		}
		QueryWrapper<Product> queryWrapper = new QueryWrapper<>();
		List<Product> products = productMapper.selectList(queryWrapper);
		for (Product product : products) {
			EsProduct esProduct = BeanCopyUtil.copyBean(product, EsProduct.class);
			try {
				assert esProduct != null;
				IndexRequest<EsProduct> request = esProduct.toIndexRequest("products");
		        esClient.index(request);
			} catch (IOException e) {
				logger.error("同步商品到ES失败: {}", e.getMessage());
			}
			if (coreConfig.isProductCacheEnabled()){
				ProductCache productCache = BeanCopyUtil.copyBean(product, ProductCache.class);
				assert productCache != null;
				redisService.setProduct(productCache);
			}
		}
	}

	@Override
	@Transactional
	public ProductSnapshotVo getSnapshot(Long id, String sessionId) {
		UserCache user = userService.getUserFromRedis(sessionId);
		if (orderService.hasSnapshot(id, user.getId())){
			return getSnapshot(id);
		}else{
			return null;
		}
	}

	@Transactional
	@Override
	public ProductSnapshotVo getSnapshot(Long id) {
		if (coreConfig.isProductCacheEnabled() && redisService.hasSnapshotCache(id)){
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
			if (coreConfig.isProductCacheEnabled()){
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
	public Long getSnapshotId(@NotNull ProductVo product) {
	    String hash = product.getHash();
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

	/**
	 * 根据多个条件获取产品列表
	 *
	 * @param name 产品名称
	 * @param categoryId 分类ID
	 * @param status 产品状态列表
	 * @param minPrice 最低价格
	 * @param maxPrice 最高价格
	 * @param detail 产品详情描述
	 * @param code 产品编码
	 * @param pageNum 当前页码
	 * @param pageSize 每页大小
	 * @return 产品列表
	 */
	@Override
	@Transactional
	public List<ProductVo> getProductList(String name, Long categoryId, List<Integer> status, BigDecimal minPrice, BigDecimal maxPrice, String detail, String code, Integer pageNum, Integer pageSize) {
	    // 参数校验
	    if (pageNum == null || pageNum < 1) pageNum = 1;
	    if (pageSize == null || pageSize < 1) pageSize = 10;
	    try {
	        // 获取分类及其子分类的 ID 列表
	        List<Long> categoryIds = categoryService.getCategoryIds(categoryId);

	        // 构建布尔查询
	        // should子句（查询）必须出现在匹配的文档中。然而，与must不同，查询的分数将被忽略。
	        // filter子句（查询）应出现在匹配的文档中。
	        // must子句（查询）必须出现在匹配的文档中，并将有助于核心
	        // mustNot子句（查询）不得出现在匹配的文档中。因为忽略了评分，所以所有文档的评分都为0。

	        BoolQuery.Builder boolBuilder = new BoolQuery.Builder();
	        // 根据产品名称查询
	        if (StringUtils.hasText(name)) {
	            boolBuilder.must(
	                    query -> query.match(m -> m
	                            .field("title")
	                            .query(name)
	                            .analyzer("ik_max_word") // 指定中文分词器
	                    )
	            );
	        }

	        // 根据分类ID查询
	        if (!categoryIds.isEmpty()) {
	             List<FieldValue> fieldValue = categoryIds.stream()
	                    .map(FieldValue::of)
	                    .toList();
	            boolBuilder.should(
	                    query -> query.terms(t -> t
	                        .field("category_id")
	                        .terms(ts -> ts.value(fieldValue))
	                    )
	             );
	        }

	        // 根据产品状态查询
	        if (status != null && !status.isEmpty()){
	            List<FieldValue> fieldValue = status.stream()
	                    .map(FieldValue::of)
	                    .toList();
	            boolBuilder.should(
	                    query -> query.terms(t -> t
	                        .field("status")
	                        .terms(ts -> ts.value(fieldValue))
	                    )
	            );
	        }

	        // 根据产品编码查询
	        if (StringUtils.hasText(code)){
	            boolBuilder.must(
	                    query -> query.match(m -> m
	                        .field("code")
	                        .query(code)
	                        .analyzer("ik_max_word") // 指定中文分词器
	                    )
	            );
	        }

	        // 全文搜索description
	        if (StringUtils.hasText(detail)) {
	            boolBuilder.must(
	                    query -> query.match(m -> m
	                        .field("description")
	                        .query(detail)
	                        .analyzer("ik_max_word") // 指定中文分词器
	                    )
	            );
	        }

	        // 价格区间过滤
	        if (minPrice != null && maxPrice != null) {
	            // 在查询中使用
	            double minVal = minPrice.setScale(2, RoundingMode.HALF_UP).doubleValue();
	            double maxVal = maxPrice.setScale(2, RoundingMode.HALF_UP).doubleValue();
	            boolBuilder.filter(
	                    query -> query.range(
	                            range -> range.number(n -> n
	                                .field("price")
	                                .gte(minVal)
	                                .lte(maxVal)
	                                .boost(1.5F)
	                                .relation(RangeRelation.Contains)
	                            )
	                    )
	            );
	        }else if (minPrice != null) {
	            // 在查询中使用
	            double minVal = minPrice.setScale(2, RoundingMode.HALF_UP).doubleValue();
	            boolBuilder.filter(
	                    query -> query.range(
	                            range -> range.number(n -> n
	                                .field("price")
	                                .gte(minVal)
	                                .boost(1.0F)
	                                .relation(RangeRelation.Contains)
	                            )
	                    )
	            );
	        }else if (maxPrice != null) {
				// 在查询中使用
	            double maxVal = maxPrice.setScale(2, RoundingMode.HALF_UP).doubleValue();
	            boolBuilder.filter(
	                    query -> query.range(
	                            range -> range.number(n -> n
	                                .field("price")
	                                .lte(maxVal)
	                                .boost(1.0F)
	                                .relation(RangeRelation.Contains)
	                            )
	                    )
	            );
	        }

	        // 构造搜索请求
	        int finalPageSize = pageSize;
	        // 构建分页参数（防止负数）
	        int from = (pageNum - 1) * pageSize;
	        int finalFrom = Math.max(from, 0);

	        SearchResponse<EsProduct> response = esClient.search(s -> s
	            .index("products")
	            .query(q -> q.bool(boolBuilder.build()))
	            .from(finalFrom)
	            .size(finalPageSize),
	            EsProduct.class
	        );

	        // 提取结果
	        List<EsProduct> hits = response.hits().hits().stream()
	                .map(Hit::source)
	                .toList();

	        List<ProductVo> productVos = new ArrayList<>();

	        // 将产品实体列表转换为产品VO列表
	        for (EsProduct esProduct : hits) {
	            ProductVo product = getProduct(esProduct.getId());
	            productVos.add(product);
	        }

	        // 返回产品VO列表
	        return productVos;
	    }catch (Exception e) {
	        logger.error("Elasticsearch 查询商品列表失败: {}", e.getMessage(), e);
	        // 降级到数据库查询
	        return fallbackGetProductList(name, categoryId, pageNum, pageSize, status);
	    }
	}

}
