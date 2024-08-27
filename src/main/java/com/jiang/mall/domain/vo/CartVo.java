package com.jiang.mall.domain.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CartVo {

    private Integer id;
    private Integer prodId;
    private String prodName;
    private Integer num;
    private Integer userId;
    private BigDecimal price;
    private String img;
}
