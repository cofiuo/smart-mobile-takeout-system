package com.go2.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.go2.common.Const;
import com.go2.common.Result;
import com.go2.entity.Cart;
import com.go2.entity.Goods;
import com.go2.service.CartService;
import com.go2.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.go2.common.BaseController;

import java.time.LocalDateTime;
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
@RequestMapping("/cart")
@CrossOrigin
public class CartController extends BaseController {

    @Autowired
    private CartService cartService;

    @Autowired
    private GoodsService goodsService;

    @GetMapping("/listCart/{accountId}/{businessId}")
    public Result listCart(@PathVariable String accountId, @PathVariable Long businessId){
        //根据商家id查询所有good编号
        QueryWrapper<Cart> qw = new QueryWrapper<>();
        qw.eq("account_id", accountId);
        qw.eq("business_id", businessId);

        List<Cart> list = cartService.list(qw);

        list.stream().forEach(cart -> {
            Goods goods = goodsService.getById(cart.getGoodsId());
            cart.setGoods(goods);
        });
        return Result.success(list);
    }

    @GetMapping("/listCartByAccountId/{accountId}")
    public Result listCartByAccountId(@PathVariable String accountId){
        QueryWrapper<Cart> qw = new QueryWrapper<>();
        qw.eq("account_id", accountId);

        List<Cart> list = cartService.list(qw);
        return Result.success(list);
    }

    @PostMapping("/del")
    public Result del(@RequestBody Cart  cart){

        QueryWrapper<Cart> qw = new QueryWrapper<>();
        qw.eq("goods_id",cart.getGoodsId());
        qw.eq("account_id",cart.getAccountId());

        cartService.remove(qw);

        return Result.success(null);
    }

    @PostMapping("/update")
    public Result update(@RequestBody Cart  cart){
        //update sys_cart set quantity=? where goods_id=? and account_id=?
        QueryWrapper<Cart> qw = new QueryWrapper<>();
        qw.eq("goods_id",cart.getGoodsId());
        qw.eq("account_id",cart.getAccountId());


        cart.setUpdated(LocalDateTime.now());
        cartService.update(cart,qw);

        return Result.success(null);
    }

    @PostMapping("/add")
    public Result add(@RequestBody Cart  cart){
        cart.setCreated(LocalDateTime.now());
        cart.setUpdated(LocalDateTime.now());
        cart.setStatu(1);

        cartService.save(cart);
        return Result.success(null);
    }

    @PostMapping("/delByBusiness/{accountId}/{businessId}")
    public Result delByBusiness(@PathVariable String accountId, @PathVariable Long businessId) {
        QueryWrapper<Cart> qw = new QueryWrapper<>();
        qw.eq("account_id", accountId);
        qw.eq("business_id", businessId);

        cartService.remove(qw);
        return Result.success(null);
    }




}
