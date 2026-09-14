package com.go2.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.go2.common.Result;
import com.go2.entity.Business;
import com.go2.entity.Favorite;
import com.go2.service.BusinessService;
import com.go2.service.FavoriteService;
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
@RequestMapping("/favorite")
@CrossOrigin
public class FavoriteController extends BaseController {
    @Autowired
    private FavoriteService favoriteService;
    @Autowired
    private BusinessService businessService;

    /**
     * 添加收藏
     * @param favorite 收藏信息
     * @return 操作结果
     */
    @PostMapping("/add")
    public Result add(@RequestBody Favorite favorite){
        favorite.setCreated(LocalDateTime.now());
        favorite.setUpdated(LocalDateTime.now());
        favorite.setStatu(1);

        // 检查是否已收藏
        QueryWrapper<Favorite> qwCheck = new QueryWrapper<>();
        qwCheck.eq("account_id", favorite.getAccountId());
        qwCheck.eq("business_id", favorite.getBusinessId());
        if(favoriteService.count(qwCheck) > 0){
            return Result.success("已收藏");
        }

        favoriteService.save(favorite);
        return Result.success("收藏成功");
    }

    /**
     * 删除收藏
     * @param favorite 收藏信息
     * @return 操作结果
     */
    @PostMapping("/del")
    public Result del(@RequestBody Favorite favorite){
        QueryWrapper<Favorite> qw = new QueryWrapper<>();
        qw.eq("account_id", favorite.getAccountId());
        qw.eq("business_id", favorite.getBusinessId());

        favoriteService.remove(qw);
        return Result.success("取消收藏成功");
    }

    /**
     * 根据用户ID查询收藏列表
     * @param accountId 用户ID
     * @return 收藏列表
     */
    @GetMapping("/listFavoriteByAccountId/{accountId}")
    public Result listFavoriteByAccountId(@PathVariable String accountId){
        QueryWrapper<Favorite> qw = new QueryWrapper<>();
        qw.eq("account_id", accountId);

        List<Favorite> favoriteList = favoriteService.list(qw);

        // 关联查询商家信息
        favoriteList.stream().forEach(favorite -> {
            Business business = businessService.getById(favorite.getBusinessId());
            favorite.setBusiness(business);
        });

        return Result.success(favoriteList);
    }

    /**
     * 检查是否已收藏
     * @param accountId 用户ID
     * @param businessId 商家ID
     * @return 收藏状态
     */
    @GetMapping("/checkFavorite/{accountId}/{businessId}")
    public Result checkFavorite(@PathVariable String accountId, @PathVariable Long businessId){
        QueryWrapper<Favorite> qw = new QueryWrapper<>();
        qw.eq("account_id", accountId);
        qw.eq("business_id", businessId);

        boolean isFavorite = favoriteService.count(qw) > 0;
        return Result.success(isFavorite);
    }


}
