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

package com.jiang.mall.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiang.mall.dao.BannerMapper;
import com.jiang.mall.domain.entity.Banner;
import com.jiang.mall.domain.vo.BannerAdminVo;
import com.jiang.mall.domain.vo.BannerVo;
import com.jiang.mall.domain.vo.UserVo;
import com.jiang.mall.service.IBannerRedisService;
import com.jiang.mall.service.IBannerService;
import com.jiang.mall.util.BeanCopyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.jiang.mall.config.BannerConfig.BannerCache;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author jiang
 * @since 2024年9月11日
 */
@Service
public class BannerServiceImpl extends ServiceImpl<BannerMapper, Banner> implements IBannerService {

    private BannerMapper bannerMapper;

    @Autowired
    public void setBannerMapper(BannerMapper bannerMapper) {
        this.bannerMapper = bannerMapper;
    }

	private IBannerRedisService redisService;

	@Autowired
	public void setRedisService(IBannerRedisService redisService) {
		this.redisService = redisService;
	}

    @Override
    public List<BannerAdminVo> getBannerList(Integer pageNum, Integer pageSize) {
        Page<Banner> bannerPage = new Page<>(pageNum, pageSize);
        List<Banner> banners = bannerMapper.selectPage(bannerPage, null).getRecords();
		List<BannerAdminVo> bannerAdminVos = new ArrayList<>();
		for (Banner banner : banners) {
			BannerAdminVo bannerAdminVo = BeanCopyUtils.copyBean(banner, BannerAdminVo.class);
			assert bannerAdminVo != null;
			bannerAdminVo.setCreator(BeanCopyUtils.copyBean(banner.getCreator(), UserVo.class));
			bannerAdminVo.setUpdater(BeanCopyUtils.copyBean(banner.getUpdater(), UserVo.class));
			bannerAdminVos.add(bannerAdminVo);
		}
	    return bannerAdminVos;
    }

    @Override
    public Long getBannerNum() {
	    return bannerMapper.selectCount(null);
    }

    @Override
    public Boolean deleteBanner(Integer id) {
	    return bannerMapper.deleteById(id) == 1;
    }

	@Override
	public List<BannerVo> getBannerList() {
		LocalDateTime now = LocalDateTime.now();
		QueryWrapper<Banner> queryWrapper = new QueryWrapper<>();
		// banner的开始时间小于等于当前时间，结束时间大于等于当前时间
		queryWrapper.le("start_time", now);
		queryWrapper.ge("end_time", now);
        List<Banner> banners = bannerMapper.selectList(queryWrapper);
	    return BeanCopyUtils.copyBeanList(banners, BannerVo.class);
	}

	@Override
	public List<BannerVo> getBannerListFromRedis() {
		if (BannerCache){
			if (redisService.hasKey("banner")) {
				String bannerListJson = redisService.getKey("banner");
				return JSON.parseArray(bannerListJson, BannerVo.class);
			}else {
				return getBannerList();
			}
		}else {
			return getBannerList();
		}
	}

	@Override
    public Boolean insertBanner(Banner banner) {
	    return bannerMapper.insert(banner) == 1;
    }

    @Override
    public Boolean updateBanner(Banner banner) {
	    return bannerMapper.updateById(banner) == 1;
    }

}
