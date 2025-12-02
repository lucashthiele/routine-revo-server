package com.lucashthiele.routine_revo_server.infrastructure.storage;

import com.lucashthiele.routine_revo_server.gateway.StorageGateway;
import io.awspring.cloud.s3.S3Template;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Component
public class S3StorageGatewayImpl implements StorageGateway {
  private static final Logger LOGGER = LoggerFactory.getLogger(S3StorageGatewayImpl.class);
  
  private final S3Template s3Template;
  private final S3Client s3Client;
  private final String bucketName;
  private final String region;

  public S3StorageGatewayImpl(
      S3Template s3Template,
      S3Client s3Client,
      @Value("${spring.cloud.aws.s3.bucket}") String bucketName,
      @Value("${spring.cloud.aws.s3.region}") String region) {
    this.s3Template = s3Template;
    this.s3Client = s3Client;
    this.bucketName = bucketName;
    this.region = region;
  }

  @Override
  public String uploadFile(String fileName, InputStream content, String contentType, long contentLength) {
    try {
      // Generate unique file name to avoid collisions
      String uniqueFileName = generateUniqueFileName(fileName);
      String key = "exercises/" + uniqueFileName;
      
      LOGGER.info("Uploading file to S3: bucket={}, key={}", bucketName, key);
      
      PutObjectRequest putObjectRequest = PutObjectRequest.builder()
          .bucket(bucketName)
          .key(key)
          .contentType(contentType)
          .contentLength(contentLength)
          .acl(ObjectCannedACL.PUBLIC_READ)
          .build();
      
      s3Client.putObject(putObjectRequest, 
          software.amazon.awssdk.core.sync.RequestBody.fromInputStream(content, contentLength));
      
      // Construct public URL
      String publicUrl = String.format("https://%s.s3.%s.amazonaws.com/%s", 
          bucketName, region, key);
      
      LOGGER.info("File uploaded successfully: {}", publicUrl);
      return publicUrl;
      
    } catch (Exception e) {
      LOGGER.error("Failed to upload file to S3: {}", fileName, e);
      throw new StorageException("Failed to upload file: " + fileName, e);
    }
  }

  @Override
  public void deleteFile(String fileUrl) {
    try {
      // Extract key from URL
      String key = extractKeyFromUrl(fileUrl);
      
      if (key == null || key.isEmpty()) {
        LOGGER.warn("Invalid file URL for deletion: {}", fileUrl);
        return;
      }
      
      LOGGER.info("Deleting file from S3: bucket={}, key={}", bucketName, key);
      
      DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
          .bucket(bucketName)
          .key(key)
          .build();
      
      s3Client.deleteObject(deleteObjectRequest);
      
      LOGGER.info("File deleted successfully: {}", key);
      
    } catch (Exception e) {
      LOGGER.error("Failed to delete file from S3: {}", fileUrl, e);
      throw new StorageException("Failed to delete file: " + fileUrl, e);
    }
  }
  
  private String generateUniqueFileName(String originalFileName) {
    String uuid = UUID.randomUUID().toString();
    String extension = "";
    
    if (originalFileName != null && originalFileName.contains(".")) {
      extension = originalFileName.substring(originalFileName.lastIndexOf("."));
    }
    
    return uuid + extension;
  }
  
  private String extractKeyFromUrl(String fileUrl) {
    try {
      // Expected format: https://bucket.s3.region.amazonaws.com/key
      if (fileUrl.contains(bucketName + ".s3")) {
        String[] parts = fileUrl.split(bucketName + ".s3." + region + ".amazonaws.com/");
        if (parts.length > 1) {
          return URLDecoder.decode(parts[1], StandardCharsets.UTF_8);
        }
      }
      return null;
    } catch (Exception e) {
      LOGGER.error("Failed to extract key from URL: {}", fileUrl, e);
      return null;
    }
  }
}

