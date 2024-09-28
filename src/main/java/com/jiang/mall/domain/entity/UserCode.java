package com.jiang.mall.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("tb_user_codes")
public class UserCode implements Serializable {
	/**
     * 序列化版本UID
     */
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID，自增
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

	/**
     * 用户ID
     */
	private Integer userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 邮箱
     */
    private String email;

	/**
     * 验证码
     */
	private String code;

	/**
     * 用途
     */
	private Integer purpose;

    /**
     * 创建时间，自动填充
     */
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createdAt;

	/**
     * 状态
     */
	private Integer status;

	public UserCode(String username, String email, String code, Config.EmailPurpose purpose, Config.EmailStatus status) {
		this.username = username;
		this.email = email;
		this.code = code;
		this.purpose = purpose.getValue();
		this.status = status.getValue();
	}

	public UserCode() {
	}

	public UserCode(String username, String email, String code, Config.EmailPurpose emailPurpose, Config.EmailStatus emailStatus, Integer userId) {
		this.username = username;
		this.email = email;
		this.code = code;
		this.purpose = emailPurpose.getValue();
		this.status = emailStatus.getValue();
		this.userId = userId;
	}
}
