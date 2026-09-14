package com.go.entity;
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
@TableName("sys_cart")
public class Cart extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 无意义编号，编号
     */
    @TableId(value = "cart_id", type = IdType.AUTO)
    private Integer cartId;

    /**
     * 商品编号-sys_goods主键goods_id
     */
    @TableField("goods_id")
    private Integer goodsId;

    /**
     * 所属商家编号-sys_business主键businessId	
     */
    @TableField("business_id")
    private Long businessId;

    /**
     * 所属用户编号sys_account表account_id
     */
    @TableField("account_id")
    private String accountId;

    /**
     * 同一类型商品的购买数量
     */
    @TableField("quantity")
    private Integer quantity;
    //告诉mp，这个变量是自己添加，无需和数据库表中字段进行映射
    @TableField(exist = false)
    private Goods goods;   //商品对象
}
