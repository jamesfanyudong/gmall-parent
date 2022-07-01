package com.atguigu.gmall.product;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.vo.CategoryVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;


/**
 *   @author fanyudong
 * @FeignClient 代表这会是一次远程调用
 */

@RequestMapping("/rpc/inner/product")
@FeignClient("service-product") // 声明要调用的微服务
public interface CategoryFeignClient {

    @GetMapping("/categorys/all")
    public Result<List<CategoryVo>> getCategorys();
}
