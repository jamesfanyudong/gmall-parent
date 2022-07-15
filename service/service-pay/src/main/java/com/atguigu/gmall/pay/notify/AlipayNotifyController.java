package com.atguigu.gmall.pay.notify;

import com.alipay.api.AlipayApiException;
import com.atguigu.gmall.common.constant.MQConst;
import com.atguigu.gmall.common.util.Jsons;
import com.atguigu.gmall.pay.service.PayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author fanyudong
 * @date 2022/7/12 20:40
 */
@Slf4j
@RestController
@RequestMapping("/rpcapi/payment")
public class AlipayNotifyController {

    @Autowired
    RabbitTemplate rabbitTemplate;
    @Autowired
    PayService payService;
    @RequestMapping("/notify/success")
    public String paySuccessNotify(@RequestParam Map<String, String> params) throws AlipayApiException {
        boolean checkSign = payService.checkSign(params);
        if (checkSign){
            rabbitTemplate.convertAndSend(
                    MQConst.EXCHANGE_ORDER_EVENT,
                    MQConst.ROUTE_KEY_ORDER_PAYED, Jsons.toStr(params));
            return "success";
        }
        return "error";
    }


}
