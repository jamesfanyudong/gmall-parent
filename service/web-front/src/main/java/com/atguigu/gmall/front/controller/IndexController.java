package com.atguigu.gmall.front.controller;


import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.vo.CategoryVo;
import com.atguigu.gmall.product.CategoryFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * 首页控制器
 * @author fanyudong
 */
@Controller
public class IndexController {
    @Autowired
    CategoryFeignClient categoryFeignClient;


    @GetMapping("/")
    public String indexPage(Model model){

        //远程调用商品服务=查询出三级分类数据。

        Result<List<CategoryVo>> categorys = categoryFeignClient.getCategorys();
        List<CategoryVo> categoryVos = categorys.getData();
        model.addAttribute("list",categoryVos);
        return "index/index";
    }
}
