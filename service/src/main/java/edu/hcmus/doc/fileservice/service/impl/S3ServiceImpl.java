package edu.hcmus.doc.fileservice.service.impl;

import edu.hcmus.doc.fileservice.service.S3Service;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CompleteMultipartUploadRequest;
import software.amazon.awssdk.services.s3.model.CompletedPart;
import software.amazon.awssdk.services.s3.model.CreateMultipartUploadRequest;
import software.amazon.awssdk.services.s3.model.CreateMultipartUploadResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Object;
import software.amazon.awssdk.services.s3.model.UploadPartRequest;

@RequiredArgsConstructor
@Transactional(rollbackFor = Throwable.class)
@Service
public class S3ServiceImpl implements S3Service {

  @Value("${aws.s3.bucket-name}")
  private String s3BucketName;

  private final S3Client s3Client;

  @Override
  public List<S3Object> getFiles() {
    ListObjectsV2Request listObjectsV2Request = ListObjectsV2Request.builder()
        .bucket(s3BucketName)
        .build();

    return s3Client.listObjectsV2(listObjectsV2Request).contents();
  }

  @Override
  public boolean uploadFile(MultipartFile multipartFile) throws IOException {
    PutObjectRequest objectRequest = PutObjectRequest.builder()
        .bucket(s3BucketName)
        .build();

    s3Client.putObject(objectRequest, RequestBody.fromByteBuffer(ByteBuffer.wrap(multipartFile.getBytes())));
    return false;
  }
}
