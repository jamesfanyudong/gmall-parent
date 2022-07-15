package com.atguigu.gmall.order.service;

import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.model.payment.PaymentInfo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 *
 */
public interface PaymentInfoService extends IService<PaymentInfo> {

    void savePayment(Map<String, String> map, OrderInfo orderInfo);
}
