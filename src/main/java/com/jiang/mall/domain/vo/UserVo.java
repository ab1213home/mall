package com.jiang.mall.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
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
     * 头像
     */
    private String img;

    /**
     * 出生日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date birthDate;

    /**
     * 默认地址ID
     */
    private Integer defaultAddressId;

    /**
     * 是否激活
     */
    private boolean isActive;

    /**
     * 角色ID
     */
    private Integer roleId;

    /**
     * 是否是管理员
     */
    private boolean isAdmin;

    /**
     * 距离下次生日的天数
     */
    private Integer nextBirthday;


    /**
     * 构造方法，用于初始化用户ID和用户名
     *
     * @param userId   用户ID
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

    public UserVo(Integer userId, String username, String email, String phone, String firstName, String lastName, Date birthDate, Integer defaultAddressId, Integer roleId, boolean isAdmin, Integer daysUntilNextBirthday) {
        this.id = userId;
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.defaultAddressId = defaultAddressId;
        this.roleId = roleId;
        this.isAdmin = isAdmin;
        this.nextBirthday = daysUntilNextBirthday;
    }

    public UserVo(Integer userId, String username, String email, boolean isAdmin,Integer roleId) {
        this.id = userId;
        this.username = username;
        this.email = email;
        this.isAdmin = isAdmin;
        this.roleId = roleId;
    }
}

