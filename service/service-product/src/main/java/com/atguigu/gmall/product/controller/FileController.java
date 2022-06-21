package com.atguigu.gmall.product.controller;


import com.atguigu.gmall.common.result.Result;

import com.atguigu.gmall.product.service.FileService;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;




@RestController
@RequestMapping("/admin/product")
public class FileController {


    @Autowired
    FileService fileService;




    // /admin/product/fileUpload

    /**
     *
     * 文件上传
     * @param file
     * @return
     */
    @PostMapping("/fileUpload")
    public Result fileUpload(@RequestPart("file") MultipartFile file) throws Exception {
        String url = fileService.fileUpload(file);
        return Result.ok(url);
    }
}
