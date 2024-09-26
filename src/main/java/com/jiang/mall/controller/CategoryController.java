package com.jiang.mall.controller;

import com.jiang.mall.domain.ResponseResult;
import com.jiang.mall.domain.entity.Category;
import com.jiang.mall.domain.vo.CategoryVo;
import com.jiang.mall.service.ICategoryService;
import com.jiang.mall.service.IUserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private ICategoryService categoryService;
    @Autowired
    private IUserService userService;

    /**
     * 获取分类列表
     *
     * @param pageNum  当前页码，默认为1
     * @param pageSize 每页大小，默认为5
     * @return 返回分类列表或相关错误信息
     */
    @GetMapping("/getList")
    public ResponseResult getCategoryList(@RequestParam(defaultValue = "1") Integer pageNum,
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

    /**
     * 通过GET请求获取分类数量
     *
     * @return 返回包含分类数量的响应结果
     */
    @GetMapping("/getNum")
    public ResponseResult getCategoryNum() {
        Integer num = categoryService.getCategoryNum();
        return ResponseResult.okResult(num);
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
    public ResponseResult insertCategory(@RequestParam("code")String code,
                                         @RequestParam("name")String name,
                                         HttpSession session) {
        // 检查会话中是否设置表示用户已登录的标志
        ResponseResult result = userService.checkAdminUser(session);
        // 如果用户未登录或无管理员权限，则返回错误结果
        if (!result.isSuccess()) {
            return result;
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
    public ResponseResult updateCategory(@RequestParam("id") Integer id,
                                         @RequestParam("code") String code,
                                         @RequestParam("name") String name,
                                         HttpSession session) {
        // 根据ID获取分类信息
        Category category = categoryService.getById(id);
        if (category == null) {
            return ResponseResult.notFoundResourceResult("没有找到资源");
        }

        // 判断当前用户是否有权限进行更新操作
        ResponseResult result = userService.hasPermission(category.getUpdater(), session);
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
    public ResponseResult deleteCategory(@RequestParam("id") Integer id,
                                         HttpSession session) {
        // 根据ID获取分类信息
        Category category = categoryService.getById(id);
        // 如果没有找到对应的分类，返回未找到资源的错误
        if (category == null) {
            return ResponseResult.notFoundResourceResult("没有找到资源");
        }

        // 检查当前会话中用户是否已登录并有权限进行操作
        ResponseResult result = userService.hasPermission(category.getUpdater(), session);
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
