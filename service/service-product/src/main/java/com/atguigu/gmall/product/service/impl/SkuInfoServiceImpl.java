package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.common.constant.RedisConst;
import com.atguigu.gmall.common.util.AuthContextHolder;
import com.atguigu.gmall.model.cart.CartInfo;
import com.atguigu.gmall.model.list.Goods;
import com.atguigu.gmall.model.vo.user.UserAuth;
import com.atguigu.gmall.product.mapper.SkuInfoMapper;
import com.atguigu.gmall.product.service.SkuImageService;
import com.atguigu.gmall.product.service.SkuSaleAttrValueService;
import com.atguigu.gmall.model.product.SkuAttrValue;
import com.atguigu.gmall.model.product.SkuImage;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SkuSaleAttrValue;
import com.atguigu.gmall.product.service.SkuAttrValueService;
import com.atguigu.gmall.product.service.SkuInfoService;
import com.atguigu.gmall.search.SearchFeignClient;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.concurrent.*;

/**
 *
 * @author fanyudong
 */
@Service
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoMapper, SkuInfo>
    implements SkuInfoService{

    @Autowired
    SkuInfoMapper skuInfoMapper;
    @Autowired
    SkuImageService skuImageService;
    @Autowired
    SkuAttrValueService skuAttrValueService;
    @Autowired
    SkuSaleAttrValueService skuSaleAttrValueService;

    @Autowired
    RedissonClient redissonClient;
    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    SearchFeignClient searchFeignClient;

//    static ThreadPoolExecutor threadPool = new ThreadPoolExecutor(
//            2,
//            4,
//            10,
//            TimeUnit.SECONDS,
//            new ArrayBlockingQueue<Runnable>(1),
//            new ThreadPoolExecutor.AbortPolicy()
//    );

    ScheduledExecutorService threadPool = Executors.newScheduledThreadPool(4);



    /**
     * 修改sku信息
     * @param skuInfo
     */
    @Override
    public void updateSkuInfo(SkuInfo skuInfo){
        // 延迟双删
        redisTemplate.delete(RedisConst.SKU_INFO_CACHE_KEY_PREFIX+skuInfo.getId());


        // 调用一个定时线程

        threadPool.schedule(new Runnable() {
            @Override
            public void run() {
                redisTemplate.delete(RedisConst.SKU_INFO_CACHE_KEY_PREFIX+skuInfo.getId());
            }
        },10,TimeUnit.SECONDS);




    }

    @Override
    public Goods getGoodsInfoBySkuId(Long skuId) {
       Goods goods =  skuInfoMapper.getGoodsInfoBySkuId(skuId);
        return goods;
    }

    /**
     * 获取购物车信息
     * @param skuId
     * @return
     */
    @Override
    public CartInfo getCartInfoBySkuId(Long skuId) {
        CartInfo info = new CartInfo();
        UserAuth userAuth = AuthContextHolder.getUserAuth();
        if (userAuth.getUserId()!=null){
            // 用户已登陆
            info.setUserId(userAuth.getUserId().toString());
        }else{
            info.setUserId(userAuth.getTempId());
        }
        info.setSkuId(skuId);
        info.setId(info.getId());

        // 查价格
        BigDecimal price = skuInfoMapper.getSkuPrice(skuId);
        info.setCartPrice(price);

        SkuInfo skuInfo = skuInfoMapper.selectById(skuId);
        info.setImgUrl(skuInfo.getSkuDefaultImg());
        info.setSkuName(skuInfo.getSkuName());

        //默认被选中
        info.setIsChecked(1);
        info.setCreateTime(new Date());
        info.setUpdateTime(new Date());
        //商品实时价格
        info.setSkuPrice(price);
        info.setCouponInfoList(null);

        return info;
    }

    @Override
    public BigDecimal get1010Price(Long skuId) {
        return skuInfoMapper.getSkuPrice(skuId);

    }

    @Transactional(rollbackFor = {RuntimeException.class})
    @Override
    public void saveSkuInfo(SkuInfo skuInfo) {
        // 保存基本基本信息
        this.save(skuInfo);
        Long skuId = skuInfo.getId();
        Long spuId = skuInfo.getSpuId();

        // 保存skuImageList信息
        List<SkuImage> imageList = skuInfo.getSkuImageList();
        for (SkuImage image : imageList) {
            image.setSkuId(skuId);
            skuImageService.save(image);
        }

        // 保存 skuAttrValueList
        List<SkuAttrValue> attrValueList = skuInfo.getSkuAttrValueList();
        for (SkuAttrValue attrValue : attrValueList) {

            attrValue.setSkuId(skuId);
            skuAttrValueService.save(attrValue);
        }
        // 保存 skuSaleAttrValueList
        List<SkuSaleAttrValue> saleAttrValueList = skuInfo.getSkuSaleAttrValueList();
        for (SkuSaleAttrValue saleAttrValue : saleAttrValueList) {
            saleAttrValue.setSkuId(skuId);
            saleAttrValue.setSpuId(spuId);
            skuSaleAttrValueService.save(saleAttrValue);
        }
        // 往布隆中存放skuid  数据库存一个，布隆就有一个
        RBloomFilter<Object> filter = redissonClient.getBloomFilter(RedisConst.SKU_BLOOM_FILTER_NAME);
        filter.add(skuId);


    }

    @Override
    public void onSale(Long skuId) {

        // 数据保存到es
        skuInfoMapper.updateStatus(skuId,1);
        Goods goods =  this.getGoodsInfoBySkuId(skuId);
        searchFeignClient.up(goods);


    }

    @Override
    public void cancelSale(Long skuId) {

        skuInfoMapper.updateStatus(skuId,0);
        searchFeignClient.down(skuId);
    }

    /**
     * 获取skuId,放入布隆过滤器
     * @return
     */
    @Override
    public List<Long> getSkuIds() {

        return skuInfoMapper.getSkuIds();
    }

    /**
     * 获取商品价格
     * @param skuId
     * @return
     */
    @Override
    public BigDecimal getSkuPrice(Long skuId) {
        return skuInfoMapper.getSkuPrice(skuId);
    }
}




