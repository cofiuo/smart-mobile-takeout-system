package com.go2.entity;

import java.math.BigDecimal;
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
@TableName("sys_business")
public class Business extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 商家编号
     */
    @TableId(value = "business_id", type = IdType.AUTO)
    private Long businessId;

    /**
     * 商家名称
     */
    @TableField("business_name")
    private String businessName;

    /**
     * 商家地址
     */
    @TableField("business_address")
    private String businessAddress;

    /**
     * 商家介绍
     */
    @TableField("business_explain")
    private String businessExplain;

    /**
     * 商家图片（base64）
     */
    @TableField("business_img")
    private String businessImg;

    /**
     * 起送费
     */
    @TableField("star_price")
    private BigDecimal starPrice;

    /**
     * 配送费
     */
    @TableField("delivery_price")
    private BigDecimal deliveryPrice;

    /**
     * 逻辑删除
     */
    @TableField("del_tag")
    private Integer delTag;

    /**
     * 备注
     */
    @TableField("remarks")
    private String remarks;


}
