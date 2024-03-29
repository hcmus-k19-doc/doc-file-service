package edu.hcmus.doc.fileservice.controller;

import static edu.hcmus.doc.fileservice.util.mapper.decorator.FileMapperDecorator.CONTENT_LENGTH_KEY;
import static edu.hcmus.doc.fileservice.util.mapper.decorator.FileMapperDecorator.CONTENT_TYPE_KEY;
import static edu.hcmus.doc.fileservice.util.mapper.decorator.FileMapperDecorator.MIME_TYPE_KEY;

import edu.hcmus.doc.fileservice.DocFileServiceURL;
import edu.hcmus.doc.fileservice.model.dto.AttachmentDto;
import edu.hcmus.doc.fileservice.model.enums.ParentFolderEnum;
import edu.hcmus.doc.fileservice.service.AwsS3Service;
import edu.hcmus.doc.fileservice.service.FileService;
import edu.hcmus.doc.fileservice.service.MinioService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.alfresco.core.model.Node;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

@RequiredArgsConstructor
@RestController
@RequestMapping(DocFileServiceURL.API_V1 + "/files")
public class FileController {

  private final FileService fileService;
  private final AwsS3Service awsS3Service;
  private final MinioService minioService;

  @GetMapping
  public List<String> getFiles() {
    return fileService.getFileTitles();
  }

  @PostMapping
  @SneakyThrows
  public Node uploadFile(
      @RequestParam("file") MultipartFile multipartFile,
      @RequestParam("parentFolderId") String parentFolderId) {
    return fileService.uploadFile(multipartFile, parentFolderId);
  }

