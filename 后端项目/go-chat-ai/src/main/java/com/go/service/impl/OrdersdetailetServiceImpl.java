package com.go.service.impl;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.go.entity.Goods;
import com.go.entity.Ordersdetailet;
import com.go.mapper.OrdersdetailetMapper;
import com.go.service.GoodsService;
import com.go.service.OrdersdetailetService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrdersdetailetServiceImpl extends ServiceImpl<OrdersdetailetMapper, Ordersdetailet> implements OrdersdetailetService {
    @Autowired
    private GoodsService goodsService;

    @Override
    public List<Ordersdetailet> listAll(Long orderId) {
        QueryWrapper<Ordersdetailet> qw = new QueryWrapper<>();
        qw.eq("order_id",orderId);

        List<Ordersdetailet> odList = this.list(qw);
        odList.stream().forEach(od->{
            Goods goods = goodsService.getById(od.getGoodsId());
            od.setGoods(goods);
        });
        return odList;
    }
}
