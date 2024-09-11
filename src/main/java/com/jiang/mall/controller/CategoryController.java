package com.jiang.mall.controller;

import com.jiang.mall.domain.ResponseResult;
import com.jiang.mall.domain.entity.Category;
import com.jiang.mall.domain.vo.CategoryVo;
import com.jiang.mall.service.ICategoryService;
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
    ICategoryService categoryService;

    @GetMapping("/getList")
    public ResponseResult getCategoryList(@RequestParam(defaultValue = "1") Integer pageNum,
                                          @RequestParam(defaultValue = "5") Integer pageSize) {
        List<CategoryVo> categoryVos = categoryService.getCategoryList(pageNum, pageSize);
        if (categoryVos.isEmpty()) {
            return ResponseResult.notFoundResourceResult("没有找到资源");
        }
        return ResponseResult.okResult(categoryVos);
    }

    @GetMapping("/getNum")
    public ResponseResult getCategoryNum() {
        Integer num = categoryService.getCategoryNum();
        return ResponseResult.okResult(num);
    }

    @PostMapping("/add")
    public ResponseResult insertCategory(@RequestParam("")) {
        return categoryService.insertCategory(category);
    }


//    通过GET请求方式接收路径参数"id"，然后调用categoryService对象的getCategory方法获取对应ID的分类信息，并返回处理结果。
    @GetMapping("/admin/{id}")
    public ResponseResult getCategory(@PathVariable Integer id) {
        return categoryService.getCategory(id);
    }



//    函数的主要功能是接收前端发送的POST请求，将请求体中的分类信息解析为Category对象，
//    然后调用服务层的方法插入数据库，并返回插入操作的结果。
    @PostMapping("/admin/insert")
    public ResponseResult insertCategory(@RequestBody Category category){
        return categoryService.insertCategory(category);
    }



//    Post请求处理方法，路径为"/admin/update"。
//    它接收JSON格式的Category对象，并通过categoryService调用其updateCategory方法更新类别信息，最后返回更新结果。
    @PostMapping("/admin/update")
    public ResponseResult updateCategory(@RequestBody Category category){
        return categoryService.updateCategory(category);
    }


//    函数采用POST请求方式，通过"/admin/delete"路径接收请求。
//    它接受一个整数列表ids作为请求体参数，代表要删除的类别ID列表。
//    然后调用categoryService中的deleteCategory方法执行删除操作，并返回操作结果。
    @PostMapping("/admin/delete")
    public ResponseResult deleteCategory(@RequestBody List<Integer> ids){
        return categoryService.deleteCategory(ids);
    }
}
