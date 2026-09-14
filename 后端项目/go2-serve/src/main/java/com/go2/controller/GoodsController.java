package com.go2.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.go2.common.Const;
import com.go2.common.Result;
import com.go2.entity.Goods;
import com.go2.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.go2.common.BaseController;

import java.util.ArrayList;
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
@RequestMapping("/goods")
@CrossOrigin
public class GoodsController extends BaseController {

    @Autowired
    private GoodsService goodsService;



    //通过商家id获取商家商品列表
    @GetMapping("/listByBusinessId/{businessId}")
    public Result listByBusinessId(@PathVariable Long businessId) {

        //根据商家id查询所有good编号
        QueryWrapper<Goods> qw = new QueryWrapper<>();
        qw.eq("business_id", businessId);

        List<Goods> gList = goodsService.list(qw);

        return Result.success(gList);
    }
}
