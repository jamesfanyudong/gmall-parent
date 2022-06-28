package com.atguigu.gmall.product.biz.impl;

import com.atguigu.gmall.common.util.Jsons;
import com.atguigu.gmall.model.vo.CategoryVo;
import com.atguigu.gmall.product.biz.CategoryBizService;
import com.atguigu.gmall.product.mapper.CategoryBizMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author fanyudong
 */

@Slf4j
@Service
public class CategoryBizServiceImpl implements CategoryBizService {
    @Autowired
    CategoryBizMapper categoryBizMapper;

    @Autowired
    StringRedisTemplate redisTemplate;




    @Override
    public List<CategoryVo> getCategorys() {


        // 先查缓存
        String categorys = redisTemplate.opsForValue().get("categorys");
        List<CategoryVo> vos = null;

        if (categorys == null) {
            // 缓存中没有，回源查数据库
            Boolean lock = redisTemplate.opsForValue().setIfAbsent("lock", "11");
            if (lock){
            // 抢到锁，查数据库
            vos = categoryBizMapper.getCategorys();
            } else {
                // 没抢到锁
            }
//             不管数据库有没有，都放缓存
            if (vos==null) {
                // 数据库中没有，缓存的过期时间短一点
                redisTemplate.opsForValue().set("categorys", Jsons.toStr(null));
            } else {
//             数据库中有，缓存的过期时间长一点
                redisTemplate.opsForValue().set("categorys", Jsons.toStr(vos));
            }
            redisTemplate.delete("lock");
            return vos;
        }


        // 缓存中有
        log.info("缓存命中+{}",categorys);
        return Jsons.toObj(categorys, new TypeReference<List<CategoryVo>>() {});


    }
}
