package com.jiang.mall.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiang.mall.dao.CategoryMapper;
import com.jiang.mall.domain.entity.Category;
import com.jiang.mall.domain.vo.CategoryVo;
import com.jiang.mall.service.ICategoryService;
import com.jiang.mall.util.BeanCopyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    @Override
    public List<CategoryVo> getCategoryList(Integer pageNum, Integer pageSize) {
        Page<Category> categoryPage = new Page<>(pageNum, pageSize);
        List<Category> categorys = categoryMapper.selectPage(categoryPage, null).getRecords();
	    return BeanCopyUtils.copyBeanList(categorys, CategoryVo.class);
    }

    @Override
    public boolean insertCategory(Category category) {
        return categoryMapper.insert(category)==1;
    }

    @Override
    public boolean updateCategory(Category category) {
        return categoryMapper.updateById(category)==1;
    }

    @Override
    public Integer getCategoryNum() {
        List<Category> categorys = categoryMapper.selectList(null);
        return categorys.size();
    }

    @Override
    public boolean deleteCategory(Category category) {
	    return categoryMapper.deleteById(category) == 1;
    }


}
