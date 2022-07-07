package com.atguigu.gmall.search.service.impl;

import com.atguigu.gmall.model.list.Goods;
import com.atguigu.gmall.model.vo.search.*;
import com.atguigu.gmall.search.repo.GoodsRepo;
import com.atguigu.gmall.search.service.GoodsSearchService;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.core.query.UpdateQuery;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author fanyudong
 */

@Service
public class GoodsSearchServiceImpl implements GoodsSearchService {

    @Autowired
    GoodsRepo goodsRepo;

    @Autowired
    ElasticsearchRestTemplate restTemplate;
    @Override
    public void upGoods(Goods goods) {
        goodsRepo.save(goods);

    }

    @Override
    public void downGoods(Long skuId) {
        goodsRepo.deleteById(skuId);
    }

    @Override
    public SearchResponseVo search(SearchParam searchParam) {

        // 根据前端传来的参数检索
        Query query = buildQuery(searchParam);
        /**
         * Query query：            查询条件
         * Class<T> clazz：         查到的数据封装成什么
         * IndexCoordinates index： 去哪个索引查
         */
        SearchHits<Goods> searchHits =  restTemplate.search(query,Goods.class, IndexCoordinates.of("goods"));
        // 把检索的结果封装成前端能用的对象


        return buildResponse(searchHits,searchParam);
    }

    /**
     * 增加热度分
     * @param skuId
     * @param score
     */

    @Override
    public void incrHotScore(Long skuId, Long score) {
        //UpdateQuery query, IndexCoordinates index
        // 构建查询条件
        UpdateQuery query = buildHotScoreUpdateQuery(skuId,score);
        restTemplate.update(query,IndexCoordinates.of("goods"));

    }

    /**
     * 构建增加热度分的修改条件
     * @param skuId：商品skuid
     * @param score：商品现有的热度分
     * @return
     */
    private UpdateQuery buildHotScoreUpdateQuery(Long skuId, Long score) {
        //1. 拿到builder
        UpdateQuery.Builder builder = UpdateQuery.builder("" + skuId);
        // 2."hotScore":2
        Map<String,Long> map = new HashMap<>();
        map.put("hotScore",score);
        builder.withDocument(Document.from(map))
                .withDocAsUpsert(true);

        return builder.build();
    }

