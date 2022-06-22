package com.atguigu.gmall.product.service;

import com.atguigu.gmall.model.product.SpuImage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 *
 */
public interface SpuImageService extends IService<SpuImage> {

    List<SpuImage> getSpuImageList(Long spuId);
}
