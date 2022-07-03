package com.atguigu.gmall.front.controller;


import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.vo.search.SearchParm;
import com.atguigu.gmall.search.SearchFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author fanyudong
 */
@Controller
public class SearchController {

    @Autowired
    SearchFeignClient searchFeignClient;


    @GetMapping("/list.html")
    public String listPage(SearchParm searchParm ,Model model,HttpServletRequest request){
        Result<Map<String, Object>> result = searchFeignClient.search(searchParm);
        Map<String, Object> data = result.getData();
        // 获取请求参数
        String queryString = request.getQueryString();
        model.addAttribute("urlParam","/list.html"+queryString);


        model.addAllAttributes(data);



        return "list/index";

    }




}
