package com.jiang.mall.controller;

import com.jiang.mall.domain.ResponseResult;
import com.jiang.mall.domain.entity.Banner;
import com.jiang.mall.service.IBannerService;
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
@RequestMapping("/banner")
public class BannerController {

    @Autowired
    IBannerService bannerService;

//    通过GET方法接收网页请求并处理。参数默认为页码1和每页条数5。
//    函数调用bannerService获取轮播图列表服务，并返回处理结果。
    @GetMapping("/list")
    public ResponseResult getBannerList(@RequestParam(defaultValue = "1") Integer pageNum, @RequestParam(defaultValue = "5") Integer pageSize) {
        return bannerService.getBannerList(pageNum, pageSize);
    }

//    通过GET请求访问"/admin/{id}"路径。函数接收路径变量"id"作为参数，然后调用bannerService对象的getBanner方法并传入该ID来获取横幅信息。
//    返回的是一个ResponseResult对象
    @GetMapping("/admin/{id}")
    public ResponseResult getBanner(@PathVariable Integer id) {
        return bannerService.getBanner(id);
    }


//    通过POST请求插入一个Banner对象。
    @PostMapping("/admin/insert")
    public ResponseResult insertBanner(@RequestBody Banner banner){
        return bannerService.insertBanner(banner);
        //调用bannerService服务层的insertBanner方法，将Banner对象插入数据库或进行相应的业务处理。
    }


//    它接收JSON格式的Banner对象数据，通过bannerService调用其updateBanner方法更新信息，并返回操作结果。
    @PostMapping("/admin/update")
    public ResponseResult updateBanner(@RequestBody Banner banner){
        return bannerService.updateBanner(banner);
    }


//    函数接收一个包含多个整数ID的List作为参数，这些ID标识了要被删除的广告位。具体删除操作则是调用bannerService的deleteBanner方法来完成。
    @PostMapping("/admin/delete")
    public ResponseResult deleteBanner(@RequestBody List<Integer> ids){
        return bannerService.deleteBanner(ids);
    }
}
