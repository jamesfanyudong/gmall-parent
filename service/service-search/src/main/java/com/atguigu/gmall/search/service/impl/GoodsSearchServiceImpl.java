package com.atguigu.gmall.search.service.impl;

import com.atguigu.gmall.model.list.Goods;
import com.atguigu.gmall.model.vo.search.SearchParm;
import com.atguigu.gmall.model.vo.search.SearchResponseVo;
import com.atguigu.gmall.search.repo.GoodsRepo;
import com.atguigu.gmall.search.service.GoodsSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author fanyudong
 */

@Service
public class GoodsSearchServiceImpl implements GoodsSearchService {

    @Autowired
    GoodsRepo goodsRepo;
    @Override
    public void upGoods(Goods goods) {
        goodsRepo.save(goods);

    }

    @Override
    public void downGoods(Long skuId) {
        goodsRepo.deleteById(skuId);
    }

    @Override
    public SearchResponseVo search(SearchParm searchParm) {
        //TODO 真正检索


        return null;
    }
}
