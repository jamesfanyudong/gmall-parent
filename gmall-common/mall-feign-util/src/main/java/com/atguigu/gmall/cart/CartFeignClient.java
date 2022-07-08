package com.atguigu.gmall.cart;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.cart.CartInfo;
import com.atguigu.gmall.model.vo.cart.AddSuccessVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author fanyudong
 * @date 2022/7/6 20:26
 */
@RequestMapping("/rpc/inner/cart")
@FeignClient("service-cart")
public interface CartFeignClient {

    /**
     * 添加商品到购物车
     * @param skuId 商品skuid
     * @param num 商品数量
     * @return
     */
    @GetMapping("/add/{skuId}")
     Result<AddSuccessVo> addSkuToCart(@PathVariable("skuId") Long skuId,
                                             @RequestParam("num") Integer num);


    /**
     * 删除选中商品
     * @return
     */
    @GetMapping("/delete/checked")
    Result deleteChecked();

    /**
     * 获取选中商品
     * @return
     */
    @GetMapping("/checked/item")
    Result<List<CartInfo>> getCheckedCartItem();
}
