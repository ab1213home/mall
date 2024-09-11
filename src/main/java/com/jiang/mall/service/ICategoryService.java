package com.jiang.mall.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jiang.mall.domain.ResponseResult;
import com.jiang.mall.domain.entity.Category;
import com.jiang.mall.domain.vo.CategoryVo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author WH
 * @since 2023-06-25
 */
public interface ICategoryService extends IService<Category> {

    List<CategoryVo> getCategoryList(Integer pageNum, Integer pageSize);

    ResponseResult getCategory(Integer id);

    ResponseResult insertCategory(Category category);

    ResponseResult updateCategory(Category category);

    ResponseResult deleteCategory(List<Integer> ids);

    Integer getCategoryNum();
}
