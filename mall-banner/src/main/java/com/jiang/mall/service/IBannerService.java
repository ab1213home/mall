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

package com.jiang.mall.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jiang.mall.domain.entity.Banner;
import com.jiang.mall.domain.vo.BannerAdminVo;
import com.jiang.mall.domain.vo.BannerVo;

import java.util.List;

/**
 * Banner服务类
 * 提供了横幅（Banner）相关操作的具体实现，包括获取横幅列表、插入、更新和删除横幅等操作
 * 同时，本类还提供了从Redis缓存中获取横幅列表的功能，以提高访问速度和系统性能
 *
 * @email  jiangrongjun2004@163.com
 * @link <a href="https://github.com/ab1213home/mall">https://github.com/ab1213home/mall</a>
 * @apiNote Banner服务类
 * @version 1.0
 * @author jiang
 * @since 2024年9月11日
 */
public interface IBannerService extends IService<Banner> {

    /**
     * 插入轮播图
     * <p>
     * 此方法负责将一个轮播图对象插入到数据库中，并通过返回值告知操作是否成功
     * 它首先尝试将轮播图数据插入数据库如果插入操作成功，它会触发一个轮播图检查任务，
     *
     * @param banner 要插入的轮播图对象，包含轮播图的相关信息
     * @return 返回一个布尔值，表示插入操作是否成功true 表示成功，false 表示失败
     */
    Boolean insertBanner(Banner banner);

    /**
     * 更新轮播图信息
     * <p>
     * 当轮播图信息在数据库中成功更新后，此方法调用指定的检查任务以确保轮播图状态的正确性
     * 如果更新操作失败，则返回false，表示更新未成功
     *
     * @param banner 要更新的轮播图对象，包含需要更新的信息
     * @return 如果轮播图信息成功更新，则返回true；否则返回false
     */
    Boolean updateBanner(Banner banner);

    /**
	 * 获取横幅列表
	 *
	 * @param pageNum  页码
	 * @param pageSize 每页数量
	 * @return 横幅的管理员视图列表
	 */
    List<BannerAdminVo> getBannerList(Integer pageNum, Integer pageSize);

    /**
     * 获取轮播图数量
     * <p>
     * 此方法用于获取数据库中轮播图的总数
     *
     * @return 轮播图的总数
     */
    Long getBannerNum();

    /**
     * 根据ID删除轮播图
     * <p>
     * 此方法首先尝试从数据库中删除指定ID的轮播图如果删除成功，
     * 则触发轮播图的检查机制，以确保轮播图数据的一致性和完整性
     *
     * @param id 要删除的轮播图的ID
     * @return 如果删除成功返回true，否则返回false
     */
    Boolean deleteBanner(Long id);

    /**
	 * 获取当前有效的Banner列表
	 * <p>
	 * 此方法旨在查询数据库中当前时间下应该显示的所有Banner信息，
	 * 并将这些信息转换为Vo格式的列表返回，以便在前端展示或进一步处理
	 *
	 * @return List<BannerVo> 返回转换后的BannerVo列表，包含当前有效的所有Banner信息
	 */
    List<BannerVo> getBannerList();

    /**
	 * 从Redis中获取轮播图列表
	 * <p>
	 * 本方法首先检查是否启用了轮播图缓存，如果启用，则尝试从Redis中获取轮播图数据
	 * 如果Redis中不存在轮播图数据，则调用getBannerList方法获取数据
	 * 如果轮播图缓存未启用，直接调用getBannerList方法获取数据
	 *
	 * @return List<BannerVo> 返回轮播图列表，如果列表为空，则返回空列表
	 */
    List<BannerVo> getBannerListFromRedis();

	void checkBanner();
}
