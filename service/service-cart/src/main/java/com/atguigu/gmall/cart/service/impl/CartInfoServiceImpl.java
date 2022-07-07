package com.atguigu.gmall.cart.service.impl;

import com.atguigu.gmall.cart.service.CartInfoService;
import com.atguigu.gmall.common.constant.RedisConst;
import com.atguigu.gmall.common.execption.GmallException;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import com.atguigu.gmall.common.util.AuthContextHolder;
import com.atguigu.gmall.common.util.Jsons;
import com.atguigu.gmall.model.cart.CartInfo;
import com.atguigu.gmall.model.vo.cart.AddSuccessVo;
import com.atguigu.gmall.model.vo.user.UserAuth;
import com.atguigu.gmall.product.SkuFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 *
 * @author fanyudong
 */
@Service
@Slf4j
public class CartInfoServiceImpl implements CartInfoService {
    @Autowired
    SkuFeignClient skuFeignClient;

    @Autowired
    StringRedisTemplate redisTemplate;
    /**
     * 添加商品到购物车
     * @return
     */
    @Override
    public AddSuccessVo addToCart(Long skuId, Integer num) {


        AddSuccessVo addSuccessVo = new AddSuccessVo();

        //1.决定使用哪个购物车
        String cartKey =  determinCartKey();
        //2. 添加：原来购物车有没有这个商品，有就是数量叠加，没有就是新增
        //3.尝试从购物车中获取到这个商品
        CartInfo item = getCartItem(cartKey,skuId);
        if (item == null){
            //3.1没有就新增
           CartInfo info =  getCartInfoFromRpc(skuId);
           //3.2设置数量
            info.setSkuNum(num);
            //3.3同步到redis
            saveItemToCart(info,cartKey);

            addSuccessVo.setSkuDefaultImg(info.getImgUrl());
            addSuccessVo.setSkuName(info.getSkuName());
            addSuccessVo.setId(info.getSkuId());
        }else {
            //3.2有就修改数量
            item.setSkuNum(item.getSkuNum() + num);
            //3.3 同步到redis
            saveItemToCart(item,cartKey);

            addSuccessVo.setSkuDefaultImg(item.getImgUrl());
            addSuccessVo.setSkuName(item.getSkuName());
            addSuccessVo.setId(item.getSkuId());
        }
        //设置过期时间
        setTempCartExpire();

        return addSuccessVo;
    }

    /**
     * 获取购物车列表
     * @return
     */
    @Override
    public List<CartInfo> getCartAllItem() {
        //0、是否需要合并： 只要tempId对应的购物车有东西，并且还有userId；合并操作
        UserAuth auth = AuthContextHolder.getUserAuth();
        if (auth.getUserId()!=null && !StringUtils.isEmpty(auth.getUserTempId())) {
            //有可能合并购物车
            //1、如果临时购物车有东西，就合并；只需要判断临时购物车是否存在
            Boolean hasKey = redisTemplate.hasKey(RedisConst.CART_INFO_PREFIX + auth.getUserTempId());
            if (hasKey) {
                //2、redis有临时购物车。就需要先合并，再获取购物车中所有数据
                //3、拿到临时购物车的商品
                List<CartInfo> cartInfos = getCartAllItem(RedisConst.CART_INFO_PREFIX + auth.getUserTempId());
                cartInfos.forEach(tempItem -> {
                    //4、每个临时购物车中的商品都添到用户购物车;
                    addToCart(tempItem.getSkuId(), tempItem.getSkuNum());
                });
                //5. 删除临时购物车
                redisTemplate.delete(RedisConst.CART_INFO_PREFIX + auth.getUserTempId());

            }
        }
            String cartKey = determinCartKey();
            List<CartInfo> allItem = getCartAllItem(cartKey);
            RequestAttributes oldRequest = RequestContextHolder.getRequestAttributes();
            //每一个查一下价格
            CompletableFuture.runAsync(()->{
                log.info("提交一个实时改价异步任务");
                allItem.forEach(item->{
                    //2线程，共享给另一个线程
                    RequestContextHolder.setRequestAttributes(oldRequest);
                    //一旦异步，因为在异步线程中 RequestContextHolder.getRequestAttributes() 是获取不到老请求
                    // 1、feign拦截器就拿不到老请求  2、feign拦截器啥都不做（tempId，userId）都无法继续透传下去
                    Result<BigDecimal> price = skuFeignClient.getSkuPrice(item.getSkuId());
                    //ThreadLocal的所有东西一定要有放，有删
                    RequestContextHolder.resetRequestAttributes();
                    if (!item.getSkuPrice().equals(price.getData())){
                        log.info("正在后台实时更新 【{}】 购物车，【{}】商品的价格；原【{}】，现：【{}】",
                                cartKey,item.getSkuId(),item.getSkuPrice(),price.getData());
                        //发现价格发生了变化
                        item.setSkuPrice(price.getData());
                        //同步到redis
                        saveItemToCart(item,cartKey);
                    }


                });


            });

            return allItem;
        }


