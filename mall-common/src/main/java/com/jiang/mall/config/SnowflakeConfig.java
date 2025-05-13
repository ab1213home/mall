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

package com.jiang.mall.config;

import com.jiang.mall.util.SeataSnowflakeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SnowflakeConfig {

	private GeneralConfig generalConfig;

	@Autowired
	public void setGeneralConfig(GeneralConfig generalConfig) {
		this.generalConfig = generalConfig;
	}

	//订单ID算法
	@Bean
	public SeataSnowflakeUtil orderIdGenerator() {
		return new SeataSnowflakeUtil(generalConfig.getMachineCode(),0L);
	}

	//用户日志ID算法
	@Bean
	public SeataSnowflakeUtil userLogIdGenerator() {
		return new SeataSnowflakeUtil(generalConfig.getMachineCode(),1L);
	}

	//通知日志ID算法
	@Bean
	public SeataSnowflakeUtil noticeLogIdGenerator() {
		return new SeataSnowflakeUtil(generalConfig.getMachineCode(),2L);
	}

}
