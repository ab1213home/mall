package com.jiang.mall.controller;

import com.jiang.mall.domain.ResponseResult;
import com.jiang.mall.domain.entity.Category;
import com.jiang.mall.service.ICategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *作者： 蒋神 HJL
 * @since 2024-08-05
 */
@RestController
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    ICategoryService categoryService;



//    通过GET方法接收网页请求并处理路径"/list"。
//    函数调用categoryService提供的方法获取分类列表，并将结果以ResponseResult形式返回。
    @GetMapping("/list")
    public ResponseResult getCategoryList(@RequestParam(defaultValue = "1") Integer pageNum, @RequestParam(defaultValue = "5") Integer pageSize) {
        return categoryService.getCategoryList(pageNum, pageSize);
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
