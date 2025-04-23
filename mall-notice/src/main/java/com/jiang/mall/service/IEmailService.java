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


import com.jiang.mall.domain.dto.NoticeResultDto;

public interface IEmailService {

	/**
	 * 发送邮件
	 *
	 * @param receiver 收件人
	 * @param subject  主题
	 * @param content  内容
	 * @return 是否发送成功
	 */
	NoticeResultDto sendEmail(String receiver, String subject, String content);

}
