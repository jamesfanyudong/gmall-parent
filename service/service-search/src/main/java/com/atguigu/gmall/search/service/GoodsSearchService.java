package com.atguigu.gmall.search.service;


import com.atguigu.gmall.model.list.Goods;
import com.atguigu.gmall.model.vo.search.SearchParam;
import com.atguigu.gmall.model.vo.search.SearchResponseVo;

/**
 * @author fanyudong
 */
public interface GoodsSearchService {
    void upGoods(Goods goods);

    void downGoods(Long skuId);

    SearchResponseVo search(SearchParam searchParm);

    void incrHotScore(Long skuId, Long score);
}
