package com.atguigu.gmall.pay.config.pay;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author fanyudong
 * @date 2022/7/12 18:46
 */
@Data
@Component
@ConfigurationProperties(prefix = "app.alipay")
public class AlipayProperties {
    private String appId;
    //商户私钥
    private String merchantPrivateKey;
    //支付宝公钥
    private String alipayPublicKey;
    //异步通知地址
    private String notifyUrl;
    //同步跳转地址（支付成功以后，浏览器跳转到的页面）
    private String returnUrl;
    //签名类型
    private String signType;
    private String charset;
    //支付宝网关
    private String gatewayUrl;
}
