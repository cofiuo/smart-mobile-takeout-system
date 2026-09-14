package com.go2.controller;


import com.go2.common.Const;
import com.go2.common.Result;
import com.go2.entity.Category;
import com.go2.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;
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
@RequestMapping("/category")
@CrossOrigin
public class CategoryController extends BaseController {

    @Autowired
    private CategoryService categoryService;

    //获得所有的category数据
    @GetMapping("/list")
    public Result list(){
        List<Category> list = categoryService.list();
        if (list==null){
            return Result.fail(Const.GET_DATA_FAIL,"商家分类数据获取失败",null);
        }else {
            return Result.success(list);
        }
    }
}
