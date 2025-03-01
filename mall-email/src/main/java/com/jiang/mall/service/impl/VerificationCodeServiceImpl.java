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

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiang.mall.config.EmailConfig;
import com.jiang.mall.dao.VerificationCodeMapper;
import com.jiang.mall.domain.entity.VerificationCode;
import com.jiang.mall.domain.enums.EmailPurpose;
import com.jiang.mall.domain.enums.EmailStatus;
import com.jiang.mall.domain.vo.VerificationCodeVo;
import com.jiang.mall.service.IVerificationCodeService;
import com.jiang.mall.util.BeanCopyUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class VerificationCodeServiceImpl extends ServiceImpl<VerificationCodeMapper, VerificationCode> implements IVerificationCodeService {

	private VerificationCodeMapper verificationCodeMapper;

	@Autowired
	public void setCodeMapper(VerificationCodeMapper verificationCodeMapper) {
		this.verificationCodeMapper = verificationCodeMapper;
	}

	private EmailConfig emailConfig;

	@Autowired
	public void setEmailConfig(EmailConfig emailConfig) {
		this.emailConfig = emailConfig;
	}

	/**
	 * 根据邮箱检查发送状态
	 * 本方法主要用于检查在过去24小时内，给定邮箱接收到的验证码的发送状态
	 * 它通过统计成功、失败和其他状态的验证码数量，结合总的请求数量，
	 * 来判断是否达到了最大失败率阈值或超过了最大请求数量
	 *
	 * @param email 需要检查的邮箱地址
	 * @return 如果失败率超过最大失败率阈值或请求数量超过最大请求数量，返回true；否则返回false
	 */
	@Override
	public Boolean inspectByEmail(String email) {
	    // 当前时间
	    Date now = new Date();
	    // 一天前的时间
	    Date yesterday = new Date(now.getTime() - 24 * 60 * 60 * 1000);

		long listCount = verificationCodeMapper.selectCountByEmailAndTimeRange(email,yesterday,now);
		long failCount = verificationCodeMapper.selectFailCountByEmailAndStatusAndTimeRange(email, EmailStatus.FAILED.getValue(), yesterday, now);

	    // 检查请求数量是否小于等于最小请求数量
	    if (listCount <= emailConfig.getEmailMinRequestNum()) {
	        return false;
	    }
	    // 检查请求数量是否大于最大请求数量
	    if (listCount > emailConfig.getEmailMaxRequestNum()) {
	        return true;
	    }
	    // 计算失败率并判断是否超过最大失败率阈值
	    return failCount / (double) listCount > emailConfig.getEmailMaxFailRate();
	}

	/**
	 * 使用验证码
	 * <p>
	 * 本方法主要用于将验证码的状态从未使用更改为已使用，并更新数据库中的记录。
	 * 它首先根据用户ID和验证码对象更新数据库中的记录，如果更新成功则返回true，否则返回false。
	 *
	 * @param userId           用户ID
	 * @param verificationCode 验证码对象
	 */
	@Override
	public void useCode(Long userId, @NotNull VerificationCode verificationCode) {
	    // 设置用户ID，以便确定哪位用户的验证码将被更新
	    verificationCode.setUserId(userId);
	    // 将验证码状态更改为“已使用”
	    verificationCode.setStatus(EmailStatus.USED.getValue());
	    // 更新数据库中的验证码记录，并返回更新结果
		verificationCodeMapper.updateById(verificationCode);
	}

	@Override
	public Boolean insert(VerificationCode userVerificationCode) {
		return verificationCodeMapper.insert(userVerificationCode) > 0;
	}

	@Override
	public List<VerificationCodeVo> getList(Integer pageNum, Integer pageSize) {
		Page<VerificationCode> page = new Page<>(pageNum, pageSize);
		List<VerificationCode> verificationCodes = verificationCodeMapper.selectPage(page, null).getRecords();
		List<VerificationCodeVo> verificationCodeVos = new ArrayList<>();
		for (VerificationCode verificationCode : verificationCodes) {
			VerificationCodeVo verificationCodeVo = BeanCopyUtils.copyBean(verificationCode, VerificationCodeVo.class);
			assert verificationCodeVo != null;
			verificationCodeVo.setStatus(EmailStatus.getNameByValue(verificationCode.getStatus()));
			verificationCodeVo.setPurpose(EmailPurpose.getNameByValue(verificationCode.getPurpose()));
			verificationCodeVos.add(verificationCodeVo);
		}
		return verificationCodeVos;
	}

	@Override
	public Long getVerificationCodeNum() {
		return verificationCodeMapper.selectCount(null);
	}

	@Override
	public Boolean add(VerificationCode userVerificationCode) {
		return verificationCodeMapper.insert(userVerificationCode)>0;
	}

	@Override
	public VerificationCode queryById(Long id) {
		return verificationCodeMapper.selectById(id);
	}

	@Override
	public void clean(String email, Long id) {
		// 获取当前时间
	    Date now = new Date();
	    // 计算expiration_time分钟前的时间，作为验证码的有效期起点
	    Date yesterday = new Date(now.getTime() - (long) emailConfig.getEmailExpirationTime() * 60 * 1000);

	    // 构建查询条件：针对特定邮箱、在有效期内的验证码
	    List<VerificationCode> list = verificationCodeMapper.selectByEmailAndTimeRangeAndStatus(email,EmailStatus.SUCCESS.getValue(),yesterday,now);

		// 如果列表为空，则返回null
		if (list.isEmpty()) {
	        return;
	    }
		// 按照创建时间降序排序
	    list.sort((a, b) -> b.getTriggerTime().compareTo(a.getTriggerTime()));
	    // 只保留最后一条记录为有效状态，其余设置为失效状态
	    for (int i = 1; i < list.size(); i++) {
	        VerificationCode verificationCode = list.get(i);
	        verificationCode.setStatus(EmailStatus.EXPIRED.getValue());
	        // 更新数据库中的状态
	        verificationCodeMapper.updateById(verificationCode);
	    }

	}

}
