package com.atguigu.gmall.order.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.vo.order.OrderConfirmVo;
import com.atguigu.gmall.order.service.OrderBizSercice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author fanyudong
 * @date 2022/7/8 19:04
 */

@RequestMapping("/rpc/inner/order")
@RestController
public class OrderRpcController {

    @Autowired
    OrderBizSercice orderBizSercice;

    /**
     * 获取订单确认数据
     * @return
     */
    @GetMapping("/confirm/data")
    public Result<OrderConfirmVo> getOrderConfirm(){

        OrderConfirmVo vo =  orderBizSercice.getOrderConfirm();
        return Result.ok(vo);
    }
}
