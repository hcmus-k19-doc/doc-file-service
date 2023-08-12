package edu.hcmus.doc.fileservice.config;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinioConfiguration {

  @Value("${minio.url}")
  private String url;

  @Value("${minio.access.secret}")
  private String secretKey;

  @Value("${minio.access.key}")
  private String accessKey;

  @Bean
  public MinioClient generateMinioClient() {
    return MinioClient.builder()
        .endpoint(url)
        .credentials(accessKey, secretKey)
        .build();
  }
}
