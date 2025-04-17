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
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiang.mall.config.BannerConfig;
import com.jiang.mall.dao.BannerMapper;
import com.jiang.mall.domain.entity.Banner;
import com.jiang.mall.domain.vo.BannerAdminVo;
import com.jiang.mall.domain.vo.BannerVo;
import com.jiang.mall.event.BannerChangedEvent;
import com.jiang.mall.service.IBannerRedisService;
import com.jiang.mall.service.IBannerService;
import com.jiang.mall.service.IUserService;
import com.jiang.mall.util.BeanCopyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Banner服务实现类
 * 提供了横幅（Banner）相关操作的具体实现，包括获取横幅列表、插入、更新和删除横幅等操作
 * 同时，本类还提供了从Redis缓存中获取横幅列表的功能，以提高访问速度和系统性能
 *
 * @email  jiangrongjun2004@163.com
 * @link <a href="https://github.com/ab1213home/mall">https://github.com/ab1213home/mall</a>
 * @apiNote Banner服务实现类
 * @version 1.0
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

	private BannerConfig bannerConfig;

	@Autowired
	public void setBannerConfig(BannerConfig bannerConfig) {
		this.bannerConfig = bannerConfig;
	}

	private IUserService userService;

	@Autowired
	public void setUserService(IUserService userService) {
		this.userService = userService;
	}

	private static final Logger logger = LoggerFactory.getLogger(BannerServiceImpl.class);

	private ApplicationEventPublisher eventPublisher;

	@Autowired
	public void setEventPublisher(ApplicationEventPublisher eventPublisher) {
		this.eventPublisher = eventPublisher;
	}

	/**
	 * 获取横幅列表
	 *
	 * @param pageNum  页码
	 * @param pageSize 每页数量
	 * @return 横幅的管理员视图列表
	 */
    @Override
    @Transactional
    public List<BannerAdminVo> getBannerList(Integer pageNum, Integer pageSize) {
        Page<Banner> bannerPage = new Page<>(pageNum, pageSize);
        List<Banner> banners = bannerMapper.selectPage(bannerPage, null).getRecords();
		List<BannerAdminVo> bannerAdminVos = new ArrayList<>();
		for (Banner banner : banners) {
			BannerAdminVo bannerAdminVo = BeanCopyUtil.copyBean(banner, BannerAdminVo.class);
			assert bannerAdminVo != null;
			//获取创建者和更新者信息
			bannerAdminVo.setCreator(userService.getUserById(banner.getCreator()));
			bannerAdminVo.setUpdater(userService.getUserById(banner.getUpdater()));
			bannerAdminVos.add(bannerAdminVo);
		}
	    return bannerAdminVos;
    }

    /**
     * 获取轮播图数量
     * <p>
     * 此方法用于获取数据库中轮播图的总数
     *
     * @return 轮播图的总数
     */
    @Override
    @Transactional
    public Long getBannerNum() {
        return bannerMapper.selectCount(null);
    }

	/**
	 * 获取当前有效的Banner列表
	 * <p>
	 * 此方法旨在查询数据库中当前时间下应该显示的所有Banner信息，
	 * 并将这些信息转换为Vo格式的列表返回，以便在前端展示或进一步处理
	 *
	 * @return List<BannerVo> 返回转换后的BannerVo列表，包含当前有效的所有Banner信息
	 */
	@Override
	@Transactional
	public List<BannerVo> getBannerList() {
	    // 查询当前时间下所有有效的Banner实体列表
	    List<Banner> banners = bannerMapper.getTeffectiveBannerList(LocalDateTime.now());
	    // 将Banner实体列表转换为BannerVo列表，并返回
	    return BeanCopyUtil.copyBeanList(banners, BannerVo.class);
	}

	/**
	 * 从Redis中获取轮播图列表
	 * <p>
	 * 本方法首先检查是否启用了轮播图缓存，如果启用，则尝试从Redis中获取轮播图数据
	 * 如果Redis中不存在轮播图数据，则调用getBannerList方法获取数据
	 * 如果轮播图缓存未启用，直接调用getBannerList方法获取数据
	 *
	 * @return List<BannerVo> 返回轮播图列表，如果列表为空，则返回空列表
	 */
	@Override
	public List<BannerVo> getBannerListFromRedis() {
	    // 检查是否启用了轮播图缓存并且Redis中是否存在轮播图数据
	    if (bannerConfig.isBannerCacheEnabled() && redisService.hasBanner()){
			return redisService.getBanner();
	    }else {
	        // 如果轮播图缓存未启用，直接调用方法获取轮播图数据
	        return getBannerList();
	    }
	}

    /**
     * 根据ID删除轮播图
     * <p>
     * 此方法首先尝试从数据库中删除指定ID的轮播图如果删除成功，
     * 则触发轮播图的检查机制，以确保轮播图数据的一致性和完整性
     *
     * @param id 要删除的轮播图的ID
     * @return 如果删除成功返回true，否则返回false
     */
    @Override
    @Transactional
    public Boolean deleteBanner(Long id) {
        // 尝试删除指定ID的轮播图，如果删除成功则进行后续操作
        if (bannerMapper.deleteById(id) == 1){
            // 调用轮播图检查机制，确保数据一致性
	        eventPublisher.publishEvent(
	            new BannerChangedEvent(this)
	        );
            return true;
        }else{
            // 删除失败，返回false
            return false;
        }
    }

    /**
     * 插入轮播图
     * <p>
     * 此方法负责将一个轮播图对象插入到数据库中，并通过返回值告知操作是否成功
     * 它首先尝试将轮播图数据插入数据库如果插入操作成功，它会触发一个轮播图检查任务，
     *
     * @param banner 要插入的轮播图对象，包含轮播图的相关信息
     * @return 返回一个布尔值，表示插入操作是否成功true 表示成功，false 表示失败
     */
    @Override
    @Transactional
    public Boolean insertBanner(Banner banner) {
        // 尝试插入轮播图数据，如果成功，触发轮播图检查任务，并返回 true 表示操作成功
    	if (bannerMapper.insert(banner) == 1){
			eventPublisher.publishEvent(
	            new BannerChangedEvent(this)
	        );
    		return true;
    	}else{
    		// 如果插入失败，返回 false 表示操作失败
    		return false;
    	}
    }

    /**
     * 更新轮播图信息
     * <p>
     * 当轮播图信息在数据库中成功更新后，此方法调用指定的检查任务以确保轮播图状态的正确性
     * 如果更新操作失败，则返回false，表示更新未成功
     *
     * @param banner 要更新的轮播图对象，包含需要更新的信息
     * @return 如果轮播图信息成功更新，则返回true；否则返回false
     */
    @Override
    @Transactional
    public Boolean updateBanner(Banner banner) {
        // 尝试更新数据库中的轮播图信息
        if (bannerMapper.updateById(banner) == 1){
            // 更新成功后，调用检查任务以确保轮播图状态的正确性
            eventPublisher.publishEvent(
	            new BannerChangedEvent(this)
	        );
            return true;
        }else{
            // 如果更新失败，返回false
            return false;
        }
    }

	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handleBannerChangedEvent(BannerChangedEvent event) {
	    try {
			if (bannerConfig.isBannerCacheEnabled()){
				checkBanner();
			}else{
				logger.info("轮播图缓存已禁用。");
			}
	    } catch (Exception e) {
			logger.error("处理轮播图变更事件失败: {}", e.getMessage());
	        // 可添加重试逻辑
	    }
	}

	/**
	 * 检查并更新轮播图数据
	 * 该方法首先从服务层获取轮播图列表，然后根据列表的情况进行处理：
	 * 如果列表为空或不存在，则记录日志并从Redis中删除现有的轮播图数据；
	 * 如果列表存在且不为空，则检查数据大小是否超过阈值，如果超过则记录警告日志，
	 * 最后将轮播图数据更新到Redis中
	 */
	@Override
	public void checkBanner() {
	    // 获取轮播图列表
	    List<BannerVo> bannerList = getBannerList();

	    // 检查列表是否为空或不存在
	    if (bannerList == null || bannerList.isEmpty()) {
	        logger.info("未找到有效的轮播图数据。");
	        // 如果为空，从Redis中删除轮播图数据
	        redisService.deleteBanner();
	        return;
	    }

	    // 添加保护措施防止大Key
	    if(JSON.toJSONString(bannerList).getBytes().length > 1024 * 1024){ // 超过1MB报警
	        logger.warn("检测到轮播图数据过大：{}字节", JSON.toJSONString(bannerList).length());
	    }

	    // 更新Redis中的轮播图数据
	    redisService.setBanner(bannerList);
	}

}
