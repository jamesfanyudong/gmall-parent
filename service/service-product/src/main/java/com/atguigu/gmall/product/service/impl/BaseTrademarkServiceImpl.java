package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.product.mapper.BaseTrademarkMapper;
import com.atguigu.gmall.model.product.BaseTrademark;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.atguigu.gmall.product.service.BaseTrademarkService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *
 */
@Service
public class BaseTrademarkServiceImpl extends ServiceImpl<BaseTrademarkMapper, BaseTrademark>
    implements BaseTrademarkService{

    @Override
    public List<BaseTrademark> getTrademarkList() {
        List<BaseTrademark> list = this.list();
        return list;
    }
}




