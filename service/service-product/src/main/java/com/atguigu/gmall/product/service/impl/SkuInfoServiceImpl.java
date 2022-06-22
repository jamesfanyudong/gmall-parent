package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.product.SkuAttrValue;
import com.atguigu.gmall.model.product.SkuImage;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SkuSaleAttrValue;
import com.atguigu.gmall.product.mapper.SkuInfoMapper;
import com.atguigu.gmall.product.service.SkuAttrValueService;
import com.atguigu.gmall.product.service.SkuImageService;
import com.atguigu.gmall.product.service.SkuInfoService;
import com.atguigu.gmall.product.service.SkuSaleAttrValueService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 *
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

    @Transactional
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


    }

    @Override
    public void onSale(Long skuId) {
        // TODO 加入检索
        skuInfoMapper.updateStatus(skuId,1);
    }

    @Override
    public void cancelSale(Long skuId) {
        // TODO 加入检索
        skuInfoMapper.updateStatus(skuId,0);
    }
}




