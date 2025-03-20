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

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;
import java.util.Set;

/**
 * 用户视图对象
 *
 * @author jiang
 * @version 1.0
 * @since 2024年9月8日
 */
@Data
public class UserVo {

    /**
     * 用户ID
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 名字
     */
    private String firstName;

    /**
     * 姓氏
     */
    private String lastName;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 出生日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date birthDate;

    /**
     * 默认地址ID
     */
    //TODO:计划删除
    private Long defaultAddressId;

    /**
     * 是否激活
     */
    //TODO:计划删除
    private boolean isActive;

    /**
     * 是否是管理员
     */
    //TODO:计划删除
    private boolean isAdmin;

    /**
     * 距离下次生日的天数
     */
    private Integer nextBirthday;

    /**
     * 权限set集合
     */
    //TODO:计划删除
    private Set<String> permissions;

    /**
     * 用户组set集合
     */
    //TODO:计划删除
    private Set<Long> groups;

    /**
     * 默认构造方法
     */
    public UserVo() {
    }
}

