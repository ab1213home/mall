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
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiang.mall.dao.NoticeLogMapper;
import com.jiang.mall.domain.entity.NoticeLog;
import com.jiang.mall.domain.enums.NoticeChannel;
import com.jiang.mall.domain.enums.NoticeStatus;
import com.jiang.mall.service.INoticeLogService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

@Service
public class NoticeLogServiceImpl extends ServiceImpl<NoticeLogMapper, NoticeLog> implements INoticeLogService {

	private NoticeLogMapper noticeLogMapper;

	@Autowired
	public void setNoticeLogMapper(NoticeLogMapper noticeLogMapper) {
		this.noticeLogMapper = noticeLogMapper;
	}

	@Override
	public Integer countSendNumber(String receiver, @NotNull NoticeChannel channel) {
		// 当前时间
	    Date now = new Date();
	    // 一天前的时间
	    Date yesterday = new Date(now.getTime() - 24 * 60 * 60 * 1000);

		long count = noticeLogMapper.selectCountByReceiverAndChannelAndTimeRange(receiver, channel.getValue(), yesterday, now);
		long failCount = noticeLogMapper.selectStatusCountByReceiverAndChannelAndTimeRangeAndStatus(receiver, channel.getValue(), NoticeStatus.FAILED.getValue(), yesterday, now);

		//TODO: 需要完善逻辑
		return (int) (count - failCount);
	}

	@Override
	public boolean defaultLog(Long templateId, String receiver, @NotNull NoticeStatus status, Map<String, Object> properties) {
		NoticeLog noticeLog = new NoticeLog();
		noticeLog.setTemplateId(templateId);
		noticeLog.setReceiver(receiver);
		noticeLog.setStatus(status.getValue());
		if (properties != null){
			noticeLog.setProperties(JSON.toJSONString(properties));
		}
		return noticeLogMapper.insert(noticeLog) > 0;
	}

	@Override
	public boolean updateStatus(Long id, @NotNull NoticeStatus status) {
		NoticeLog noticeLog = new NoticeLog();
		noticeLog.setId(id);
		noticeLog.setStatus(status.getValue());
		return noticeLogMapper.updateById(noticeLog) > 0;
	}

	@Override
	public void clean() {
		// 获取当前时间
	    Date now = new Date();
//	    // 计算expiration_time分钟前的时间，作为验证码的有效期起点
//	    Date yesterday = new Date(now.getTime() - (long) emailConfig.getEmailExpirationTime() * 60 * 1000);
//
//	    // 构建查询条件：针对特定邮箱、在有效期内的验证码
//	    List<VerificationCode> list = verificationCodeMapper.selectByEmailAndTimeRangeAndStatus(email,EmailStatus.SUCCESS.getValue(),yesterday,now);
//
//		// 如果列表为空，则返回null
//		if (list.isEmpty()) {
//	        return;
//	    }
//		// 按照创建时间降序排序
//	    list.sort((a, b) -> b.getTriggerTime().compareTo(a.getTriggerTime()));
//	    // 只保留最后一条记录为有效状态，其余设置为失效状态
//	    for (int i = 1; i < list.size(); i++) {
//	        VerificationCode verificationCode = list.get(i);
//	        verificationCode.setStatus(EmailStatus.EXPIRED.getValue());
//	        // 更新数据库中的状态
//	        verificationCodeMapper.updateById(verificationCode);
//	    }
	}
}
