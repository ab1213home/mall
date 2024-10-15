package com.jiang.mall.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 地址实体类，对应数据库表 tb_addresses
 *
 * @author jiang
 * @email  jiangrongjun2004@163.com
 * @link <a href="https://gitee.com/jiangrongjun/mall">https://gitee.com/jiangrongjun/mall</a>
 * @apiNote 地址实体类
 * @version 1.0
 * @since 2024年9月8日
 */
@Data
@TableName("tb_addresses")
public class Address implements Serializable {

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
     * 用户ID
     */
    private Integer userId;

    /**
     * 收件人名 - 名字
     */
    private String firstName;

    /**
     * 收件人名 - 姓氏
     */
    private String lastName;

    /**
     * 联系电话
     */
    private String phone;

    /**
     * 国家
     */
    private String country;

    /**
     * 行政代码
     */
    private Long areaCode;

    /**
     * 详细地址
     */
    private String addressDetail;

    /**
     * 邮政编码
     */
    private String postalCode;

    /**
     * 创建时间，自动填充
     */
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createdAt;

    /**
     * 更新时间，自动填充
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updatedAt;

    /**
     * 逻辑删除标记，默认为 false
     */
    @TableLogic
    private Boolean isDel;

    public Address() {
    }

    /**
     * 构造函数：初始化地址对象
     * <p>
     * 该构造函数用于创建一个地址对象，并初始化其所有字段。包括用户ID、收件人姓名、联系电话、
     * 国家地区、省份、城市、区县、详细地址及邮政编码。这些信息共同构成了一个完整的地址，
     * 用于例如物流配送、联系地址登记等场景。
     *
     * @param userId 用户ID，唯一标识一个用户
     * @param firstName 收件人的名
     * @param lastName 收件人的姓
     * @param phone 联系电话，用于配送过程中与收件人联系
     * @param country 国家地区，地址的最高级别划分
     * @param areaCode 行政代码，用于标识地址
     * @param addressDetail 详细地址，包括街道、门牌号等具体信息
     * @param postalCode 邮政编码，与地址相关联的邮政编码信息
     */
    public Address(Integer userId, String firstName, String lastName, String phone, String country,Long areaCode, String addressDetail, String postalCode) {
        this.userId=userId;
        this.firstName=firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.areaCode=areaCode;
        this.country = country;
        this.addressDetail = addressDetail;
        this.postalCode = postalCode;
    }
}
