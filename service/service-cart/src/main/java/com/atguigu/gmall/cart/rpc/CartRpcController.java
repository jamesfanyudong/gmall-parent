package com.atguigu.gmall.cart.rpc;

import com.atguigu.gmall.cart.service.CartInfoService;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.cart.CartInfo;
import com.atguigu.gmall.model.vo.cart.AddSuccessVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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


    @GetMapping("/delete/checked")
    Result deleteChecked(){
        cartInfoService.deleteChecked();
        return Result.ok();

    }

    @GetMapping("/checked/item")
    Result<List<CartInfo>> getCheckedCartItem(){
        String cartKey = cartInfoService.determinCartKey();

      List<CartInfo> item =   cartInfoService.getAllCheckedItem(cartKey);
      return Result.ok(item);



    }



}
