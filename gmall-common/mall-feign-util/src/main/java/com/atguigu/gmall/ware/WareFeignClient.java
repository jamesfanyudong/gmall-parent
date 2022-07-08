package com.atguigu.gmall.ware;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author fanyudong
 * @date 2022/7/8 21:01
 */
@FeignClient(url = "http://localhost:9001",value = "ware-manage")
public interface WareFeignClient {


    @GetMapping("/hasStock")
     String hasStock(@RequestParam("skuId") Long skuId,
                           @RequestParam("num") Integer num);

}
