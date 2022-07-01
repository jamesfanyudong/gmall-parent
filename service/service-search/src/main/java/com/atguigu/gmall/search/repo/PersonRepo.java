package com.atguigu.gmall.search.repo;

import com.atguigu.gmall.search.bean.Person;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 * @author fanyudong
 */
@Repository
public interface PersonRepo  extends PagingAndSortingRepository<Person,Long> {
}
