package edu.hcmus.doc.fileservice.service.impl;

import static edu.hcmus.doc.fileservice.DocFileServiceConst.DELIMITER;
import static edu.hcmus.doc.fileservice.model.enums.FileType.ALLOWED_FILE_TYPES;

import edu.hcmus.doc.fileservice.model.dto.FileWrapper;
import edu.hcmus.doc.fileservice.model.enums.ParentFolderEnum;
import edu.hcmus.doc.fileservice.model.exception.AttachmentNoContentException;
import edu.hcmus.doc.fileservice.model.exception.AttachmentNotFoundException;
import edu.hcmus.doc.fileservice.model.exception.DocFileServiceRuntimeException;
import edu.hcmus.doc.fileservice.model.exception.FileTypeNotAcceptedException;
import edu.hcmus.doc.fileservice.model.exception.ResourceNotFoundException;
import edu.hcmus.doc.fileservice.service.MinioService;
import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.ListObjectsArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.Result;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.errors.InvalidResponseException;
import io.minio.errors.ServerException;
import io.minio.errors.XmlParserException;
import io.minio.messages.Item;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Objects;
import java.util.stream.StreamSupport;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RequiredArgsConstructor
@Transactional(rollbackFor = Throwable.class)
@Service
public class MinioServiceImpl implements MinioService {

  @Value("${minio.bucket.name}")
  private String minioBucketName;

  private final MinioClient minioClient;

  @Override
  public ByteArrayResource zipFilesByParentFolderAndFolderName(
      ParentFolderEnum parentFolder,
      String folderName) throws IOException {
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    String prefix = StringUtils.join(List.of(parentFolder, folderName), DELIMITER) + DELIMITER;

    try (ZipOutputStream zipOutputStream = new ZipOutputStream(byteArrayOutputStream)) {
      Iterable<Result<Item>> results = minioClient.listObjects(getListObjectsArgs(prefix));
      List<String> fileKeys = StreamSupport.stream(results.spliterator(), false)
          .map(result -> {
            try {
              return result.get();
            } catch (InvalidKeyException | NoSuchAlgorithmException | ServerException |
                     InsufficientDataException | InternalException | XmlParserException |
                     InvalidResponseException | ErrorResponseException | IOException e) {
              throw new DocFileServiceRuntimeException("Error when zipping files", e);
            }
          })
          .filter(item -> !item.isDir() && !item.objectName().equals(prefix + DELIMITER))
          .map(Item::objectName)
          .toList();

      if (CollectionUtils.isEmpty(fileKeys)) {
        throw new AttachmentNoContentException(AttachmentNoContentException.ATTACHMENT_NO_CONTENT);
      }

      fileKeys.forEach(k -> {
        try {
          InputStream inputStream = getObject(k);
          ZipEntry zipEntry = new ZipEntry(k);
          zipOutputStream.putNextEntry(zipEntry);
          IOUtils.copy(inputStream, zipOutputStream);
          inputStream.close();
        } catch (IOException | InvalidKeyException | NoSuchAlgorithmException | ServerException | InsufficientDataException | InternalException | XmlParserException | InvalidResponseException | ErrorResponseException e) {
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
      Iterable<Result<Item>> results = minioClient.listObjects(getListObjectsArgs(prefix));
      List<String> fileKeys = StreamSupport.stream(results.spliterator(), false)
          .map(result -> {
            try {
              return result.get();
            } catch (InvalidKeyException | NoSuchAlgorithmException | ServerException |
                     InsufficientDataException | InternalException | XmlParserException |
                     InvalidResponseException | ErrorResponseException | IOException e) {
              throw new DocFileServiceRuntimeException("Error when zipping files", e);
            }
          })
          .filter(item -> fileNameList.contains(item.objectName().substring(item.objectName().lastIndexOf("/") + 1)))
          .map(Item::objectName)
          .toList();

      if (CollectionUtils.isEmpty(fileKeys)) {
        throw new AttachmentNoContentException(AttachmentNoContentException.ATTACHMENT_NO_CONTENT);
      }

      fileKeys.forEach(k -> {
        try {
          InputStream inputStream = getObject(k);
          ZipEntry zipEntry = new ZipEntry(k);
          zipOutputStream.putNextEntry(zipEntry);
          IOUtils.copy(inputStream, zipOutputStream);
          inputStream.close();
        } catch (IOException | InvalidKeyException | NoSuchAlgorithmException | ServerException | InsufficientDataException | InternalException | XmlParserException | InvalidResponseException | ErrorResponseException e) {
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
      MultipartFile file)
      throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {

    if (!CollectionUtils.containsAny(ALLOWED_FILE_TYPES, file.getContentType())) {
      throw new FileTypeNotAcceptedException(FileTypeNotAcceptedException.FILE_TYPE_NOT_ACCEPTED);
    }

    PutObjectArgs objectArgs = PutObjectArgs.builder()
        .bucket(minioBucketName)
        .object(StringUtils.join(
            List.of(
                parentFolder,
                folderName,
                Objects.requireNonNull(file.getOriginalFilename()))
            ,
            "/"
        ))
        .contentType(file.getContentType())
        .stream(new ByteArrayInputStream(file.getBytes()), file.getSize(), -1)
        .build();

    minioClient.putObject(objectArgs);
  }

  @Override
  public GetObjectResponse getFile(String key)
      throws ServerException, InsufficientDataException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
    GetObjectArgs objectArgs = GetObjectArgs.builder()
        .bucket(minioBucketName)
        .object(key)
        .build();

    GetObjectResponse response = null;
    try {
      response = minioClient.getObject(objectArgs);
    } catch (ErrorResponseException e) {
      if (e.response().code() == HttpStatus.NOT_FOUND.value()) {
        throw new AttachmentNotFoundException("Attachment not found");
      }
    }

    return response;
  }

  @Override
  public GetObjectResponse uploadFile(
      ParentFolderEnum parentFolder, String folderName, FileWrapper file)
      throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
    if (!CollectionUtils.containsAny(ALLOWED_FILE_TYPES, file.getContentType())) {
      throw new FileTypeNotAcceptedException(FileTypeNotAcceptedException.FILE_TYPE_NOT_ACCEPTED);
    }

    String key = StringUtils.join(List.of(parentFolder, folderName, file.getFileName()), "/");
    var data = file.getData();

    PutObjectArgs objectArgs = PutObjectArgs.builder()
        .object(key)
        .bucket(minioBucketName)
        .contentType(file.getContentType())
        .stream(new ByteArrayInputStream(data), data.length, -1)
        .build();

    minioClient.putObject(objectArgs);
    return getFile(key);
  }

  private GetObjectResponse getObject(String fileName)
      throws ErrorResponseException, InsufficientDataException, InternalException, InvalidKeyException, InvalidResponseException, IOException, NoSuchAlgorithmException, ServerException, XmlParserException {
    return minioClient.getObject(GetObjectArgs
        .builder()
        .bucket(minioBucketName)
        .object(fileName)
        .build());
  }

  private ListObjectsArgs getListObjectsArgs(String prefix) {
    return ListObjectsArgs.builder()
        .bucket(minioBucketName)
        .prefix(prefix)
        .delimiter(DELIMITER)
        .build();
  }
}
