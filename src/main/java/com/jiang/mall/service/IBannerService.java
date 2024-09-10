package com.jiang.mall.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jiang.mall.domain.ResponseResult;
import com.jiang.mall.domain.entity.Banner;
import com.jiang.mall.domain.vo.BannerVo;

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

    ResponseResult getBanner(Integer id);

    boolean insertBanner(Banner banner);

    boolean updateBanner(Banner banner);

    ResponseResult deleteBanner(List<Integer> ids);

    List<BannerVo> getBannerList(Integer categoryId, Integer pageNum);

    Integer getBannerNum();

    boolean deleteBanner(Integer id);
}
