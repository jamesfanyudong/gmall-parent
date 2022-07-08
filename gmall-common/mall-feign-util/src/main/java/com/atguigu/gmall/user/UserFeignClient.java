package com.atguigu.gmall.user;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.user.UserAddress;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * @author fanyudong
 * @date 2022/7/8 19:32
 */
@FeignClient("service-user")
@RequestMapping("/rpc/inner/user")
public interface UserFeignClient {

    /**
     * 获取用户地址信息
     *
     * @return
     */
    @GetMapping("/user/addressList")
    public Result<List<UserAddress>> getUserAddress();



}
