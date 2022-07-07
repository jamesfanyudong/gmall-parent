package com.atguigu.gmall.gateway.properties;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author fanyudong
 */
@Data
@Component
@ConfigurationProperties(prefix = "app.auth")
public class AuthProperties {

    // 需要登陆才能访问

    private List<String> loginUrl;

    //任何情况都不能让浏览器访问的

    private List<String> noAuthUrl;
    // 登陆页地址

    private String loginPage;
}
