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

package com.jiang.mall.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jiang.mall.domain.entity.NoticeLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.Date;
import java.util.List;

@Mapper
public interface NoticeLogMapper extends BaseMapper<NoticeLog> {

	@Select("SELECT COUNT(DISTINCT log.id) FROM tb_notice_logs log JOIN tb_templates templates ON templates.id = log.templateId \n" +
			"WHERE log.receiver = #{receiver} AND templates.channel = #{channel} AND log.trigger_time BETWEEN #{start} AND #{end}")
	long selectCountByReceiverAndChannelAndTimeRange(String receiver, int channel, Date start, Date end);

	@Select("SELECT COUNT(id) FROM tb_notice_logs WHERE templateId = #{templateId}")
	long selectCountByTemplateId(Long templateId);

	@Select("SELECT COUNT(DISTINCT log.id) FROM tb_notice_logs log JOIN tb_templates templates ON templates.id = log.templateId \n" +
			"WHERE log.receiver = #{receiver} AND templates.channel = #{channel} AND log.trigger_time BETWEEN #{start} AND #{end} AND log.status = #{status}")
	long selectStatusCountByReceiverAndChannelAndTimeRangeAndStatus(String receiver, int channel, int status, Date start, Date end);

	@Select("SELECT id FROM tb_notice_logs WHERE status = #{status} AND trigger_time < #{end}")
	List<Long> selectIdListByTimeRangeAndStatus(int status, Date end);

	@Update("UPDATE tb_notice_logs SET status = #{status} WHERE id = #{id}")
	void updateStatusById(Long id, int status);
}
