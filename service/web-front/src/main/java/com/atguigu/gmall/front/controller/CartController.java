package com.atguigu.gmall.front.controller;

import com.atguigu.gmall.cart.CartFeignClient;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.vo.cart.AddSuccessVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author fanyudong
 * @date 2022/7/6 18:16
 */
@Controller
public class CartController {

    @Autowired
    CartFeignClient cartFeignClient;
    /**
     * 添加商品到购物车
     * ?skuId=49&skuNum=1&sourceType=query
     * @return
     */
    @GetMapping("/addCart.html")
    public String  addCart(@RequestParam("skuId") Long skuId,
                           @RequestParam("skuNum") Integer skuNum,
                           Model model){

        Result<AddSuccessVo> result = cartFeignClient.addSkuToCart(skuId, skuNum);

        model.addAttribute("skuInfo",result.getData());
        model.addAttribute("skuNum",skuNum);
        return "cart/addCart";

    }

    @GetMapping("/cart.html")
    public String cartList(){

        return "cart/index";
    }

    @GetMapping("/cart/deleteChecked")
    public String deleteChecked(){
        cartFeignClient.deleteChecked();
        return "cart/index";
    }


}
