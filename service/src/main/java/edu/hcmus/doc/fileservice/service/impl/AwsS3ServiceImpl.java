package edu.hcmus.doc.fileservice.service.impl;

import static edu.hcmus.doc.fileservice.model.enums.FileType.ALLOWED_FILE_TYPES;

import edu.hcmus.doc.fileservice.model.dto.FileWrapper;
import edu.hcmus.doc.fileservice.model.enums.ParentFolderEnum;
import edu.hcmus.doc.fileservice.model.exception.AttachmentNoContentException;
import edu.hcmus.doc.fileservice.model.exception.DocFileServiceRuntimeException;
import edu.hcmus.doc.fileservice.model.exception.FileTypeNotAcceptedException;
import edu.hcmus.doc.fileservice.service.AwsS3Service;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Object;


@Slf4j
@RequiredArgsConstructor
@Transactional(rollbackFor = Throwable.class)
@Service
public class AwsS3ServiceImpl implements AwsS3Service {

  private static final String MIME_TYPE = "MIME-Type";
  private static final String DELIMITER = "/";
  private final S3Client s3Client;
  @Value("${aws.s3.bucket-name}")
  private String s3BucketName;

  @Override
  public List<S3Object> getFiles() {
    ListObjectsV2Request listObjectsV2Request = ListObjectsV2Request.builder()
        .bucket(s3BucketName)
        .build();

    return s3Client.listObjectsV2(listObjectsV2Request).contents();
  }

  @Override
  public ByteArrayResource zipFilesByParentFolderAndFolderName(
      ParentFolderEnum parentFolder,
      String folderName) throws IOException {

    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    String prefix = StringUtils.join(List.of(parentFolder, folderName), DELIMITER) + DELIMITER;

    try (ZipOutputStream zipOutputStream = new ZipOutputStream(byteArrayOutputStream)) {
      ListObjectsV2Request listObjectsV2Request = ListObjectsV2Request.builder()
          .bucket(s3BucketName)
          .prefix(prefix)
          .delimiter(DELIMITER)
          .build();
      List<S3Object> contents = s3Client.listObjectsV2(listObjectsV2Request).contents();
      List<String> fileKeys = contents
          .stream()
          .map(S3Object::key)
          .filter(key -> !key.equals(prefix + DELIMITER))
          .toList();

      if (CollectionUtils.isEmpty(fileKeys)) {
        throw new AttachmentNoContentException(AttachmentNoContentException.ATTACHMENT_NO_CONTENT);
      }

      fileKeys.forEach(k -> {
        GetObjectRequest objectRequest = GetObjectRequest
            .builder()
            .key(k)
            .bucket(s3BucketName)
            .build();

        try {
          ZipEntry zipEntry = new ZipEntry(k);
          zipOutputStream.putNextEntry(zipEntry);
          zipOutputStream.write(s3Client.getObject(objectRequest).readAllBytes());
        } catch (IOException e) {
          throw new DocFileServiceRuntimeException("Error when zip file", e);
        }
      });
    }

    return new ByteArrayResource(byteArrayOutputStream.toByteArray());
  }

