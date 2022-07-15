package com.atguigu.gmall.pay.service.impl;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.atguigu.gmall.common.util.Jsons;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.order.OrderFeignClient;
import com.atguigu.gmall.pay.config.pay.AlipayProperties;
import com.atguigu.gmall.pay.service.PayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author fanyudong
 * @date 2022/7/12 19:05
 */

@Service
public class PayServiceImpl implements PayService {

    @Autowired
    AlipayClient alipayClient;
    @Autowired
    AlipayProperties alipayProperties;

    @Autowired
    OrderFeignClient orderFeignClient;
    @Override
    public String generatePayPage(Long orderId) throws AlipayApiException {
        //1、拿到ali支付客户端
        //2、创建一个支付请求
        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        request.setNotifyUrl(alipayProperties.getNotifyUrl());
        request.setReturnUrl(alipayProperties.getReturnUrl());
        OrderInfo orderInfo = orderFeignClient.getOrderInfoByIdAndUserId(orderId).getData();
        //3、准备请求体数据;
        Map<String,String> body = new HashMap<>();
        //订单流水
        body.put("out_trade_no",orderInfo.getOutTradeNo());
        //订单价格
        body.put("total_amount",orderInfo.getTotalAmount().toString());
        //订单名称
        body.put("subject",orderInfo.getTradeBody().split("<br/>")[0]);
        body.put("product_code","FAST_INSTANT_TRADE_PAY");
        request.setBizContent(Jsons.toStr(body));
        //5、执行请求
        AlipayTradePagePayResponse response = alipayClient.pageExecute(request);


        if (response.isSuccess()) {
            return response.getBody();
    }
        return null;

    }

    @Override
    public boolean checkSign(Map<String, String> params) throws AlipayApiException {
        boolean checkV1 = AlipaySignature.rsaCheckV1(
                params,
                alipayProperties.getAlipayPublicKey(),
                alipayProperties.getCharset(),
                alipayProperties.getSignType()
        );
        return checkV1;
    }
}
