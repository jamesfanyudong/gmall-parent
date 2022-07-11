package com.atguigu.gmall.order.service;

import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.model.vo.order.OrderConfirmVo;
import com.atguigu.gmall.model.vo.order.OrderSubmitVo;

/**
 * @author fanyudong
 * @date 2022/7/8 19:05
 */
public interface OrderBizService {
    OrderConfirmVo getOrderConfirm();
     String generateTradeToken();
     boolean checkTradeToken(String token);
     OrderInfo saveOrder(String tradeNo, OrderSubmitVo order);
    Long submitOrder(String tradeNo, OrderSubmitVo order);


    void closeOrder(Long orderId, Long userId);
}
