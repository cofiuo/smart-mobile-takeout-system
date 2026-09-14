package com.go.service;

import com.go.entity.Ordersdetailet;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Byterain
 * @since 2026-01-08
 */
public interface OrdersdetailetService extends IService<Ordersdetailet> {
    //根据订单编号，查询到该订单的具体订单明细数据
    List<Ordersdetailet> listAll(Long orderId);
}
