package com.atguigu.gmall.item.service.impl;

import com.atguigu.gmall.common.constant.RedisConst;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.item.service.ItemService;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.model.vo.CategoryView;
import com.atguigu.gmall.model.vo.SkuDetailVo;
import com.atguigu.gmall.product.SkuFeignClient;
import com.atguigu.gmall.starter.cache.annotation.Cache;
import com.atguigu.gmall.starter.cache.component.CacheService;
import lombok.SneakyThrows;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;


/**
 * @author fanyudong
 */
@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    SkuFeignClient skuFeignClient;
    @Autowired
    CacheService cacheService;
    @Autowired
    RedissonClient redissonClient;


    @Cache(key = RedisConst.SKU_INFO_CACHE_KEY_PREFIX+"#{#params[0]}"
            ,bloomName = RedisConst.SKU_BLOOM_FILTER_NAME
                ,bloomIf = "#{#params[0]}",
            ttl = RedisConst.SKU_INFO_CACHE_TIMEOUT )
    @Override
    public SkuDetailVo getItemDetail(Long skuId) {
        return getItemDetailFromRpc(skuId);
    }


    public SkuDetailVo getItemDetailWithRedisson(Long skuId){

        String cacheKey = RedisConst.SKU_INFO_CACHE_KEY_PREFIX + skuId;

        // 1.先查缓存
       SkuDetailVo data =  cacheService.getData(cacheKey,SkuDetailVo.class);

       // 2.缓存中是否存在
        if (data==null){
            RBloomFilter<Object> filter = redissonClient.getBloomFilter(RedisConst.SKU_BLOOM_FILTER_NAME);
            // 布隆说没有就是没有
            if (!filter.contains(skuId)){
                return null;
            }
            // 22.2加分布式锁
            RLock lock = redissonClient.getLock(RedisConst.SKU_INFO_LOCK_PREFIX + skuId);
            // 枷锁
            boolean tryLock = lock.tryLock();
            // 获得锁
            if (tryLock){
                // 回源
                SkuDetailVo detail = getItemDetailFromRpc(skuId);
                // 缓存一份
                cacheService.saveData(cacheKey,detail,RedisConst.SKU_INFO_CACHE_TIMEOUT, TimeUnit.MILLISECONDS);
                lock.unlock();
                return detail;
            } else {
                //2.5、没获得锁
                try {
                    TimeUnit.MILLISECONDS.sleep(500);
                    //2.6、直接查缓存即可
                    return  cacheService.getData(cacheKey, SkuDetailVo.class);
                } catch (InterruptedException e) {

                }
            }
        }
        return data;





    }




    @SneakyThrows
    public SkuDetailVo getItemDetailFromRpc(Long skuId)  {



        SkuDetailVo vo = new SkuDetailVo();

        // 1.查基本信息
        CompletableFuture<SkuInfo> baseFuture = CompletableFuture.supplyAsync(() -> {

            // sku的info
            Result<SkuInfo> skuInfo = skuFeignClient.getSkuInfo(skuId);
            SkuInfo info = skuInfo.getData();
            vo.setSkuInfo(info);
            return info;
        });
        // 2.编排 - 查分类
        CompletableFuture<Void> categorysFuture = baseFuture.thenAcceptAsync(info -> {

            // 1.sku所在分类
            Long category3Id = info.getCategory3Id();
            // 按照三级分类id查出所在的完整分类信息
            Result<CategoryView> categoryView = skuFeignClient.getCategoryView(category3Id);
            vo.setCategoryView(categoryView.getData());

        });

        //3 .异步编排价格
        CompletableFuture<Void> priceFuture = baseFuture.thenAcceptAsync(info -> {


            vo.setPrice(info.getPrice());
        });


        // 4.编排 -- sku的销售属性列表
        CompletableFuture<Void> saleAttrFuture = baseFuture.thenAcceptAsync(info -> {

            Long spuId = info.getSpuId();
            Result<List<SpuSaleAttr>> saleAttr = skuFeignClient.getSaleAttr(skuId, spuId);
            if (saleAttr.isOk()) {
                vo.setSpuSaleAttrList(saleAttr.getData());
            }
        });

        //5、编排 - 查 一个sku对应的spu的所有sku的组合关系、
        CompletableFuture<Void> skuOtherFuture = baseFuture.thenAcceptAsync(info -> {

            //5、得到一个sku对应的spu的所有sku的组合关系
            Result<String> value = skuFeignClient.getSpudeAllSkuSaleAttrAndValue(info.getSpuId());
            vo.setValuesSkuJson(value.getData());

        });

        // 6.编排 -- 等到所有任务运行完成
        CompletableFuture.allOf(categorysFuture,priceFuture,saleAttrFuture,skuOtherFuture)
                .get();

        return vo;



    }


}
