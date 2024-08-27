package com.jiang.mall.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jiang.mall.domain.ResponseResult;
import com.jiang.mall.domain.entity.Banner;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author WH
 * @since 2023-06-25
 */
public interface IBannerService extends IService<Banner> {

    ResponseResult getBannerList(Integer categoryId, Integer pageNum);

    ResponseResult getBanner(Integer id);

    ResponseResult insertBanner(Banner banner);

    ResponseResult updateBanner(Banner banner);

    ResponseResult deleteBanner(List<Integer> ids);
}
