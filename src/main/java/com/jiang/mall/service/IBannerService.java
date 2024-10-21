package com.jiang.mall.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jiang.mall.domain.entity.Banner;
import com.jiang.mall.domain.vo.BannerVo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author jiang
 * @since 2024年9月11日
 */
public interface IBannerService extends IService<Banner> {

    boolean insertBanner(Banner banner);

    boolean updateBanner(Banner banner);

    List<BannerVo> getBannerList(Integer categoryId, Integer pageNum);

    Long getBannerNum();

    boolean deleteBanner(Integer id);
}
