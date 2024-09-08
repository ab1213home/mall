package com.jiang.mall.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * 订单实体类，对应数据库表 tb_orders
 *
 * @author jiang
 * @email  jiangrongjun2004@163.com
 * @link <a href="https://gitee.com/jiangrongjun/mall">https://gitee.com/jiangrongjun/mall</a>
 * @apiNote 订单实体类
 * @version 1.0
 * @since 2024年9月8日
 */
@Data
@TableName("tb_orders")
public class Order implements Serializable {

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
     * 地址ID
     */
    private Integer addressId;

    /**
     * 用户ID
     */
    private Integer userId;

    /**
     * 下单日期
     */
    private Date date;

    /**
     * 订单总金额
     */
    private Double totalAmount;

    /**
     * 订单状态
     */
    private Integer status;

    /**
     * 支付方式
     */
    private Integer paymentMethod;

    /**
     * 创建时间，自动填充
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * 更新时间，自动填充
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    /**
     * 逻辑删除标记，默认为 false
     */
    @TableLogic
    private Boolean isDel;
}
