package com.atguigu.gmall.order.service.impl;

import com.atguigu.gmall.cart.CartFeignClient;
import com.atguigu.gmall.common.constant.MQConst;
import com.atguigu.gmall.common.constant.RedisConst;
import com.atguigu.gmall.common.execption.GmallException;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import com.atguigu.gmall.common.util.AuthContextHolder;
import com.atguigu.gmall.common.util.Jsons;
import com.atguigu.gmall.model.cart.CartInfo;
import com.atguigu.gmall.model.enums.OrderStatus;
import com.atguigu.gmall.model.enums.ProcessStatus;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.model.order.OrderStatusLog;
import com.atguigu.gmall.model.to.mq.OrderCreateMsg;
import com.atguigu.gmall.model.vo.order.CartOrderDetailVo;
import com.atguigu.gmall.model.vo.order.OrderConfirmVo;
import com.atguigu.gmall.model.vo.order.OrderSubmitVo;
import com.atguigu.gmall.order.service.OrderBizService;
import com.atguigu.gmall.order.service.OrderInfoService;
import com.atguigu.gmall.order.service.OrderStatusLogService;
import com.atguigu.gmall.product.SkuFeignClient;
import com.atguigu.gmall.user.UserFeignClient;
import com.atguigu.gmall.ware.WareFeignClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author fanyudong
 * @date 2022/7/8 19:06
 */
@Service
public class OrderBizServiceImpl implements OrderBizService {

    @Autowired
    UserFeignClient userFeignClient;

    @Autowired
    CartFeignClient cartFeignClient;

    @Autowired
    SkuFeignClient skuFeignClient;

    @Autowired
    WareFeignClient wareFeignClient;
    @Autowired
    ThreadPoolExecutor executor;
    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    OrderBizService orderBizSercice;
    @Autowired
    OrderInfoService orderInfoService;

    @Autowired
    OrderStatusLogService orderStatusLogService;

    @Autowired
    RabbitTemplate rabbitTemplate;


    @Override
    public OrderConfirmVo getOrderConfirm() {
        OrderConfirmVo vo = new OrderConfirmVo();

        // 用户id透传
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();

        // 1.异步编排--获取用户收货地址列表
        CompletableFuture<Void> userAddressFuture = CompletableFuture.runAsync(() -> {

            RequestContextHolder.setRequestAttributes(attributes);
            vo.setUserAddressList(userFeignClient.getUserAddress().getData());
            RequestContextHolder.resetRequestAttributes();
        }, executor);

        // 2.获取购物车中选中的商品item

        CompletableFuture<Result<List<CartInfo>>> checkedItemFuture = CompletableFuture.supplyAsync(() -> {
            RequestContextHolder.setRequestAttributes(attributes);
            Result<List<CartInfo>> cartItem = cartFeignClient.getCheckedCartItem();
            RequestContextHolder.resetRequestAttributes();
            return cartItem;

        }, executor);
        // 3.结算被选中商品的数量和金额
        CompletableFuture<List<CartOrderDetailVo>> orderDetailFuture = checkedItemFuture.thenApplyAsync(cartItem -> {
            List<CartOrderDetailVo> detailVos = cartItem.getData().stream()
                    .parallel()
                    .map(cartInfo -> {
                        CartOrderDetailVo detailVo = new CartOrderDetailVo();
                        // 实时价格
                        Result<BigDecimal> price = skuFeignClient.get1010Price(cartInfo.getSkuId());
                        detailVo.setOrderPrice(price.getData());
                        detailVo.setImgUrl(cartInfo.getImgUrl());
                        detailVo.setSkuName(cartInfo.getSkuName());
                        detailVo.setSkuNum(cartInfo.getSkuNum());
                        detailVo.setSkuId(cartInfo.getSkuId());
                        // 获取库存
                        String stock = wareFeignClient.hasStock(cartInfo.getSkuId(), cartInfo.getSkuNum());
                        detailVo.setStock(stock);
                        return detailVo;

                    })
                    .collect(Collectors.toList());
            vo.setDetailArrayList(detailVos);
            return detailVos;

        },executor);







        //4、异步编排总数量
        CompletableFuture<Void> totalNumFuture = checkedItemFuture.thenAcceptAsync(item -> {
            Integer totalNum = item.getData().stream()
                    .map(info -> info.getSkuNum())
                    .reduce((o1, o2) -> o1 + o2)
                    .get();
            vo.setTotalNum(totalNum);
        },executor);


        //4、总金额  每个商品实时价格*数量 的加和
        CompletableFuture<Void> totalAmountFuture = orderDetailFuture.thenAcceptAsync(detailVos -> {
            BigDecimal totalAmount = detailVos.stream()
                    .map(cart -> cart.getOrderPrice().multiply(new BigDecimal(cart.getSkuNum())))
                    .reduce((o1, o2) -> o1.add(o2))
                    .get();
            vo.setTotalAmount(totalAmount);

        },executor);



        //5、防重令牌
        vo.setTradeNo(generateTradeToken());
        CompletableFuture.allOf(userAddressFuture,orderDetailFuture,totalNumFuture,totalAmountFuture)
                .join();


        return vo;
    }

