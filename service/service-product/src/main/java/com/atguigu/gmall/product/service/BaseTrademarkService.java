package com.atguigu.gmall.product.service;


import com.atguigu.gmall.model.product.BaseTrademark;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 *
 */
public interface BaseTrademarkService extends IService<BaseTrademark> {

    List<BaseTrademark> getTrademarkList();

}
