package com.go2.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import com.go2.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author fish
 * @since 2026-01-01
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_deliveryaddress")
public class Deliveryaddress extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 送货地址编号
     */
    @TableId(value = "da_id", type = IdType.AUTO)
    private Integer daId;

    /**
     * 联系人姓名
     */
    @TableField("contact_name")
    private String contactName;

    /**
     * 联系人性别
     */
    @TableField("contact_sex")
    private Integer contactSex;

    /**
     * 联系人电话
     */
    @TableField("contact_tel")
    private String contactTel;

    /**
     * 送货地址
     */
    @TableField("address")
    private String address;

    /**
     * 下单用户编号--sys_account表account_id
     */
    @TableField("account_id")
    private String accountId;




}