    /**
     * 生成交易防重令牌
     * @return
     */
    @Override
    public String generateTradeToken() {
        //1、生成令牌
        String token = UUID.randomUUID().toString().replace("-", "");
        //2、给redis一份;  trade:token:uuid； 10分钟过期，页面10分钟不动就必须重新刷新
        redisTemplate.opsForValue().set(RedisConst.TRADE_TOKEN_PREFIX+token,RedisConst.A_KEN_VALUE,10, TimeUnit.MINUTES);
        return token;
    }

    /**
     * 校验防重令牌
     * @param token ： 令牌
     * @return true false
     */
    @Override
    public boolean checkTradeToken(String token) {
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        Long execute = redisTemplate.execute(new DefaultRedisScript<>(script, Long.class),
                Arrays.asList(RedisConst.TRADE_TOKEN_PREFIX + token),
                RedisConst.A_KEN_VALUE);
        //true
        return execute == 1L;
    }

    /**
     * 保存订单
     * @param tradeNo 防重令牌
     * @param order 订单vo
     * @return OrderInfo
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public OrderInfo saveOrder(String tradeNo, OrderSubmitVo order) {
        // 订单信息保存
        OrderInfo orderInfo = prepareOrderInfo(tradeNo,order);
        orderInfoService.save(orderInfo);

        // 订单明细保存
        orderInfoService.saveDetail(orderInfo,order);
        // 订单日志记录
        OrderStatusLog log = prepareOrderStatusLog(orderInfo);
        orderStatusLogService.save(log);




        return orderInfo;
    }

    /**
     * 准备订单日志信息
     * @param orderInfo 订单信息
     * @return OrderStatusLog
     */
    private OrderStatusLog prepareOrderStatusLog(OrderInfo orderInfo) {
        Long userId = AuthContextHolder.getUserAuth().getUserId();
        OrderStatusLog log = new OrderStatusLog();
        log.setOrderId(orderInfo.getId());
        log.setUserId(userId);
        log.setOrderStatus(orderInfo.getOrderStatus());
        log.setOperateTime(orderInfo.getCreateTime());
        return log;
    }

    /**
     * 准备订单信息
     * @param tradeNo orderInfo
     * @param vo 订单vo
     * @return OrderInfo
     */
    private OrderInfo prepareOrderInfo(String tradeNo, OrderSubmitVo vo) {
        OrderInfo orderInfo = new OrderInfo();
        // 订单详情
        BigDecimal totalAmount = vo.getOrderDetailList().stream()
                .map(item -> item.getOrderPrice().multiply(new BigDecimal(item.getSkuNum())))
                .reduce((o1, o2) -> o1.add(o2))
                .get();

        Long userId = AuthContextHolder.getUserAuth().getUserId();
        orderInfo.setConsignee(vo.getConsignee());
        orderInfo.setUserId(userId);
        orderInfo.setConsigneeTel(vo.getConsigneeTel());
        // 订单结账总价
        orderInfo.setTotalAmount(totalAmount);
        orderInfo.setOrderStatus(OrderStatus.UNPAID.name());
        // 线上支付
        orderInfo.setPaymentWay("ONLINE");
        // 收获人地址
        orderInfo.setDeliveryAddress(vo.getDeliveryAddress());
        // 订单备注
        orderInfo.setOrderComment(vo.getOrderComment());
        // 对外交易号
        orderInfo.setOutTradeNo("ATGUIGU_"+tradeNo+"_"+userId);
        // 交易实体
        String allName = vo.getOrderDetailList().stream()
                .map(item -> item.getSkuName())
                .reduce((o1, o2) -> o1 + "<br/>" + o2)
                .get();
        orderInfo.setTradeBody(allName);


        // 创建时间
        orderInfo.setCreateTime(new Date());
        // 过期时间
        long expireTime =  System.currentTimeMillis() + 1000 * 60 * 30L;
        orderInfo.setExpireTime(new Date(expireTime));
        // 订单处理状态
        orderInfo.setProcessStatus(ProcessStatus.UNPAID.name());
        // 物流号，发货以后才又
        orderInfo.setTrackingNo("");
        //订单需要拆单的话，要指定父单id
        orderInfo.setParentOrderId(0L);
        //第一个商品的图片，未来展示订单列表的时候，可以看的
        orderInfo.setImgUrl(vo.getOrderDetailList().get(0).getImgUrl());


        //仓库id
        orderInfo.setWareId("");
        //
        orderInfo.setProvinceId(0L);
        //远程调用
        orderInfo.setActivityReduceAmount(new BigDecimal("0"));
        orderInfo.setCouponAmount(new BigDecimal("0"));
        //订单的原始价格 TotalAmount = OriginalTotalAmount - ActivityReduceAmount - CouponAmount
        orderInfo.setOriginalTotalAmount(totalAmount);
        ///收到货以后，立即修改为 30天后
        orderInfo.setRefundableTime(new Date());
        //运费系统
        orderInfo.setFeightFee(new BigDecimal("0"));
        orderInfo.setOperateTime(new Date());



        return orderInfo;
    }

