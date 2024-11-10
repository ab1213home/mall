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

package com.jiang.mall.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("tb_user_records")
public class UserRecord implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;
	/**
	 * 主键
	 */
	@TableId(value = "id", type = IdType.AUTO)
	private Long id;

	/**
	 * 用户名
	 */
	private String username;

	/**
	 * 邮箱
	 */
	private String oldEmail;

	/**
	 * 用户id
	 */
	private Long userId;

	/**
	 * ip地址
	 */
	private String ip;

	/**
	 * 浏览器指纹
	 */
	private String fingerprint;

	/**
	 * 状态
	 */
	private Byte state;

	@TableField(fill = FieldFill.INSERT)
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private LocalDateTime triggerTime;

	/**
	 * 更新人
	 */
	@TableField(fill = FieldFill.INSERT_UPDATE)
	private Long updater;

	public UserRecord() {
		this.triggerTime = LocalDateTime.now();
	}

	public UserRecord(Long userId, String username, String clientIp, byte value, String fingerprint) {
		this.userId = userId;
		this.username = username;
		this.ip = clientIp;
		this.state = value;
		this.fingerprint = fingerprint;
		this.triggerTime = LocalDateTime.now();
	}

	public UserRecord(String username, String clientIp, String fingerprint, byte value) {
		this.username = username;
		this.ip = clientIp;
		this.state = value;
		this.fingerprint = fingerprint;
		this.triggerTime = LocalDateTime.now();
	}
}
