package com.go2.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.go2.common.Result;
import com.go2.entity.Account;
import com.go2.entity.Business;
import com.go2.entity.Comment;
import com.go2.service.AccountService;
import com.go2.service.BusinessService;
import com.go2.service.CommentService;
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
@RequestMapping("/comment")
@CrossOrigin
public class CommentController extends BaseController {
    @Autowired
    private CommentService commentService;
    @Autowired
    private BusinessService businessService;
    @Autowired
    private AccountService accountService;

    @GetMapping("/listByAccountId/{accountId}")
    public Result listCartByAccountId(@PathVariable String accountId){
        QueryWrapper<Comment> qw = new QueryWrapper<>();
        qw.eq("account_id",accountId);

        List<Comment> list = commentService.list(qw);
        list.stream().forEach(comment->{
            Business business = businessService.getById(comment.getBusinessId());
            comment.setBusiness(business);
        });
        return Result.success(list);
    }

    @GetMapping("/listByBusinessId/{businessId}")
    public Result listCartByBusinessId(@PathVariable Long businessId){
        QueryWrapper<Comment> qw = new QueryWrapper<>();
        qw.eq("business_id",businessId);
        List<Comment> list = commentService.list(qw);
        list.stream().forEach(comment->{
            Account account = accountService.getById(comment.getAccountId());
            comment.setAccount(account);
            Business business = businessService.getById(comment.getBusinessId());
            comment.setBusiness(business);
        });
        return Result.success(list);
    }
    @PostMapping("/add")
    public Result add(@RequestBody Comment comment){
        comment.setCreated(LocalDateTime.now());
        comment.setUpdated(LocalDateTime.now());
        comment.setStatu(1);
        commentService.save(comment);
        return Result.success(null);
    }
}
