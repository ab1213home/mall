package com.jiang.mall.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 订单列表实体类，对应数据库表 tb_orderLists
 *
 * @author jiang
 * @email  jiangrongjun2004@163.com
 * @link <a href="https://gitee.com/jiangrongjun/mall">https://gitee.com/jiangrongjun/mall</a>
 * @apiNote 订单列表实体类
 * @version 1.0
 * @since 2024年9月8日
 */
@Data
@TableName("tb_orderLists")
public class OrderList implements Serializable {

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
     * 订单ID
     */
    private Long orderId;

    /**
     * 商品快照ID
     */
    private Long prodId;

    /**
     * 购买数量
     */
    private Integer num;

    /**
     * 创建时间，自动填充
     */
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createdAt;

    /**
     * 更新时间，自动填充
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updatedAt;

    /**
     * 逻辑删除标记，默认为 false
     */
    @TableLogic
    private Boolean isDel;

    /**
     * 构造函数，用于创建订单列表实体
     *
     * @param id 订单ID
     * @param num 购买数量
     */
    public OrderList(Long id, Integer num) {
        this.orderId = id;
        this.num = num;
    }

    public OrderList() {
    }
}