  @PostMapping("/download/{incomingDocId}")
  public ResponseEntity<ByteArrayResource> downloadFile(@RequestBody List<AttachmentDto> attachmentDtoList,
      @PathVariable String incomingDocId) {

    byte[] data = fileService.downloadIncomingDocFolder(attachmentDtoList);
    String zipName = "ICD_" + incomingDocId + "_attachments.zip";
    ByteArrayResource resource = new ByteArrayResource(data);
    HttpHeaders headers = new HttpHeaders();
    headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + zipName);
    headers.add(HttpHeaders.CONTENT_TYPE, "application/zip");
    headers.add(HttpHeaders.CONTENT_LENGTH, String.valueOf(data.length));
    return ResponseEntity.ok()
        .headers(headers)
        .body(resource);
  }

  @SneakyThrows
  @GetMapping("/s3/{parentFolder}/{folderName}/{fileName}")
  public ResponseEntity<byte[]> getFileFromS3(
      @PathVariable ParentFolderEnum parentFolder,
      @PathVariable String folderName,
      @PathVariable String fileName) {
    ResponseInputStream<GetObjectResponse> responseResponseInputStream = awsS3Service.getFile(parentFolder, folderName, fileName);
    GetObjectResponse response = responseResponseInputStream.response();


    HttpHeaders headers = new HttpHeaders();
    headers.add(HttpHeaders.CONTENT_TYPE, response.metadata().get(MIME_TYPE_KEY));
    headers.add(HttpHeaders.CONTENT_LENGTH, response.contentLength().toString());
    return ResponseEntity.ok()
        .headers(headers)
        .body(responseResponseInputStream.readAllBytes());
  }

  @SneakyThrows
  @GetMapping("/s3/{parentFolder}/{folderName}")
  public ResponseEntity<ByteArrayResource> downloadZipFileFromS3(
      @PathVariable ParentFolderEnum parentFolder,
      @PathVariable String folderName) {
    ByteArrayResource resource = awsS3Service.zipFilesByParentFolderAndFolderName(parentFolder, folderName);
    String zipName = StringUtils.join(
        List.of(parentFolder, folderName, "attachments.zip"),
        "_"
    );

    HttpHeaders headers = new HttpHeaders();
    headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + zipName);
    headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);
    headers.add(HttpHeaders.CONTENT_LENGTH, String.valueOf(resource.contentLength()));
    return ResponseEntity.ok()
        .headers(headers)
        .body(resource);
  }

  @SneakyThrows
  @PostMapping("/s3/download/{parentFolder}/{folderName}")
  public ResponseEntity<ByteArrayResource> downloadZipFileFromS3IgnoreDeleted(
      @RequestBody List<String> fileNameList,
      @PathVariable ParentFolderEnum parentFolder,
      @PathVariable String folderName) {
    ByteArrayResource resource = awsS3Service.zipFilesByParentFolderAndFolderNameIgnoreDeleted(parentFolder, folderName, fileNameList);
    String zipName = StringUtils.join(
        List.of(parentFolder, folderName, "attachments.zip"),
        "_"
    );

    HttpHeaders headers = new HttpHeaders();
    headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + zipName);
    headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);
    headers.add(HttpHeaders.CONTENT_LENGTH, String.valueOf(resource.contentLength()));
    return ResponseEntity.ok()
        .headers(headers)
        .body(resource);
  }

  @SneakyThrows
  @PostMapping(value = "/s3/{parentFolder}/{folderName}")
  @ResponseStatus(HttpStatus.CREATED)
  public void uploadFileToS3(
      @PathVariable ParentFolderEnum parentFolder,
      @PathVariable String folderName,
      @RequestParam MultipartFile file) {
    awsS3Service.uploadFile(parentFolder, folderName, file);
  }

  @SneakyThrows
  @GetMapping("/get-byte-array-from-s3-key")
  public ResponseEntity<byte[]> getFileUrlFromS3Key(@RequestParam("fileKey") String fileKey) {
    ResponseInputStream<GetObjectResponse> responseResponseInputStream = awsS3Service.getFileFromS3Key(fileKey);
    GetObjectResponse response = responseResponseInputStream.response();

    HttpHeaders headers = new HttpHeaders();
    headers.add(HttpHeaders.CONTENT_TYPE, response.metadata().get(MIME_TYPE_KEY));
    headers.add(HttpHeaders.CONTENT_LENGTH, response.contentLength().toString());
    return ResponseEntity.ok()
        .headers(headers)
        .body(responseResponseInputStream.readAllBytes());
  }

  @SneakyThrows
  @GetMapping("/minio/{parentFolder}/{folderName}")
  public ResponseEntity<ByteArrayResource> downloadZipFileFromMinio(
      @PathVariable ParentFolderEnum parentFolder,
      @PathVariable String folderName) {
    ByteArrayResource resource = minioService.zipFilesByParentFolderAndFolderName(parentFolder, folderName);
    String zipName = StringUtils.join(
        List.of(parentFolder, folderName, "attachments.zip"),
        "_"
    );

    HttpHeaders headers = new HttpHeaders();
    headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + zipName);
    headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);
    headers.add(HttpHeaders.CONTENT_LENGTH, String.valueOf(resource.contentLength()));
    return ResponseEntity.ok()
        .headers(headers)
        .body(resource);
  }

  @SneakyThrows
  @PostMapping(value = "/minio/{parentFolder}/{folderName}")
  @ResponseStatus(HttpStatus.CREATED)
  public void uploadFileToMinio(
      @PathVariable ParentFolderEnum parentFolder,
      @PathVariable String folderName,
      @RequestParam MultipartFile file) {
    minioService.uploadFile(parentFolder, folderName, file);
  }

  @SneakyThrows
  @GetMapping("/minio/{parentFolder}/{folderName}/{fileName}")
  public ResponseEntity<byte[]> getFileFromMinio(
      @PathVariable ParentFolderEnum parentFolder,
      @PathVariable String folderName,
      @PathVariable String fileName) {
    io.minio.GetObjectResponse minioObjectResponse = minioService.getFile(StringUtils.join(List.of(parentFolder, folderName, fileName), "/"));

    HttpHeaders headers = new HttpHeaders();
    headers.add(HttpHeaders.CONTENT_TYPE, minioObjectResponse.headers().get(CONTENT_TYPE_KEY));
    headers.add(HttpHeaders.CONTENT_LENGTH, minioObjectResponse.headers().get(CONTENT_LENGTH_KEY));
    return ResponseEntity.ok()
        .headers(headers)
        .body(minioObjectResponse.readAllBytes());
  }

  @SneakyThrows
  @PostMapping("/minio/download/{parentFolder}/{folderName}")
  public ResponseEntity<ByteArrayResource> downloadZipFileFromMinioIgnoreDeleted(
      @RequestBody List<String> fileNameList,
      @PathVariable ParentFolderEnum parentFolder,
      @PathVariable String folderName) {
    ByteArrayResource resource = minioService.zipFilesByParentFolderAndFolderNameIgnoreDeleted(parentFolder, folderName, fileNameList);
    String zipName = StringUtils.join(
        List.of(parentFolder, folderName, "attachments.zip"),
        "_"
    );

    HttpHeaders headers = new HttpHeaders();
    headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + zipName);
    headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);
    headers.add(HttpHeaders.CONTENT_LENGTH, String.valueOf(resource.contentLength()));
    return ResponseEntity.ok()
        .headers(headers)
        .body(resource);
  }

  @SneakyThrows
  @GetMapping("/minio/files")
  public ResponseEntity<byte[]> getFileContentFromMinio(@RequestParam String fileKey) {
    io.minio.GetObjectResponse minioObjectResponse = minioService.getFile(fileKey);

    HttpHeaders headers = new HttpHeaders();
    headers.add(HttpHeaders.CONTENT_TYPE, minioObjectResponse.headers().get(CONTENT_TYPE_KEY));
    headers.add(HttpHeaders.CONTENT_LENGTH, minioObjectResponse.headers().get(CONTENT_LENGTH_KEY));
    return ResponseEntity.ok()
        .headers(headers)
        .body(minioObjectResponse.readAllBytes());
  }
}
