package com.go2.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.go2.common.Const;
import com.go2.common.Result;
import com.go2.entity.Business;
import com.go2.entity.BusinessCategory;
import com.go2.service.BusinessCategoryService;
import com.go2.service.BusinessService;
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
@RequestMapping("/business")
@CrossOrigin
public class BusinessController extends BaseController {

    @Autowired
    private BusinessService businessService;

    @Autowired
    private BusinessCategoryService bcService;


    @GetMapping("/list")
    public Result listBusinessCategory(){
        List<Business> list = businessService.list();
        if(list==null){
            return Result.fail(Const.GET_DATA_FAIL,"商家分类数据获取失败",null);
        }else {
            return Result.success(list);
        }
    }

    // 根据分类id查询商家
    @GetMapping("/listByCategoryId/{categoryId}")
    public Result listByCategoryId(@PathVariable Integer categoryId) {

        List<Business> businessList = new ArrayList<>();

        //条件构造器
        QueryWrapper<BusinessCategory> qw = new QueryWrapper<>();
        qw.eq("category_id", categoryId);

        //根据分类id查询所有商家编号
        List<BusinessCategory> bcList = bcService.list(qw);

        bcList.stream().forEach(bc -> {
            //根据商家编号查询商家信息
            Business business = businessService.getById(bc.getBusinessId());
            businessList.add(business);
        });
        return Result.success(businessList);
    }


    @GetMapping("/info/{businessId}")
    public Result info(@PathVariable Long businessId){
        Business business = businessService.getById(businessId);
        if(business==null){
            return Result.fail(Const.GET_DATA_FAIL,"商家数据获取失败",null);
        }else {
            return Result.success(business);
        }
    }



}
