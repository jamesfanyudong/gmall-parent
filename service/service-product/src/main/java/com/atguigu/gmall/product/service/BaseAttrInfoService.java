package com.atguigu.gmall.product.service;


import com.atguigu.gmall.model.product.BaseAttrInfo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 *
 */
public interface BaseAttrInfoService extends IService<BaseAttrInfo> {


    List<BaseAttrInfo> getAttrInfoList(Long c1Id, Long c2Id, Long c3Id);

    void updateAttrInfo(BaseAttrInfo baseAttrInfo);

    void saveAttrInfo(BaseAttrInfo baseAttrInfo);
}
