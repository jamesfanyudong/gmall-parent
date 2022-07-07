package com.atguigu.gmall.user.service;


import com.atguigu.gmall.model.user.UserInfo;
import com.atguigu.gmall.model.vo.user.LoginSuccessRespVo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 *
 * @author fanyudong
 */
public interface UserInfoService extends IService<UserInfo> {

    LoginSuccessRespVo login(UserInfo userInfo, String ipAddress);

    void logout(String token);
}
