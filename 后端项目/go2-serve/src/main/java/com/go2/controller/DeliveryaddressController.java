package com.go2.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.go2.common.Result;
import com.go2.entity.Deliveryaddress;
import com.go2.service.DeliveryaddressService;
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
@RequestMapping("/deliveryaddress")
@CrossOrigin
public class DeliveryaddressController extends BaseController {

    @Autowired
    private DeliveryaddressService deliveryaddressService;

    @GetMapping("/listByAccountId/{accountId}")
    public Result listByAccountId(@PathVariable String accountId){
        QueryWrapper<Deliveryaddress> qw = new QueryWrapper<>();
        qw.eq("account_id", accountId);

        List<Deliveryaddress> list = deliveryaddressService.list(qw);
        return Result.success(list);
    }

    @PostMapping("/save")
    public Result save(@RequestBody Deliveryaddress deliveryaddress){
        deliveryaddress.setCreated(LocalDateTime.now());
        deliveryaddress.setUpdated(LocalDateTime.now());
        deliveryaddress.setStatu(1);

        deliveryaddressService.save(deliveryaddress);
        return Result.success(null);
    }

    @PostMapping("/update")
    public Result update(@RequestBody Deliveryaddress deliveryaddress){

        QueryWrapper<Deliveryaddress> qw = new QueryWrapper<>();
        qw.eq("da_id",deliveryaddress.getDaId());
        qw.eq("account_id",deliveryaddress.getAccountId());

        deliveryaddress.setUpdated(LocalDateTime.now());
        deliveryaddressService.update(deliveryaddress,qw);
        return Result.success(null);
    }


    @PostMapping("/del/{daId}")
    public Result del(@PathVariable Integer daId){
        if(deliveryaddressService.removeById(daId)){
            return Result.success(null);
        }else{
            return Result.fail("删除失败");
        }

    }

}
