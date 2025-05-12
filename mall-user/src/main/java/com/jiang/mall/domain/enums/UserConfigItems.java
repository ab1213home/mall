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

package com.jiang.mall.domain.enums;

import lombok.Getter;

@Getter
public enum UserConfigItems {
	USER_MAX_TRY("user.max.try", "最大尝试登录次数", "5"),
	USER_MAX_ADDRESS("user.max.address", "最大收货地址数量", "50"),
	USER_DEFAULT_GROUP("user.default.group", "默认用户组", "1"),
	USER_CACHE_TIME("user.cache.time", "用户会话保持时间(小时)", "24"),
	USER_REGISTER_ENABLED("user.register.enabled", "是否允许注册", "true"),
	USER_REDIS_ENCRYPTION("user.redis.encryption", "用户redis缓存键是否加密(SHA-256)", "false"),
	USER_LOG_COMMIT_STRATEGY("user.log.commit.strategy", "用户日志提交策略:BATCH(批处理)SIMPLE(简单模式)", "SIMPLE"),
	USER_LOG_BATCH_FLUSH_SIZE("user.log.batch.flush.size", "用户日志批处理提交大小(SIMPLE模式不生效)", "500");

	private final String key;
	private final String description;
	private final String defaultValue;

	UserConfigItems(String key, String description, String defaultValue) {
		this.key = key;
		this.description = description;
		this.defaultValue = defaultValue;
	}

}
