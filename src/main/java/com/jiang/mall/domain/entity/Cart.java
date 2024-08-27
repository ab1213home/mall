package com.jiang.mall.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * <p>
 * 实体类
 * </p>
 *作者： 蒋神 HJL
 * @since 2024-08-05
 */
@Getter
@Setter
@Data
@TableName("cart")
public class Cart implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 商品id
     */
    private Integer prodId;

    /**
     * 商品数量
     */
    private Integer num;

    /**
     * 用户id
     */
    private Integer userId;

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