    /**
     * 提交订单
     * @param tradeNo orderInfo
     * @param order 订单vo
     * @return Long
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Long submitOrder(String tradeNo, OrderSubmitVo order) {
        // 验令牌
        boolean tradeToken = checkTradeToken(tradeNo);
        if (!tradeToken){
            // 假的令牌
            throw new GmallException(ResultCodeEnum.ORRDER_INVALIDE_TOKEN);
        }
        // 验库存
        List<String> noStockSku = order.getOrderDetailList().stream()
                .filter(item -> {
                    Long skuId = item.getSkuId();
                    Integer skuNum = item.getSkuNum();
                    String stock = wareFeignClient.hasStock(skuId, skuNum);
                    return "0".equals(stock);
                }).map(item -> item.getSkuName())
                .collect(Collectors.toList());

        if (noStockSku.size()>0 && noStockSku!=null){
            GmallException exception = new GmallException(
                    ResultCodeEnum.ORDER_ITEM_NO_STOCK.getMessage() + Jsons.toStr(noStockSku),
                    ResultCodeEnum.ORDER_ITEM_NO_STOCK.getCode()
            );
            throw exception;
        }
        // 验价格
        List<String> priceChangeSku = order.getOrderDetailList().stream()
                .filter(item -> {
                    Result<BigDecimal> price = skuFeignClient.get1010Price(item.getSkuId());
                    return !item.getOrderPrice().equals(price.getData());

                }).map(item -> item.getSkuName())
                .collect(Collectors.toList());
        if (priceChangeSku != null && priceChangeSku.size()>0){
            GmallException exception = new GmallException(
                    ResultCodeEnum.ORDER_PRICE_CHANGE.getMessage() + Jsons.toStr(noStockSku),
                    ResultCodeEnum.ORDER_PRICE_CHANGE.getCode()
            );
            throw exception;
        }
        OrderInfo orderInfo = orderBizSercice.saveOrder(tradeNo, order);


        //5、发送消息给MQ； //orderId、userId、totalAmout、status
        OrderCreateMsg msg = prepareOrderMsg(orderInfo);
        rabbitTemplate.convertAndSend(MQConst.EXCHANGE_ORDER_EVENT,
                MQConst.ROUTE_KEY_ORDER_CREATE,
                Jsons.toStr(msg)
                );


        return orderInfo.getId();
    }

    private OrderCreateMsg prepareOrderMsg(OrderInfo info) {
        return new OrderCreateMsg(info.getId(),
                info.getUserId(),
                info.getTotalAmount(),
                info.getOrderStatus());

    }

    /**
     * 关闭过期订单
     * @param orderId
     * @param userId
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void closeOrder(Long orderId, Long userId) {

        ProcessStatus closedStatus = ProcessStatus.CLOSED;
        //1.修改订单状态为已关闭
        orderInfoService.updateOrderStatus(orderId,userId,
                closedStatus.getOrderStatus().name(),
                closedStatus.name(),
                ProcessStatus.UNPAID.name());

    }


}
