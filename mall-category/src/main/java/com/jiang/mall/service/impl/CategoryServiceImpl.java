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
import com.jiang.mall.domain.vo.CategoryVo;
import com.jiang.mall.service.ICategoryRedisService;
import com.jiang.mall.service.ICategoryService;
import com.jiang.mall.util.BeanCopyUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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


    @Override
    @Transactional
    public List<CategoryVo> getCategoryList(Integer pageNum, Integer pageSize, Long parentId, Integer level) {
        Page<Category> categoryPage = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(parentId != null,Category::getParentId, parentId);
		queryWrapper.eq(level != null,Category::getLevel, level);
        List<Category> categories = categoryMapper.selectPage(categoryPage, queryWrapper).getRecords();
        List<CategoryVo> categoryVos = new ArrayList<>();
        for (Category category : categories) {
            CategoryVo categoryVo = BeanCopyUtil.copyBean(category, CategoryVo.class);
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
    public Boolean insertCategory(@NotNull Category category) {
		//检查是否存在父分类
	    if (category.getParentId() == null){
			return null;
		}else if (!hasCategory(category.getParentId())){
			return null;
	    }
	    if (categoryMapper.insert(category)==1){
			if (categoryConfig.isCategoryCacheEnabled()){
				CategoryTreeCache father = redisService.getCategory(category.getParentId());
				if (father != null){
					father.getChildren().add(category.getId());
					redisService.setCategory(father);
				}
				CategoryTreeCache categoryTreeCache = BeanCopyUtil.copyBean(category, CategoryTreeCache.class);
				assert categoryTreeCache != null;
				categoryTreeCache.setChildren(new ArrayList<>());
				redisService.setCategory(categoryTreeCache);
			}
			return true;
		}else{
			return false;
		}
    }

    @Override
    @Transactional
    public Boolean updateCategory(@NotNull Category category) {
		//检查是否存在父分类
	    if (category.getParentId() == null){
			return null;
		}else if (!hasCategory(category.getParentId())){
			return null;
	    }
	    if (categoryMapper.updateById(category) == 1){
			// 调用分类检查机制，确保数据一致性
			if (categoryConfig.isCategoryCacheEnabled() && redisService.hasCategory(category.getId())){
				CategoryTreeCache child = redisService.getCategory(category.getId());
				//判断分类父分类是否有变化
				assert child != null;
				//父分类有变化
				if (!child.getParentId().equals(category.getParentId())){
					//旧父分类更新孩子
					CategoryTreeCache oldFather = redisService.getCategory(child.getParentId());
					if (oldFather != null){
						oldFather.getChildren().remove(category.getId());
						redisService.setCategory(oldFather);
					}
					//新父分类更新孩子
					CategoryTreeCache newFather = redisService.getCategory(category.getParentId());
					if (newFather != null){
						newFather.getChildren().add(category.getId());
						redisService.setCategory(newFather);
					}
				}
				CategoryTreeCache categoryTreeCache = BeanCopyUtil.copyBean(category, CategoryTreeCache.class);
				assert categoryTreeCache != null;
				categoryTreeCache.setChildren(child.getChildren());
				redisService.setCategory(categoryTreeCache);
			}else if (categoryConfig.isCategoryCacheEnabled()){
				//TODO:分类缓存过期？？？
				logger.warn("分类缓存过期");
				checkCategory();
			}
			return true;
		}else{
			// 更新失败，返回false
			return false;
		}
    }

    @Override
    @Transactional
    public Long getCategoryNum(Long parentId, Integer level) {
		LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(parentId != null,Category::getParentId, parentId);
		queryWrapper.eq(level != null,Category::getLevel, level);
	    return categoryMapper.selectCount(queryWrapper);
    }

    @Override
    @Transactional
    public Boolean deleteCategory(Long id) {
	    if (!hasCategory(id)){
			return null;
	    }
		// 递归获取所有子分类id
		List<Long> ids = getCategoryIds(id);
	    if (categoryMapper.deleteByIds(ids) == ids.size()){
			if (categoryConfig.isCategoryCacheEnabled() && redisService.hasCategory(id)){
				CategoryTreeCache categoryTreeCache = redisService.getCategory(id);
				assert categoryTreeCache != null;
				//删除父分类的子分类
				CategoryTreeCache father = redisService.getCategory(categoryTreeCache.getParentId());
				if (father != null){
					father.getChildren().remove(id);
					redisService.setCategory(father);
				}
				//批量删除子分类
				redisService.deleteCategory(ids);
			}else if (categoryConfig.isCategoryCacheEnabled()){
				//TODO:分类缓存过期？？？
				logger.warn("分类缓存过期");
				checkCategory();
			}
			return true;
	    }else{
			return false;
		}
    }

    @Override
    @Transactional
    public List<CategoryVo> getCategoryTopList() {
	    if (categoryConfig.isCategoryCacheEnabled() && redisService.hasCategory(0L)){
			return getCategoryTopListFromRedis();
	    }else {
	        return getCategoryTopListFromMySQL();
	    }
    }


	private @NotNull List<CategoryVo> getCategoryTopListFromMySQL() {
		QueryWrapper<Category> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("parent_id", 0);
	    List<Category> category = categoryMapper.selectList(queryWrapper);
		List<CategoryVo> categoryVos = new ArrayList<>();
		for (Category category_item : category) {
			CategoryVo categoryVo = BeanCopyUtil.copyBean(category_item, CategoryVo.class);
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
				CategoryVo categoryVo = BeanCopyUtil.copyBean(childrenCache, CategoryVo.class);
				assert categoryVo != null;
				categoryVo.setParent("根分类");
				categoryVos.add(categoryVo);
			}else{
				Category category = categoryMapper.selectById(id);
				CategoryVo categoryVo = BeanCopyUtil.copyBean(category, CategoryVo.class);
				assert categoryVo != null;
				categoryVo.setParent("根分类");
				categoryVos.add(categoryVo);
			}
		}
		return categoryVos;
	}

	@Override
	@Transactional
    public String getCategoryName(Long id) {
		if (categoryConfig.isCategoryCacheEnabled() && redisService.hasCategory(id)){
			return getCategoryNameFromRedis(id);
	    }else {
			return getCategoryNameFromMySQL(id);
	    }
    }

	@Transactional
	protected String getCategoryNameFromRedis(Long id) {
		CategoryTreeCache category = redisService.getCategory(id);
		if (category.getParentId() == 0 || category.getParentId() == -1){
			return category.getName();
		}else{
			return getCategoryName(category.getParentId()) + "-" + category.getName();
		}
	}


	@Transactional
	protected String getCategoryNameFromMySQL(Long id) {
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
    @Transactional
    public @NotNull List<Long> getCategoryIds(Long id){
		if (id == null){
			return new ArrayList<>();
		}
		if (categoryConfig.isCategoryCacheEnabled() && redisService.hasCategory(0L)){
			return getCategoryIdsFromRedis(id);
	    }else {
	        return getCategoryIdsFromMySQL(id);
	    }
	}

	@Transactional
	protected @NotNull List<Long> getCategoryIdsFromMySQL(Long id) {
		// 初始化列表以存储类别ID
	    List<Long> categoryIds = new ArrayList<>();
	    // 如果传入的类别ID非空，则继续处理
	    if (id != null){
	        // 将当前类别ID添加到列表中
	        categoryIds.add(id);
	        // 用于查找所有父类别ID等于当前类别ID的子类别
	        List<Long> list = categoryMapper.getIdListByParentId(id);
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

	@Transactional
	protected @NotNull List<Long> getCategoryIdsFromRedis(Long id) {
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
	@Transactional
	public List<CategoryVo> getList() {
		List<Category> categories = categoryMapper.selectList(null);
        List<CategoryVo> categoryVos = new ArrayList<>();
        for (Category category : categories) {
            CategoryVo categoryVo = BeanCopyUtil.copyBean(category, CategoryVo.class);
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
	@Transactional
	public CategoryVo getCategory(Long id) {
		if (categoryConfig.isCategoryCacheEnabled() && redisService.hasCategory(id)){
			return selectByIdFromRedis(id);
	    }else {
			return selectByIdFromMySQL(id);
	    }
	}

	@Override
	@Transactional
	public boolean hasCategory(Long id) {
		if (id == null){
			return false;
		}
		if (id == 0){
			return true;
		}
		if (categoryConfig.isCategoryCacheEnabled() && redisService.hasCategory(id)){
			return true;
	    }else {
			return countByIdFromMySQL(id);
	    }
	}

	private boolean countByIdFromMySQL(Long id) {
		QueryWrapper<Category> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("id", id);
		return categoryMapper.selectCount(queryWrapper) > 0;
	}

	@Transactional
	protected @Nullable CategoryVo selectByIdFromMySQL(Long id) {
		Category category = categoryMapper.selectById(id);
		if (category != null){
			CategoryVo categoryVo = BeanCopyUtil.copyBean(category, CategoryVo.class);
			assert categoryVo != null;
			categoryVo.setParent(getCategoryName(category.getParentId()));
			return categoryVo;
		}
		return null;
	}
	@Transactional
	protected @Nullable CategoryVo selectByIdFromRedis(Long id) {
		CategoryTreeCache category = redisService.getCategory(id);
		if (category != null){
			CategoryVo categoryVo = BeanCopyUtil.copyBean(category, CategoryVo.class);
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
			CategoryTreeCache childCache = BeanCopyUtil.copyBean(child, CategoryTreeCache.class);
			assert childCache != null;
			childCache.setChildren(findCategoryChildren(child.getId()));
			redisService.setCategory(childCache);
			childIds.add(child.getId());
		}
		return childIds;
	}

}
