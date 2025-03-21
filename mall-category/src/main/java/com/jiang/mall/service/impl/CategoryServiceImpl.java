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
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiang.mall.config.CategoryConfig;
import com.jiang.mall.dao.CategoryMapper;
import com.jiang.mall.domain.cache.CategoryTreeCache;
import com.jiang.mall.domain.entity.Category;
import com.jiang.mall.domain.enums.ChangeType;
import com.jiang.mall.domain.vo.CategoryVo;
import com.jiang.mall.event.CategoryChangedEvent;
import com.jiang.mall.service.ICategoryRedisService;
import com.jiang.mall.service.ICategoryService;
import com.jiang.mall.util.BeanCopyUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author jiang
 * @since 2024年9月11日
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements ICategoryService {

    private CategoryMapper categoryMapper;

    @Autowired
    public void setCategoryMapper(CategoryMapper categoryMapper) {
        this.categoryMapper = categoryMapper;
    }

	private ICategoryRedisService redisService;

	@Autowired
	public void setRedisService(ICategoryRedisService redisService) {
		this.redisService = redisService;
	}

	private CategoryConfig categoryConfig;

	@Autowired
	public void setCategoryConfig(CategoryConfig categoryConfig) {
		this.categoryConfig = categoryConfig;
	}

	private static final Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);

	private ApplicationEventPublisher eventPublisher;

	@Autowired
	public void setEventPublisher(ApplicationEventPublisher eventPublisher) {
		this.eventPublisher = eventPublisher;
	}

    @Override
    public List<CategoryVo> getCategoryList(Integer pageNum, Integer pageSize, Long parentId, Integer level) {
        Page<Category> categoryPage = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(parentId != null,Category::getParentId, parentId);
		queryWrapper.eq(level != null,Category::getLevel, level);
        List<Category> categories = categoryMapper.selectPage(categoryPage, queryWrapper).getRecords();
        List<CategoryVo> categoryVos = new ArrayList<>();
        for (Category category : categories) {
            CategoryVo categoryVo = BeanCopyUtils.copyBean(category, CategoryVo.class);
	        assert categoryVo != null;
			if (category.getParentId() == 0){
				categoryVo.setParent("根分类");
			}else{
				categoryVo.setParent(getCategoryName(category.getParentId()));
			}
            categoryVos.add(categoryVo);
        }
	    return categoryVos;
    }

    @Override
    @Transactional
    public Boolean insertCategory(Category category) {
	    if (categoryMapper.insert(category)==1){
			// 调用分类检查机制，确保数据一致性
			eventPublisher.publishEvent(
					new CategoryChangedEvent(this, category.getId(), ChangeType.CREATE)
			);
			return true;
		}else{
			return false;
		}
    }

    @Override
    @Transactional
    public Boolean updateCategory(@NotNull Category category) {
		Category oldCategory = categoryMapper.selectById(category.getId());
		//TODO: 更新父分类同时也要更新子分类
	    if (categoryMapper.updateById(category) == 1){
			// 调用分类检查机制，确保数据一致性
			eventPublisher.publishEvent(
					new CategoryChangedEvent(this, category.getId(), ChangeType.UPDATE)
			);
			return true;
		}else{
			// 更新失败，返回false
			return false;
		}
    }

    @Override
    public Long getCategoryNum(Long parentId, Integer level) {
		LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(parentId != null,Category::getParentId, parentId);
		queryWrapper.eq(level != null,Category::getLevel, level);
	    return categoryMapper.selectCount(queryWrapper);
    }

    @Override
    @Transactional
    public Boolean deleteCategory(Long id) {
		Category oldCategory = categoryMapper.selectById(id);
		//TODO:删除父分类同时也要删除子分类
	    if (categoryMapper.deleteById(id) == 1){
			// 调用分类检查机制，确保数据一致性
			eventPublisher.publishEvent(
					new CategoryChangedEvent(this, id, ChangeType.DELETE)
			);
			return true;
		}else{
			return false;
		}
    }

    @Override
    public List<CategoryVo> getCategoryTopList() {
	    if (categoryConfig.isCategoryCacheEnabled() && redisService.hasCategory(0L)){
			logger.debug("从redis中获取分类数据");
			return getCategoryTopListFromRedis();
	    }else {
			logger.debug("从mysql中获取分类数据");
	        return getCategoryTopListFromMySQL();
	    }
    }

	private @NotNull List<CategoryVo> getCategoryTopListFromMySQL() {
		QueryWrapper<Category> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("parent_id", 0);
	    List<Category> category = categoryMapper.selectList(queryWrapper);
		List<CategoryVo> categoryVos = new ArrayList<>();
		for (Category category_item : category) {
			CategoryVo categoryVo = BeanCopyUtils.copyBean(category_item, CategoryVo.class);
			assert categoryVo != null;
			categoryVo.setParent("根分类");
			categoryVos.add(categoryVo);
		}
	    return categoryVos;
	}

	private @NotNull List<CategoryVo> getCategoryTopListFromRedis() {
		CategoryTreeCache rootCache = redisService.getCategory(0L);
		List<CategoryVo> categoryVos = new ArrayList<>();
		for (Long id : rootCache.getChildren()){
			if (redisService.hasCategory(id)){
				CategoryTreeCache childrenCache = redisService.getCategory(id);
				CategoryVo categoryVo = BeanCopyUtils.copyBean(childrenCache, CategoryVo.class);
				assert categoryVo != null;
				categoryVo.setParent("根分类");
				categoryVos.add(categoryVo);
			}else{
				Category category = categoryMapper.selectById(id);
				CategoryVo categoryVo = BeanCopyUtils.copyBean(category, CategoryVo.class);
				assert categoryVo != null;
				categoryVo.setParent("根分类");
				categoryVos.add(categoryVo);
			}
		}
		return categoryVos;
	}

	@Override
    public String getCategoryName(Long id) {
		if (categoryConfig.isCategoryCacheEnabled() && redisService.hasCategory(id)){
			logger.debug("从Redis中获取分类名称");
			return getCategoryNameFromRedis(id);
	    }else {
			logger.debug("从MySQL中获取分类名称");
			return getCategoryNameFromMySQL(id);
	    }
    }

	private String getCategoryNameFromRedis(Long id) {
		CategoryTreeCache category = redisService.getCategory(id);
		if (category.getParentId() == 0){
			return category.getName();
		}else{
			return getCategoryName(category.getParentId()) + "-" + category.getName();
		}
	}

	private String getCategoryNameFromMySQL(Long id) {
		Category category = categoryMapper.selectById(id);
		if (category != null){
			if (category.getParentId() == 0){
				return category.getName();
			}else{
				return getCategoryName(category.getParentId()) + "-" + category.getName();
			}
		}else{
			return "";
		}
	}

	/**
	 * 获取指定类别ID及其所有子类别的ID
	 * <p>
	 * 该方法用于递归地收集给定类别ID下的所有子类别ID，包括自身ID在内它首先检查传入的类别ID是否非空，
	 * 然后创建一个查询条件以查找所有父类别ID匹配的子类别，并对每个找到的子类别递归调用自身，
	 * 直到收集完所有相关子类别ID
	 *
	 * @param id 指定的类别ID，作为收集的起始点如果传入的ID为null，方法将返回一个空的列表
	 * @return 包含指定类别及其所有子类别ID的列表
	 */
    @Override
    public @NotNull List<Long> getCategoryIds(Long id){
		if (categoryConfig.isCategoryCacheEnabled() && redisService.hasCategory(0L)){
			logger.debug("从Redis中获取子类别ID");
			return getCategoryIdsFromRedis(id);
	    }else {
			logger.debug("从MySQL中获取子类别ID");
	        return getCategoryIdsFromMySQL(id);
	    }
	}

	private @NotNull List<Long> getCategoryIdsFromMySQL(Long id) {
		// 初始化列表以存储类别ID
	    List<Long> categoryIds = new ArrayList<>();
	    // 如果传入的类别ID非空，则继续处理
	    if (id != null){
	        // 将当前类别ID添加到列表中
	        categoryIds.add(id);
	        // 用于查找所有父类别ID等于当前类别ID的子类别
	        List<Long> list = categoryMapper.selectIdListByParentId(id);
            if (list.isEmpty()){
                return categoryIds;
            }
	        // 遍历子类别列表，对每个子类别递归调用本方法，并合并结果
	        for (Long _id : list) {
	            List<Long> ids = getCategoryIds(_id);
	            categoryIds.addAll(ids);
	        }
	    }
	    // 返回收集到的所有类别ID列表
	    return categoryIds;
	}

	private @NotNull List<Long> getCategoryIdsFromRedis(Long id) {
		// 初始化列表以存储类别ID
	    List<Long> categoryIds = new ArrayList<>();
	    // 如果传入的类别ID非空，则继续处理
	    if (id != null){
	        // 将当前类别ID添加到列表中
	        categoryIds.add(id);
	        // 用于查找所有父类别ID等于当前类别ID的子类别
		    CategoryTreeCache rootCache = redisService.getCategory(id);
			if (rootCache.getChildren() == null){
				return categoryIds;
			}
			// 遍历子类别列表，对每个子类别递归调用本方法，并合并结果
			for (Long _id : rootCache.getChildren()){
				List<Long> ids = getCategoryIds(_id);
	            categoryIds.addAll(ids);
			}
	    }
	    // 返回收集到的所有类别ID列表
	    return categoryIds;
	}

	@Override
	public List<CategoryVo> getList() {
		List<Category> categories = categoryMapper.selectList(null);
        List<CategoryVo> categoryVos = new ArrayList<>();
        for (Category category : categories) {
            CategoryVo categoryVo = BeanCopyUtils.copyBean(category, CategoryVo.class);
	        assert categoryVo != null;
			if (category.getParentId() == 0){
				categoryVo.setParent("根分类");
			}else{
				categoryVo.setParent(getCategoryName(category.getParentId()));
			}
            categoryVos.add(categoryVo);
        }
	    return categoryVos;
	}

	@Override
	public void checkCategory() {
		CategoryTreeCache rootCache = new CategoryTreeCache();
		rootCache.setId(0L);
		rootCache.setName("根分类");
		rootCache.setParentId(-1L);
		rootCache.setLevel(0);
		rootCache.setSort(0);
		rootCache.setChildren(findCategoryChildren(0L));
		redisService.setCategory(rootCache);
	}

	@Override
	public CategoryVo selectById(Long id) {
		if (categoryConfig.isCategoryCacheEnabled() && redisService.hasCategory(id)){
			logger.debug("从Redis中获取分类");
			return selectByIdFromRedis(id);
	    }else {
			logger.debug("从MySQL中获取分类");
			return selectByIdFromMySQL(id);
	    }
	}

	private @Nullable CategoryVo selectByIdFromMySQL(Long id) {
		Category category = categoryMapper.selectById(id);
		if (category != null){
			CategoryVo categoryVo = BeanCopyUtils.copyBean(category, CategoryVo.class);
			assert categoryVo != null;
			categoryVo.setParent(getCategoryName(category.getParentId()));
			return categoryVo;
		}
		return null;
	}

	private @Nullable CategoryVo selectByIdFromRedis(Long id) {
		CategoryTreeCache category = redisService.getCategory(id);
		if (category != null){
			CategoryVo categoryVo = BeanCopyUtils.copyBean(category, CategoryVo.class);
			assert categoryVo != null;
			categoryVo.setParent(getCategoryName(category.getParentId()));
			return categoryVo;
		}
		return null;
	}

	private @NotNull List<Long> findCategoryChildren(@NotNull Long id) {
		QueryWrapper<Category> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("parent_id", id);
		List<Category> children = categoryMapper.selectList(queryWrapper);
		List<Long> childIds = new ArrayList<>();
		for (Category child : children) {
			CategoryTreeCache childCache = BeanCopyUtils.copyBean(child, CategoryTreeCache.class);
			assert childCache != null;
//			childCache.setId(child.getId());
//			childCache.setName(child.getName());
			childCache.setParentId(id);
//			childCache.setLevel(child.getLevel());
//			childCache.setSort(child.getSort());
			childCache.setChildren(findCategoryChildren(child.getId()));
			redisService.setCategory(childCache);
			childIds.add(child.getId());
		}
		return childIds;
	}

	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handleCategoryChangedEvent(CategoryChangedEvent event) {
	    try {
			if (categoryConfig.isCategoryCacheEnabled()){
				switch (event.getChangeType()) {
		            case CREATE:
						insertCategoryToRedis(event.getCategoryId());
						break;
		            case UPDATE:
		                updateCategoryToRedis(event.getCategoryId());
		                break;
		            case DELETE:
		                deleteCategoryFromRedis(event.getCategoryId());
		                break;
		        }
	//	        refreshCache(event.getCategoryId());
			}else{
				logger.debug("分类缓存已禁用。");
			}

	    } catch (Exception e) {
			logger.error("处理分类变更事件失败: {}", e.getMessage());
	        // 可添加重试逻辑
	    }
	}

	private void deleteCategoryFromRedis(Long id) {

	}

	private void updateCategoryToRedis(Long id) {

	}

	private void insertCategoryToRedis(Long id) {

	}

}
