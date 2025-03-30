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

import com.jiang.mall.service.INoticeLogService;
import com.jiang.mall.service.INoticeService;
import com.jiang.mall.service.ITemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NoticeServiceImpl implements INoticeService {

	private INoticeLogService noticeLogService;

	@Autowired
	public void setNoticeLogService(INoticeLogService noticeLogService) {
		this.noticeLogService = noticeLogService;
	}

	private ITemplateService templateService;

	@Autowired
	public void setTemplateService(ITemplateService templateService) {
		this.templateService = templateService;
	}


}