    // 把检索的结果封装成前端能用的对象
    private SearchResponseVo buildResponse(SearchHits<Goods> result, SearchParam searchParam) {

        SearchResponseVo vo = new SearchResponseVo();


        //1、拿到所有查到的商品

        List<Goods> list = new ArrayList<>();
        result.getSearchHits().forEach(hit ->{
            Goods good = hit.getContent();
            list.add(good);
        });
        vo.setGoodsList(list);

        //2.当前页面，总页数
        vo.setPageNo(searchParam.getPageNo());
        // 总记录数
        long hits = result.getTotalHits();
        long totalPages = hits % 10 ==0?hits / 10:(hits / 10) + 1;
        vo.setTotalPages(new Integer(totalPages+""));


        //3.检索条件

        vo.setSearchParam(searchParam);
        // 构建url
        String url = this.makeUrl(searchParam);
        vo.setUrlParam(url);

        //4.品牌列表
        List<TradeMarkVo> tmList = new ArrayList<>();
        // 拿到所有聚合结果
        ParsedLongTerms tmIdAgg = result.getAggregations().get("tmIdAgg");

        tmIdAgg.getBuckets().forEach(bucket->{
            //4.1品牌id
            long tmId = bucket.getKeyAsNumber().longValue();
            //4.2品牌名字
            ParsedStringTerms tmNameAgg = bucket.getAggregations().get("tmNameAgg");
            String tmName = tmNameAgg.getBuckets().get(0).getKeyAsString();

            //4.3品牌logo
            ParsedStringTerms tmLogoAgg = bucket.getAggregations().get("tmLogoAgg");
            String tmLogo = tmLogoAgg.getBuckets().get(0).getKeyAsString();
            // 封装vo
            TradeMarkVo tradeMarkVo = new TradeMarkVo();
            tradeMarkVo.setTmId(tmId);
            tradeMarkVo.setTmName(tmName);
            tradeMarkVo.setTmLogoUrl(tmLogo);

            tmList.add(tradeMarkVo);
        });
        vo.setTrademarkList(tmList);

        //5、属性列表（属性的进阶检索）：分析聚合结果
        List<AttrSearchVo> attrsList = new ArrayList<>();

        ParsedNested attrAgg = result.getAggregations().get("attrAgg");
        ParsedLongTerms attrIdAgg = attrAgg.getAggregations().get("attrIdAgg");
        attrIdAgg.getBuckets().forEach(bucket->{
            AttrSearchVo searchVo = new AttrSearchVo();
            // 5.1 属性id
            long attrId = bucket.getKeyAsNumber().longValue();
            ParsedStringTerms attrNameAgg = bucket.getAggregations().get("attrNameAgg");
            // 5.2 属性名
            String attrName = attrNameAgg.getBuckets().get(0).getKeyAsString();
            // 5.3 属性值
            ParsedStringTerms attrValueAgg = bucket.getAggregations().get("attrValueAgg");

            List<String> vals = new ArrayList<>();
            attrValueAgg.getBuckets().forEach(val->{
                String attrValue = val.getKeyAsString();
                vals.add(attrValue);
            });
            searchVo.setAttrId(attrId);
            searchVo.setAttrName(attrName);
            searchVo.setAttrValueList(vals);
            attrsList.add(searchVo);

        });
        vo.setAttrsList(attrsList);

        // 1:小米；制作品牌面包屑
        String trademark = searchParam.getTrademark();
        if (!StringUtils.isEmpty(trademark)){
            vo.setTrademarkParam("品牌："+trademark.split(":")[1]);
        }
        // 属性面包屑
        if (searchParam.getProps()!=null && searchParam.getProps().length>0){
            String[] props = searchParam.getProps();
            //3:6GB:运行内存   4:64GB:机身存储
            //制作每个检索属性的面包屑
            List<AttrBread> breads = Arrays.stream(props).map(str -> {
                String[] split = str.split(":");
                AttrBread bread = new AttrBread();
                bread.setAttrId(Long.parseLong(split[0]));
                bread.setAttrValue(split[1]);
                bread.setAttrName(split[2]);
                return bread;
            }).collect(Collectors.toList());
            vo.setPropsParamList(breads);
        }
        // order=2:desc
        //回显orderMap
        String order = searchParam.getOrder();
        OrderMap orderMap = new OrderMap();
        if (!StringUtils.isEmpty(order) && !"null".equalsIgnoreCase(order)){
            orderMap.setType(order.split(":")[0]);
            orderMap.setSort(order.split(":")[1]);
        }
        vo.setOrderMap(orderMap);


        return vo;
    }

    /**
     * 获取url
     * @param param
     * @return
     */
    private String makeUrl(SearchParam param) {
        StringBuilder urlBuilder = new StringBuilder("/list.html?");
        if(param.getPageNo()!=null){
            // /list.html?pageNo=1
            urlBuilder.append("&pageNo="+param.getPageNo());
        }

        if(param.getCategory1Id()!=null){
            urlBuilder.append("&category1Id="+param.getCategory1Id());
        }
        if(param.getCategory2Id()!=null){
            urlBuilder.append("&category2Id="+param.getCategory2Id());
        }
        if(param.getCategory3Id()!=null){
            urlBuilder.append("&category3Id="+param.getCategory3Id());
        }
        if(!StringUtils.isEmpty(param.getKeyword())){
            urlBuilder.append("&keyword="+param.getKeyword());
        }
        if(!StringUtils.isEmpty(param.getTrademark())){
            urlBuilder.append("&trademark="+param.getTrademark());
        }
        if(param.getProps()!=null &&param.getProps().length>0 ){
            Arrays.stream(param.getProps()).forEach(prop->{
                urlBuilder.append("&props="+prop);
            });
        }
//        if(!StringUtils.isEmpty(param.getOrder())){
//            urlBuilder.append("&order="+param.getOrder());
//        }



        return urlBuilder.toString();

    }

