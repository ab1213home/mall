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
@TableName("tb_templates")
public class Template implements Serializable {
//CREATE TABLE notice_template (
//    id BIGINT PRIMARY KEY AUTO_INCREMENT,
//    channels VARCHAR(50) COMMENT '支持渠道（逗号分隔，如：0,1,2）',
//    purposes VARCHAR(255) COMMENT '适用场景（逗号分隔，如：1,3,5）',
//    template_content TEXT NOT NULL,
//    variables VARCHAR(255) COMMENT '变量列表（逗号分隔）',
//    region_config JSON COMMENT '区域配置（JSON格式）',
//    priority INT DEFAULT 0 COMMENT '优先级',
//    status TINYINT DEFAULT 1,
//    content_type VARCHAR(20) DEFAULT 'TEXT'
//);
	/**
     * 序列化版本UID
     */
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID，自增
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

	/**
     * 模板名称
     */
	private String name;

	/**
     * 模板内容
     */
	private String content;

	/**
     * 通道
     */
	private String channels;

	/**
     * 用途
     */
	private String purposes;

	/**
     * 区域
     */
	private String region;

	/**
     * 优先级
     */
	private Integer priority;

	/**
     * 创建人
     */
    @TableField(fill = FieldFill.INSERT)
    private Long creator;

    /**
     * 创建时间，自动填充
     */
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createdAt;

    /**
     * 更新人
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updater;

    /**
     * 更新时间，自动填充
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updatedAt;

	@TableLogic
	private String isDel;
}
