package com.atguigu.gmall.front.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.enums.OrderStatus;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.order.OrderFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author fanyudong
 * @date 2022/7/10 11:53
 */
@Controller
public class PayController {

    @Autowired
    OrderFeignClient orderFeignClient;

    @GetMapping("/pay.html")
    public String payPage(@RequestParam("orderId") Long orderId, Model model){
        Result<OrderInfo> result =
                orderFeignClient.getOrderInfoByIdAndUserId(orderId);
        OrderInfo info = result.getData();
        if (OrderStatus.UNPAID.name().equals(info.getOrderStatus())){
            model.addAttribute("orderInfo",result.getData());
            return "payment/pay";
        }

        //订单列表页
        return "redirect:/myOrder.html";




    }
}
