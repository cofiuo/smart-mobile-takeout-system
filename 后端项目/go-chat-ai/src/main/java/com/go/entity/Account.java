package com.go.entity;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import com.go.common.BaseEntity;
import lombok.Data;

@Data
@TableName("sys_account")
public class Account extends BaseEntity {
    private static final long serialVersionUID = 1L;

    @TableId(value = "account_id", type = IdType.INPUT)
    private String accountId;

    @TableField("password")
    private String password;

    @TableField("account_name")
    private String accountName;

    @TableField("account_sex")
    private Integer accountSex;

    @TableField("account_img")
    private String accountImg;

    @TableField("del_tag")
    private Integer delTag;
}
