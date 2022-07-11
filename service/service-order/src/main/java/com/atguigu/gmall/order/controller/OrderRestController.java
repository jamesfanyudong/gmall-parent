package com.atguigu.gmall.order.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.vo.order.OrderSubmitVo;
import com.atguigu.gmall.order.service.OrderBizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author fanyudong
 * @date 2022/7/10 11:59
 */
@RestController
@RequestMapping("/api/order/auth")
public class OrderRestController {

    @Autowired
    OrderBizService orderBizSercice;

    @PostMapping("/submitOrder")
    public Result submitOrder(@RequestParam("tradeNo") String tradeNo,
                              @RequestBody OrderSubmitVo order){
        Long id = orderBizSercice.submitOrder(tradeNo, order);
        return Result.ok(id+"");

    }
}
