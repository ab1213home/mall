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

package com.jiang.mall.controller.category;

import com.jiang.mall.domain.ResponseResult;
import com.jiang.mall.domain.vo.CategoryVo;
import com.jiang.mall.service.ICategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


/**
 * 商品控制器
 * @author jiang
 * @version 1.0
 * @since 2024年9月8日
 */
@RestController
@RequestMapping("/category")
public class CategoryController {

    private ICategoryService categoryService;

    @Autowired
    public void setCategoryService(ICategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/getTopList")
    public ResponseResult<Object> getCategoryTopList(@RequestParam(defaultValue = "1") Integer pageNum,
                                                     @RequestParam(defaultValue = "5") Integer pageSize) {
        // 调用服务方法获取分类列表
        List<CategoryVo> categoryVos = categoryService.getCategoryTopList(pageNum, pageSize);

        // 检查返回的列表是否为空
        if (categoryVos.isEmpty()) {
            // 如果列表为空，返回资源未找到的响应结果
            return ResponseResult.notFoundResourceResult("没有找到资源");
        }

        // 如果列表不为空，返回成功的响应结果，携带分类列表数据
        return ResponseResult.okResult(categoryVos);
    }
}
