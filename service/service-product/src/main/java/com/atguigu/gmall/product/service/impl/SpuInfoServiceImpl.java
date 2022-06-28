package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.product.mapper.SpuInfoMapper;
import com.atguigu.gmall.product.service.SpuImageService;
import com.atguigu.gmall.product.service.SpuInfoService;
import com.atguigu.gmall.product.service.SpuSaleAttrService;
import com.atguigu.gmall.model.product.SpuImage;
import com.atguigu.gmall.model.product.SpuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.model.product.SpuSaleAttrValue;
import com.atguigu.gmall.product.service.SpuSaleAttrValueService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * author：fyd
 */
@Service
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoMapper, SpuInfo>
    implements SpuInfoService {
    @Autowired
    SpuImageService spuImageService;


    @Autowired
    SpuSaleAttrService spuSaleAttrService;
    @Autowired
    SpuSaleAttrValueService spuSaleAttrValueService;


    /**
     * 保存spu 信息
     * @param spuInfo
     */

    @Transactional
    @Override
    public void saveSpuInfo(SpuInfo spuInfo) {
        //保存spu基本信息.
        this.save(spuInfo);
        // 获取自增id
        Long spuId = spuInfo.getId();

        //2. 保存spu图片列表
        List<SpuImage> imageList = spuInfo.getSpuImageList();
        for (SpuImage image : imageList) {
            image.setSpuId(spuId);
            spuImageService.save(image);

        }

        // 3.保存销售属性信息
        List<SpuSaleAttr> spuSaleAttrList = spuInfo.getSpuSaleAttrList();
        for (SpuSaleAttr spuSaleAttr : spuSaleAttrList) {
            spuSaleAttr.setSpuId(spuId); // 回填spuId
            spuSaleAttrService.save(spuSaleAttr);
            String saleAttrName = spuSaleAttr.getSaleAttrName();

            // 4.保存销售属性值
            List<SpuSaleAttrValue> valueList = spuSaleAttr.getSpuSaleAttrValueList();
            for (SpuSaleAttrValue value : valueList) {
                value.setSpuId(spuId);
                value.setSaleAttrName(saleAttrName);
                spuSaleAttrValueService.save(value);

            }
        }


    }
}




