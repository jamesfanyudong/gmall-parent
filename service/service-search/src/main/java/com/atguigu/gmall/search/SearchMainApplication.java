package com.atguigu.gmall.search;


import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

/**
 * @author fanyudong
 */

@EnableElasticsearchRepositories
@SpringCloudApplication
public class SearchMainApplication {


    public static void main(String[] args) {
        SpringApplication.run(SearchMainApplication.class,args);
    }
}
