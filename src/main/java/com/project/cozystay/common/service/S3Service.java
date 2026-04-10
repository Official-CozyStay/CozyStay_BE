package com.project.cozystay.common.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Client s3Client;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${spring.cloud.aws.region.static}")
    private String region;

    public String uploadFile(MultipartFile file) {
        validateFile(file);

        String originalFilename = file.getOriginalFilename();
        String fileName = createFileName(originalFilename);

        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(fileName)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(
                    putObjectRequest,
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize())
            );

            return getFileUrl(fileName);

        } catch (IOException e) {
            throw new RuntimeException("S3 파일 업로드 중 오류가 발생했습니다.", e);
        }
    }

    public void deleteFile(String fileUrl){
        String key = extractKeyFromUrl(fileUrl);

        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        s3Client.deleteObject(deleteObjectRequest);
    }

    public String updateFile(String oldFileUrl, MultipartFile newFile) {
        deleteFile(oldFileUrl);

        return uploadFile(newFile);
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("업로드할 파일이 없습니다.");
        }
    }

    private String createFileName(String originalFilename) {
        String uuid = UUID.randomUUID().toString();

        if (originalFilename == null || originalFilename.isBlank()) {
            return uuid;
        }

        return uuid + "_" + originalFilename;
    }

    private String getFileUrl(String fileName) {
        return s3Client.utilities()
                .getUrl(builder -> builder.bucket(bucket).key(fileName))
                .toString();
    }

    private String extractKeyFromUrl(String fileUrl) {
        return fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
    }
}