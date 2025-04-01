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

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiang.mall.dao.NoticeLogMapper;
import com.jiang.mall.dao.TemplateMapper;
import com.jiang.mall.domain.cache.UserCache;
import com.jiang.mall.domain.entity.Template;
import com.jiang.mall.domain.enums.NoticeChannel;
import com.jiang.mall.domain.enums.NoticePurpose;
import com.jiang.mall.domain.vo.TemplateVo;
import com.jiang.mall.service.ITemplateService;
import com.jiang.mall.service.IUserService;
import com.jiang.mall.util.BeanCopyUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TemplateServiceImpl extends ServiceImpl<TemplateMapper, Template> implements ITemplateService {

	private TemplateMapper templateMapper;

	@Autowired
	public void setTemplateMapper(TemplateMapper templateMapper) {
		this.templateMapper = templateMapper;
	}

	private NoticeLogMapper noticeLogMapper;

	@Autowired
	public void setNoticeLogMapper(NoticeLogMapper noticeLogMapper) {
		this.noticeLogMapper = noticeLogMapper;
	}

	private IUserService userService;

	@Autowired
	public void setUserService(IUserService userService) {
		this.userService = userService;
	}

	@Override
	public List<TemplateVo> getTemplateList(Integer pageNum, Integer pageSize) {
		Page<Template> page = new Page<>(pageNum, pageSize);
		List<Template> templates = templateMapper.selectPage(page, null).getRecords();
		if (templates.isEmpty()) {
			return null;
		}
		List<TemplateVo> templateVos = new ArrayList<>();
		for (Template template : templates) {
			TemplateVo templateVo = BeanCopyUtils.copyBean(template, TemplateVo.class);
			assert templateVo != null;
			templateVo.setChannel(NoticeChannel.getNameByValue(template.getChannel()));
			templateVo.setPurpose(NoticePurpose.getNameByValue(template.getPurpose()));
			templateVo.setCreator(userService.getUserById(template.getCreator()));
			templateVo.setUpdater(userService.getUserById(template.getUpdater()));
			templateVos.add(templateVo);
		}
		return templateVos;
	}

	@Override
	public Long getTemplateNum() {
		return templateMapper.selectCount(null);
	}

	@Override
	public boolean insertTemplate(@NotNull Template template, String sessionId) {
		UserCache userCache = userService.getUserFromRedis(sessionId);
		QueryWrapper<Template> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("purpose", template.getPurpose());
		queryWrapper.eq("channel", template.getChannel());
		if (templateMapper.selectCount(queryWrapper) > 0) {
			//去除旧模板用途
			templateMapper.removePurposeByChannelAndPurpose(template.getChannel(), template.getPurpose(), userCache.getId());
			return templateMapper.insert(template) > 0;
		}else{
			return templateMapper.insert(template) > 0;
		}
	}

	@Override
	public boolean updateTemplate(@NotNull Template template, String sessionId) {
		if (noticeLogMapper.selectCountByTemplateId(template.getId())> 0){
			return false;
		}
		UserCache userCache = userService.getUserFromRedis(sessionId);
		QueryWrapper<Template> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("purpose", template.getPurpose());
		queryWrapper.eq("channel", template.getChannel());
		if (templateMapper.selectCount(queryWrapper) > 0) {
			//去除旧模板用途
			templateMapper.removePurposeByChannelAndPurpose(template.getChannel(), template.getPurpose(), userCache.getId());
			return templateMapper.updateById(template) > 0;
		}else{
			return templateMapper.updateById(template) > 0;
		}
	}

	@Override
	public boolean deleteTemplate(Long id) {
		if (noticeLogMapper.selectCountByTemplateId(id)> 0){
			return false;
		}
		return templateMapper.deleteById(id) > 0;
	}

	@Override
	public String getTemplate(@NotNull NoticePurpose purpose, @NotNull NoticeChannel channel) {
		QueryWrapper<Template> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("purpose", purpose.getValue());
		queryWrapper.eq("channel", channel.getValue());
		if (templateMapper.selectCount(queryWrapper) == 0){
			return null;
		}
		return templateMapper.selectOne(queryWrapper).getContent();
	}
}
