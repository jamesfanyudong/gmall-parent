package com.atguigu.gmall.item;


import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.vo.SkuDetailVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/rpc/inner/item")
@FeignClient("service-item")
public interface ItemFeginClient {

    /**
     * 查询商品详情
     * @param skuId
     * @return
     */

    @GetMapping("/sku/{skuId}")
    public Result<SkuDetailVo> getSkuDetail(@PathVariable("skuId")Long skuId);


}