    /**
     * 构建查询条件
     * @param parm
     * @return
     */
    private Query buildQuery(SearchParam parm) {
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();


        // 1.总的query
        NativeSearchQuery dsl = new NativeSearchQuery(boolQuery);

        // ==========查询条件=========
        if (parm.getCategory1Id()!=null){

            boolQuery.must(QueryBuilders.termQuery("category1Id",parm.getCategory1Id()));
        }

        if (parm.getCategory2Id()!=null){

            boolQuery.must(QueryBuilders.termQuery("category2Id",parm.getCategory2Id()));
        }

        if (parm.getCategory3Id()!=null){

            boolQuery.must(QueryBuilders.termQuery("category3Id",parm.getCategory3Id()));
        }

        // 模糊匹配 keyword
        if (!StringUtils.isEmpty(parm.getKeyword())){

            boolQuery.must(QueryBuilders.matchQuery("title",parm.getKeyword()));
        }
        //5、bool-must- 品牌的精确匹配
        if (!StringUtils.isEmpty(parm.getTrademark())){
            String[] split = parm.getTrademark().split(":");
            boolQuery.must(QueryBuilders.termQuery("tmId",split[0]));
        }
        // 嵌入查询
        if (parm.getProps()!=null && parm.getProps().length>0  ){
            Arrays.stream(parm.getProps()).forEach(prop->{
                //["3:6GB:运行内存","4:64GB:机身存储"]
                        String[] split = prop.split(":");
                        BoolQueryBuilder propQuery = QueryBuilders.boolQuery();
                        propQuery.must(QueryBuilders.termQuery("attrs.attrId",split[0]));
                        propQuery.must(QueryBuilders.termQuery("attrs.attrValue",split[1]));

                        boolQuery.must(QueryBuilders.nestedQuery("attrs",propQuery, ScoreMode.None));


             }
             );
        }
        //=====排序条件======
        if (!StringUtils.isEmpty(parm.getOrder())){
            //2:asc
            Sort sort = null;
            String[] split = parm.getOrder().split(":");
            switch (split[0]){
                case "1":
                    sort = split[1].equalsIgnoreCase("asc") ? Sort.by("hotScore").ascending():Sort.by("hotScore").descending();
                    break;
                case "2":
                    sort = split[1].equalsIgnoreCase("desc") ? Sort.by("price").ascending():Sort.by("price").descending();
                    break;
            }
            dsl.addSort(sort);


        }

        //====分页条件====
        if (parm.getPageNo()!=null){
            PageRequest request = PageRequest.of(parm.getPageNo().intValue()-1,10);
            dsl.setPageable(request);
        }

        //====聚合分析====
        //==分析：品牌==
        // 1.品牌id-聚合
        TermsAggregationBuilder tmIdAgg = AggregationBuilders
                .terms("tmIdAgg")
                .field("tmId")
                .size(100);
        //2、品牌id-聚合- 品牌名子聚合
        TermsAggregationBuilder tmNameAgg = AggregationBuilders.terms("tmNameAgg")
                .field("tmName")
                .size(1);
        TermsAggregationBuilder tmLogoAgg = AggregationBuilders.terms("tmLogoAgg")
                .field("tmLogoUrl")
                .size(1);
        tmIdAgg.subAggregation(tmNameAgg);
        tmIdAgg.subAggregation(tmLogoAgg);
        dsl.addAggregation(tmIdAgg);


        //==分析：平台属性==
        NestedAggregationBuilder attrAgg = AggregationBuilders.nested("attrAgg", "attrs");
        TermsAggregationBuilder attrIdAgg = AggregationBuilders.terms("attrIdAgg")
                .field("attrs.attrId")
                .size(100);
        TermsAggregationBuilder attrNameAgg = AggregationBuilders.terms("attrNameAgg")
                .field("attrs.attrName")
                .size(1);
        TermsAggregationBuilder attrValueAgg = AggregationBuilders.terms("attrValueAgg")
                .field("attrs.attrValue")
                .size(100);
        attrIdAgg.subAggregation(attrNameAgg);
        attrIdAgg.subAggregation(attrValueAgg);
        attrAgg.subAggregation(attrIdAgg);


        dsl.addAggregation(attrAgg);





        return dsl;
    }
}
