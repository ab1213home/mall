/*
 * Copyright (c) 2024 Jiang RongJun
 * Jiang Mall is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan
 * PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY
 * KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

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
    private Long id;


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
    private String county;

    /**
     * 乡镇
     */
    private String township;

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

    @Override
    public String toString() {
        return "AddressVo{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", phone='" + phone + '\'' +
                ", country='" + country + '\'' +
                ", province='" + province + '\'' +
                ", city='" + city + '\'' +
                ", district='" + county + '\'' +
                ", addressDetail='" + addressDetail + '\'' +
                ", postalCode='" + postalCode + '\'' +
                ", isDefault=" + isDefault +
                '}';
    }
}

