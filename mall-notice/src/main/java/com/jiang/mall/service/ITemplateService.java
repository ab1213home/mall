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

import com.jiang.mall.domain.entity.Template;
import com.jiang.mall.domain.enums.NoticeChannel;
import com.jiang.mall.domain.enums.NoticePurpose;
import com.jiang.mall.domain.vo.TemplateVo;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface ITemplateService {

	List<TemplateVo> getTemplateList(Integer pageNum, Integer pageSize);

	Long getTemplateNum();

	boolean insertTemplate(@NotNull Template template, String sessionId);

	boolean updateTemplate(@NotNull Template template, String sessionId);

	boolean deleteTemplate(Long id);

	String getTemplate(@NotNull NoticePurpose purpose, @NotNull NoticeChannel channel);

	Long getTemplateId(String template);

	String getTemplate(Long id);
}
