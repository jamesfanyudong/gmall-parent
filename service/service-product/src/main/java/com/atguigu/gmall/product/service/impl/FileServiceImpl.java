package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.product.config.minioconfig.MinioProperties;
import com.atguigu.gmall.product.service.FileService;
import io.minio.MinioClient;
import io.minio.PutObjectOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;


@Service
public class FileServiceImpl implements FileService {

    @Autowired
    MinioClient minioClient;

    @Autowired
    MinioProperties minioProperties;
    @Override
    public String fileUpload(MultipartFile file) throws Exception {

        // 检查存储桶是否已经存在
        boolean isExist = minioClient.bucketExists(minioProperties.getBucketName());
        if(!isExist) {
            // 创建一个桶
            minioClient.makeBucket(minioProperties.getBucketName());
        }

        // 获取文件的二进制流
        InputStream stream = file.getInputStream();

        // 获取文件名
        String fileName = UUID.randomUUID().toString().replace("-","")+ "_"+ file.getOriginalFilename();
        // 使用putObject上传一个文件到存储桶中。
        PutObjectOptions options = new PutObjectOptions(stream.available(),-1);
        // 告诉浏览器，上传的文件格式
        options.setContentType(file.getContentType());
        minioClient.putObject(minioProperties.getBucketName(),fileName,
                stream,
                options
        );
        // endpoint/bucketName/filename
        //System.out.println("http://172.99.0.14:9000/gmall/11.jpg");
        // 返回url路径
        String url = minioProperties.getEndPoint() + "/" +
                minioProperties.getBucketName() + "/" +
                fileName;
        return url;
    }
}
