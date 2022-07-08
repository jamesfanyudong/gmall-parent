package com.atguigu.gmall.order.service.impl;

import com.atguigu.gmall.cart.CartFeignClient;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.cart.CartInfo;
import com.atguigu.gmall.model.vo.order.CartOrderDetailVo;
import com.atguigu.gmall.model.vo.order.OrderConfirmVo;
import com.atguigu.gmall.order.service.OrderBizSercice;
import com.atguigu.gmall.product.SkuFeignClient;
import com.atguigu.gmall.user.UserFeignClient;
import com.atguigu.gmall.ware.WareFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author fanyudong
 * @date 2022/7/8 19:06
 */
@Service
public class OrderBizSerciceImpl implements OrderBizSercice {

    @Autowired
    UserFeignClient userFeignClient;

    @Autowired
    CartFeignClient cartFeignClient;

    @Autowired
    SkuFeignClient skuFeignClient;

    @Autowired
    WareFeignClient wareFeignClient;


    @Override
    public OrderConfirmVo getOrderConfirm() {
        OrderConfirmVo vo = new OrderConfirmVo();
        //1、获取用户收货地址列表
        vo.setUserAddressList(userFeignClient.getUserAddress().getData());


        //2、获取购物车中选中的需要结算的商品
        Result<List<CartInfo>> cartItem = cartFeignClient.getCheckedCartItem();
        List<CartOrderDetailVo> detailVos = cartItem.getData().stream()
                .map(cartInfo -> {
                    CartOrderDetailVo detailVo = new CartOrderDetailVo();
                    // 实时价格
                    Result<BigDecimal> price = skuFeignClient.get1010Price(cartInfo.getSkuId());
                    detailVo.setOrderPrice(price.getData());
                    detailVo.setImgUrl(cartInfo.getImgUrl());
                    detailVo.setSkuName(cartInfo.getSkuName());
                    detailVo.setSkuNum(cartInfo.getSkuNum());
                    String stock = wareFeignClient.hasStock(cartInfo.getSkuId(), cartInfo.getSkuNum());
                    detailVo.setStock(stock);
                    return detailVo;

                }).collect(Collectors.toList());

        vo.setDetailArrayList(detailVos);

        //3、总数量
        Integer totalNum = cartItem.getData().stream()
                .map(cartInfo -> cartInfo.getSkuNum())
                .reduce((o1, o2) -> o1 + o2)
                .get();

        vo.setTotalNum(totalNum);
        //4、总金额  每个商品实时价格*数量 的加和
        BigDecimal totalAmount = detailVos.stream()
                .map(cart -> cart.getOrderPrice().multiply(new BigDecimal(cart.getSkuNum())))
                .reduce((o1, o2) -> o1.add(o2))
                .get();

        vo.setTotalAmount(totalAmount);


        //5、防重令牌
        String token = UUID.randomUUID().toString().replace("-", "");
        vo.setTradeNo(token);


        return vo;
    }
}
