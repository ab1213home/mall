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

package com.jiang.mall.controller;

import com.jiang.mall.domain.ResponseResult;
import com.jiang.mall.domain.entity.Category;
import com.jiang.mall.domain.vo.CategoryVo;
import com.jiang.mall.service.ICategoryService;
import com.jiang.mall.service.IUserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

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

    /**
     * 注入分类服务实例
     *
     * @param categoryService 分类服务实例
     */
    @Autowired
    public void setCategoryService(ICategoryService categoryService) {
        this.categoryService = categoryService;
    }

    private IUserService userService;

    /**
     * 注入用户服务实例
     *
     * @param userService 用户服务实例
     */
    @Autowired
    public void setUserService(IUserService userService) {
        this.userService = userService;
    }
    /**
     * 获取分类列表
     *
     * @param pageNum  当前页码，默认为1
     * @param pageSize 每页大小，默认为5
     * @return 返回分类列表或相关错误信息
     */
    @GetMapping("/getList")
    public ResponseResult<Object> getCategoryList(@RequestParam(defaultValue = "1") Integer pageNum,
                                          @RequestParam(defaultValue = "5") Integer pageSize) {
        // 调用服务方法获取分类列表
        List<CategoryVo> categoryVos = categoryService.getCategoryList(pageNum, pageSize);

        // 检查返回的列表是否为空
        if (categoryVos.isEmpty()) {
            // 如果列表为空，返回资源未找到的响应结果
            return ResponseResult.notFoundResourceResult("没有找到资源");
        }

        // 如果列表不为空，返回成功的响应结果，携带分类列表数据
        return ResponseResult.okResult(categoryVos);
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

    /**
     * 通过GET请求获取分类数量
     *
     * @return 返回包含分类数量的响应结果
     */
    @GetMapping("/getNum")
    public ResponseResult<Object> getCategoryNum() {
        return ResponseResult.okResult(categoryService.getCategoryNum());
    }

    /**
     * 添加分类信息
     * <p>
     * 通过POST请求接收前端传来的分类代码和名称，验证用户是否登录并有管理员权限后，插入数据库
     *
     * @param code 分类的代码
     * @param name 分类的名称
     * @param session 当前的会话，用于验证用户登录状态
     * @return 插入操作的结果或错误信息
     */
    @PostMapping("/add")
    public ResponseResult<Object> insertCategory(@RequestParam("code")String code,
                                         @RequestParam("name")String name,
                                         HttpSession session) {
        // 检查会话中是否设置表示用户已登录的标志
        ResponseResult<Object> result = userService.checkAdminUser(session);
        // 如果用户未登录或无管理员权限，则返回错误结果
        if (!result.isSuccess()) {
            return result;
        }
        if (code == null || name == null) {
            return ResponseResult.failResult("参数错误");
        }
        if (!StringUtils.hasText(code)){
            return ResponseResult.failResult("请输入分类代码");
        }
        if (!StringUtils.hasText(name)){
            return ResponseResult.failResult("请输入分类名称");
        }
        // 创建Category对象，使用传入的代码和名称进行初始化
        Category category = new Category(code, name);
        // 调用服务层方法尝试插入分类信息，根据插入结果返回相应响应
        if (categoryService.insertCategory(category)) {
            return ResponseResult.okResult();
        } else {
            return ResponseResult.serverErrorResult("添加失败");
        }
    }

    /**
     * 更新分类信息
     *
     * @param id 分类的ID
     * @param code 分类的代码
     * @param name 分类的名称
     * @param session 当前会话
     * @return 返回操作结果
     */
    @PostMapping("/update")
    public ResponseResult<Object> updateCategory(@RequestParam("id") Long id,
                                         @RequestParam("code") String code,
                                         @RequestParam("name") String name,
                                         HttpSession session) {
        if (id == null || code == null || name == null||id <= 0) {
            return ResponseResult.failResult("参数错误");
        }
        if (!StringUtils.hasText(code)){
            return ResponseResult.failResult("请输入分类代码");
        }
        if (!StringUtils.hasText(name)){
            return ResponseResult.failResult("请输入分类名称");
        }
        if (!StringUtils.hasText(id.toString())){
            return ResponseResult.failResult("请输入分类ID");
        }
        // 根据ID获取分类信息
        Category category = categoryService.getById(id);
        if (category == null) {
            return ResponseResult.notFoundResourceResult("没有找到资源");
        }

        // 判断当前用户是否有权限进行更新操作
        ResponseResult<Object> result = userService.hasPermission(category.getUpdater(), session);
        if (!result.isSuccess()) {
            return result;
        }

        // 创建一个新的Category对象并设置其属性
        category = new Category(id, code, name);
        // 尝试更新数据库中的分类信息
        if (categoryService.updateCategory(category)) {
            return ResponseResult.okResult();
        } else {
            return ResponseResult.serverErrorResult("修改失败");
        }
    }

    /**
     * 处理删除分类的请求
     *
     * @param id 分类的ID
     * @param session HTTP会话，用于检查用户登录状态和权限
     * @return 删除操作的结果
     */
    @GetMapping("/delete")
    public ResponseResult<Object> deleteCategory(@RequestParam("id") Integer id,
                                         HttpSession session) {
        if (id == null || id <= 0) {
            return ResponseResult.failResult("参数错误");
        }
        if (!StringUtils.hasText(id.toString())){
            return ResponseResult.failResult("请输入分类ID");
        }
        // 根据ID获取分类信息
        Category category = categoryService.getById(id);
        // 如果没有找到对应的分类，返回未找到资源的错误
        if (category == null) {
            return ResponseResult.notFoundResourceResult("没有找到资源");
        }

        // 检查当前会话中用户是否已登录并有权限进行操作
        ResponseResult<Object> result = userService.hasPermission(category.getUpdater(), session);
        // 如果用户没有权限（未登录或不是要求的管理员角色），返回错误信息
        if (!result.isSuccess()) {
            return result;
        }

        // 尝试从数据库中删除分类
        if (categoryService.deleteCategory(category)) {
            // 删除成功，返回成功信息
            return ResponseResult.okResult();
        } else {
            // 删除失败，返回服务器错误信息
            return ResponseResult.serverErrorResult("修改失败");
        }
    }
}
