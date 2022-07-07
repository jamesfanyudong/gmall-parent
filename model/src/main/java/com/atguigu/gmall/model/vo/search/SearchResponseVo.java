package com.atguigu.gmall.model.vo.search;

import com.atguigu.gmall.model.list.Goods;
import lombok.Data;

import java.util.List;

/**
 * @author fanyudong
 */
@Data
public class SearchResponseVo {


    // 此次检索用的所有参数
    private SearchParam searchParam;

    // 品牌面包屑
    private String trademarkParam;

    // 属性面包屑
    private List<AttrBread> propsParamList;

    // 品牌列表
    private List<TradeMarkVo> trademarkList;

    // 属性列表
    private List<AttrSearchVo> attrsList;
    // 排序规则
    private OrderMap orderMap;

    // 检索到所有的商品
    private List<Goods> goodsList;

    // 当前页码
    private Long pageNo;
    // 总页码
    private long totalPages;
    //返回路径
    private String urlParam;






}
