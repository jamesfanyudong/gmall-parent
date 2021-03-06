package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.common.constant.RedisConst;
import com.atguigu.gmall.model.list.Goods;
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
     * ??????sku??????
     * @param skuInfo
     */
    @Override
    public void updateSkuInfo(SkuInfo skuInfo){
        // ????????????
        redisTemplate.delete(RedisConst.SKU_INFO_CACHE_KEY_PREFIX+skuInfo.getId());


        // ????????????????????????

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

    @Transactional(rollbackFor = {RuntimeException.class})
    @Override
    public void saveSkuInfo(SkuInfo skuInfo) {
        // ????????????????????????
        this.save(skuInfo);
        Long skuId = skuInfo.getId();
        Long spuId = skuInfo.getSpuId();

        // ??????skuImageList??????
        List<SkuImage> imageList = skuInfo.getSkuImageList();
        for (SkuImage image : imageList) {
            image.setSkuId(skuId);
            skuImageService.save(image);
        }

        // ?????? skuAttrValueList
        List<SkuAttrValue> attrValueList = skuInfo.getSkuAttrValueList();
        for (SkuAttrValue attrValue : attrValueList) {

            attrValue.setSkuId(skuId);
            skuAttrValueService.save(attrValue);
        }
        // ?????? skuSaleAttrValueList
        List<SkuSaleAttrValue> saleAttrValueList = skuInfo.getSkuSaleAttrValueList();
        for (SkuSaleAttrValue saleAttrValue : saleAttrValueList) {
            saleAttrValue.setSkuId(skuId);
            saleAttrValue.setSpuId(spuId);
            skuSaleAttrValueService.save(saleAttrValue);
        }
        // ??????????????????skuid  ???????????????????????????????????????
        RBloomFilter<Object> filter = redissonClient.getBloomFilter(RedisConst.SKU_BLOOM_FILTER_NAME);
        filter.add(skuId);


    }

    @Override
    public void onSale(Long skuId) {

        // ???????????????es
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
     * ??????skuId,?????????????????????
     * @return
     */
    @Override
    public List<Long> getSkuIds() {

        return skuInfoMapper.getSkuIds();
    }

    /**
     * ??????????????????
     * @param skuId
     * @return
     */
    @Override
    public BigDecimal getSkuPrice(Long skuId) {
        return skuInfoMapper.getSkuPrice(skuId);
    }
}




