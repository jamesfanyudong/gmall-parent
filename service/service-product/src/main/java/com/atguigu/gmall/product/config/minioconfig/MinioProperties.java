package com.atguigu.gmall.product.config.minioconfig;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "app.minio")
public class MinioProperties {

    private String accessKey;
    private String secretKey;
    private String endPoint;
    private String bucketName;

}
