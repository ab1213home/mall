package com.jiang.mall.controller;

import com.jiang.mall.domain.ResponseResult;
import com.jiang.mall.domain.entity.Product;
import com.jiang.mall.domain.vo.ProductVo;
import com.jiang.mall.service.IProductService;
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
@RequestMapping("/product")
public class ProductController {

    private IUserService userService;

    /**
     * 注入IUserService实例，用于处理用户相关的业务逻辑。
     *
     * @param userService IUserService实例，用于处理用户相关的业务逻辑
     * @return 返回注入后的IUserService实例
     */
    @Autowired
    public IUserService setUserService(IUserService userService) {
        this.userService = userService;
        return userService;
    }

    IProductService productService;

    /**
     * 注入IProductService实例，用于处理产品相关的业务逻辑。
     *
     * @param productService IProductService实例，用于处理产品相关的业务逻辑
     * @return 返回注入后的IProductService实例
     */
    @Autowired
    public IProductService productService(IProductService productService) {
        this.productService = productService;
        return productService;
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
    public ResponseResult getProductList(@RequestParam(required = false) String name,
                                         @RequestParam(required = false) Integer categoryId,
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
    public ResponseResult getProductInfo(@RequestParam("productId") Integer productId) {
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

    /**
     * 添加产品信息
     *
     * @param code 产品编码
     * @param title 产品标题
     * @param categoryId 类别ID
     * @param img 图片链接
     * @param price 价格
     * @param stocks 库存数量
     * @param description 产品描述
     * @param session HTTP会话
     * @return 操作结果
     */
    @PostMapping("/add")
    public ResponseResult insertProduct(@RequestParam("code") String code,
                                        @RequestParam("title") String title,
                                        @RequestParam("categoryId") Integer categoryId,
                                        @RequestParam("img") String img,
                                        @RequestParam("price") Double price,
                                        @RequestParam("stocks") Integer stocks,
                                        @RequestParam("description") String description,
                                        HttpSession session) {
        // 检查会话中是否设置表示用户已登录的标志
        ResponseResult result = userService.checkAdminUser(session);
        if (!result.isSuccess()) {
            return result;
        }
        if (code==null||title==null||categoryId==null||img==null||price==null||stocks==null||description==null||price<=0||stocks<=0||categoryId<=0){
            return ResponseResult.failResult("参数错误");
        }
        if (!StringUtils.hasText(title)){
            return ResponseResult.failResult("产品标题不能为空");
        }
        if (!StringUtils.hasText(code)){
            return ResponseResult.failResult("产品编码不能为空");
        }
        if (!StringUtils.hasText(img)){
            return ResponseResult.failResult("产品图片不能为空");
        }
        if (!StringUtils.hasText(description)){
            return ResponseResult.failResult("产品描述不能为空");
        }
        if (!StringUtils.hasText(price.toString())){
            return ResponseResult.failResult("价格不能为空");
        }
        if (!StringUtils.hasText(stocks.toString())){
            return ResponseResult.failResult("库存不能为空");
        }
        // 创建产品对象
        Product product = new Product(code, title, categoryId, img, price, stocks, description);
        if (productService.queryCode(product.getCode())) {
            return ResponseResult.failResult("产品编码已存在");
        }
        // 调用服务层方法插入产品信息
        if (productService.insertProduct(product)) {
            return ResponseResult.okResult();
        } else {
            return ResponseResult.serverErrorResult("添加失败");
        }
    }


    /**
     * 更新产品信息的接口
     *
     * @param id            产品的ID
     * @param code          产品代码
     * @param title         产品标题
     * @param categoryId    产品类别ID
     * @param img           产品图片路径
     * @param price         产品价格
     * @param stocks        产品库存
     * @param description   产品描述
     * @param session       HTTP会话，用于检查用户登录状态
     * @return              返回操作结果的响应对象
     */
    @PostMapping("/update")
    public ResponseResult updateProduct(@RequestParam("id") Integer id,
                                        @RequestParam("code") String code,
                                        @RequestParam("title") String title,
                                        @RequestParam("categoryId") Integer categoryId,
                                        @RequestParam("img") String img,
                                        @RequestParam("price") Double price,
                                        @RequestParam("stocks") Integer stocks,
                                        @RequestParam("description") String description,
                                        HttpSession session) {
        if (id==null||code==null||title==null||categoryId==null||img==null||price==null||stocks==null||description==null||price<=0||stocks<=0||categoryId<=0||id<=0){
            return ResponseResult.failResult("参数错误");
        }
        if (!StringUtils.hasText(title)){
            return ResponseResult.failResult("产品标题不能为空");
        }
        if (!StringUtils.hasText(code)){
            return ResponseResult.failResult("产品编码不能为空");
        }
        if (!StringUtils.hasText(img)){
            return ResponseResult.failResult("产品图片不能为空");
        }
        if (!StringUtils.hasText(description)){
            return ResponseResult.failResult("产品描述不能为空");
        }
        if (!StringUtils.hasText(price.toString())){
            return ResponseResult.failResult("价格不能为空");
        }
        if (!StringUtils.hasText(stocks.toString())){
            return ResponseResult.failResult("库存不能为空");
        }
        // 通过ID获取产品信息
        Product product = productService.getById(id);

        // 如果产品不存在，返回未找到资源的响应
        if (product == null) {
            return ResponseResult.notFoundResourceResult("没有找到资源");
        }

        // 检查会话中是否设置表示用户已登录的标志
        ResponseResult result = userService.hasPermission(product.getUpdater(),session);
        // 如果用户未登录或不是管理员，则返回错误信息
        if (!result.isSuccess()) {
            return result;
        }
        if (productService.queryCode(product.getCode())&&!product.getCode().equals(code)) {
            return ResponseResult.failResult("产品编码已存在");
        }
        // 使用传入的参数更新产品信息
        product = new Product(id,code, title, categoryId, img, price, stocks, description);

        // 尝试更新产品信息，根据更新结果返回相应的响应
        if (productService.updateProduct(product)) {
            return ResponseResult.okResult();
        } else {
            return ResponseResult.serverErrorResult("修改失败");
        }
    }

    /**
     * 处理删除请求
     *
     * @param id 删除资源的ID
     * @param session 当前用户会话
     * @return 删除操作的结果
     */
    @GetMapping("/delete")
    public ResponseResult deleteProduct(@RequestParam("id") Integer id,
                                       HttpSession session) {
        if (id==null||id<=0){
            return ResponseResult.failResult("参数错误");
        }
        if (!StringUtils.hasText(id.toString())){
            return ResponseResult.failResult("请输入商品ID");
        }
        // 通过ID获取产品信息
        Product product = productService.getById(id);

        // 检查资源是否存在
        if (product == null) {
            return ResponseResult.notFoundResourceResult("没有找到资源");
        }
        // 检查会话中是否设置表示用户已登录的标志
        ResponseResult result = userService.hasPermission(product.getUpdater(),session);
        // 验证用户权限，确保用户已登录并有权限进行删除操作
        if (!result.isSuccess()) {
            return result;
        }

        // 尝试删除产品
        if (productService.deleteProduct(id)) {
            // 如果删除成功，返回成功结果
            return ResponseResult.okResult();
        } else {
            // 如果删除失败，返回服务器错误结果
            return ResponseResult.serverErrorResult("删除失败");
        }
    }

    @GetMapping("/getNum")
    public ResponseResult getProductNum(HttpSession session) {
        // 检查会话中是否设置表示用户已登录的标志
        ResponseResult result = userService.checkAdminUser(session);
        if (!result.isSuccess()) {
            return result;
        }
        return ResponseResult.okResult(productService.getProductNum());
    }

}
