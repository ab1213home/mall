package com.jiang.mall.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jiang.mall.domain.entity.Category;
import com.jiang.mall.domain.vo.CategoryVo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author jiang
 * @since 2024年9月11日
 */
public interface ICategoryService extends IService<Category> {

    List<CategoryVo> getCategoryList(Integer pageNum, Integer pageSize);

    boolean insertCategory(Category category);

    boolean updateCategory(Category category);

    Long getCategoryNum();

    boolean deleteCategory(Category category);
}
