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
@TableName("sys_favorite")
public class Favorite extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 收藏id
     */
    @TableId(value = "favorite_id", type = IdType.AUTO)
    private Long favoriteId;

    /**
     * 收藏的用户
     */
    @TableField("account_id")
    private String accountId;

    /**
     * 被收藏商家
     */
    @TableField("business_id")
    private Long businessId;

    @TableField(exist = false)
    private Business business;


}
