package com.jiang.mall.controller;

import com.jiang.mall.domain.ResponseResult;
import com.jiang.mall.domain.entity.Product;
import com.jiang.mall.service.IProductService;
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
@RequestMapping("/product")
public class ProductController {
    @Autowired
    IProductService productService;


//    函数通过GET方法接收路径"/list"的请求并返回ResponseResult类型的数据。
    @GetMapping("/list")
    public ResponseResult getProductList(@RequestParam(required = false)String name, @RequestParam(required = false)Integer categoryId, @RequestParam(defaultValue = "1") Integer pageNum, @RequestParam(defaultValue = "5") Integer pageSize) {
        return productService.getProductList(name, categoryId, pageNum, pageSize);
    }


//    函数通过GET方法访问"/admin/{id}"路径。调用productService对象的getProduct方法并传入产品ID，
//    返回查询到的产品信息封装在ResponseResult对象中。
    @GetMapping("/admin/{id}")
    public ResponseResult getProduct(@PathVariable Integer id) {
        return productService.getProduct(id);
    }


//    通过POST请求插入一个产品,
//    函数的主要功能是接收前端发送的产品信息，调用业务层服务插入产品数据，并将业务层的处理结果封装后返回给前端。
    @PostMapping("/admin/insert")
    public ResponseResult insertProduct(@RequestBody Product product){
        return productService.insertProduct(product);
    }


//    通过POST请求接收路径"/admin/update"。
//    函数接受请求体中的Product对象数据，
//    然后调用productService中的updateProduct方法更新产品信息，并返回更新结果。
    @PostMapping("/admin/update")
    public ResponseResult updateProduct(@RequestBody Product product){
        return productService.updateProduct(product);
    }

//    调用productService.deleteProduct(ids)方法，将接收到的产品ID列表传递给该方法。
//    productService.deleteProduct(ids)方法负责执行删除操作，并返回一个表示操作结果的ResponseResult对象
//    函数将该操作结果直接返回给客户端
    @PostMapping("/admin/delete")
    public ResponseResult deleteProduct(@RequestBody List<Integer> ids){
        return productService.deleteProduct(ids);
    }
}
