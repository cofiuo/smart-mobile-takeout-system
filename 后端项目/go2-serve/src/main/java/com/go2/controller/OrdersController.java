package com.go2.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.go2.common.Const;
import com.go2.common.Result;
import com.go2.entity.*;
import com.go2.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import com.go2.common.BaseController;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author fish
 * @since 2026-01-09
 */
@RestController
@RequestMapping("/orders")
@CrossOrigin
public class OrdersController extends BaseController {

    @Autowired
    private CartService cartService;

    @Autowired
    private OrdersService ordersService;

    @Autowired
    private OrdersdetailetService ordersdetailetService;

    @Autowired
    private BusinessService businessService;

    @Autowired
    private GoodsService goodsService;
    
    @Autowired
    private CommentService commentService;

    @Transactional
    @PostMapping("/save")
    public Result save(@RequestBody Orders  orders){
        //查询购物车数据
        QueryWrapper<Cart> qw = new QueryWrapper<>();
        qw.eq("account_id", orders.getAccountId());
        qw.eq("business_id", orders.getBusinessId());
        List<Cart> cartList = cartService.list(qw);

        //保存订单
        orders.setCreated(LocalDateTime.now());
        orders.setUpdated(LocalDateTime.now());
        orders.setStatu(0);
        ordersService.save(orders);

        //保存后，获得订单编号
        Long orderId = orders.getOrderId();

        //保存订单详情
        List<Ordersdetailet> odList = new ArrayList<>();
        cartList.forEach(cart -> {
            Ordersdetailet od = new Ordersdetailet();
            od.setGoodsId(cart.getGoodsId());
            od.setQuantity(cart.getQuantity());
            od.setOrderId(orderId);

            odList.add(od);

        });
        //调用MP的批量保存方法
        ordersdetailetService.saveBatch(odList);


        cartService.remove(qw);
        return Result.success(orderId);
    }

    // 修改订单状态
    @PostMapping("/update")
    public Result update(@RequestBody Orders orders){
        QueryWrapper<Orders> qw = new QueryWrapper<>();
        qw.eq("order_id",orders.getOrderId());
        orders.setStatu(1);
        ordersService.update(orders,qw);

        return Result.success(null);
    }

    // 查询订单
    @GetMapping("/get/{orderId}")
    public Result get(@PathVariable Integer orderId){
        Orders orders = ordersService.getById(orderId);
        return Result.success(orders);

    }

    @GetMapping("/listByAccountId/{accountId}")
    public Result listByAccountId(@PathVariable String accountId) {
        // 构建查询条件：按用户ID筛选，按创建时间倒序
        QueryWrapper<Orders> qw = new QueryWrapper<>();
        qw.eq("account_id", accountId).orderByDesc("created");
        List<Orders> ordersList = ordersService.list(qw);

        // 空结果处理（与 BusinessController 错误提示风格一致）
        if (ordersList == null || ordersList.isEmpty()) {
            return Result.fail(Const.GET_DATA_FAIL, "该用户暂无订单记录", null);
        }

        // 为每个订单设置关联的商家信息和订单明细
        ordersList = setOrderAssociations(ordersList);

        return Result.success(ordersList);
    }



    @GetMapping("/listByAccountIdAndStatu/{accountId}/{statu}")
    public Result listByAccountIdAndStatu(
            @PathVariable String accountId,
            @PathVariable Integer statu
    ) {
        // 构建多条件查询：用户ID + 订单状态，按创建时间倒序
        QueryWrapper<Orders> qw = new QueryWrapper<>();
        qw.eq("account_id", accountId)
                .eq("statu", statu)
                .orderByDesc("created");
        List<Orders> ordersList = ordersService.list(qw);

        // 空结果处理
        if (ordersList == null || ordersList.isEmpty()) {
            return Result.fail(Const.GET_DATA_FAIL, "该用户暂无对应状态的订单", null);
        }

        // 为每个订单设置关联的商家信息和订单明细
        ordersList = setOrderAssociations(ordersList);

        return Result.success(ordersList);
    }

//根据订单ID获取订单详情

    @GetMapping("/getOrderDetail/{orderId}")
    public Result getOrderDetail(@PathVariable Long orderId) {
        // 查询订单信息
        Orders order = ordersService.getById(orderId);
        if (order == null) {
            return Result.fail(Const.GET_DATA_FAIL, "订单不存在", null);
        }

        // 查询关联的商家信息
        Business business = businessService.getById(order.getBusinessId());
        order.setBusiness(business);

        // 查询关联的订单明细
        QueryWrapper<Ordersdetailet> qd = new QueryWrapper<>();
        qd.eq("order_id", order.getOrderId());
        List<Ordersdetailet> detailList = ordersdetailetService.list(qd);
        order.setDetails(detailList);

        // 为每个订单明细设置关联的商品信息
        detailList.forEach(detail -> {
            Goods goods = goodsService.getById(detail.getGoodsId());
            detail.setGoods(goods);
        });

        return Result.success(order);
    }

    /**
     * 为订单列表设置关联信息（商家、订单明细、商品）
     * @param ordersList 订单列表
     * @return 包含关联信息的订单列表
     */
    private List<Orders> setOrderAssociations(List<Orders> ordersList) {
        // 为每个订单设置关联的商家信息和订单明细
        ordersList.forEach(order -> {
            // 查询关联的商家信息
            Business business = businessService.getById(order.getBusinessId());
            order.setBusiness(business);

            // 查询关联的订单明细
            QueryWrapper<Ordersdetailet> qd = new QueryWrapper<>();
            qd.eq("order_id", order.getOrderId());
            List<Ordersdetailet> detailList = ordersdetailetService.list(qd);
            order.setDetails(detailList);

            // 为每个订单明细设置关联的商品信息
            detailList.forEach(detail -> {
                Goods goods = goodsService.getById(detail.getGoodsId());
                detail.setGoods(goods);
            });
            
            // 查询订单是否已评价
            QueryWrapper<Comment> qc = new QueryWrapper<>();
            qc.eq("order_id", order.getOrderId());
            boolean isCommented = commentService.count(qc) > 0;
            order.setIsCommented(isCommented);
        });
        return ordersList;
    }
}
