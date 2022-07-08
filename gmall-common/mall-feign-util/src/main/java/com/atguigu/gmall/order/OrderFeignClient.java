package com.atguigu.gmall.order;

import com.atguigu.gmall.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

/**
 * @author fanyudong
 * @date 2022/7/8 19:11
 */
@RequestMapping("/rpc/inner/order")
@FeignClient("service-order")
public interface OrderFeignClient {

    /**
     * 获取订单确认数据
     * @return
     */
    @GetMapping("/confirm/data")
    Result<Map<String,Object>> getOrderConfirm();
}
