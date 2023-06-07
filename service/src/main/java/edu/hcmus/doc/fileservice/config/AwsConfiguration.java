package edu.hcmus.doc.fileservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
@EnableConfigurationProperties
public class AwsConfiguration {

  @Value("${aws.s3.access-key}")
  private String s3AccessKey;

  @Value("${aws.s3.secret-key}")
  private String s3SecretKey;

  @Value("${aws.s3.region}")
  private String s3Region;

  @Bean
  public S3Client s3Client() {
    AwsCredentials credentials = AwsBasicCredentials.create(
        s3AccessKey,
        s3SecretKey
    );

    return S3Client.builder()
        .credentialsProvider(() -> credentials)
        .region(Region.of(s3Region))
        .build();
  }
}
