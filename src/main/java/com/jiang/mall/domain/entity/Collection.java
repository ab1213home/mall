package com.jiang.mall.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serial;
import java.time.LocalDateTime;

/**
 * 商品分类实体类，对应数据库表 tb_collection
 *
 * @author jiang
 * @email  jiangrongjun2004@163.com
 * @link <a href="https://gitee.com/jiangrongjun/mall">https://gitee.com/jiangrongjun/mall</a>
 * @apiNote 收藏实体类
 * @version 1.0
 * @since 2024年9月20日
 */
@Data
@TableName("tb_collection")
public class Collection {

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
     * 商品ID
     */
    private Integer prodId;


    /**
     * 用户ID
     */
    private Integer userId;

	/**
     * 创建时间，自动填充
     */
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createdAt;

	@TableLogic
	private Boolean isDel;

	public Collection() {
	}

	public Collection(Integer productId, Integer userId) {
		this.prodId = productId;
		this.userId = userId;
	}
}
