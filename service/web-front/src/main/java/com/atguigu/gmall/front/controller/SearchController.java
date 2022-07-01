package com.atguigu.gmall.front.controller;


import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.vo.search.SearchParm;
import com.atguigu.gmall.search.SearchFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

/**
 * @author fanyudong
 */
@Controller
public class SearchController {

    @Autowired
    SearchFeignClient searchFeignClient;


    @GetMapping("/list.html")
    public String listPage(SearchParm searchParm ,Model model){
        Result<Map<String, Object>> result = searchFeignClient.search(searchParm);
        Map<String, Object> data = result.getData();



        return "list/index";

    }




}
