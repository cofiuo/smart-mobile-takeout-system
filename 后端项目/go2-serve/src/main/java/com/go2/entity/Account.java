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
@TableName("sys_account")
public class Account extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @TableId(value = "account_id")
    private String accountId;

    @TableField("password")
    private String password;

    @TableField(exist = false)
    private String NewPassword;

    @TableField("account_name")
    private String accountName;

    @TableField("account_sex")
    private Integer accountSex;

    @TableField("account_img")
    private String accountImg;

    @TableField("del_tag")
    private Integer delTag;



}
