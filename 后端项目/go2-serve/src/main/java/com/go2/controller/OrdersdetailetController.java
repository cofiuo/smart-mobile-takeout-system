package com.go2.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.go2.common.Result;
import com.go2.entity.Goods;
import com.go2.entity.Ordersdetailet;
import com.go2.service.GoodsService;
import com.go2.service.OrdersdetailetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.go2.common.BaseController;

import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author fish
 * @since 2026-01-01
 */
@RestController
@RequestMapping("/ordersdetailet")
@CrossOrigin
public class OrdersdetailetController extends BaseController {
    @Autowired
    private OrdersdetailetService ordersdetailetService;
    @Autowired
    private GoodsService goodsService;


    @GetMapping("/listByOrderId/{orderId}")
    public Result listCartByOrderId(@PathVariable Long orderId){
        QueryWrapper<Ordersdetailet> qw = new QueryWrapper<>();
        qw.eq("order_id",orderId);
        List<Ordersdetailet> list = ordersdetailetService.list(qw);

        list.stream().forEach(ordersdetailet->{
            Goods goods = goodsService.getById(ordersdetailet.getGoodsId());
            ordersdetailet.setGoods(goods);
        });
        return Result.success(list);
    }
}
