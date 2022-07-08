package com.atguigu.gmall.front.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.order.OrderFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

/**
 * @author fanyudong
 * @date 2022/7/8 18:24
 */

@Controller
public class OrderController {
    @Autowired
    OrderFeignClient orderFeignClient;


    @GetMapping("/trade.html")
    public String orderConfirmPage(Model model){

        Result<Map<String, Object>> result = orderFeignClient.getOrderConfirm();

        model.addAllAttributes(result.getData());


        return "order/trade";
    }
}
