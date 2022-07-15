package com.atguigu.gmall.order.service.impl;

import com.atguigu.gmall.common.util.Jsons;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.model.payment.PaymentInfo;
import com.atguigu.gmall.order.mapper.PaymentInfoMapper;
import com.atguigu.gmall.order.service.PaymentInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

/**
 *
 * @author fanyudong
 */
@Service
public class PaymentInfoServiceImpl extends ServiceImpl<PaymentInfoMapper, PaymentInfo>
    implements PaymentInfoService{

    @Override
    public void savePayment(Map<String, String> map, OrderInfo orderInfo) {
        PaymentInfo paymentInfo = new PaymentInfo();

        paymentInfo.setOutTradeNo(map.get("out_trade_no"));
        paymentInfo.setOrderId(orderInfo.getId());
        paymentInfo.setUserId(orderInfo.getUserId());
        paymentInfo.setPaymentType("alipay");
        paymentInfo.setTradeNo(map.get("trade_no"));
        paymentInfo.setTotalAmount(new BigDecimal(map.get("invoice_amount")));
        paymentInfo.setSubject(map.get("subject"));
        paymentInfo.setPaymentStatus(map.get("trade_status"));
        paymentInfo.setCreateTime(new Date());
        paymentInfo.setCallbackTime(new Date());

        paymentInfo.setCallbackContent(Jsons.toStr(map));

        // 保存支付信息
        save(paymentInfo);
    }
}




