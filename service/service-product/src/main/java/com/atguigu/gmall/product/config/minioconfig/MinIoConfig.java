package com.atguigu.gmall.product.config.minioconfig;


import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration

public class MinIoConfig {

    @Autowired
    MinioProperties minioProperties;

    @Bean
    public MinioClient getMinioClient() throws Exception {
        return new MinioClient(
            minioProperties.getEndPoint(),
                minioProperties.getAccessKey(),
                minioProperties.getSecretKey()
                        );
    }

}