    @Override
    public List<CartInfo> getCartAllItem(String cartKey) {
        HashOperations<String, String, String> ops = redisTemplate.opsForHash();
        List<CartInfo> collect = ops.values(cartKey)
                .stream()
                .map(jsonStr -> Jsons.toObj(jsonStr,CartInfo.class))
                .sorted((pre,next)->
                        (int)(next.getCreateTime().getTime() - pre.getCreateTime().getTime()))
                .collect(Collectors.toList());
        return collect;
    }

    @Override
    public void updateCartItemStatus(Long skuId, Integer status) {
        String cartKey = determinCartKey();
        CartInfo cartItem = getCartItem(cartKey, skuId);
        cartItem.setIsChecked(status);

        // 同步到redis
        saveItemToCart(cartItem,cartKey);
    }

    @Override
    public void deleteCartItem(Long skuId) {
        String cartKey = determinCartKey();
        redisTemplate.opsForHash().delete(cartKey,skuId.toString());
    }

    /**
     * 设置过期时间
     */
    private void setTempCartExpire() {
        UserAuth userAuth = AuthContextHolder.getUserAuth();
        //1.用户只是操作临时购物车
        if (!StringUtils.isEmpty(userAuth.getUserTempId()) && userAuth.getUserId() == null){
            //用户带了临时token；
            //1、如果有临时购物车就设置过期时间。
            Boolean hasKey = redisTemplate.hasKey(RedisConst.CART_INFO_PREFIX + userAuth.getUserTempId());
            if (hasKey){
                //临时购物车有一年的时间
                redisTemplate.expire(RedisConst.CART_INFO_PREFIX + userAuth.getUserTempId(),365, TimeUnit.DAYS);
            }

        }
    }

    /**
     * 同步到redis
     * @param info
     * @param cartKey
     */
    private void saveItemToCart(CartInfo info, String cartKey) {

        //1./拿到一个能操作hash的对象
        HashOperations<String, String, String> opos = redisTemplate.opsForHash();
        Long skuId = info.getSkuId();
        //2、给redis保存一个hash数据
        //3、判断购物车是否已经满了
        if (opos.size(cartKey) < RedisConst.CART_SIZE_LIMIT){
            opos.put(cartKey,skuId.toString(),Jsons.toStr(info));
        }else {
            throw new GmallException(ResultCodeEnum.OUT_OF_CART);
        }
    }

    /**
     * 远程获取cartinfo
     * @param skuId
     * @return
     */
    private CartInfo getCartInfoFromRpc(Long skuId) {
        Result<CartInfo> info = skuFeignClient.getCartInfoBySkuId(skuId);

        return info.getData();
    }

    /**
     * 3、尝试从购物车中获取到这个商品
     * @param cartKey
     * @param skuId
     * @return
     */
    private CartInfo getCartItem(String cartKey, Long skuId) {
        //1.拿到一个能操作hash的对象
        HashOperations<String, String, String> ops = redisTemplate.opsForHash();
        //2、获取cartKey购物车指定的skuId商品
        String json = ops.get(cartKey, skuId.toString());
        //3.逆转
        if (StringUtils.isEmpty(json)){
            return null;
        }
        CartInfo cartInfo = Jsons.toObj(json, CartInfo.class);
        return cartInfo;
    }

    /**
     * 决定使用哪个购物车
     * @return
     */
    private String determinCartKey() {
        //1.拿到用户信息
        UserAuth userAuth = AuthContextHolder.getUserAuth();
        String cartKey = "";


        if (userAuth.getUserId()!=null){
            //用户登陆了
            cartKey = RedisConst.CART_INFO_PREFIX + userAuth.getUserId();
        }else {
            //如果没有临时id，前端会自己造一个传给我们
            cartKey = RedisConst.CART_INFO_PREFIX + userAuth.getUserTempId();
        }

        return cartKey;

    }


}


