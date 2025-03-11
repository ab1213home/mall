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
import com.jiang.mall.dao.CategoryMapper;
import com.jiang.mall.domain.entity.Category;
import com.jiang.mall.domain.vo.CategoryVo;
import com.jiang.mall.service.ICategoryService;
import com.jiang.mall.util.BeanCopyUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public Boolean insertCategory(Category category) {

        return categoryMapper.insert(category)==1;
    }

    @Override
    public Boolean updateCategory(Category category) {
        return categoryMapper.updateById(category)==1;
    }

    @Override
    public Long getCategoryNum(Long parentId, Integer level) {
		LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(parentId != null,Category::getParentId, parentId);
		queryWrapper.eq(level != null,Category::getLevel, level);
	    return categoryMapper.selectCount(queryWrapper);
    }

    @Override
    public Boolean deleteCategory(Category category) {
	    return categoryMapper.deleteById(category) == 1;
    }

    @Override
    public List<CategoryVo> getCategoryTopList() {
        QueryWrapper<Category> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("parent_id", 0);
//        Page<Category> categoryPage = new Page<>(pageNum, pageSize);
//        List<Category> category = categoryMapper.selectPage(categoryPage, queryWrapper).getRecords();
	    List<Category> category = categoryMapper.selectList(queryWrapper);
	    return BeanCopyUtils.copyBeanList(category, CategoryVo.class);
    }

    @Override
    public String getCategoryName(Long id) {
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
	public @NotNull List<Category> buildCategoryTree(@NotNull List<Category> list) {
        Map<Long, Category> map = new HashMap<>();
        List<Category> roots = new ArrayList<>();

        for (Category category : list) {
            map.put(category.getId(), category);
            if (category.getParentId() == 0L) {
                roots.add(category);
            }
        }

        for (Category category : list) {
            if (category.getParentId() != 0L) {
                Category parent = map.get(category.getParentId());
                if (parent != null) {
//                    parent.getChildren().add(category); // 假设Category有children字段
                }
            }
        }
        return roots;
    }

}
