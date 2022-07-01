package com.atguigu.gmall.search.repo;

import com.atguigu.gmall.model.list.Goods;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 * @author fanyudong
 */
@Repository
public interface GoodsRepo extends PagingAndSortingRepository<Goods,Long> {
}
