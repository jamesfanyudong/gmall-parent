package com.atguigu.gmall.order;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.order.OrderInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

    /**
     * 获取某个用户的指定订单信息
     * @param id
     * @return
     */
    @GetMapping("/info/{id}")
     Result<OrderInfo> getOrderInfoByIdAndUserId(@PathVariable("id") Long id);
}
