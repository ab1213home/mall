package com.jiang.mall.domain.vo;

import lombok.Data;

import java.util.Date;

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
    private Integer id;

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
     * 出生日期
     */
    private Date birthDate;

    /**
     * 默认地址ID
     */
    private Integer defaultAddressId;

    /**
     * 角色ID
     */
    private Integer roleId;

    /**
     * 构造方法，用于初始化用户ID和用户名
     *
     * @param userId 用户ID
     * @param username 用户名
     */
    public UserVo(Integer userId, String username) {
        this.id = userId;
        this.username = username;
    }

    /**
     * 默认构造方法
     */
    public UserVo() {
    }
}

