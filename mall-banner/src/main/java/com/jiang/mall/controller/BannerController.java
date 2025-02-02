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

    @Autowired
    public void setBannerService(IBannerService bannerService) {
        this.bannerService = bannerService;
    }

    /**
     * 获取轮播图列表
     *
     * @return 轮播图列表或未找到资源的提示
     */
    @GetMapping("/getList")
    public ResponseResult<Object> getBannerList() {
        List<BannerVo> banner_list = bannerService.getBannerListFromRedis();
        return ResponseResult.okResult(banner_list);
    }
}