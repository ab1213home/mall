package com.jiang.mall.domain.vo;

import lombok.Data;

/**
 * 地址视图对象
 *
 * @author jiang
 * @version 1.0
 * @since 2024年9月8日
 */
@Data
public class AddressVo {

    /**
     * 地址ID
     */
    private Integer id;

    /**
     * 收件人名字
     */
    private String firstName;

    /**
     * 收件人姓氏
     */
    private String lastName;

    /**
     * 手机号码
     */
    private String phone;

    /**
     * 国家
     */
    private String country;

    /**
     * 省份
     */
    private String province;

    /**
     * 城市
     */
    private String city;

    /**
     * 区县
     */
    private String district;

    /**
     * 详细地址
     */
    private String addressDetail;

    /**
     * 邮政编码
     */
    private String postalCode;

    /**
     * 是否默认地址
     */
    private boolean isDefault;
}

