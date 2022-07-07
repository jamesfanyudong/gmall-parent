package com.atguigu.gmall.cart.rpc;

import com.atguigu.gmall.cart.service.CartInfoService;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.vo.cart.AddSuccessVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author fanyudong
 * @date 2022/7/6 18:21
 */
@RestController
@RequestMapping("/rpc/inner/cart")
public class CartRpcController {

    @Autowired
    CartInfoService cartInfoService;

    /**
     * skuId 商品添加到购物车
     * @return
     */
    @GetMapping("/add/{skuId}")
    public Result<AddSuccessVo> addSkuToCart(@PathVariable("skuId") Long skuId,
                                             @RequestParam("num") Integer num){

        // 获取当前用户信息，隐士透传
       AddSuccessVo vo =  cartInfoService.addToCart(skuId,num);
       return Result.ok(vo);


    }
}
