package com.atguigu.gmall.user.rpc;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.user.UserAddress;
import com.atguigu.gmall.user.service.UserAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author fanyudong
 * @date 2022/7/8 19:23
 */
@RequestMapping("/rpc/inner/user")
@RestController
public class RpcController {
    @Autowired
    UserAddressService userAddressService;


    /**
     * 获取用户地址信息
     *
     * @return
     */
    @GetMapping("/user/addressList")
    public Result<List<UserAddress>> getUserAddress(){
       List<UserAddress> list =  userAddressService.getUserAddress();
       return Result.ok(list);

    }
}
