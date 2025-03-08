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

package com.jiang.mall.controller.product;

import com.jiang.mall.domain.ResponseResult;
import com.jiang.mall.domain.vo.ProductSnapshotVo;
import com.jiang.mall.domain.vo.ProductVo;
import com.jiang.mall.service.IProductService;
import com.jiang.mall.service.IProductSnapshotService;
import com.jiang.mall.service.IUserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
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
@RequestMapping("/product")
public class ProductController {

    private IUserService userService;

    @Autowired
    public void setUserService(IUserService userService) {
        this.userService = userService;
    }

    IProductService productService;

    @Autowired
    public void productService(IProductService productService) {
        this.productService = productService;
    }

    private IProductSnapshotService productSnapshotService;

    @Autowired
    public void productSnapshotService(IProductSnapshotService productSnapshotService) {
        this.productSnapshotService = productSnapshotService;
    }

    @GetMapping("/getSnapshotInfo")
    public ResponseResult<Object> getSnapshotInfo(@RequestParam("id") Long id,
                                          HttpSession session) {
        if (id == null|| id < 0) {
            return ResponseResult.failResult("参数错误");
        }
        if (!StringUtils.hasText(id.toString())){
            return ResponseResult.failResult("请输入商品ID");
        }
        // 根据产品ID获取产品信息
        ProductSnapshotVo snapshot = productSnapshotService.getSnapshotInfo(id,session.getId());

        if (snapshot == null) {
            return ResponseResult.notFoundResourceResult("没有找到相关数据");
        }
        return ResponseResult.okResult(snapshot);
    }


    /**
     * 获取产品列表
     * <p>
     * 说明：
     * - 该方法是一个处理HTTP GET请求的处理器方法，用于根据不同的筛选条件获取产品列表。
     * - 支持根据产品名称和产品类别进行筛选，同时提供了分页查询的功能。
     * - 参数pageNum和pageSize用于指定查询的页码和每页显示数量，以便于处理大量数据。
     * - 方法将调用productService中的getProductList方法来执行实际的查询逻辑。
     * @param name       产品名称的模糊搜索字符串，可选参数
     * @param categoryId 产品的类别ID，用于筛选特定类别的产品，可选参数
     * @param pageNum    当前页码，默认值为1，用于分页查询
     * @param pageSize   每页显示的结果数量，默认值为5，用于分页查询
     * @return 返回包含产品列表的响应结果，具体结构由productService定义
     */
    @GetMapping("/getList")
    public ResponseResult<Object> getProductList(@RequestParam(required = false) String name,
                                         @RequestParam(required = false) Long categoryId,
                                         @RequestParam(defaultValue = "1") Integer pageNum,
                                         @RequestParam(defaultValue = "5") Integer pageSize) {
        List<ProductVo> list = productService.getProductList(name, categoryId, pageNum, pageSize);
        if (list.isEmpty()) {
            return ResponseResult.notFoundResourceResult("没有找到相关数据");
        }
        return ResponseResult.okResult(list);
    }

    /**
     * 通过GET请求方式获取产品信息
     *
     * @param productId 从请求参数中获取的产品ID
     * @return 返回产品信息或者错误信息
     */
    @GetMapping("/getInfo")
    public ResponseResult<Object> getProductInfo(@RequestParam("productId") Long productId) {
        if (productId == null|| productId < 0) {
            return ResponseResult.failResult("参数错误");
        }
        if (!StringUtils.hasText(productId.toString())){
            return ResponseResult.failResult("请输入商品ID");
        }
        // 根据产品ID获取产品信息
        ProductVo product = productService.getProduct(productId);

        // 如果产品信息为空，则返回未找到资源的错误信息
        if (product == null) {
            return ResponseResult.notFoundResourceResult("没有找到相关数据");
        }

        // 如果产品信息存在，则返回成功获取产品信息的结果
        return ResponseResult.okResult(product);
    }
}
