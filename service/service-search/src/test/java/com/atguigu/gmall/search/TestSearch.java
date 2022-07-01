package com.atguigu.gmall.search;


import com.atguigu.gmall.search.bean.Person;
import com.atguigu.gmall.search.repo.PersonRepo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class TestSearch {

    @Autowired
    PersonRepo personRepo;

    @Test
    void testrestRepo(){
        personRepo.save(new Person(12L,12,"撒旦解放"));
    }
}
