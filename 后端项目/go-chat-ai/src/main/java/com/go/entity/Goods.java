package com.go.entity;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import com.go.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author Byterain
 * @since 2026-01-04
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_goods")
public class Goods extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 食品编号
     */
    @TableId(value = "goods_id", type = IdType.AUTO)
    private Integer goodsId;

    /**
     * 食品名称
     */
    @TableField("goods_name")
    private String goodsName;

    /**
     * 食品介绍
     */
    @TableField("goods_explain")
    private String goodsExplain;

    /**
     * 食品图片
     */
//    @TableField("goods_img")
//    private String goodsImg;

    /**
     * 食品价格
     */
    @TableField("goods_price")
    private BigDecimal goodsPrice;

    /**
     * 所属商家编号
     */
    @TableField("business_id")
    private Long businessId;

    /**
     * 备注
     */
    @TableField("remarks")
    private String remarks;

    /**
     * 销售数量
     */
    @TableField("sold_num")
    private Integer soldNum;


}
