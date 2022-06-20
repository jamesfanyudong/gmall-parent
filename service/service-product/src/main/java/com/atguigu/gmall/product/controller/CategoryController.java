package com.atguigu.gmall.product.controller;


import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BaseCategory1;
import com.atguigu.gmall.product.service.BaseCategory1Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin/product")
public class CategoryController {


    @Autowired
    BaseCategory1Service baseCategory1Service;
    /**
     *
     * 查询一级分类
     * @return
     */
    @GetMapping
    public Result getCategory1(){
        List<BaseCategory1> category1s = baseCategory1Service.list();
        return Result.ok(category1s);

    }



}
