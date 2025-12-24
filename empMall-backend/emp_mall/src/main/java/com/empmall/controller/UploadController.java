package com.empmall.controller;

import com.empmall.pojo.Result;
import com.empmall.utils.UploadFileUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
public class UploadController {

    @Autowired
    private UploadFileUtils uploadFileUtils;

    /**
     * 文件上傳
     */
    @PostMapping("/upload")
    public Result upload(MultipartFile image) {
        log.info("接收到文件上傳請求: {}", image.getOriginalFilename());
        try {
            String url = uploadFileUtils.upload(image); // 呼叫工具類上傳

            return Result.success(url); // 回傳 S3 的網址給前端
        } catch (Exception e) {
            log.error("文件上傳失敗", e);
            return Result.error("文件上傳失敗");
        }
    }
}