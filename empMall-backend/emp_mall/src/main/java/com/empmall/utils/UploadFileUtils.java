package com.empmall.utils;

import jakarta.annotation.PostConstruct; // Spring Boot 3 使用 jakarta
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Component
public class UploadFileUtils {

    // 1. 從 application-local.yml 讀取配置
    @Value("${aws.access-key}")
    private String accessKey;

    @Value("${aws.secret-key}")
    private String secretKey;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    @Value("${aws.s3.region}")
    private String regionString;

    // S3Client
    private S3Client s3Client;

    // 2. 初始化 S3Client (只執行一次，提高效能)
    @PostConstruct
    public void init() {
        Region region = Region.of(regionString); // 將字串轉為 AWS Region 物件

        this.s3Client = S3Client.builder()
                .region(region)
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKey, secretKey)
                ))
                .build();
    }

    public String upload(MultipartFile file) throws IOException {
        // 3. 產生唯一檔名 (UUID + 原始副檔名)
        String originalFilename = file.getOriginalFilename();
        // 如果沒有副檔名，預設給 .jpg 或直接用 UUID
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String fileName = UUID.randomUUID().toString() + extension;

        // 4.建立上傳請求
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .contentType(file.getContentType()) // 設定檔案類型，讓瀏覽器可以直接預覽圖片
                .build();

        // 5. 執行上傳
        s3Client.putObject(request, RequestBody.fromBytes(file.getBytes()));

        // 6. 組合回傳 URL
        // 格式: https://{bucket}.s3.{region}.amazonaws.com/{filename}
        String fileUrl = String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, regionString, fileName);

        log.info("AWS S3 上傳成功: {}", fileUrl);
        return fileUrl;
    }
}