package com.atguigu.gmall.cart.controller;

import com.atguigu.gmall.cart.service.CartInfoService;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.cart.CartInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author fanyudong
 * @date 2022/7/7 9:57
 */
@RequestMapping("/api/cart")
@RestController
public class CartRestController {

    @Autowired
    CartInfoService cartInfoService;

    /**
     * 获取购物车列表
     * @return
     */
    @GetMapping("/cartList")
    public Result cartList(){
       List<CartInfo> items =  cartInfoService.getCartAllItem();
       //2.实时改价，异步
       return Result.ok(items);

    }
    /**
     * 增加购物车中某个商品数量
     * @param skuId
     * @param num
     * @return
     */
    @PostMapping("/addToCart/{skuId}/{num}")
    public Result addToCart(@PathVariable("skuId")Long skuId,
                            @PathVariable("num")Integer num){
        cartInfoService.addToCart(skuId,num);
        return Result.ok();
    }
    /**
     * 修改购物车中商品状态
     * @param skuId
     * @param status
     * @return
     */
    @GetMapping("/checkCart/{skuId}/{status}")
    public Result checkCart(@PathVariable("skuId")Long skuId,
                            @PathVariable("status")Integer status){
        cartInfoService.updateCartItemStatus(skuId,status);
        return Result.ok();
    }
    /**
     * 删除购物车中指定商品
     * @param skuId
     * @return
     */
    @DeleteMapping("/deleteCart/{skuId}")
    public Result deleteCartItem(@PathVariable("skuId")Long skuId){
        cartInfoService.deleteCartItem(skuId);

        return Result.ok();



    }










}
