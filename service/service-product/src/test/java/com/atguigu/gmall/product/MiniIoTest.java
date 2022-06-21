package com.atguigu.gmall.product;


import io.minio.MinioClient;
import io.minio.PutObjectOptions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.FileInputStream;

@SpringBootTest
public class MiniIoTest{


    @Autowired
    MinioClient minioClient;



    @Test
    public void fileUpload(){

        try {
            // 使用MinIO服务的URL，端口，Access key和Secret key创建一个MinioClient对象
//            MinioClient minioClient = new MinioClient("http://192.168.200.128:9000",
//                    "admin",
//                    "admin123456");

            // 检查存储桶是否已经存在
            boolean isExist = minioClient.bucketExists("gmall");
            if(!isExist) {
                // 创建一个名为asiatrip的存储桶，用于存储照片的zip文件。
                minioClient.makeBucket("gmall");
            }

            FileInputStream stream = new FileInputStream("C:\\Users\\fanyudong\\Pictures\\Camera Roll\\11.jpg");


            // 使用putObject上传一个文件到存储桶中。
            PutObjectOptions options = new PutObjectOptions(stream.available(),-1);
            minioClient.putObject("gmall","11.jpg",
                    stream,
                    options
                    );
            System.out.println("http://172.99.0.14:9000/gmall/11.jpg");
        } catch(Exception e) {
            System.out.println("Error occurred: " + e);
        }


    }




}
