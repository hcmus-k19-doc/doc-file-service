package edu.hcmus.doc.fileservice.controller;

import edu.hcmus.doc.fileservice.DocURL;
import edu.hcmus.doc.fileservice.model.dto.Attachment.AttachmentDto;
import edu.hcmus.doc.fileservice.model.dto.FileDto;
import edu.hcmus.doc.fileservice.service.FileService;
import edu.hcmus.doc.fileservice.service.S3Service;
import edu.hcmus.doc.fileservice.util.mapper.FileMapper;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.alfresco.core.model.Node;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RestController
@RequestMapping(DocURL.API_V1 + "/files")
public class FileController {

  private final FileService fileService;
  private final S3Service s3Service;
  private final FileMapper fileMapper;

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

  @GetMapping("/s3")
  public List<FileDto> getS3BucketName() {
    return s3Service.getFiles().stream().map(fileMapper::toDto).toList();
  }

  @SneakyThrows
  @PostMapping("/s3")
  public boolean uploadFileToS3(@RequestParam("file") MultipartFile multipartFile) {
    return s3Service.uploadFile(multipartFile);
  }
}
