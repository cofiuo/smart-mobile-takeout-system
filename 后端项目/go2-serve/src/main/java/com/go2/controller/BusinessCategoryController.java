package com.go2.controller;


import com.go2.common.Const;
import com.go2.common.Result;
import com.go2.entity.BusinessCategory;
import com.go2.service.BusinessCategoryService;
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
@RequestMapping("/business-category")
public class BusinessCategoryController extends BaseController {

}
