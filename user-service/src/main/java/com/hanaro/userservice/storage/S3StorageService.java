package com.hanaro.userservice.storage;

import com.hanaro.common.exception.BaseException;
import com.hanaro.common.exception.GlobalErrorCode;
import com.hanaro.common.storage.StorageService;
import com.hanaro.userservice.exception.UserErrorCode;
import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3StorageService implements StorageService {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Override
    public String upload(MultipartFile file, String directory) {
        String key = directory + "/" + UUID.randomUUID() + "_" + file.getOriginalFilename();

        try {
            s3Client.putObject(
                PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .contentType(file.getContentType())
                    .contentLength(file.getSize())
                    .build(),
                RequestBody.fromInputStream(file.getInputStream(), file.getSize())
            );
        } catch (IOException e) {
            throw new BaseException(UserErrorCode.FILE_UPLOAD_FAILED);
        }

        return key;
    }

    @Override
    public void delete(String key) {
        try {
            s3Client.deleteObject(req -> req.bucket(bucket).key(key));
        } catch (Exception e) {
            throw new BaseException(GlobalErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public String getPresignedUrl(String key) {
        if (key == null || key.isEmpty()) return null;
        return s3Presigner.presignGetObject(req -> req
            .signatureDuration(Duration.ofHours(1))
            .getObjectRequest(gor -> gor
                .bucket(bucket)
                .key(key)
            )
        ).url().toString();
    }
}
