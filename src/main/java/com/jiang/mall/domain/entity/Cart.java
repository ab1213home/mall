package com.jiang.mall.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 购物车实体类，对应数据库表 tb_cart
 *
 * @author jiang
 * @email  jiangrongjun2004@163.com
 * @link <a href="https://gitee.com/jiangrongjun/mall">https://gitee.com/jiangrongjun/mall</a>
 * @apiNote 购物车实体类
 * @version 1.0
 * @since 2024年9月8日
 */
@Data
@TableName("tb_cart")
public class Cart implements Serializable {

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
     * 商品数量
     */
    private Integer num;

    /**
     * 用户ID
     */
    private Integer userId;

    /**
     * 购物车对象的字符串表示形式
     */
    @Override
    public String toString() {
        return "Cart{" +
            "id = " + id +
            ", prodId = " + prodId +
            ", num = " + num +
            ", userId = " + userId +
        "}";
    }
}
