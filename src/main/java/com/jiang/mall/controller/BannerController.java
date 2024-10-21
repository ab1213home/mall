package com.jiang.mall.controller;

import com.jiang.mall.domain.ResponseResult;
import com.jiang.mall.domain.entity.Banner;
import com.jiang.mall.domain.vo.BannerVo;
import com.jiang.mall.service.IBannerService;
import com.jiang.mall.service.IUserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 轮播图控制器
 * @author jiang
 * @version 1.0
 * @since 2024年9月8日
 */
@RestController
@RequestMapping("/banner")
public class BannerController {

    private IBannerService bannerService;

    /**
     * 轮播图服务接口的依赖注入
     *
     * @param bannerService 轮播图服务接口
     */
    @Autowired
    public void setBannerService(IBannerService bannerService) {
        this.bannerService = bannerService;
    }

    private IUserService userService;

    /**
     * 用户服务接口的依赖注入
     *
     * @param userService 用户服务接口
     */
    @Autowired
    public void setUserService(IUserService userService) {
        this.userService = userService;
    }

    /**
     * 获取轮播图列表
     *
     * @param pageNum  当前页码，默认为1
     * @param pageSize 每页大小，默认为5
     * @param session  HTTP会话
     * @return 轮播图列表或未找到资源的提示
     */
    @GetMapping("/getList")
    public ResponseResult getBannerList(@RequestParam(defaultValue = "1") Integer pageNum,
                                        @RequestParam(defaultValue = "5") Integer pageSize,
                                        HttpSession session) {
        List<BannerVo> banner_list = bannerService.getBannerList(pageNum, pageSize);
        if (banner_list.isEmpty()) {
            return ResponseResult.notFoundResourceResult("没有找到资源");
        }
        return ResponseResult.okResult(banner_list);
    }

    /**
     * 获取轮播图数量
     *
     * @param session HTTP会话
     * @return 轮播图数量
     */
    @GetMapping("/getNum")
    public ResponseResult getBannerNum(HttpSession session) {
        return ResponseResult.okResult(bannerService.getBannerNum());
    }

    /**
     * 添加轮播图
     *
     * @param img         图片链接
     * @param url         轮播图链接
     * @param description 描述
     * @param session     HTTP会话
     * @return 添加结果或错误信息
     */
    @PostMapping("/add")
    public ResponseResult insertBanner(@RequestParam("img") String img,
                                       @RequestParam("url") String url,
                                       @RequestParam("description") String description,
                                       HttpSession session) {
        // 检查会话中是否设置表示用户已登录的标志
        ResponseResult result = userService.checkAdminUser(session);
        if (!result.isSuccess()) {
            return result;
        }
        if (img==null||url==null||description==null){
            return ResponseResult.failResult("参数错误");
        }
        if (!StringUtils.hasText(img)){
            return ResponseResult.failResult("请输入图片链接");
        }
        if (!StringUtils.hasText(url)){
            return ResponseResult.failResult("请输入轮播图链接");
        }
        if (!StringUtils.hasText(description)){
            return ResponseResult.failResult("请输入描述");
        }
        // 创建Banner对象并调用服务层方法尝试添加轮播图
        Banner banner = new Banner(img, url, description);
        if (bannerService.insertBanner(banner)) {
            return ResponseResult.okResult();
        } else {
            return ResponseResult.serverErrorResult("添加失败");
        }
    }


    /**
     * 更新轮播图信息
     *
     * @param id          轮播图ID
     * @param img         新的图片链接
     * @param url         新的轮播图链接
     * @param description 新的描述
     * @param session     HTTP会话
     * @return 更新结果或错误信息
     */
    @PostMapping("/update")
    public ResponseResult updateBanner(@RequestParam("id") Long id,
                                       @RequestParam("img") String img,
                                       @RequestParam("url") String url,
                                       @RequestParam("description") String description,
                                       HttpSession session) {
        if (img==null||url==null||description==null||id==null||id<=0){
            return ResponseResult.failResult("参数错误");
        }
        if (!StringUtils.hasText(img)){
            return ResponseResult.failResult("请输入图片链接");
        }
        if (!StringUtils.hasText(url)){
            return ResponseResult.failResult("请输入轮播图链接");
        }
        if (!StringUtils.hasText(description)){
            return ResponseResult.failResult("请输入描述");
        }
        if (!StringUtils.hasText(id.toString())){
            return ResponseResult.failResult("请输入轮播图ID");
        }
        // 根据ID获取轮播图信息
        Banner banner = bannerService.getById(id);
        // 如果找不到对应的轮播图信息，则返回未找到资源的错误信息
        if (banner == null) {
            return ResponseResult.notFoundResourceResult("没有找到资源");
        }
        // 检查会话中是否设置表示用户已登录的标志
        ResponseResult result = userService.hasPermission(banner.getUpdater(),session);
        // 如果用户未登录或不是管理员，则返回错误信息
        if (!result.isSuccess()) {
            return result;
        }

        // 创建一个新的Banner对象并更新其信息
        banner = new Banner(id, img, url, description);
        // 尝试更新数据库中的轮播图信息
        if (bannerService.updateBanner(banner)) {
            return ResponseResult.okResult();
        } else {
            return ResponseResult.serverErrorResult("修改失败");
        }
    }

    /**
     * 删除轮播图
     *
     * @param id      轮播图ID
     * @param session HTTP会话
     * @return 删除结果或错误信息
     */
    @GetMapping("/delete")
    public ResponseResult deleteBanner(@RequestParam("id") Integer id,
                                       HttpSession session) {
        if (id==null||id<=0){
            return ResponseResult.failResult("参数错误");
        }
        if (!StringUtils.hasText(id.toString())){
            return ResponseResult.failResult("请输入轮播图ID");
        }
        // 根据ID获取轮播图信息
        Banner banner = bannerService.getById(id);
        // 如果找不到对应的轮播图信息，则返回未找到资源的错误信息
        if (banner == null) {
            return ResponseResult.notFoundResourceResult("没有找到资源");
        }
        // 检查会话中是否设置表示用户已登录的标志
        ResponseResult result = userService.hasPermission(banner.getUpdater(),session);
        // 如果用户未登录或不是管理员，则返回错误信息
        if (!result.isSuccess()) {
            return result;
        }

        // 尝试删除轮播图
        if (bannerService.deleteBanner(id)) {
            // 如果删除成功，返回成功结果
            return ResponseResult.okResult();
        } else {
            // 如果删除失败，返回服务器错误结果
            return ResponseResult.serverErrorResult("删除失败");
        }
    }
}