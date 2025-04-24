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
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiang.mall.config.NoticeConfig;
import com.jiang.mall.dao.NoticeLogMapper;
import com.jiang.mall.domain.entity.NoticeLog;
import com.jiang.mall.domain.enums.NoticeChannel;
import com.jiang.mall.domain.enums.NoticeStatus;
import com.jiang.mall.service.INoticeLogService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class NoticeLogServiceImpl extends ServiceImpl<NoticeLogMapper, NoticeLog> implements INoticeLogService {

	private NoticeLogMapper noticeLogMapper;

	@Autowired
	public void setNoticeLogMapper(NoticeLogMapper noticeLogMapper) {
		this.noticeLogMapper = noticeLogMapper;
	}

	private NoticeConfig noticeConfig;

	@Autowired
	public void setNoticeConfig(NoticeConfig noticeConfig) {
	    this.noticeConfig = noticeConfig;
	}

	@Override
	public boolean inspectByChannel(String receiver, @NotNull NoticeChannel channel) {
		if (channel == NoticeChannel.WEB){
			return true;
		}
		// 当前时间
	    Date now = new Date();
	    // 一天前的时间
	    Date yesterday = new Date(now.getTime() - 24 * 60 * 60 * 1000);

		long count = noticeLogMapper.selectCountBySingleChannelAndTimeRange(receiver, channel.getKey(), yesterday, now);
		long failCount = noticeLogMapper.selectCountBySingleChannelAndTimeRangeAndStatus(receiver, channel.getKey(), NoticeStatus.FAILED.getKey(), yesterday, now);

		//检查请求数量是否小于等于最小请求数量
	    if (count <= noticeConfig.getNoticeMinRequestNum()) {
	        return false;
	    }
	    // 检查请求数量是否大于最大请求数量
	    if (count > noticeConfig.getNoticeMaxRequestNum()) {
	        return true;
	    }
	    // 计算失败率并判断是否超过最大失败率阈值
	    return failCount / (double) count > noticeConfig.getNoticeMaxFail();
	}

	@Override
	public boolean defaultLog(Long templateId, String receiver, @NotNull NoticeStatus status, @NotNull NoticeChannel channel, Map<String, Object> properties) {
		NoticeLog noticeLog = new NoticeLog();
		noticeLog.setTemplateId(templateId);
		noticeLog.setReceiver(receiver);
		noticeLog.setStatus(status.getKey());
		noticeLog.setChannel(channel.getKey());
		if (properties != null){
			noticeLog.setProperties(JSON.toJSONString(properties));
		}
		return noticeLogMapper.insert(noticeLog) > 0;
	}

	@Override
	public Long defaultLogWithId(Long templateId, String receiver, @NotNull NoticeStatus status, @NotNull NoticeChannel channel, Map<String, Object> properties) {
		NoticeLog noticeLog = new NoticeLog();
		noticeLog.setTemplateId(templateId);
		noticeLog.setReceiver(receiver);
		noticeLog.setStatus(status.getKey());
		noticeLog.setChannel(channel.getKey());
		if (properties != null){
			noticeLog.setProperties(JSON.toJSONString(properties));
		}
		if (noticeLogMapper.insert(noticeLog) > 0){
			return noticeLog.getId();
		}else{
			return null;
		}
	}

	@Override
	public boolean updateStatus(Long id, @NotNull NoticeStatus status) {
		NoticeLog noticeLog = new NoticeLog();
		noticeLog.setId(id);
		noticeLog.setStatus(status.getKey());
		return noticeLogMapper.updateStatusById(id, status.getKey()) > 0;
	}

	@Override
	public void check() {
		// 获取当前时间
	    Date now = new Date();
	    // 计算expiration_time分钟前的时间，作为验证码的有效期起点
	    Date yesterday = new Date(now.getTime() -noticeConfig.getNoticeExpirationTime() * 60 * 1000);

	    // 构建查询条件：需要检查的日志id列表
		QueryWrapper<NoticeLog> queryWrapper = new QueryWrapper<>();
		queryWrapper.in("purpose", 1,2,3,4);
		queryWrapper.between("trigger_time", yesterday, now);
		queryWrapper.select("id");
		List<NoticeLog> list = noticeLogMapper.selectList(queryWrapper);

		// 如果列表为空，则返回null
		if (list.isEmpty()) {
	        return;
	    }
		for (NoticeLog noticeLog : list) {
			noticeLogMapper.updateStatusById(noticeLog.getId(),NoticeStatus.EXPIRED.getKey());
		}

	}

	@Override
	public NoticeLog getNoticeLog(Long id) {
		return noticeLogMapper.selectById(id);
	}
}
