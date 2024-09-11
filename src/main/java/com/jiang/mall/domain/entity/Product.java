package com.jiang.mall.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;


/**
 * 商品实体类，对应数据库表 tb_products
 *
 * @author jiang
 * @email  jiangrongjun2004@163.com
 * @link <a href="https://gitee.com/jiangrongjun/mall">https://gitee.com/jiangrongjun/mall</a>
 * @apiNote 商品实体类
 * @version 1.0
 * @since 2024年9月8日
 */
@Data
@TableName("tb_products")
public class Product implements Serializable {

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
     * 商品编码，不可重复
     */
    private String code;

    /**
     * 商品标题
     */
    private String title;

    /**
     * 商品分类ID
     */
    private Integer categoryId;

    /**
     * 商品图片
     */
    private String img;

    /**
     * 商品价格
     */
    private Double price;

    /**
     * 商品库存
     */
    private Integer stocks;

    /**
     * 商品描述
     */
    private String description;

    /**
     * 创建人
     */
    @TableField(fill = FieldFill.INSERT)
    private Integer creator;

    /**
     * 更新人
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Integer updater;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    /**
     * 是否删除，默认为 false
     */
    @TableLogic
    private Boolean isDel;

    /**
     * 商品对象的字符串表示形式
     */
    @Override
    public String toString() {
        return "Product{" +
            "id = " + id +
            ", code = " + code +
            ", title = " + title +
            ", categoryId = " + categoryId +
            ", img = " + img +
            ", price = " + price +
            ", stocks = " + stocks +
            ", description = " + description +
            ", creator = " + creator +
            ", createdAt = " + createdAt +
            ", updatedAt = " + updatedAt +
            ", isDel = " + isDel +
        "}";
    }
}

