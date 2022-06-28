package com.atguigu.gmall.item.service.impl;

import com.atguigu.gmall.common.constant.RedisConst;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.item.component.CacheService;
import com.atguigu.gmall.item.service.ItemService;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.model.vo.CategoryView;
import com.atguigu.gmall.model.vo.SkuDetailVo;
import com.atguigu.gmall.product.SkuFeignClient;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    SkuFeignClient skuFeignClient;
    @Autowired
    CacheService cacheService;
    @Autowired
    RedissonClient redissonClient;


    @Override
    public SkuDetailVo getItemDetail(Long skuId){

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




    public SkuDetailVo getItemDetailFromRpc(Long skuId) {
        SkuDetailVo vo = new SkuDetailVo();
        // sku的info
        Result<SkuInfo> skuInfo = skuFeignClient.getSkuInfo(skuId);
        SkuInfo info = skuInfo.getData();
        vo.setSkuInfo(info);


        // 1.sku所在分类
        Long category3Id = info.getCategory3Id();
        // 按照三级分类id查出所在的完整分类信息
        Result<CategoryView> categoryView = skuFeignClient.getCategoryView(category3Id);
        vo.setCategoryView(categoryView.getData());


        // 3.sku的价格
        vo.setPrice(info.getPrice());

        // 4.sku的销售属性列表
        Long spuId = info.getSpuId();
        Result<List<SpuSaleAttr>> saleAttr = skuFeignClient.getSaleAttr(skuId, spuId);
        if (saleAttr.isOk()){
            vo.setSpuSaleAttrList(saleAttr.getData());
        }


        //5、得到一个sku对应的spu的所有sku的组合关系
        Result<String> value = skuFeignClient.getSpudeAllSkuSaleAttrAndValue(spuId);
        vo.setValuesSkuJson(value.getData());
        return vo;
    }
}
