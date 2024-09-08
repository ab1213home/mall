package com.jiang.mall.domain.vo;

import lombok.Data;

/**
 * 轮播图视图对象
 *
 * @author jiang
 * @version 1.0
 * @since 2024年9月8日
 */
@Data
public class BannerVo {

    /**
     * 轮播图ID
     */
    private Integer id;

    /**
     * 轮播图图片地址
     */
    private String img;

    /**
     * 跳转URL
     */
    private String url;

    /**
     * 描述信息
     */
    private String description;
}
