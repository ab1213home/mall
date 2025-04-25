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
import com.jiang.mall.dao.TemplateMapper;
import com.jiang.mall.dao.TemplateSnapshotMapper;
import com.jiang.mall.domain.cache.UserCache;
import com.jiang.mall.domain.entity.Template;
import com.jiang.mall.domain.entity.TemplateSnapshot;
import com.jiang.mall.domain.enums.NoticeChannel;
import com.jiang.mall.domain.enums.NoticePurpose;
import com.jiang.mall.domain.vo.TemplateVo;
import com.jiang.mall.service.INoticeRedisService;
import com.jiang.mall.service.ITemplateService;
import com.jiang.mall.service.IUserService;
import com.jiang.mall.util.BeanCopyUtil;
import com.jiang.mall.util.SecureUtil;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TemplateServiceImpl extends ServiceImpl<TemplateMapper, Template> implements ITemplateService {

	private static final Logger logger = LoggerFactory.getLogger(TemplateServiceImpl.class);

	private TemplateMapper templateMapper;

	@Autowired
	public void setTemplateMapper(TemplateMapper templateMapper) {
		this.templateMapper = templateMapper;
	}

	private TemplateSnapshotMapper templateSnapshotMapper;

	@Autowired
	public void setTemplateSnapshotMapper(TemplateSnapshotMapper templateSnapshotMapper) {
		this.templateSnapshotMapper = templateSnapshotMapper;
	}

	private INoticeRedisService redisService;

	@Autowired
	public void setRedisService(INoticeRedisService redisService) {
		this.redisService = redisService;
	}

	private IUserService userService;

	@Autowired
	public void setUserService(@Lazy IUserService userService) {
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
			TemplateVo templateVo = BeanCopyUtil.copyBean(template, TemplateVo.class);
			assert templateVo != null;
			//TODO:使用json映射
//			templateVo.setChannel(NoticeChannel.fromKey(template.getChannel()).getName());
//			templateVo.setPurpose(NoticePurpose.fromKey(template.getPurpose()).getName());
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
//		queryWrapper.eq("purpose", template.getPurpose());
//		queryWrapper.eq("channel", template.getChannel());
//		if (templateMapper.selectCount(queryWrapper) > 0) {
//			//去除旧模板用途
//			templateMapper.removePurposeByChannelAndPurpose(template.getChannel(), template.getPurpose(), userCache.getId());
//			return templateMapper.insert(template) > 0;
//		}else{
//			return templateMapper.insert(template) > 0;
//		}
		return false;
	}

	@Override
	public boolean updateTemplate(@NotNull Template template, String sessionId) {
		UserCache userCache = userService.getUserFromRedis(sessionId);
//		QueryWrapper<Template> queryWrapper = new QueryWrapper<>();
//		queryWrapper.eq("purpose", template.getPurpose());
//		queryWrapper.eq("channel", template.getChannel());
//		if (templateMapper.selectCount(queryWrapper) > 0) {
//			//去除旧模板用途
//			templateMapper.removePurposeByChannelAndPurpose(template.getChannel(), template.getPurpose(), userCache.getId());
//			return templateMapper.updateById(template) > 0;
//		}else{
//			return templateMapper.updateById(template) > 0;
//		}
		return false;
	}

	@Override
	public boolean deleteTemplate(Long id) {
		return templateMapper.deleteById(id) > 0;
	}

	@Override
	public String getTemplate(@NotNull NoticePurpose purpose, @NotNull NoticeChannel channel) {
		QueryWrapper<Template> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("purpose", purpose.getKey());
		queryWrapper.eq("channel", channel.getKey());
		if (templateMapper.selectCount(queryWrapper) == 0){
			return null;
		}
//		Template template = templateMapper.selectOne(queryWrapper);
//		if (template.getChannel() == NoticeChannel.SMS_OVERSEAS.getKey()){
//			return template.getName();
//		}else{
//			return template.getContent();
//		}
		return null;
	}

	@Override
	public Long getTemplate(String template) {
		//模板哈希值
		String templateHash = SecureUtil.sha256Hex(template);
		Long templateId = templateSnapshotMapper.selectByHash(templateHash);
		if (templateId == null){
			TemplateSnapshot templateSnapshot = new TemplateSnapshot();
			templateSnapshot.setContent(template);
			templateSnapshot.setHash(templateHash);
			if (templateSnapshotMapper.insert(templateSnapshot) > 0){
				templateId = templateSnapshot.getId();
			}
		}
		return templateId;
	}

	@Override
	public String getTemplate(Long id) {
//		Template template = templateMapper.selectById(id);
//		if (template == null){
//			return null;
//		}else {
//			if (template.getChannel() == NoticeChannel.SMS_MAINLAND.getKey()){
//				return template.getName();
//			}else{
//				return template.getContent();
//			}
//		}
		return null;
	}

	@Override
	public Map<String, Object> getTemplate(@NotNull NoticeChannel channel, @NotNull NoticePurpose purpose, String region) {
		Map<String, Object> result = new HashMap<>();
		logger.error("{}{}模板不存在", purpose.getName(), channel.getName());
		//邮件
//		Long templateId = (Long) template.get("id");
//		String templateContent = (String) template.get("template");

		//短信（中国境内）
//		Long templateId = (Long) template.get("id");
//		String templateContent = (String) template.get("template");
//		String templateCode = (String) template.get("name");

		//短信（中国香港、中国澳门、中国台湾以及中国境外地区）
//		Long templateId = (Long) template.get("id");
//		String templateContent = (String) template.get("template");

		//SSE
//		Long templateId = (Long) template.get("id");
//		String templateContent = (String) template.get("template");

//		String template;
//		if (redisService.hasTemplate(purpose, channel)){
//			template = redisService.getTemplate(purpose, channel);
//		}else {
//			template = getTemplate(purpose, channel);
//			if (template == null){
//				logger.error("{}{}模板不存在", purpose.getName(), channel.getName());
//				return null;
//			}else {
//				redisService.setTemplate(purpose, channel, template);
//			}
//		}
//		result.put("template", template);
//		result.put("name", template);
//		result.put("id", getTemplate(template));
		return result;
	}
}