  @Override
  public ByteArrayResource zipFilesByParentFolderAndFolderNameIgnoreDeleted(
      ParentFolderEnum parentFolder, String folderName, List<String> fileNameList)
      throws IOException {
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    String prefix = StringUtils.join(List.of(parentFolder, folderName), DELIMITER) + DELIMITER;

    try (ZipOutputStream zipOutputStream = new ZipOutputStream(byteArrayOutputStream)) {
      ListObjectsV2Request listObjectsV2Request = ListObjectsV2Request.builder()
          .bucket(s3BucketName)
          .prefix(prefix)
          .delimiter(DELIMITER)
          .build();
      List<S3Object> contents = s3Client.listObjectsV2(listObjectsV2Request).contents();
      List<String> fileKeys = contents
          .stream()
          .map(S3Object::key)
          .filter(key -> fileNameList.contains(key.substring(key.lastIndexOf("/") + 1)))
          .toList();

      if (CollectionUtils.isEmpty(fileKeys)) {
        throw new AttachmentNoContentException(AttachmentNoContentException.ATTACHMENT_NO_CONTENT);
      }

      fileKeys.forEach(k -> {
        GetObjectRequest objectRequest = GetObjectRequest
            .builder()
            .key(k)
            .bucket(s3BucketName)
            .build();

        try {
          ZipEntry zipEntry = new ZipEntry(k);
          zipOutputStream.putNextEntry(zipEntry);
          zipOutputStream.write(s3Client.getObject(objectRequest).readAllBytes());
        } catch (IOException e) {
          throw new DocFileServiceRuntimeException("Error when zip file", e);
        }
      });
    }

    return new ByteArrayResource(byteArrayOutputStream.toByteArray());
  }

  @Override
  public void uploadFile(
      ParentFolderEnum parentFolder,
      String folderName,
      MultipartFile file) throws IOException {

    if (!CollectionUtils.containsAny(ALLOWED_FILE_TYPES, file.getContentType())) {
      throw new FileTypeNotAcceptedException(FileTypeNotAcceptedException.FILE_TYPE_NOT_ACCEPTED);
    }

    PutObjectRequest objectRequest = PutObjectRequest.builder()
        .key(StringUtils.join(
            List.of(
                parentFolder,
                folderName,
                Objects.requireNonNull(file.getOriginalFilename()))
            ,
            "/"
        ))
        .bucket(s3BucketName)
        .metadata(Map.of(
            MIME_TYPE, Objects.requireNonNull(file.getContentType())
        ))
        .build();

    s3Client.putObject(objectRequest, RequestBody.fromByteBuffer(ByteBuffer.wrap(file.getBytes())));
  }

  @Override
  public GetObjectResponse uploadFile(ParentFolderEnum parentFolder, String folderName,
      FileWrapper file) {
    if (!CollectionUtils.containsAny(ALLOWED_FILE_TYPES, file.getContentType())) {
      throw new FileTypeNotAcceptedException(FileTypeNotAcceptedException.FILE_TYPE_NOT_ACCEPTED);
    }

    String key = StringUtils.join(List.of(parentFolder, folderName, file.getFileName()), "/");
    PutObjectRequest objectRequest = PutObjectRequest.builder()
        .key(key)
        .bucket(s3BucketName)
        .metadata(Map.of(
            MIME_TYPE, file.getContentType()
        ))
        .build();

    s3Client.putObject(objectRequest, RequestBody.fromByteBuffer(ByteBuffer.wrap(file.getData())));

    return getFile(key);
  }

  @Override
  public ResponseInputStream<GetObjectResponse> getFile(
      ParentFolderEnum parentFolder, String folderName, String fileName) {
    GetObjectRequest objectRequest = GetObjectRequest.builder()
        .key(StringUtils.join(List.of(parentFolder, folderName, fileName), "/"))
        .bucket(s3BucketName)
        .build();

    return s3Client.getObject(objectRequest);
  }

  public GetObjectResponse getFile(String key) {
    GetObjectRequest objectRequest = GetObjectRequest.builder()
        .key(key)
        .bucket(s3BucketName)
        .build();

    return s3Client.getObject(objectRequest).response();
  }

  @Override
  public ResponseInputStream<GetObjectResponse> getFileFromS3Key(String fileKey) {
    // check if this key exists in s3
    GetObjectRequest objectRequest = GetObjectRequest.builder()
        .key(fileKey)
        .bucket(s3BucketName)
        .build();

    try {
      return s3Client.getObject(objectRequest);
    } catch (Exception e) {
      log.error("Error when getting file from server");
      throw new AttachmentNoContentException(AttachmentNoContentException.ATTACHMENT_NO_CONTENT);
    }
  }
}
