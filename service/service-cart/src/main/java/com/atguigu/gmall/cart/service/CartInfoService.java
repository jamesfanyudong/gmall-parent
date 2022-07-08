package com.atguigu.gmall.cart.service;


import com.atguigu.gmall.model.cart.CartInfo;
import com.atguigu.gmall.model.vo.cart.AddSuccessVo;

import java.util.List;

/**
 *
 * @author fanyudong
 */
public interface CartInfoService  {


    AddSuccessVo addToCart(Long skuId, Integer num);

    List<CartInfo> getCartAllItem();
    List<CartInfo> getCartAllItem(String cartKey);


    void updateCartItemStatus(Long skuId, Integer status);

    void deleteCartItem(Long skuId);

    void deleteChecked();
    String determinCartKey();

    List<CartInfo> getAllCheckedItem(String cartKey);
}
