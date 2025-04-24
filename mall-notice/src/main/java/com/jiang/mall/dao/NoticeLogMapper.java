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

@Mapper
public interface NoticeLogMapper extends BaseMapper<NoticeLog> {

	@Select("SELECT COUNT(*) FROM tb_notice_logs  WHERE receiver = #{receiver} AND channel = #{channel} AND trigger_time BETWEEN #{start} AND #{end} AND purpose BETWEEN 1 AND 5")
	long selectCountBySingleChannelAndTimeRange(String receiver, int channel, Date start, Date end);

	@Select("SELECT COUNT(*) FROM tb_notice_logs  WHERE receiver = #{receiver} AND channel = #{channel} AND trigger_time BETWEEN #{start} AND #{end} AND status = #{status} AND purpose BETWEEN 1 AND 5")
	long selectCountBySingleChannelAndTimeRangeAndStatus(String receiver, int channel, int status, Date start, Date end);

	@Update("UPDATE tb_notice_logs SET status = #{status} WHERE id = #{id}")
	int updateStatusById(Long id, int status);
}
