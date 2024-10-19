package com.jiang.mall.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("tb_login_records")
public class LoginRecord implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;
	/**
	 * 主键
	 */
	@TableId(value = "id", type = IdType.AUTO)
	private Integer id;

	/**
	 * 用户名
	 */
	private String username;

	/**
	 * 用户id
	 */
	private Integer userId;

	/**
	 * ip地址
	 */
	private String ip;

	/**
	 * 状态
	 */
	private byte state;

	@TableField(fill = FieldFill.INSERT)
	private LocalDateTime loginTime;

	public LoginRecord() {
	}

	public LoginRecord(Integer userId,String username, String ip, byte state) {
		this.userId = userId;
		this.ip = ip;
		this.state = state;
		this.username = username;
	}
}
